package com.hb.encrypt.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtils {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取私钥
     *
     * @param privateKey 私钥字符串
     * @return PrivateKey
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥字符串
     * @return PublicKey
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return encrypt string
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        PublicKey key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return Base64.encodeBase64String(encryptedData);
    }

    /**
     * RSA解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return decrypt String
     */
    public static String decrypt(String data, String privateKey) throws Exception {
        PrivateKey key = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        out.close();
        return out.toString(String.valueOf(StandardCharsets.UTF_8));
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, String privateKey) throws Exception {
        PrivateKey privateKeys = getPrivateKey(privateKey);
        byte[] keyBytes = privateKeys.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, String publicKey, String sign) throws Exception {
        PublicKey publicKeys = getPublicKey(publicKey);
        byte[] keyBytes = publicKeys.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static void main(String[] args) {
        try {
            String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMQon5hJ27J7RwujylkHkagRcoXuuArsLaSMQi4dJGXAN9aLo4aT6mpoMuISu6k0Ro/fovRyKuD8PmOFPGyWFR4ZOyarQj9FB5O0sT+0rr5dhIcB66Ia4ZoMVfgkwg937K5a4sgLg9cY7stl1ifkBC54BNWVaAnulDkILKDo60rbAgMBAAECgYEAhxo7oZXtHSbPLfMnoN4XshwiNqDOXaeBYD3CLySfNv2G99vBSyWBZLxTn2WrnsSEfP9YqugfJEpXZhAUJ+3OqHCV0oKq9D/I6uRKUY5LRDhjUb+kvfKUo5aFcY59RKKLMUxO/YXJS1HIXk2Utg+5CfYWGTOF3CXntuml4XqL5gECQQDzbys984DTBsCTeuTyV9QdMqczjXnvKosOOl7LW7tQuDHBgd2CfCXyNPwYsGP9FgWUpIKuMv3fQHLxKocvxQOBAkEAzki8zK65HwuYw2BQKUuRss6oYPkjd556MzLRU8e/T7wJT9FQmFmMUID7G7ho06QXfjUww0z1wskW+BJE6jkMWwJAVweZiA50Mf2p/4/iJhnsRXwEdtPPkge9qxqHJWDoONWBRFMvZCUKU+tJbXIybRrgZ+HgrnKgfJ5H+ZxgtF1VAQJAeOltsNPhMm9LtYlosyvWVOaJD3446c58djIdPGximMvw8VYRVZt3gWB4z4DCAPu3wYRP9snPa8MOsY2T025o7QJAZ3Lx9Dsse6718c1R1TJ0+7DIfFaDNsSbtv9xLuE2DCugnA1dR5hiDOBmVLzg/rNKGwyaFgVlhb0h/SVeXXoqhw==";
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDEKJ+YSduye0cLo8pZB5GoEXKF7rgK7C2kjEIuHSRlwDfWi6OGk+pqaDLiErupNEaP36L0cirg/D5jhTxslhUeGTsmq0I/RQeTtLE/tK6+XYSHAeuiGuGaDFX4JMIPd+yuWuLIC4PXGO7LZdYn5AQueATVlWgJ7pQ5CCyg6OtK2wIDAQAB";
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            // RSA加密
            String data="{\"a\":1,\"b\":\"mm测试cs\",\"c\":{\"e\":[1,2,3],\"d\":\"测试\"}}";
            String encryptData = encrypt(data, publicKey);
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, privateKey);
            System.out.println("解密后内容:" + decryptData);

            // RSA签名
            String sign = sign(data, privateKey);
            System.out.println(sign);
            // RSA验签
            boolean result = verify(data, publicKey, sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }
}