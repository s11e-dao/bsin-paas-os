package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.ContractProtocol;
import me.flyray.bsin.domain.domain.CustomerBase;
import me.flyray.bsin.domain.domain.CustomerDigitalAssets;
import me.flyray.bsin.domain.domain.DigitalAssetsCollection;
import me.flyray.bsin.domain.domain.Orderbook;
import me.flyray.bsin.domain.domain.OrderbookMatchJournal;
import me.flyray.bsin.domain.domain.TransferJournal;
import me.flyray.bsin.domain.enums.OrderbookStatus;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.DigitalAssetsDetailRes;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.OrderbookService;
import me.flyray.bsin.infrastructure.mapper.ContractProtocolMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerDigitalAssetsMapper;
import me.flyray.bsin.infrastructure.mapper.DigitalAssetsCollectionMapper;
import me.flyray.bsin.infrastructure.mapper.OrderbookMapper;
import me.flyray.bsin.infrastructure.mapper.OrderbookMatchJournalMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.DigitalAssetsBiz;
import me.flyray.bsin.server.biz.DigitalAssetsItemBiz;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/6/26 15:22
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/orderbook", timeout = 6000)
@ApiModule(value = "orderbook")
@Service
public class OrderbookServiceImpl implements OrderbookService {

    @Autowired
    private OrderbookMapper orderbookMapper;
    @Autowired
    private OrderbookMatchJournalMapper orderbookMatchSerialMapper;
    @Autowired
    private DigitalAssetsBiz digitalAssetsBiz;
    @Autowired
    private ContractProtocolMapper contractProtocolMapper;
    @Autowired
    private CustomerDigitalAssetsMapper customerDigitalAssetsMapper;
    @Autowired
    private DigitalAssetsCollectionMapper digitalAssetsMapper;
    @Autowired
    private DigitalAssetsItemBiz digitalAssetsItemBiz;
    @DubboReference(version = "dev")
    private CustomerService customerService;


    /**
     * 用户将数字资产在集市上挂单卖出，兑换生态积分
     * @param requestMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> maker(Map<String, Object> requestMap){
        // 当前登录用户
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String customerNo = loginUser.getCustomerNo();
        Orderbook orderbook = BsinServiceContext.getReqBodyDto(Orderbook.class, requestMap);
        // 检查用户是否拥有资产，
        LambdaQueryWrapper<CustomerDigitalAssets> customerDigitalAssetsWarapper = new LambdaQueryWrapper<>();
        customerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getCustomerNo, customerNo);
        customerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getDigitalAssetsItemNo, orderbook.getFromDigitalAssetsNo());
        customerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getTokenId, orderbook.getFromTokenId());
        CustomerDigitalAssets customerDigitalAssets = customerDigitalAssetsMapper.selectOne(customerDigitalAssetsWarapper);
        if (customerDigitalAssets == null){
            throw new BusinessException("200000","数字资产不存在");
        }
        // 是否已经挂单
        LambdaQueryWrapper<Orderbook> orderbookWarapper = new LambdaQueryWrapper<>();
        orderbookWarapper.eq(Orderbook::getFromCustomerNo, customerNo);
        orderbookWarapper.eq(Orderbook::getFromDigitalAssetsNo, orderbook.getFromDigitalAssetsNo());
        orderbookWarapper.eq(Orderbook::getFromTokenId, orderbook.getFromTokenId());
        Orderbook orderbookResult = orderbookMapper.selectOne(orderbookWarapper);
        if (orderbookResult != null){
            throw new BusinessException("200000","已经挂单，请勿重复操作！");
        }
        orderbook.setFromCustomerNo(customerNo);
        orderbookMapper.insert(orderbook);
        return RespBodyHandler.setRespBodyDto(orderbook);
    }

    /**
     * 写入交易流水
     * 1、冻结用户积分
     * 2、解除资产所属关系
     * 3、写入交易流水
     * 4、执行数字资产执行链上转移
     * 5、扣除用户积分
     * @param requestMap
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public Map<String, Object> taker(Map<String, Object> requestMap){
        Orderbook orderbookReq = BsinServiceContext.getReqBodyDto(Orderbook.class, requestMap);
        // 买方客户号
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String customerNo = loginUser.getCustomerNo();

        // 查询订单
        Orderbook orderbook = orderbookMapper.selectById(orderbookReq.getSerialNo());

        // 查询卖方资产数量
        LambdaUpdateWrapper<CustomerDigitalAssets> sellerDigitalAssetsWarapper = new LambdaUpdateWrapper<>();
        sellerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getCustomerNo, orderbook.getFromCustomerNo());
        sellerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getDigitalAssetsItemNo, orderbook.getFromDigitalAssetsNo());
        sellerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getTokenId, orderbook.getFromTokenId());
        CustomerDigitalAssets sellerDigitalAssets = customerDigitalAssetsMapper.selectOne(sellerDigitalAssetsWarapper);
        // 1、冻结买方账户金额
//        Map<String, Object> outAccountReq = new HashMap<>();
//        crmClientBiz.outAccount(outAccountReq);

        // 2、解除资产所属关系 判断用户是拥有一个资产还是拥有一个以上
        if(sellerDigitalAssets.getAmount().longValue() == 1 ){
            customerDigitalAssetsMapper.delete(sellerDigitalAssetsWarapper);
        }else {
            sellerDigitalAssets.setAmount(sellerDigitalAssets.getAmount().subtract(new BigDecimal("1")));
            customerDigitalAssetsMapper.update(sellerDigitalAssets,sellerDigitalAssetsWarapper);
        }

        // 查询买方同类型的资产
        LambdaUpdateWrapper<CustomerDigitalAssets> buyerDigitalAssetsWarapper = new LambdaUpdateWrapper<>();
        buyerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getCustomerNo, customerNo);
        buyerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getDigitalAssetsItemNo, orderbook.getFromDigitalAssetsNo());
        buyerDigitalAssetsWarapper.eq(CustomerDigitalAssets::getTokenId, orderbook.getFromTokenId());
        CustomerDigitalAssets buyerDigitalAssets = customerDigitalAssetsMapper.selectOne(buyerDigitalAssetsWarapper);

        if(buyerDigitalAssets == null){
            buyerDigitalAssets = new CustomerDigitalAssets();
            buyerDigitalAssets.setCustomerNo(customerNo);
            buyerDigitalAssets.setDigitalAssetsItemNo(orderbook.getFromDigitalAssetsNo());
            buyerDigitalAssets.setTokenId(orderbook.getFromTokenId());
            buyerDigitalAssets.setAmount(new BigDecimal("1"));
            customerDigitalAssetsMapper.insert(buyerDigitalAssets);
        }else {
            buyerDigitalAssets.setAmount(sellerDigitalAssets.getAmount().add(new BigDecimal("1")));
            customerDigitalAssetsMapper.updateById(buyerDigitalAssets);
        }

        // 3、写入交易流水
        OrderbookMatchJournal orderbookMatchSerial = new OrderbookMatchJournal();
        orderbook.setSerialNo(null);
        BeanUtil.copyProperties(orderbook,orderbookMatchSerial);
        orderbookMatchSerial.setOrderbookNo(orderbook.getSerialNo());
        orderbookMatchSerial.setToCustomerNo(customerNo);
        orderbookMatchSerialMapper.insert(orderbookMatchSerial);

        // 根据资产编号（digitalAssetsNo）查询资产合约信息
        DigitalAssetsCollection digitalAssetsColletion = digitalAssetsMapper.selectById(orderbook.getFromDigitalAssetsNo());

        // TODO 4、数字资产执行链上转移
        TransferJournal transferJournal = new TransferJournal();
        ContractProtocol fromContractProtocol = contractProtocolMapper.getContractProtocolByContract(orderbook.getFromDigitalAssetsNo());
        // 虚拟账户入账
        //ContractProtocol toontractProtocol = contractProtocolMapper.getContractProtocolByContract(orderbook.getToDigitalAssetsNo());

        Map<String, Object> fromResult = new HashMap<>();
        // 查询客户的钱包地址进行转移
        log.debug("部署ERC1155!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        //fromResult = digitalAssetsBiz.transfer(digitalAssets.getContractAddress(),null,true,transferJournal, fromContractProtocol);

        // TODO 5、给卖方入账
//        Map<String, Object> inAccountReq = new HashMap<>();
//        crmClientBiz.inAccount(inAccountReq);

        return RespBodyHandler.setRespBodyDto(orderbook);
    }

    @Override
    public Map<String, Object> cancel(Map<String, Object> requestMap){
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Orderbook orderbook = new Orderbook();
        orderbook.setSerialNo(serialNo);
        orderbook.setStatus(OrderbookStatus.CANCED.getCode());
        // 修改挂单状态
        orderbookMapper.updateById(orderbook);
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 市集挂单中列表查询
     * @param requestMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap){
        Orderbook orderbook = BsinServiceContext.getReqBodyDto(Orderbook.class, requestMap);
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<Orderbook> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        IPage<DigitalAssetsItemRes> orderbookPageList = orderbookMapper.selectOrderbookPage(page,orderbook);
        List<String> customerNos = new ArrayList<>();
        for (DigitalAssetsItemRes digitalAssetsItemRes : orderbookPageList.getRecords()) {
            // 获取商户信息和用户信息
            customerNos.add(digitalAssetsItemRes.getMerchantNo());
            customerNos.add(digitalAssetsItemRes.getCustomerNo());
        }
        if (customerNos.size() > 0){
            Map crmReqMap = new HashMap();
            crmReqMap.put("customerNos",customerNos);
            Map<String, Object> customerResult = customerService.getListByCustomerNos(crmReqMap);
            List<CustomerBase> customerList = (List<CustomerBase>) customerResult.get("data");
            List<DigitalAssetsItemRes> digitalAssetsItemResList = new ArrayList<>();
            for (DigitalAssetsItemRes digitalAssetsItemRes : orderbookPageList.getRecords()) {
                // 找出客户信息
                for (CustomerBase customer : customerList) {
                    if (digitalAssetsItemRes.getCustomerNo().equals(customer.getCustomerNo())){
                        digitalAssetsItemRes.setUsername(customer.getUsername());
                        digitalAssetsItemRes.setAvatar(customer.getAvatar());
                    }else if(digitalAssetsItemRes.getMerchantNo().equals(customer.getCustomerNo())){
                        digitalAssetsItemRes.setMerchantName(customer.getUsername());
                    }
                }
                digitalAssetsItemResList.add(digitalAssetsItemRes);
            }
            orderbookPageList.setRecords(digitalAssetsItemResList);
        }
        return RespBodyHandler.setRespPageInfoBodyDto(orderbookPageList);
    }

    /**
     * 市集挂单详情
     * 1、订单信息
     * 2、订单对应的资产信息
     * @param requestMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap){
        // 挂单号
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Orderbook orderbook = orderbookMapper.selectById(serialNo);

        DigitalAssetsDetailRes digitalAssetsDetailRes = digitalAssetsItemBiz.getDetail(orderbook.getFromDigitalAssetsNo(),null, orderbook.getFromTokenId());
        digitalAssetsDetailRes.setOrderbook(orderbook);
        // TODO 成交记录数据
        return RespBodyHandler.setRespBodyDto(digitalAssetsDetailRes);
    }

}
