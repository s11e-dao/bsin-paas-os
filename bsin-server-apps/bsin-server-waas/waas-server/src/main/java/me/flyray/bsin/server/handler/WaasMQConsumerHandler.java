package me.flyray.bsin.server.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.infrastructure.biz.TransactionBiz;
import me.flyray.bsin.infrastructure.biz.WalletAccountBiz;
import me.flyray.bsin.mq.enums.MqEventCode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author bolei
 * @date 2024/5/20
 * @desc
 */

@Slf4j
@Component
//@RocketMQMessageListener(consumerGroup = "consumer_group",topic = "waas-test")
public class WaasMQConsumerHandler implements RocketMQListener<String> {

    @Autowired
    private WalletAccountBiz walletAccountBiz;
    @Autowired
    private TransactionBiz transactionBiz;

    /**
     * 1、处理钱包创建消息
     * 2、处理加油确认消息
     * 3、确认交易确认消息
     * @param message
     */
    @Override
    public void onMessage(String message) {
        // 处理消息的逻辑
        log.info("wallet mq received message:{}", message);
        JSONObject mQMsg = JSON.parseObject(message);
        MqEventCode eventCode = MqEventCode.getInstanceById((String) mQMsg.get("eventCode"));

        switch (eventCode){
            case CREATE_MPC_WALLET:
                // 如果是创建钱包队列，则请求MPC网络查询地址
                log.info("createMpcWallet {}", message);
                walletAccountBiz.getAppChainWalletAddress(mQMsg);
                break;
            case GET_GAS_NOTIFY:
                // 如果是gas加油，则发送归集交易
                log.info("getGas {}", message);
                try {
                    transactionBiz.cashConcentration(mQMsg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case CASH_CONCENTRATION_NOTIFY:
                // 如果是归集交易，则确认修改交易状态
                log.info("cashConcentration {}", message);
                try {
                    transactionBiz.cashConcentrationEventNotify(mQMsg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }
}

