package me.flyray.bsin.server.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.request.ChainCoinDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ChainCoinService;
import me.flyray.bsin.server.listen.ChainTransactionListen;
import me.flyray.bsin.infrastructure.mapper.ChainCoinMapper;
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
* @description 针对表【crm_coin(币;)】的数据库操作Service实现
* @createDate 2024-04-24 20:36:46
*/

@Slf4j
@DubboService
@ApiModule(value = "chainCoin")
@ShenyuDubboService("/chainCoin")
public class ChainCoinServiceImpl implements ChainCoinService {

    @Autowired
    private ChainCoinMapper chainCoinMapper;
    @Autowired
    private ChainTransactionListen chainTransactionListen;

    @Override
    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    @Transactional(rollbackFor = BusinessException.class)
    public void add(ChainCoinDTO chainCoinDTO) {
        log.debug("请求ChainCoinService.saveChainCoin,参数:{}", chainCoinDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            // 短信验证
            ChainCoin chainCoin = new ChainCoin();
            BeanUtils.copyProperties(chainCoinDTO,chainCoin);

            List<ChainCoin> chainCoinList;
            QueryWrapper<ChainCoin> queryWrapper = new QueryWrapper<>();
            // 链上货币KEY不能重复
            {
                queryWrapper.eq("chain_coin_key", chainCoinDTO.getChainCoinKey());
                chainCoinList = chainCoinMapper.selectList(queryWrapper);
                if(!chainCoinList.isEmpty()) {
                    throw new BusinessException("CHAIN_COIN_KEY_ALREADY_EXISTS");
                }
            }
            // 链名和币种 不能重复
            {
                queryWrapper.eq("chain_name", chainCoinDTO.getChainName());
                queryWrapper.eq("coin", chainCoinDTO.getCoin());
                chainCoinList = chainCoinMapper.selectList(queryWrapper);
                if(!chainCoinList.isEmpty()) {
                    throw new BusinessException("CHAIN_NAME_COIN_ALREADY_EXISTS");
                }
            }

            chainCoin.setSerialNo(BsinSnowflake.getId());
            chainCoin.setStatus(chainCoin.getStatus() == null ? 0 : chainCoin.getStatus());  // 默认为下架状态
            chainCoin.setType(chainCoin.getType() == null ? 1 : chainCoin.getType());  //默认类型 1、默认 2、自定义
            chainCoin.setBizRoleTypeNo(user.getBizRoleTypeNo());
            chainCoin.setBizRoleType(user.getBizRoleType());
            chainCoin.setCreateBy(user.getUserId());
            chainCoin.setCreateTime(new Date());
            chainCoinMapper.insert(chainCoin);

            // TODO 监听智能合约上该货币的交易
            if(chainCoin.getStatus() == 1){
                chainTransactionListen.contractAddressMonitor(chainCoin.getContractAddress());
            }
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }



    @Override
    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Transactional(rollbackFor = Exception.class)
    public void edit(ChainCoinDTO chainCoinDTO) {
        log.debug("请求ChainCoinService.editChainCoin,参数:{}", chainCoinDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            ChainCoin chainCoin = new ChainCoin();
            BeanUtils.copyProperties(chainCoinDTO,chainCoin);

            List<ChainCoin> chainCoinList;
            ChainCoin oldChainCoin = chainCoinMapper.selectById(chainCoin.getSerialNo());
            if(oldChainCoin == null) {
                throw new BusinessException("CHAIN_COIN_NOT_EXISTS");
            }
            QueryWrapper<ChainCoin> queryWrapper = new QueryWrapper<>();
            queryWrapper.notIn("serial_no", chainCoin.getSerialNo());
            // 链上货币KEY不能重复
            {
                queryWrapper.eq("chain_coin_key", chainCoin.getChainCoinKey());
                chainCoinList = chainCoinMapper.selectList(queryWrapper);
                if(!chainCoinList.isEmpty()) {
                    throw new BusinessException("CHAIN_COIN_KEY_ALREADY_EXISTS");
                }
            }
            // 链名和币种 不能重复
            {
                queryWrapper.eq("chain_name", chainCoin.getChainName());
                queryWrapper.eq("coin", chainCoin.getCoin());
                chainCoinList = chainCoinMapper.selectList(queryWrapper);
                if(!chainCoinList.isEmpty()) {
                    throw new BusinessException("CHAIN_NAME_COIN_ALREADY_EXISTS");
                }
            }
            chainCoin.setUpdateBy(user.getUserId());
            chainCoin.setUpdateTime(new Date());
            chainCoinMapper.updateById(chainCoin);
            // 币种状态改为上架或修改合约地址，则启动智能合约监听
            if(oldChainCoin.getStatus() == 0 && chainCoin.getStatus() == 1 || !oldChainCoin.getContractAddress().equals(chainCoin.getContractAddress()) ){
                chainTransactionListen.contractAddressMonitor(chainCoin.getContractAddress());
            }
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
    public void delete(ChainCoinDTO chainCoinDTO) {
        log.debug("请求ChainCoinService.deleteChainCoin,参数:{}", chainCoinDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{

            ChainCoin chainCoin = chainCoinMapper.selectById(chainCoinDTO.getSerialNo());
            if(chainCoin == null) {
                throw new BusinessException("CHAIN_COIN_NOT_EXISTS");
            }
            chainCoin.setUpdateBy(user.getUserId());
            chainCoin.setUpdateTime(new Date());
            chainCoinMapper.updateDelFlag(chainCoin);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    public Page<ChainCoin> getPageList(ChainCoinDTO coinDTO) {
        log.debug("请求ChainCoinService.pageList,参数:{}", coinDTO);
        Integer current = coinDTO.getCurrent() == null?1:coinDTO.getCurrent();
        Integer size = coinDTO.getSize() == null?10:coinDTO.getSize();
        QueryWrapper<ChainCoin>  queryWrapper = new QueryWrapper();
        if(coinDTO.getCoin() != null) {
            queryWrapper.eq("coin", coinDTO.getCoin());
        }
        if(coinDTO.getChainCoinKey() != null) {
            queryWrapper.eq("chain_coin_key", coinDTO.getChainCoinKey());
        }
        if(coinDTO.getChainCoinName()!=null){
            queryWrapper.like("chain_coin_name", coinDTO.getChainCoinName());
        }
        if(coinDTO.getStatus()!=null){
            queryWrapper.eq("status", coinDTO.getStatus());
        }
        if(coinDTO.getStartTime()!=null){
            queryWrapper.ge("create_time", coinDTO.getStartTime());
        }
        if(coinDTO.getEndTime()!=null){
            queryWrapper.le("create_time", coinDTO.getEndTime());
        }
        return chainCoinMapper.selectPage(new Page<>(current,size),queryWrapper);
    }

    @Override
    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    public List<ChainCoin> getList(ChainCoinDTO coinDTO) {
        log.debug("请求ChainCoinService.getList,参数:{}", coinDTO);
        QueryWrapper<ChainCoin>  queryWrapper = new QueryWrapper();
        queryWrapper.eq("status", 1);
        return chainCoinMapper.selectList(queryWrapper);
    }

    @Override
    @ShenyuDubboClient("/coinDropDown")
    @ApiDoc(desc = "coinDropDown")
    public List<String> coinDropDown() {
        return chainCoinMapper.coinDropDown();
    }

    @Override
    @ShenyuDubboClient("/chainDropDown")
    @ApiDoc(desc = "chainDropDown")
    public List<String> chainDropDown() {
        return chainCoinMapper.chainDropDown();
    }
}




