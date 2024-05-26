package me.flyray.bsin.infrastructure.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import me.flyray.bsin.domain.domain.WalletAccount;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.WalletAccountMapper;
import me.flyray.bsin.infrastructure.utils.OkHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Service
public class TransferBiz {
    private static final Logger log = LoggerFactory.getLogger(TransferBiz.class);
    @Autowired
    private WalletAccountMapper walletAccountMapper;

    // 节点
    private static final String HTTP_URL = "https://go.getblock.io/dc197e59d9b34e0c9428f2f13df66d6e";
    // MPC签名连接URL
    private static final String MPC_SIGN_URL = "http://192.168.1.118:8125/api/v1/mpc/sign/";


    // 连接以太坊节点
    Web3j web3 = Web3j.build(new HttpService(HTTP_URL));



    /**
     * 代币转出交易
     * 1、构建未签名交易
     * 2、序列化交易生成交易 Hash
     * 3、调用 API 创建 MPC Sign 签名任务
     * 4、交易任务审批和签名
     * 5、构建签名交易
     * 6、广播交易
     * fromAddress 发起地址
     * toAddress  接受地址
     * contractAddress 合约地址
     * amount 交易金额
     * decimals 位数
     * gasValue gasPrice
     */
    @Async("taskExecutor")
    public String tokenTransfer(String fromAddress, String toAddress, String contractAddress, BigInteger amount, BigInteger decimals) throws Exception {
        // 1、构建未签名交易
        // 获取发起地址的nonce、chainId
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        EthChainId ethChainId = web3.ethChainId().send();

        // 代币数量
        BigDecimal tokenValue = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, decimals.longValue())));
        // 创建Function对象
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(tokenValue.toBigInteger())), // 接受地址、携带代币数量
                Arrays.asList(new TypeReference<Type>() {
                }));
        String data = FunctionEncoder.encode(function);

        // 预估gas费
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);
        EthEstimateGas gasLimit = web3.ethEstimateGas(transaction).send();
        if(gasLimit.hasError()){
            throw new Exception(String.format("error estimate gas:%s-%s", gasLimit.getError().getCode(), gasLimit.getError().getMessage()));
        }
        // 设定最大优先费 maxfepergas
        BigInteger maxPriorityFeePerGas = Convert.toWei("60", Convert.Unit.GWEI).toBigInteger();
        // 获取最新区块
        EthBlock.Block latestBlock = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();

        // 建议将basefepergas值设置为最新数据块basefepergas值的2倍
        // maxfepergas不能小于basefepergas + maxpriority fepergas
        BigDecimal maxFeePerGas = new BigDecimal(latestBlock.getBaseFeePerGas())
                .multiply(new BigDecimal("2"))
                .add(new BigDecimal(maxPriorityFeePerGas));

        // 加油--ETH转出交易
        String ethAddress ="0xf4f762dcf0e1ad96a9e98c321ef52fb1174c9325";
        BigInteger amountGas = gasLimit.getAmountUsed().multiply(maxFeePerGas.toBigInteger());
        log.info("加油gas费为："+amountGas);
        String ethTxHash = ethTransfer(ethAddress, fromAddress, amountGas);

        // 使线程休眠 5 秒钟
        Thread.sleep(60000); // 5000 毫秒 = 5 秒钟

        // 创建原始交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                ethChainId.getChainId().longValue(),
                nonce,
                gasLimit.getAmountUsed(),
//                BigInteger.valueOf(21000), // Gas limit
                contractAddress,
                Convert.toWei("0", Convert.Unit.ETHER).toBigInteger(),
                data,
                maxPriorityFeePerGas,
                maxFeePerGas.toBigInteger());

        // 2、序列化交易生成交易 Hash
        // 对交易进行编码并计算哈希值
        byte[] encodedRawTransaction = TransactionEncoder.encode(rawTransaction);
        String unsignedHash = Numeric.toHexString(Hash.sha3(encodedRawTransaction));

        // 3、签名交易
        String signedTransaction = signRawTransaction(rawTransaction, unsignedHash, fromAddress);

        // 4、广播交易
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(signedTransaction).send();
        String transactionHash = ethSendTransaction.getTransactionHash();
        if(transactionHash == null){
            log.info("转出合约代币，token_error:{}",ethSendTransaction.getError().getMessage());
        }
        System.out.println("TOKEN_transactionHash:"+transactionHash);
        return transactionHash;
    }

    /**
     * ETH转出交易
     * fromAddress  发起地址
     * toAddress    接受地址
     * amount       eth数量
     * gasLimit     最大gas费
     * gasPrice     gas费价格
     */
    public String ethTransfer(String fromAddress, String toAddress, BigInteger amount) throws Exception {
        log.info("开始ETH转出交易，fromAddress:{}，toAddress:{},amount:{}",fromAddress,toAddress,amount);
        // 获取发起地址的nonce、chainId
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        EthChainId ethChainId = web3.ethChainId().send();

        // 设置 maxPriorityFeePerGas 和 maxFeePerGas
        // 设定最大优先费 maxfepergas
        BigInteger maxPriorityFeePerGas = Convert.toWei("60", Convert.Unit.GWEI).toBigInteger();
        // 获取最新区块
        EthBlock.Block latestBlock = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();

        // 建议将basefepergas值设置为最新数据块basefepergas值的2倍
        // maxfepergas不能小于basefepergas + maxpriority fepergas
        BigDecimal maxFeePerGas = new BigDecimal(latestBlock.getBaseFeePerGas())
                .multiply(new BigDecimal("2"))
                .add(new BigDecimal(maxPriorityFeePerGas));

        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                ethChainId.getChainId().longValue(),
                nonce,
                BigInteger.valueOf(21000), // Gas limit
                toAddress,
                amount , // amount
                maxPriorityFeePerGas,
                maxFeePerGas.toBigInteger()
        );

        // 2、序列化交易生成交易 Hash
        byte[] encodedRawTransaction = TransactionEncoder.encode(rawTransaction);
        String unsignedHash = Numeric.toHexString(Hash.sha3(encodedRawTransaction));

        // 3、签名交易
        String signedTransaction = signRawTransaction(rawTransaction, unsignedHash, fromAddress);

        // 4、广播交易
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(signedTransaction).send();
        String transactionHash = ethSendTransaction.getTransactionHash();
        if(transactionHash == null){
            log.info("转出ETH错误，eth_error:{}",ethSendTransaction.getError().getMessage());
        }
        log.info("ETH_transactionHash: " + transactionHash);
        return transactionHash;
    }

    /**
     * 签名交易
     * @param rawTransaction
     * @param unsignedHash
     * @param address
     * @return
     * @throws Exception
     */
    public String signRawTransaction(RawTransaction rawTransaction,String unsignedHash,String address) throws Exception {
        try{
            log.info("开始签名交易，address:{}",address);
            QueryWrapper<WalletAccount> queryWrapper = new QueryWrapper();
            queryWrapper.eq("address", address);
            WalletAccount walletAccount = walletAccountMapper.selectOne(queryWrapper);

            // 3、调用 API 创建 MPC Sign 签名任务
            String url = MPC_SIGN_URL;
            String pubkey = walletAccount.getPubKey();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", unsignedHash);
            JSONObject jsonData = OkHttpUtils.httpPost(url + pubkey, jsonObject);
            String sig = (String) jsonData.get("signature");

            // 5、构建签名交易
            // Split sig into R, S, V
            String sigR = sig.substring(0, 64);
            String sigS = sig.substring(64, 128);
            String sigV = sig.substring(128);

            Integer v = Integer.parseInt(sigV, 16) + 27;

            // 创建签名。SignatureData对象
            Sign.SignatureData signatureData = new Sign.SignatureData(v.byteValue(),
                    Numeric.hexStringToByteArray(sigR),
                    Numeric.hexStringToByteArray(sigS));
            // 这里的 unsignedTransaction 是获取到签名结果后重新构建的，构建方法与上文中的 "构建未签名交易" 完全一致，
            // 需要注意的是，两次构建过程中使用的所有数据必须保持完全一致。
            byte[] signedMessage = TransactionEncoder.encode(rawTransaction, signatureData);
            String signedTransaction = Numeric.toHexString(signedMessage);
            log.info("签名交易完成，address:{}",address);
            return signedTransaction;
        }catch (Exception e){
            e.printStackTrace();
            log.debug("签名交易失败，原因：{}",e.getMessage());
            throw new BusinessException("error signing raw transaction",e);
        }
    }

    /**
     * 查询
     * @param contractAddress 代币合约地址
     * @param holderAddress 代币持有者地址
     * @return
     * @throws Exception
     */
    public BigInteger getTokenBalance (String contractAddress,String holderAddress) throws Exception {
        // 构造查询代币余额的函数
        Function function = new Function(
                "balanceOf",
                Arrays.asList(new org.web3j.abi.datatypes.Address(holderAddress)),
                Arrays.asList(new TypeReference<Uint256>() {})
        );
        String encodedFunction = FunctionEncoder.encode(function);

        // 发送以太坊调用请求
        EthCall ethCall = web3.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                null, contractAddress, encodedFunction), DefaultBlockParameterName.LATEST).send();

        // 解析调用结果
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        BigInteger balance = (BigInteger) results.get(0).getValue();

        // 打印余额
        System.out.println("Token Balance: " + balance);
        return balance;
    }
}
