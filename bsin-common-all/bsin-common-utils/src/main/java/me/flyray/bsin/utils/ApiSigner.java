package me.flyray.bsin.utils;

/***
 *
 */
public interface ApiSigner {
    String sign(byte[] message);

    String getPublicKey();
}
