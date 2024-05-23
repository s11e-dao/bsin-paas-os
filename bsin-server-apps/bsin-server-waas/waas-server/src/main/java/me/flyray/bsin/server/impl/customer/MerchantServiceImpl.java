package me.flyray.bsin.server.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.entity.customer.CustomerBase;
import me.flyray.bsin.domain.entity.customer.Merchant;
import me.flyray.bsin.domain.entity.customer.CustomerConfig;
import me.flyray.bsin.domain.request.customer.MerchantDTO;
import me.flyray.bsin.domain.request.customer.MerchantRegisterRequest;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.biz.WalletAccountBiz;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.infrastructure.mapper.customer.*;
import me.flyray.bsin.infrastructure.utils.RedisClientUtil;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.*;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.facade.service.customer.MerchantService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bolei
 * @date 2023/6/28 16:41
 * @desc
 */

@Slf4j
@DubboService
@ApiModule(value = "merchant")
@ShenyuDubboService("/merchant")
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private CustomerBaseMapper customerBaseMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private CustomerConfigMapper customerConfigMapper;
    @Autowired
    private CustomerApiKeyMapper customerApiKeyMapper;
    @Autowired
    private SettlementAccountMapper settlementAccountMapper;
    @Autowired
    private ChainCoinMapper chainCoinMapper;
    @Autowired
    private WalletAccountBiz walletAccountBiz;
    @Autowired
    private WalletMapper walletMapper;

    @Override
    @ShenyuDubboClient("/register")
    @ApiDoc(desc = "register")
    @Transactional(rollbackFor = Exception.class)
    public BsinResultEntity register(MerchantRegisterRequest merchantRegisterRequest) {
        log.debug("请求MemberService.register,参数:{}", merchantRegisterRequest);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("tenant_Id", merchantRegisterRequest.getTenantId());
            queryWrapper.eq("username", merchantRegisterRequest.getUsername());
            queryWrapper.eq("auth_method", merchantRegisterRequest.getAuthMethod());
            CustomerBase oldCustomerBase = customerBaseMapper.selectOne(queryWrapper);
            if(oldCustomerBase != null) {
                throw new BusinessException(ResponseCode.USER_EXIST);
            }

            // 1、保存客户基础信息
            CustomerBase customerBase = new CustomerBase();
            String customerId = BsinSnowflake.getId();
            // TODO 一期验证方法默认为 0、手机号
            customerBase.setAuthMethod(merchantRegisterRequest.getAuthMethod());
            customerBase.setUsername(merchantRegisterRequest.getUsername());
            customerBase.setPassword(merchantRegisterRequest.getPassword());
            // TODO 一期客户类型默认为 3、顶级平台商家客户
            customerBase.setType(merchantRegisterRequest.getCustomerType());
            customerBase.setTxPasswordStatus("0"); //支付密码未设置
            customerBase.setCertificationStatus(false); //未实名认证
            customerBase.setCreateTime(new Date());
            customerBase.setCreateBy(user.getUserId());
            customerBase.setTenantId(merchantRegisterRequest.getTenantId());
            // 生成谷歌验证器token
            String googleToken = AESUtils.AESEnCode(GoogleAuthenticator.getSecretKey());
            customerBase.setGoogleToken(googleToken);
            customerBaseMapper.insert(customerBase);

            // 2、保存商户信息
            Merchant merchant = new Merchant();
            BeanUtils.copyProperties(merchantRegisterRequest, merchant);
            String serialNo = BsinSnowflake.getId();
            merchant.setSerialNo(serialNo);
            merchant.setStatus(1); // 正常
            merchant.setStep(0); // 初始步骤
            merchant.setAuditStatus(0); // 资料待完善
            // TODO 一期默认审核通过
            merchant.setAuditStatus(2);
            merchant.setCreateTime(new Date());
            merchant.setCreateBy(user.getUserId());
            merchantMapper.insert(merchant);

            // 4、创建默认钱包
            Wallet wallet = new Wallet();
            String walletId = BsinSnowflake.getId();
            wallet.setSerialNo(walletId);
            wallet.setWalletName("wallet");     // 钱包的默认名称
            wallet.setBizRoleType(2);   // 客户类型：2、商户
            wallet.setType(1);  // 1、默认钱包
            wallet.setWalletTag("NONE");
            wallet.setStatus(1);    // 正常
            wallet.setCategory(1);  // 钱包分类 1、MVP 2、多签
            wallet.setEnv("EVM");
            wallet.setBizRoleNo(serialNo);
            wallet.setCreateBy(user.getUserId());
            wallet.setCreateTime(new Date());
            walletMapper.insert(wallet);

            // 5、创建钱包账户（以平台上架币种为准）
            QueryWrapper<ChainCoin> queryCoin = new QueryWrapper();
            queryCoin.eq("status", 1);      // 上架
            queryCoin.eq("type", 1);        // 平台默认
            queryCoin.eq("tenantId", merchantRegisterRequest.getTenantId());
            List<ChainCoin> chainCoinList = chainCoinMapper.selectList(queryCoin);
            for(ChainCoin chainCoin : chainCoinList) {
                walletAccountBiz.createWalletAccount(wallet,chainCoin.getSerialNo());
            }
        }catch (BusinessException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            log.debug("请求MemberService.register错误:{}", e.getMessage());
            throw new BusinessException("MERCHANT_REGISTER_FAIL");
        }
        return BsinResultEntity.ok();
    }

    @Override
    @ApiDoc(desc = "login")
    @ShenyuDubboClient("/login")
    public BsinResultEntity<Map<String, Object>> login(MerchantRegisterRequest merchantRegisterRequest) {
        log.info("请求MemberService.login,参数:{}", merchantRegisterRequest);
        String tenantId = merchantRegisterRequest.getTenantId();
        String username = merchantRegisterRequest.getUsername();
        String password = merchantRegisterRequest.getPassword();

        // 判断用户名密码是否为空
        if (EmptyChecker.isEmpty(username) || EmptyChecker.isEmpty(password)) {
            throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
        }
        try{
            // 判断客户是否存在
            QueryWrapper customerWrapper = new QueryWrapper();
            customerWrapper.eq("username", username);
            customerWrapper.eq("tenant_id", tenantId);
            customerWrapper.eq("password", password);
            CustomerBase customerBase = customerBaseMapper.selectOne(customerWrapper);
            if(customerBase == null) {
                throw new BusinessException(ResponseCode.USER_NOT_EXIST);
            }
            // 查询商户信息
            QueryWrapper merchantWrapper = new QueryWrapper();
            merchantWrapper.eq("customer_no", customerBase.getSerialNo());
            merchantWrapper.eq("tenant_id", customerBase.getTenantId());
            Merchant merchant = merchantMapper.selectOne(merchantWrapper);
            if(merchant == null || merchant.getStatus() != 1) {
                throw new BusinessException("MERCHANT_NOT_EXISTS");
            }

            String tempToken = BsinSnowflake.getId();
            RedisClientUtil.append(tempToken,merchant.toString());
            Map<String,Object> map = new HashMap<>();
            map.put("tempToken",tempToken);
            return BsinResultEntity.ok(map);
        }catch (BusinessException e){
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            log.debug("请求MemberService.login错误:{}", e.getMessage());
            throw new BusinessException("MERCHANT_LOGIN_FAIL");
        }
    }

    @Override
    public Map<String, Object> authentication(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> audit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> subscribeFunction(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getMerchantCustomerInfoByUsername(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public Map<String, Object> getListByMerchantNos(Map<String, Object> requestMap) {
        return null;
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public BsinResultEntity<Merchant> editMerchant(Merchant merchant) {
        log.debug("请求MerchantService.editMerchant,参数{}:", merchant);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            Merchant oldMerchant =  merchantMapper.selectById(merchant.getSerialNo());
            if(oldMerchant == null || oldMerchant.getDelFlag().equals("1") ){
                throw new BusinessException("MERCHANT_NOT_EXISTS");
            }
            merchant.setUpdateBy(user.getUserId());
            merchantMapper.updateById(merchant);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            log.debug("edit merchant error: {}" , e.getMessage());
            e.printStackTrace();
            throw new BusinessException("edit merchant error");
        }
        return BsinResultEntity.ok(merchant);
    }

    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Override
    public BsinResultEntity<Merchant> deleteMerchant(String merchantId) {
        log.debug("请求MerchantService.deleteMerchant,参数:{}" , merchantId);
        try{
            Merchant oldMerchant =  merchantMapper.selectById(merchantId);
            if(oldMerchant == null || oldMerchant.getDelFlag().equals("1")){
                throw new BusinessException("MERCHANT_ALREADY_DELETE");
            }
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            // 删除商户信息
            oldMerchant.setDelFlag("1");
            oldMerchant.setUpdateBy(user.getUserId());
            merchantMapper.updateById(oldMerchant);
            // 删除商户配置信息
            customerConfigMapper.batchDeleteByMerchantId(merchantId);
            // 删除商户api秘钥信息
            customerApiKeyMapper.batchDeleteByMerchantId(merchantId);
            // 删除商户结算账户信息
            settlementAccountMapper.batchDeleteByMerchantId(merchantId);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            log.debug("delete merchant error: " ,e.getMessage());
            throw new BusinessException("delete merchant error");
        }
        return BsinResultEntity.ok();
    }

    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    @Override
    public BsinResultEntity<Page<Merchant>> pageList(MerchantDTO merchant){
        log.debug("请求MerchantService.pageList,参数：{}" , merchant);
        Page page = new Page();
        LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
        try{
            if(merchant.getCurrent() == null){
                merchant.setCurrent(1);
            }
            if(merchant.getSize() == null){
                merchant.setSize(10);
            }
            merchant.setTenantId(user.getTenantId());
            page = merchantMapper.pageList(new Page(merchant.getCurrent(),merchant.getSize()),merchant);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            log.debug("find merchant error: " ,e.getMessage());
            e.printStackTrace();
            throw new BusinessException("find merchant error");
        }
        return BsinResultEntity.ok(page);
    }

}
