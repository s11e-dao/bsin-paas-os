package me.flyray.bsin.blockchain.protocol.impl;

import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.protocol.S11eProtocolService;

/**
 * @author ：leonard
 * @date ：Created in 2022/12/24 21：00
 * @description： s11e protocol 协议接口实现
 * @modified By：
 */

@Slf4j
@Component
public class S11eProtocolServiceImpl implements S11eProtocolService {

    @Override
    public Map<String, Object> getS11eDaoAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getS11eDaoFactoryAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getERC721TokenExtensionFactoryAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getERC1155TokenExtensionFactoryAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getS11eDaoAllContractAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getDaoAllContractAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getDaoAddress(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getDaoWrapperOrExtensionAddressByName(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> createDao(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> holdShareOnboardingConfigDao(Map<String, Object> requestMap) throws Exception {
        return null;
    }
}
