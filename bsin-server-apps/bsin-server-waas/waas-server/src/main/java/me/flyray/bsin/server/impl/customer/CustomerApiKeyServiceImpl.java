package me.flyray.bsin.server.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cobo.custody.api.client.impl.LocalSigner;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.customer.CustomerApiKey;
import me.flyray.bsin.domain.entity.customer.CustomerChainCoin;
import me.flyray.bsin.domain.request.customer.CustomerApiKeyDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.customer.CustomerApiKeyService;
import me.flyray.bsin.infrastructure.biz.ApiSignerBiz;
import me.flyray.bsin.infrastructure.biz.SmsBiz;
import me.flyray.bsin.infrastructure.mapper.customer.CustomerApiKeyMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.I18eCode;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@Slf4j
@DubboService
@ApiModule(value = "apiKey")
@ShenyuDubboService("/apiKey")
public class CustomerApiKeyServiceImpl implements CustomerApiKeyService {
    @Autowired
    private CustomerApiKeyMapper customerApiKeyMapper;
    @Autowired
    private ApiSignerBiz apiSignerBiz;
    @Autowired
    private SmsBiz smsBiz;


    @Override
    @ShenyuDubboClient("/generateKeyPair")
    @ApiDoc(desc = "generateKeyPair")
    public Map<String, Object> generateKeyPair() {
        log.debug("请求CustomerApiKeyService.generateKeyPair");
        return apiSignerBiz.generateKeyPair();
    }

    @Override
    @ShenyuDubboClient("/signature")
    @ApiDoc(desc = "signature")
    public Map<String, Object> signature(String content,String secretKey) {
        return apiSignerBiz.appendParam(content, secretKey);
    }

    @Override
    @ShenyuDubboClient("/verifyEcdsaSignature")
    @ApiDoc(desc = "verifyEcdsaSignature")
    public Map<String, Object> verifyEcdsaSignature(String content, String signature, String publicKey) {
        Map<String,Object> stringObjectMap = new LinkedHashMap<>();
        Boolean verifyResult = LocalSigner.verifyEcdsaSignature(content, signature, publicKey);
        stringObjectMap.put("verifyResult", verifyResult);
        return stringObjectMap;
    }

    @Override
    @ShenyuDubboClient("/save")
    @ApiDoc(desc = "save")
    public void saveCustomerApiKey(CustomerApiKeyDTO customerApiKeyDTO) {
        log.debug("请求CustomerApiKeyService.saveCustomerApiKey,参数：{}" , customerApiKeyDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            // 短信验证
            smsBiz.verifyCode(customerApiKeyDTO.getUniqueKey(), customerApiKeyDTO.getValidateCode());
            CustomerApiKey customerApiKey = new CustomerApiKey();
            BeanUtils.copyProperties(customerApiKeyDTO,customerApiKey);

            customerApiKey.setSerialNo(BsinSnowflake.getId());
            customerApiKey.setBizRoleNo(user.getBizRoleNo());
            customerApiKey.setBizRoleType(user.getBizRoleType());
            customerApiKey.setTenantId(user.getTenantId());
            customerApiKey.setCreateBy(user.getUserId());
            customerApiKey.setCreateTime(new Date());
            customerApiKeyMapper.insert(customerApiKey);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            log.debug("save merchant api secret error: " , e.getMessage());
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public void editCustomerApiKey(CustomerApiKeyDTO customerApiKeyDTO) {
        log.debug("请求 CustomerApiKeyService.editCustomerApiKey,参数：{} " , customerApiKeyDTO);
        try{
            // 短信验证
            smsBiz.verifyCode(customerApiKeyDTO.getUniqueKey(), customerApiKeyDTO.getValidateCode());
            CustomerApiKey customerApiKey = new CustomerApiKey();
            BeanUtils.copyProperties(customerApiKeyDTO,customerApiKey);

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            customerApiKey.setUpdateBy(user.getUserId());
            customerApiKey.setUpdateTime(new Date());
            customerApiKeyMapper.updateById(customerApiKey);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Override
    public void deleteCustomerApiKey(CustomerApiKeyDTO customerApiKeyDTO) {
        log.debug("请求 CustomerApiKeyService.deleteCustomerApiKey,参数：{}",customerApiKeyDTO);
        try{
            // 短信验证
            smsBiz.verifyCode(customerApiKeyDTO.getUniqueKey(), customerApiKeyDTO.getValidateCode());

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            CustomerApiKey customerApiKey = customerApiKeyMapper.selectById(customerApiKeyDTO.getSerialNo());
            if(customerApiKey == null){
                throw new BusinessException("API_KEY_NOT_FOUND");
            }
            customerApiKey.setUpdateTime(new Date());
            customerApiKey.setUpdateBy(user.getUserId());
            customerApiKeyMapper.updateDelFlag(customerApiKey);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    @Override
    public Page<CustomerApiKey> pageList(CustomerApiKeyDTO customerApiKeyDTO) {
        log.debug("CustomerApiKeyService.pageList: " + customerApiKeyDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            int current = customerApiKeyDTO.getCurrent() == null ? 1 : customerApiKeyDTO.getCurrent();
            int size = customerApiKeyDTO.getSize() == null ? 10 : customerApiKeyDTO.getSize();
            QueryWrapper queryWrapper = new QueryWrapper<>(customerApiKeyDTO);
            queryWrapper.eq("business_role_no", user.getBizRoleNo());
            queryWrapper.eq("business_role_type", user.getCustomerNo());
            queryWrapper.eq("tenant_id", user.getTenantId());
            queryWrapper.orderByDesc("create_time");
            return customerApiKeyMapper.selectPage(new Page<>(current,size), queryWrapper);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

}
