package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.ApiKey;
import me.flyray.bsin.domain.request.ApiKeyDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ApiKeyService;
import me.flyray.bsin.infrastructure.biz.ApiSignerBiz;
import me.flyray.bsin.infrastructure.mapper.ApiKeyMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.I18eCode;
import me.flyray.bsin.utils.LocalSigner;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@DubboService
@ApiModule(value = "apiKey")
@ShenyuDubboService("/apiKey")
public class ApiKeyServiceImpl implements ApiKeyService {

    @Autowired
    private ApiKeyMapper apiKeyMapper;
    @Autowired
    private ApiSignerBiz apiSignerBiz;

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
    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    public void add(ApiKeyDTO apiKeyDTO) {
        log.debug("请求ApiKeyService.apiKeyDTO,参数：{}" , apiKeyDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            // 短信验证
            ApiKey customerApiKey = new ApiKey();
            BeanUtils.copyProperties(apiKeyDTO,customerApiKey);

            customerApiKey.setSerialNo(BsinSnowflake.getId());
            customerApiKey.setBizRoleNo(user.getBizRoleTypeNo());
            customerApiKey.setBizRoleType(user.getBizRoleType());
            customerApiKey.setTenantId(user.getTenantId());
            customerApiKey.setCreateBy(user.getUserId());
            customerApiKey.setCreateTime(new Date());
            apiKeyMapper.insert(customerApiKey);
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
    public void edit(ApiKeyDTO apiKeyDTO) {
        log.debug("请求ApiKeyService.apiKeyDTO,参数：{} " , apiKeyDTO);
        try{
            // 短信验证
            ApiKey apiKey = new ApiKey();
            BeanUtils.copyProperties(apiKeyDTO,apiKey);

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            apiKey.setUpdateBy(user.getUserId());
            apiKey.setUpdateTime(new Date());
            apiKeyMapper.updateById(apiKey);
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
    public void delete(ApiKeyDTO apiKeyDTO) {
        log.debug("请求 CustomerApiKeyService.deleteCustomerApiKey,参数：{}",apiKeyDTO);
        try{
            // 短信验证
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            ApiKey apiKey = apiKeyMapper.selectById(apiKeyDTO.getSerialNo());
            if(apiKey == null){
                throw new BusinessException("API_KEY_NOT_FOUND");
            }
            apiKey.setUpdateTime(new Date());
            apiKey.setUpdateBy(user.getUserId());
            apiKeyMapper.updateDelFlag(apiKey);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    @Override
    public Page<ApiKey> getPageList(ApiKeyDTO apiKeyDTO) {
        log.debug("CustomerApiKeyService.pageList: " + apiKeyDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            int current = apiKeyDTO.getCurrent() == null ? 1 : apiKeyDTO.getCurrent();
            int size = apiKeyDTO.getSize() == null ? 10 : apiKeyDTO.getSize();
            QueryWrapper queryWrapper = new QueryWrapper<>(apiKeyDTO);
            queryWrapper.eq("business_role_no", user.getBizRoleTypeNo());
            queryWrapper.eq("business_role_type", user.getCustomerNo());
            queryWrapper.eq("tenant_id", user.getTenantId());
            queryWrapper.orderByDesc("create_time");
            return apiKeyMapper.selectPage(new Page<>(current,size), queryWrapper);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

}
