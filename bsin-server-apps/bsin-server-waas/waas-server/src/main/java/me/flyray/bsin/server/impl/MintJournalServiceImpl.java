package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MintJournal;
import me.flyray.bsin.facade.service.MintJournalService;
import me.flyray.bsin.infrastructure.mapper.MintJournalMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/13
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/mintJournal", timeout = 6000)
@ApiModule(value = "mintJournal")
@Service
public class MintJournalServiceImpl implements MintJournalService {

  @Autowired private MintJournalMapper mintJournalMapper;

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    MintJournal mintJournal = BsinServiceContext.getReqBodyDto(MintJournal.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<MintJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<MintJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(MintJournal::getCreateTime);
    warapper.eq(MintJournal::getTenantId, loginUser.getTenantId());
    warapper.eq(MintJournal::getMerchantNo, loginUser.getMerchantNo());
    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getTokenId()),
        MintJournal::getTokenId,
        mintJournal.getTokenId());
    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getTxHash()),
        MintJournal::getTxHash,
        mintJournal.getTxHash());
    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getDigitalAssetsCollectionNo()),
        MintJournal::getDigitalAssetsCollectionNo,
        mintJournal.getDigitalAssetsCollectionNo());

    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getAssetsType()),
        MintJournal::getAssetsType,
        mintJournal.getAssetsType());

    IPage<MintJournal> pageList = mintJournalMapper.selectPage(page, warapper);
    return pageList;
  }

  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public MintJournal getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    MintJournal mintJournal = mintJournalMapper.selectById(serialNo);
    return mintJournal;
  }

}
