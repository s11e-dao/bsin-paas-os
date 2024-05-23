package me.flyray.bsin.server.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.WxPlatform;
import me.flyray.bsin.domain.domain.WxPlatformMenu;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.WxPlatformMenuTemplateVo;
import me.flyray.bsin.facade.response.WxPlatformMenuTreeVo;
import me.flyray.bsin.facade.service.WxPlatformMenuService;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMenuMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.thirdauth.wx.utils.BsinWxCpServiceUtil;
import me.flyray.bsin.thirdauth.wx.utils.BsinWxMpServiceUtil;
import me.flyray.bsin.thirdauth.wx.utils.WxMpProperties;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform_menu】的数据库操作Service实现
 * @createDate 2024-01-26 01:11:19
 */
@ShenyuDubboService(path = "/tenantWxPlatformMenu", timeout = 6000)
@ApiModule(value = "tenantWxPlatformMenu")
@Service
@Slf4j
public class WxPlatformMenuServiceImpl implements WxPlatformMenuService {

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Value("${wx.mp.config-storage.redis.host}")
  private String wxRedisHost;

  @Value("${wx.mp.config-storage.redis.port}")
  private Integer wxRedisPort;

  @Value("${wx.mp.config-storage.redis.password}")
  private String wxRedisPassword;

  private WxMpProperties.RedisConfig redisConfig;

  @Autowired private WxPlatformMenuMapper wxPlatformMenuMapper;
  @Autowired private WxPlatformMapper wxPlatformMapper;
  @Autowired BsinWxMpServiceUtil bsinWxMpServiceUtil;
  @Autowired BsinWxCpServiceUtil bsinWxCpServiceUtil;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public Map<String, Object> add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    WxPlatformMenu wxPlatformMenu =
        BsinServiceContext.getReqBodyDto(WxPlatformMenu.class, requestMap);
    wxPlatformMenu.setSerialNo(BsinSnowflake.getId());
    wxPlatformMenuMapper.insert(wxPlatformMenu);
    return RespBodyHandler.setRespBodyDto(wxPlatformMenu);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = (String) requestMap.get("serialNo");
    wxPlatformMenuMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    WxPlatformMenu wxPlatformMenu =
        BsinServiceContext.getReqBodyDto(WxPlatformMenu.class, requestMap);
    if (Boolean.TRUE.equals(wxPlatformMenu.getDefaultFlag())) {
      setDefault(requestMap);
    }
    wxPlatformMenuMapper.updateById(wxPlatformMenu);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    WxPlatformMenu wxPlatformMenu = wxPlatformMenuMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(wxPlatformMenu);
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();

    WxPlatformMenu wxPlatformMenu =
        BsinServiceContext.getReqBodyDto(WxPlatformMenu.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<WxPlatformMenu> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<WxPlatformMenu> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(WxPlatformMenu::getCreateTime);
    wrapper.eq(
        StringUtils.isNotBlank(wxPlatformMenu.getSerialNo()),
        WxPlatformMenu::getSerialNo,
        wxPlatformMenu.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatformMenu.getType()),
        WxPlatformMenu::getType,
        wxPlatformMenu.getType());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatformMenu.getStatus()),
        WxPlatformMenu::getStatus,
        wxPlatformMenu.getStatus());
    IPage<WxPlatformMenu> pageList = wxPlatformMenuMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    WxPlatformMenu wxPlatformMenu =
        BsinServiceContext.getReqBodyDto(WxPlatformMenu.class, requestMap);
    LambdaUpdateWrapper<WxPlatformMenu> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(WxPlatformMenu::getCreateTime);
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatformMenu.getSerialNo()),
        WxPlatformMenu::getSerialNo,
        wxPlatformMenu.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatformMenu.getType()),
        WxPlatformMenu::getType,
        wxPlatformMenu.getType());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatformMenu.getStatus()),
        WxPlatformMenu::getStatus,
        wxPlatformMenu.getStatus());
    List<WxPlatformMenu> wxPlatformMenuList = wxPlatformMenuMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(wxPlatformMenuList);
  }

  @ApiDoc(desc = "getDefault")
  @ShenyuDubboClient("/getDefault")
  @Override
  public Map<String, Object> getDefault(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    // 类型：click|view|event
    String type = MapUtils.getString(requestMap, "type");
    LambdaQueryWrapper<WxPlatformMenu> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(StringUtils.isNotBlank(type), WxPlatformMenu::getType, type);
    wrapper.eq(WxPlatformMenu::getDefaultFlag, true);
    WxPlatformMenu wxPlatformMenu = wxPlatformMenuMapper.selectOne(wrapper);
    return RespBodyHandler.setRespBodyDto(wxPlatformMenu);
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public Map<String, Object> setDefault(Map<String, Object> requestMap) {
    WxPlatformMenu wxPlatformMenu =
        BsinServiceContext.getReqBodyDto(WxPlatformMenu.class, requestMap);
    LambdaUpdateWrapper<WxPlatformMenu> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(WxPlatformMenu::getDefaultFlag, wxPlatformMenu.getDefaultFlag());
    lambdaUpdateWrapper.eq(WxPlatformMenu::getSerialNo, wxPlatformMenu.getSerialNo());
    if (Boolean.TRUE.equals(wxPlatformMenu.getDefaultFlag())) {
      wxPlatformMenuMapper.update(
          null,
          new LambdaUpdateWrapper<>(WxPlatformMenu.class)
              .eq(
                  WxPlatformMenu::getWxPlatformMenuTemplateNo,
                  wxPlatformMenu.getWxPlatformMenuTemplateNo())
              .set(WxPlatformMenu::getDefaultFlag, Boolean.FALSE));
    }
    wxPlatformMenuMapper.update(null, lambdaUpdateWrapper);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "syncMenu")
  @ShenyuDubboClient("/syncMenu")
  @Override
  public Map<String, Object> syncMenu(Map<String, Object> requestMap) {

    return RespBodyHandler.setRespBodyDto(null);
  }

  @ApiDoc(desc = "getMenuTemplateMenuTreeList")
  @ShenyuDubboClient("/getMenuTemplateMenuTreeList")
  @Override
  public Map<String, Object> getMenuTemplateMenuTreeList(Map<String, Object> requestMap) {
    List<WxPlatformMenuTreeVo> wxPlatformMenuTreeVos = new ArrayList<WxPlatformMenuTreeVo>();

    return RespBodyHandler.setRespBodyListDto(wxPlatformMenuTreeVos);
  }

  @ApiDoc(desc = "getMenuTemplateMenuTree")
  @ShenyuDubboClient("/getMenuTemplateMenuTree")
  @Override
  public Map<String, Object> getMenuTemplateMenuTree(Map<String, Object> requestMap) {
    // 根据MenuTemplate ID 查出菜单模版详情
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    WxPlatform wxPlatform = wxPlatformMapper.selectById(serialNo);
    if (wxPlatform == null) {
      throw new BusinessException("100000", "未找到微信菜单模版:" + serialNo);
    }

    WxPlatformMenuTemplateVo wxPlatformMenuTemplateVo = new WxPlatformMenuTemplateVo();
    // 填充基础字段
    BeanUtil.copyProperties(wxPlatform, wxPlatformMenuTemplateVo);
    List<WxPlatformMenuTreeVo> wxPlatformMenuTreeVoOnes = findMenuTrees(serialNo);
    // 填充菜单树
    wxPlatformMenuTemplateVo.setChildren(wxPlatformMenuTreeVoOnes);

    return RespBodyHandler.setRespBodyDto(wxPlatformMenuTemplateVo);
  }

  @ApiDoc(desc = "syncMpMenu")
  @ShenyuDubboClient("/syncMpMenu")
  @Override
  public Map<String, Object> syncMpMenu(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    // 模版ID
    String wxPlatformNo = MapUtils.getString(requestMap, "wxPlatformNo");
    if (wxPlatformNo == null) {
      wxPlatformNo = MapUtils.getString(requestMap, "serialNo");
    }
    WxPlatform wxPlatform = wxPlatformMapper.selectById(wxPlatformNo);
    if (wxPlatform == null) {
      throw new BusinessException("100000", "未找到微信平台配置:" + wxPlatformNo);
    }

    try {
      // 获取所有菜单数据(一级菜单）
      List<WxPlatformMenuTreeVo> menuVoList = this.findMenuTrees(wxPlatform.getMenuNo());

      WxMenu wxMenu = new WxMenu();
      List<WxMenuButton> buttons = new ArrayList<WxMenuButton>();
      // 封装button里面的结构，数组格式
      JSONArray buttonList = new JSONArray();
      menuVoList.forEach(
          oneMenuVo -> {
            // json对象  一级菜单
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            one.put("type", oneMenuVo.getType());
            one.put("key", oneMenuVo.getMenuKey());
            one.put("url", oneMenuVo.getUrl());

            WxMenuButton buttonOne = new WxMenuButton();
            buttonOne.setName(oneMenuVo.getName());
            buttonOne.setType(oneMenuVo.getType());
            buttonOne.setKey(oneMenuVo.getMenuKey());
            buttonOne.setUrl(oneMenuVo.getUrl());
            // json数组   二级菜单
            JSONArray subButton = new JSONArray();
            List<WxMenuButton> buttonSub = new ArrayList<WxMenuButton>();
            oneMenuVo
                .getChildren()
                .forEach(
                    twoMenuVo -> {
                      JSONObject view = new JSONObject();
                      WxMenuButton viewButton = new WxMenuButton();
                      view.put("type", twoMenuVo.getType());
                      viewButton.setType(twoMenuVo.getType());
                      if (twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        view.put("url", twoMenuVo.getUrl());
                        viewButton.setName(twoMenuVo.getName());
                        viewButton.setUrl(twoMenuVo.getUrl());
                      } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMenuKey());
                        viewButton.setName(twoMenuVo.getName());
                        viewButton.setKey(twoMenuVo.getMenuKey());
                      }
                      subButton.add(view);
                      buttonSub.add(viewButton);
                    });
            one.put("sub_button", subButton);
            buttonList.add(one);
            buttons.add(buttonOne);
          });
      // 封装最外层的button部分
      JSONObject button = new JSONObject();
      button.put("button", buttonList);
      wxMenu.setButtons(buttons);

      try {
        log.info("button:{}", button.toJSONString());
        log.info("button:{}", wxMenu.toString());

        WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
        config.setAesKey(wxPlatform.getAesKey());
        config.setAppId(wxPlatform.getAppId());
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
        String wxAppSecret = aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8);
        config.setSecret(wxAppSecret);
        config.setToken(wxPlatform.getToken());

        if (redisConfig == null) {
          redisConfig = new WxMpProperties.RedisConfig();
          redisConfig.setHost(wxRedisHost);
          redisConfig.setPort(wxRedisPort);
          redisConfig.setPassword(wxRedisPassword);
        }
        WxMpService wxMpService = bsinWxMpServiceUtil.getWxMpService(config, redisConfig);
        String menuId = wxMpService.getMenuService().menuCreate(button.toJSONString());
        log.info("menuId:{}", menuId);
      } catch (WxErrorException e) {
        e.printStackTrace();
        throw new BusinessException("100000", e.toString());
      }

      return RespBodyHandler.setRespBodyDto(requestMap);

    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }

  @ApiDoc(desc = "removeMpMenu")
  @ShenyuDubboClient("/removeMpMenu")
  @Override
  public Map<String, Object> removeMpMenu(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    // 模版ID
    String wxPlatformNo = MapUtils.getString(requestMap, "wxPlatformNo");
    if (wxPlatformNo == null) {
      wxPlatformNo = MapUtils.getString(requestMap, "serialNo");
    }
    WxPlatform wxPlatform = wxPlatformMapper.selectById(wxPlatformNo);
    if (wxPlatform == null) {
      throw new BusinessException("100000", "未找到微信平台配置:" + wxPlatformNo);
    }
    try {
      WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
      config.setAesKey(wxPlatform.getAesKey());
      config.setAppId(wxPlatform.getAppId());
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      String wxAppSecret = aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8);
      config.setSecret(wxAppSecret);
      config.setToken(wxPlatform.getToken());
      if (redisConfig == null) {
        redisConfig = new WxMpProperties.RedisConfig();
        redisConfig.setHost(wxRedisHost);
        redisConfig.setPort(wxRedisPort);
        redisConfig.setPassword(wxRedisPassword);
      }
      WxMpService wxMpService = bsinWxMpServiceUtil.getWxMpService(config, redisConfig);
      wxMpService.getMenuService().menuDelete();
      return RespBodyHandler.setRespBodyDto(requestMap);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }

  @ApiDoc(desc = "findMenuTrees")
  @ShenyuDubboClient("/findMenuTrees")
  private List<WxPlatformMenuTreeVo> findMenuTrees(String templateNo) {
    // 1、创建List集合，用于最终数据封装
    List<WxPlatformMenuTreeVo> wxPlatformMenuTreeVoOnes = new ArrayList<WxPlatformMenuTreeVo>();
    List<WxPlatformMenu> wxPlatformMenus =
        wxPlatformMenuMapper.selectByWxPlatformMenuTemplateNo(templateNo);
    List<WxPlatformMenu> wxPlatformMenuOnes = new ArrayList<WxPlatformMenu>();
    List<WxPlatformMenu> wxPlatformMenuTwos = new ArrayList<WxPlatformMenu>();
    List<WxPlatformMenu> wxPlatformMenuThrees = new ArrayList<WxPlatformMenu>();

    // 遍历找出一级菜单
    for (WxPlatformMenu wxPlatformMenu : wxPlatformMenus) {
      if (wxPlatformMenu.getParentId().equals("0")) {
        wxPlatformMenuOnes.add(wxPlatformMenu);
        //        wxPlatformMenus.remove(wxPlatformMenu);
      }
    }
    // 根据一级遍历找出二级级菜单
    for (WxPlatformMenu wxPlatformMenuOne : wxPlatformMenuOnes) {
      for (WxPlatformMenu wxPlatformMenu : wxPlatformMenus) {
        // 找出与一级菜单关联的二级菜单
        if (wxPlatformMenu.getParentId().equals(wxPlatformMenuOne.getSerialNo())) {
          wxPlatformMenuTwos.add(wxPlatformMenu);
          //          wxPlatformMenus.remove(wxPlatformMenu);
        }
      }
    }

    // 根据二级遍历找出三级级菜单
    for (WxPlatformMenu wxPlatformMenu : wxPlatformMenus) {
      for (WxPlatformMenu wxPlatformMenuTwo : wxPlatformMenuTwos) {
        if (wxPlatformMenu.getParentId().equals(wxPlatformMenuTwo.getSerialNo())) {
          wxPlatformMenuThrees.add(wxPlatformMenu);
          //          wxPlatformMenus.remove(wxPlatformMenu);
        }
      }
    }
    System.out.println("剩余：" + wxPlatformMenus.size());
    for (WxPlatformMenu wxPlatformMenuOne : wxPlatformMenuOnes) {
      WxPlatformMenuTreeVo wxPlatformMenuTreeVoOne = new WxPlatformMenuTreeVo();
      BeanUtil.copyProperties(wxPlatformMenuOne, wxPlatformMenuTreeVoOne);
      List<WxPlatformMenuTreeVo> wxPlatformMenuTreeVoTwos = new ArrayList<WxPlatformMenuTreeVo>();
      for (WxPlatformMenu wxPlatformMenuTwo : wxPlatformMenuTwos) {
        WxPlatformMenuTreeVo wxPlatformMenuTreeVoTwo = new WxPlatformMenuTreeVo();
        BeanUtil.copyProperties(wxPlatformMenuTwo, wxPlatformMenuTreeVoTwo);
        List<WxPlatformMenuTreeVo> wxPlatformMenuTreeVoThrees =
            new ArrayList<WxPlatformMenuTreeVo>();
        for (WxPlatformMenu wxPlatformMenuThree : wxPlatformMenuThrees) {
          WxPlatformMenuTreeVo wxPlatformMenuTreeVoThree = new WxPlatformMenuTreeVo();
          BeanUtil.copyProperties(wxPlatformMenuThree, wxPlatformMenuTreeVoThree);
          // 3级菜单列表
          if (wxPlatformMenuThree.getParentId().equals(wxPlatformMenuTwo.getSerialNo())) {
            wxPlatformMenuTreeVoThrees.add(wxPlatformMenuTreeVoThree);
          }
        }
        wxPlatformMenuTreeVoTwo.setChildren(wxPlatformMenuTreeVoThrees);
        // 2级菜单列表
        if (wxPlatformMenuTwo.getParentId().equals(wxPlatformMenuOne.getSerialNo())) {
          wxPlatformMenuTreeVoTwos.add(wxPlatformMenuTreeVoTwo);
        }
      }
      wxPlatformMenuTreeVoOne.setChildren(wxPlatformMenuTreeVoTwos);
      // 1级菜单列表
      wxPlatformMenuTreeVoOnes.add(wxPlatformMenuTreeVoOne);
    }
    return wxPlatformMenuTreeVoOnes;
  }

}
