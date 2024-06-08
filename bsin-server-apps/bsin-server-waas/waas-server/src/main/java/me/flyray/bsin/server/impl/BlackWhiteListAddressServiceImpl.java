package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;
import me.flyray.bsin.domain.request.BlackWhiteListAddressDTO;
import me.flyray.bsin.domain.response.BlackWhiteListAddressVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BlackWhiteListAddressService;
import me.flyray.bsin.infrastructure.mapper.BlackWhiteListAddressMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author Admin
* @description 针对表【crm_address_black_white_list(地址黑白名单;)】的数据库操作Service实现
* @createDate 2024-04-24 20:37:18
*/
@Slf4j
@DubboService
@ApiModule(value = "blackWhiteListAddress")
@ShenyuDubboService("/blackWhiteListAddress")
public class BlackWhiteListAddressServiceImpl implements BlackWhiteListAddressService {

    @Autowired
    private BlackWhiteListAddressMapper blackWhiteListAddressMapper;

    @Override
    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    @Transactional(rollbackFor = Exception.class)
    public void add(BlackWhiteListAddressDTO blackWhiteListAddressDTO) {
        log.debug("请求BlackWhiteListAddressService.save,参数:{}", blackWhiteListAddressDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        BlackWhiteListAddress blackWhiteListAddress = new BlackWhiteListAddress();
        BeanUtils.copyProperties(blackWhiteListAddressDTO,blackWhiteListAddress);

        String addressId = BsinSnowflake.getId();
        blackWhiteListAddress.setStatus(1); // 启用
        blackWhiteListAddress.setSerialNo(addressId);
        blackWhiteListAddress.setCreateTime(new Date());
        blackWhiteListAddress.setCreateBy(user.getUserId());
        blackWhiteListAddress.setBizRoleNo(user.getBizRoleTypeNo());
        blackWhiteListAddress.setBizRoleType(user.getBizRoleType());
        blackWhiteListAddressMapper.insert(blackWhiteListAddress);
    }

    @Override
    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Transactional(rollbackFor = Exception.class)
    public void edit(BlackWhiteListAddressDTO blackWhiteListAddressDTO) {
        log.debug("请求BlackWhiteListAddressService.edit,参数:{}", blackWhiteListAddressDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            BlackWhiteListAddress blackWhiteListAddress = new BlackWhiteListAddress();
            BeanUtils.copyProperties(blackWhiteListAddressDTO,blackWhiteListAddress);

            blackWhiteListAddress.setUpdateBy(user.getUserId());
            blackWhiteListAddress.setUpdateTime(new Date());
            blackWhiteListAddressMapper.updateById(blackWhiteListAddress);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Transactional(rollbackFor = Exception.class)
    public void delete(BlackWhiteListAddressDTO blackWhiteListAddressDTO) {
        log.debug("请求BlackWhiteListAddressService.delete,参数:{}", blackWhiteListAddressDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{

            BlackWhiteListAddress blackWhiteListAddress = blackWhiteListAddressMapper.selectById(blackWhiteListAddressDTO.getSerialNo());
            if (blackWhiteListAddress == null) {
                throw new BusinessException("ADDRESS_NOT_EXIST");
            }
            blackWhiteListAddress.setUpdateBy(user.getUserId());
            blackWhiteListAddress.setUpdateTime(new Date());
            int i = blackWhiteListAddressMapper.updateDelFlag(blackWhiteListAddress);
            if (i == 0) {
                throw new BusinessException("DELETE_FAIL");
            }
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    public List<BlackWhiteListAddressVO> getList(BlackWhiteListAddressDTO address) {
        log.debug("请求BlackWhiteListAddressService.getList,参数:{}", address);
        return blackWhiteListAddressMapper.selectList(address);
    }

    @Override
    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    public Page<BlackWhiteListAddressVO> getPageList(BlackWhiteListAddressDTO blackWhiteListAddressDTO) {
        log.debug("请求BlackWhiteListAddressService.pageList,参数:{}", blackWhiteListAddressDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        Pagination pagination = blackWhiteListAddressDTO.getPagination();
        blackWhiteListAddressDTO.setBizRoleNo(user.getBizRoleTypeNo());
        blackWhiteListAddressDTO.setBizRoleType(user.getBizRoleType());
        return blackWhiteListAddressMapper.pageList(new Page(pagination.getPageNum(),pagination.getPageSize()),blackWhiteListAddressDTO);
    }
}




