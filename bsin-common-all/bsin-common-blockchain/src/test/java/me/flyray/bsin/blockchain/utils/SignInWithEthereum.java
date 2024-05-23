package me.flyray.bsin.blockchain.utils;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author bolei
 * @date 2024/3/23
 * @desc
 */
public class SignInWithEthereum {

    public static void main(String[] args) {
        String address = "0x4adbe2009cff6a1e9d280d28815c49e91b8ebad0";
        String nonce = "3618473";
        String signature = "0x9a38fb504315869609ef2e948b1a80f670e6ff725d16b5ae443b118eb2d108bc3c659c2417d96bbff240b44d4f1078fde73b72f83cc71e4e726640bc19a9c2a91c";
        String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
        try {
            String digest = Hash.sha3(
                    Numeric.toHexStringNoPrefix(
                            (MESSAGE_PREFIX + nonce.length() + nonce).getBytes(StandardCharsets.UTF_8)));

            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            byte v = signatureBytes[64];
            System.out.println(v);
            if (v < 27) {
                System.out.println(v);
                v += 27;
            }
            byte[] r = (byte[]) Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = (byte[]) Arrays.copyOfRange(signatureBytes, 32, 64);

            // 使用BigInteger将byte[]转换成大整数
            BigInteger number = new BigInteger(1, r); // 使用1作为signum，表示正数
            // 判断奇偶性
            boolean isEven = number.mod(BigInteger.TWO).equals(BigInteger.ZERO);
            if (isEven) {
                System.out.println("整数是偶数");
            } else {
                System.out.println("整数是奇数");
            }

            Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
            int header = 0;
            for (byte b : signatureData.getV()) {
                header = (header << 8) + (b & 0xFF);
            }
            if (header < 27 || header > 34) {
                System.out.println("false");
            }
            int recId = header - 27;
            BigInteger key = Sign.recoverFromSignature(
                    recId,
                    new ECDSASignature(
                            new BigInteger(1, signatureData.getR()), new BigInteger(1, signatureData.getS())),
                    Numeric.hexStringToByteArray(digest));
            if (key == null) {
                System.out.println("false");
            }
            String signAddress = ("0x" + Keys.getAddress(key)).trim();
            System.out.println("signAddress:" + signAddress);
            if (address.toLowerCase().equals(signAddress.toLowerCase())) {
                System.out.println("true");
            }
        } catch (Exception e) {
            System.out.println("false");
        }
    }

}
