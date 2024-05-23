package me.flyray.bsin.facade.service.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.customer.CustomerApiKey;
import me.flyray.bsin.domain.request.customer.CustomerApiKeyDTO;
import me.flyray.bsin.utils.BsinResultEntity;

import java.util.List;
import java.util.Map;

public interface CustomerApiKeyService {


    /**
     * 生成秘钥对
     * @return
     * @
     */
    Map<String,Object> generateKeyPair() ;

    /**
     * 签名
     * @content 样例：POST|/v1/custody/test/|1537498830736|amount=100.0&price=100.0&side=buy
     *
     */
    Map<String,Object> signature(String content,String publicKey);

    /**
     * 验证签名
     * content 样例：POST|/v1/custody/test/|1537498830736|amount=100.0&price=100.0&side=buy
     */
    Map<String,Object> verifyEcdsaSignature(String content, String signature, String coboPubKey);

    /**
     * 保存商户api秘钥以及设置ip白名单
     * ipWhitList：ip白名单以;(英文分号)进行分割
     * @param customerApiKeyDTO
     * @return
     * @
     */
    void saveCustomerApiKey(CustomerApiKeyDTO customerApiKeyDTO) ;

    /**
     * 编辑商户api秘钥以及设置ip白名单
     * ipWhitList：ip白名单以;(英文分号)进行分割
     * @param customerApiKeyDTO
     * @return
     * @
     */
    void editCustomerApiKey(CustomerApiKeyDTO customerApiKeyDTO) ;

    /**
     * 删除商户api秘钥
     * @param customerApiKeyDTO
     * @return
     * @
     */
    void deleteCustomerApiKey(CustomerApiKeyDTO customerApiKeyDTO) ;

    /**
     * 分页商户api秘钥以及设置ip白名单
     * @param customerApiKeyDTO
     * @return
     * @
     */
    Page<CustomerApiKey> pageList(CustomerApiKeyDTO customerApiKeyDTO);
}
