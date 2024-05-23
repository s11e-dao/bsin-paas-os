package me.flyray.bsin.blockchain.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.ObjectUtils;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.Contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import conflux.web3j.Web3;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bolei
 * @date 2024/3/13
 * @desc 链上合约事件监听
 */

@Slf4j
public class BsinBlockChainEventListonService {

    /**
     * 链上监听数据
     * @param args
     * @throws Exception
     */
//    public static void main(String[] args) throws Exception {
//        // testEventSubscribe();
//        customMonitor();
//        // testEventLogQuery();
//    }

    /**
     * 地址生成
     */
    public static void main(String[] args) throws Exception {
        // 假设这是你的公钥，通常是一个0x开头的字符串
        String publicKey = "0xb845657292c59d8e421055bf76a480942af59b6b57dd1e52ae1a677bf264f6c24f5e920bb377f766ff0752858e4494105d39a480add41c3d0bf483e7ed74329";
        // 使用Web3j的Keys类来生成以太坊地址
        String address = Keys.getAddress(publicKey);
        // 打印生成的以太坊地址
        System.out.println("Eth Address: " + address);
    }

    /**
     * 解析log返回的data
     * @param event 合约中定义的事件
     * @param log 监听到的log
     * @return 解析后的数据
     */
    public static EventValues staticExtractEventParameters(Event event, Log log) {
        final List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (topics == null || topics.size() == 0 || !topics.get(0).equals(encodedEventSignature)) {
            return null;
        }
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());
        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }


    //支持单个和多个事件，同时可以根据事件的indexed参数进行过滤
    private static void customMonitor() throws Exception{
        WebSocketService ws = new WebSocketService("wss://go.getblock.io/466b806dd386446c8c4dcf8e6aa62b78",true);
        ws.connect();
        Web3j web3jWs = Web3j.build(ws);

        //设置过滤条件 这个示例是监听最新的1000个块
        Web3j web3j = Web3j.build(new HttpService("https://rpc.ankr.com/bsc_testnet_chapel"));
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber()
                .subtract(new BigInteger("1000"));
        EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber),
                DefaultBlockParameterName.LATEST, "0x06A0F0fa38AE42b7B3C8698e987862AfA58e90D9");

//             [.addSingleTopic(...) | .addOptionalTopics(..., ...) | ...];
        // 监听指定的事件
//        Event SETEVENT_EVENT = new Event("SetEvent",
//                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
//        filter.addSingleTopic(EventEncoder.encode(SETEVENT_EVENT));
// filter.addOptionalTopics("0x0000000000000000000000000000000000000000000000000000000000000001");

        web3jWs.ethLogFlowable(ethFilter).subscribe(log -> {
            System.out.println(log);
        });

    }

    /**
     * 链上交易订阅
     * websocket 订阅
     */

    public static void testEventSubscribe() {

        Web3j web3j = Web3j.build(new HttpService("https://rpc.ankr.com/bsc_testnet_chapel"));

        // Web3j web3j = Web3j.build(new HttpService("https://rpc.ankr.com/bsc_testnet_chapel"));
        //第一步生成的合约实体
        Contract contract;
        Disposable subscribe = null;
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            log.info("wen3j subscribe --clientVersion-- :{} ", clientVersion);
            //设置过滤条件 这个示例是监听最新的1000个块
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber()
                    .subtract(new BigInteger("1000"));
            EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber),
                    DefaultBlockParameterName.LATEST, "0x06A0F0fa38AE42b7B3C8698e987862AfA58e90D9");
            //监听哪个事件，合约中的event写了几个参数，这里就写几个，类型对应好
            Event event = new Event("Transfer",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>(true) {}));
            ethFilter.addSingleTopic(EventEncoder.encode(event));
            subscribe = web3j.ethLogFlowable(ethFilter).subscribe(tx -> {
                int newBlock = tx.getBlockNumber().intValue();
                log.info("wen3j subscribe --newBlock-- :{} ", newBlock);
                log.info("wen3j subscribe --tx-- :{} ", tx);
                EventValues eventValues = staticExtractEventParameters(event, tx);
                //定义接收参数(本示例使用的事件返回了6个，具体按自己合约来)
                String address1 = "";String address2 = "";int uint1 = 0;
                int uint2 = 0;int uint3 = 0;int uint4 = 0;
                List<Type> indexedValues = eventValues.getIndexedValues();
                if (ObjectUtils.isNotEmpty(indexedValues) && indexedValues.size() == 3) {
                    Type type1 = indexedValues.get(0);
                    address1 = type1.getValue().toString();
                    Type type2 = indexedValues.get(1);
                    address2 = type2.getValue().toString();
                    Type type3 = indexedValues.get(2);
                    uint1 = Integer.parseInt(type3.getValue().toString());
                }
                List<Type> nonIndexedValues = eventValues.getNonIndexedValues();
                if (ObjectUtils.isNotEmpty(nonIndexedValues) && nonIndexedValues.size() == 3) {
                    Type type1 = nonIndexedValues.get(0);
                    uint2 = Integer.parseInt(type1.getValue().toString());
                    Type type2 = nonIndexedValues.get(1);
                    uint3 = Integer.parseInt(type2.getValue().toString());
                    Type type3 = nonIndexedValues.get(2);
                    uint4 = Integer.parseInt(type3.getValue().toString());
                }
                log.info("address1:{};address2:{};uint1:{};uint2:{};uint3:{};uint4:{}",address1, address2, uint1, uint2, uint3, uint4);
            });
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭监听
            if(ObjectUtils.isNotEmpty(subscribe) && !subscribe.isDisposed()){
                subscribe.dispose();
            }
        }
    }

    /**
     * http查询
     */
    public static void testEventLogQuery() {
        Web3j web3j = Web3j.build(new HttpService("https://rpc.ankr.com/bsc_testnet_chapel"));
        //第一步生成的合约实体
        Contract contract;
        Disposable subscribe = null;
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            log.info("wen3j subscribe --clientVersion-- :{} ", clientVersion);
            //设置过滤条件 这个示例是监听最新的1000个块
            BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber()
                    .subtract(new BigInteger("1000"));
            EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber),
                    DefaultBlockParameterName.LATEST, "0x06A0F0fa38AE42b7B3C8698e987862AfA58e90D9");
            //监听哪个事件，合约中的event写了几个参数，这里就写几个，类型对应好
            Event event = new Event("Transfer",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>(true) {}));
            ethFilter.addSingleTopic(EventEncoder.encode(event));

            List<EthLog.LogResult> resp = web3j.ethGetLogs(ethFilter).send().getLogs();
            for (EthLog.LogResult logItem : resp) {
                EthLog.LogObject tx = ObjectUtils.isNotEmpty(logItem.get()) ? (EthLog.LogObject) logItem.get() : null;
                if(ObjectUtils.isEmpty(tx)){
                    return;
                }
                int newBlock = tx.getBlockNumber().intValue();
                log.info("wen3j subscribe --newBlock-- :{} ", newBlock);
                log.info("wen3j subscribe --tx-- :{} ", tx);
                EventValues eventValues = staticExtractEventParameters(event, tx);
                //定义接收参数(本示例使用的事件返回了6个，具体按自己合约来)
                String address1 = "";String address2 = ""; String uint1 = "";
                int uint2 = 0;int uint3 = 0;int uint4 = 0;
                List<Type> indexedValues = eventValues.getIndexedValues();
                if (ObjectUtils.isNotEmpty(indexedValues) && indexedValues.size() == 3) {
                    Type type1 = indexedValues.get(0);
                    address1 = type1.getValue().toString();
                    Type type2 = indexedValues.get(1);
                    address2 = type2.getValue().toString();
                    Type type3 = indexedValues.get(2);
                    uint1 = type3.getValue().toString();
                }
                List<Type> nonIndexedValues = eventValues.getNonIndexedValues();
                if (ObjectUtils.isNotEmpty(nonIndexedValues) && nonIndexedValues.size() == 3) {
                    Type type1 = nonIndexedValues.get(0);
                    uint2 = Integer.parseInt(type1.getValue().toString());
                    Type type2 = nonIndexedValues.get(1);
                    uint3 = Integer.parseInt(type2.getValue().toString());
                    Type type3 = nonIndexedValues.get(2);
                    uint4 = Integer.parseInt(type3.getValue().toString());
                }
                log.info("address1:{};address2:{};uint1:{};uint2:{};uint3:{};uint4:{}",address1, address2, uint1, uint2, uint3, uint4);
            };
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭监听
            if(ObjectUtils.isNotEmpty(subscribe) && !subscribe.isDisposed()){
                subscribe.dispose();
            }
        }
    }


}
