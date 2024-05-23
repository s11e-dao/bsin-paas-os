package me.flyray.bsin.blockchain.utils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

/**
 * @author bolei
 * @date 2024/3/19
 * @desc
 */
public class MpcSignTest {

    static Web3j web3;
    private static final String READ_ONLY_FROM_ADDRESS = "0x0000000000000000000000000000000000000000";

    static Map<String, String> config;

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException {

        Yaml yaml = new Yaml();
        File file = new File("src/test/resources/config.yaml");
        InputStream inputStream = new FileInputStream(file);
        config = yaml.load(inputStream);

        web3 = Web3j.build(new HttpService(config.get("ethereumRpcApi"),
                new OkHttpClient.Builder().build()));

    }

    @Test
    public void testPackegeTx() throws Exception {
        // mpc签名的交易
        String mpcSig = "73da3fe2f81cac1b83310a08e2d3de5d9895bde49f4c9f15a162f6b442ec59c65e671c440fbe8b52cb4c7de3f1863d7a7ad48fd363e28ce1d458116b183b1c45";
        String rawTransaction = "";
    }
    /**
     * 拆分以太坊交易
     * @throws Exception
     */
    @Test
    public void testSendToken() throws Exception {
        // Wallet Account key
        String fromAddress = config.get("accountTokenAddress");
        String contractAddress = config.get("erc20ContractAddress");
        String toAddress = config.get("toAddress");
        String privateKey = config.get("privateKey");
        System.out.println(String.format("Attempting to send contract transaction from %s to %s, EIP-1559: %s",
                fromAddress, toAddress, true));
        // Create function
        Function function = createERC20Function(contractAddress,"10", toAddress);
        // Create transaction object
        RawTransaction rawTransaction = createRawTransaction(fromAddress,"0", contractAddress, function);

        String mpcSig = null;
        if(true){
            // mpc签名
            // 序列化交易生成交易hash
            // Encode the transaction and compute the hash value
            byte[] encodedRawTransaction = TransactionEncoder.encode(rawTransaction);
            System.out.println("未签名数据: " + encodedRawTransaction);
            String unsignedHash = Numeric.toHexString(Hash.sha3(encodedRawTransaction));
            // 生成未签名的交易hash，用来签名
            // String unsignedHash = Numeric.toHexString(Hash.sha3(encodedRawTransaction)).substring(2);
            System.out.println("未签名hash: " + unsignedHash);
            // mpc签名
            //TODO Sign with safeheron mpc
            String customerRefId = UUID.randomUUID().toString();
            mpcSig = "0x98d577d55127c3fe7e7b84c898f5e1bd2c2a9247074d183ca19295ba495bdb8466168e13b83e0890d9dab1dc4cac072b0d0b94de2768514445656bc07f979caf1b";
            //TODO Get sig
            if(mpcSig == null){
                mpcSig = retrieveSig(customerRefId);
                System.out.println(String.format("got mpc sign result, sig: %s", mpcSig));
            }
        }else {
            // 传统的签名
            Credentials credentials = Credentials.create(privateKey);
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            System.out.println(signMessage);
            mpcSig = Numeric.toHexString(signMessage);
        }
        System.out.println("签名数据: " + mpcSig);

//        // Encode with sig and send transaction
           // encodeWithSigAndSendTransaction(rawTransaction, mpcSig);
    }

    /**
     * 构建未签名的交易
     * @param fromAddress
     * @param value
     * @param contractAddress
     * @param contractFunction
     * @return
     * @throws Exception
     */
    private RawTransaction createRawTransaction(String fromAddress, String value, String contractAddress, Function contractFunction) throws Exception {
        // Get data from block chain: nonce, chainId, gasLimit and latestBlock's baseFeePerGas
        EthGetTransactionCount ethGetTransactionCount = web3
                .ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        EthChainId ethChainId = web3.ethChainId().send();

        String data = contractFunction == null ? "" : FunctionEncoder.encode(contractFunction);

        System.out.println("function data: " + data);
        // Estimate gas
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);
        EthEstimateGas gasLimit = web3.ethEstimateGas(transaction).send();
        if(gasLimit.hasError()){
            throw new Exception(String.format("error estimate gas:%s-%s", gasLimit.getError().getCode(), gasLimit.getError().getMessage()));
        }
        // Estimate maxFeePerGas, we assume maxPriorityFeePerGas's value is 2(gwei)
        // maxPriorityFeePerGas : 给矿工的小费
        BigInteger maxPriorityFeePerGas = Convert.toWei("1000", Convert.Unit.GWEI).toBigInteger();
        EthBlock.Block latestBlock = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock();

        // The baseFeePerGas is recommended to be 2 times the latest block's baseFeePerGas value
        // maxFeePerGas must not less than baseFeePerGas + maxPriorityFeePerGas
        // 每笔gas最大费用
        BigDecimal maxFeePerGas = new BigDecimal(latestBlock.getBaseFeePerGas())
                .multiply(new BigDecimal("2"))
                .add(new BigDecimal(maxPriorityFeePerGas));
        System.out.println("nonce: 随机数:" + nonce);
        // Create raw transaction
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                ethChainId.getChainId().longValue(),
                nonce,
                gasLimit.getAmountUsed(),
                contractAddress,
                Convert.toWei(value, Convert.Unit.ETHER).toBigInteger(),
                data,
                maxPriorityFeePerGas,
                maxFeePerGas.toBigInteger());

        return rawTransaction;
    }

    private Function createERC20Function(String contractAddress, String tokenAmount, String toAddress) throws IOException {
        BigInteger decimals = getERC20Decimals(contractAddress);
        BigDecimal tokenValue = new BigDecimal(tokenAmount).multiply(new BigDecimal(Math.pow(10, decimals.longValue())));
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(tokenValue.toBigInteger())),
                Arrays.asList(new TypeReference<Type>() {
                }));

        return function;
    }

    private String requestMpcSig(String customerRefId, String hash){
//        CreateMPCSignTransactionRequest request = new CreateMPCSignTransactionRequest();
//        request.setCustomerRefId(customerRefId);
//        request.setSourceAccountKey(accountKey);
//        request.setSignAlg("Secp256k1");
//
//        CreateMPCSignTransactionRequest.Date hashItem = new CreateMPCSignTransactionRequest.Date();
//        hashItem.setData(hash);
//        request.setDataList(Arrays.asList(hashItem));
//
//        TxKeyResult response = ServiceExecutor.execute(mpcSignApi.createMPCSignTransactions(request));

        return "response.getTxKey()";
    }

    private String retrieveSig(String customerRefId) throws Exception {
//        OneMPCSignTransactionsRequest request = new OneMPCSignTransactionsRequest();
//        request.setCustomerRefId(customerRefId);

        for(int i = 0; i < 100 ; i++){
//            MPCSignTransactionsResponse response = ServiceExecutor.execute(mpcSignApi.oneMPCSignTransactions(request));
//            System.out.println(String.format("mpc sign transaction status: %s, sub status: %s", response.getTransactionStatus(), response.getTransactionSubStatus()));
//
//            if ("FAILED".equalsIgnoreCase(response.getTransactionStatus()) || "REJECTED".equalsIgnoreCase(response.getTransactionStatus())){
//                System.out.println("mpc sign transaction was FAILED or REJECTED");
//                System.exit(1);
//            }
//
//            if ("COMPLETED".equalsIgnoreCase(response.getTransactionStatus()) && "CONFIRMED".equalsIgnoreCase(response.getTransactionSubStatus())){
//                return response.getDataList().get(0).getSig();
//            }

            System.out.println("wait 5000ms");
            Thread.sleep(5000);
        }

        throw new Exception("can't get sig.");
    }

    private void encodeWithSigAndSendTransaction(RawTransaction rawTransaction, String sig) throws IOException {
        // Encode transaction with signature
        System.out.println("signed transaction: " + sig);

        // Send transaction
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(sig).send();
        if(ethSendTransaction.getError() != null){
            System.out.println("send transaction error, code: " + ethSendTransaction.getError().getCode()
                    + ", message: " + ethSendTransaction.getError().getMessage());
            return;
        }

        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println(String.format("Transaction successful with hash: %s", transactionHash));

    }

    private Sign.SignatureData convertSig(String sig){
        // Split sig into R, S, V
        String sigR = sig.substring(0, 64);
        String sigS = sig.substring(64, 128);
        String sigV = sig.substring(128);

        Integer v = Integer.parseInt(sigV.trim(), 16) + 27;

        // Create and return the Sign.SignatureData Object
        return new Sign.SignatureData(v.byteValue(),
                Numeric.hexStringToByteArray(sigR),
                Numeric.hexStringToByteArray(sigS));
    }

    private BigInteger getERC20Decimals(String contractAddress) throws IOException {
        final Function function = new Function("decimals",
                Arrays.asList(),
                Arrays.asList(new TypeReference<Uint256>() {}));

        String callResult = web3.ethCall(
                        Transaction.createEthCallTransaction(READ_ONLY_FROM_ADDRESS, contractAddress, FunctionEncoder.encode(function)),
                        DefaultBlockParameterName.LATEST)
                .send()
                .getValue();

        List<Type> result = FunctionReturnDecoder.decode(callResult, function.getOutputParameters());
        return (BigInteger)result.get(0).getValue();
    }

    /**
     * ETH代币转账
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void signTokenTransaction() throws IOException, ExecutionException, InterruptedException {

        //发送方地址
        String from = "";
        //转账数量
        String amount = "1";
        //接收者地址
        String to = "";
        //发送方私钥
        String privateKey = "";
        //代币合约地址
        String coinAddress = "";
        //查询地址交易编号
        BigInteger nonce = web3.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //支付的矿工费
        BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("90000");

        Credentials credentials = Credentials.create(privateKey);
        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

        //封装转账交易
        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to),
                        new org.web3j.abi.datatypes.generated.Uint256(amountWei)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        //签名交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);


        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        //广播交易
        String hash = web3.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
        System.out.println("hash:"+hash);
    }

}
