package com.hb.encrypt.test.web;

import cn.hutool.crypto.asymmetric.RSA;

public class RSATest {
    public static void main(String[] args) {
        RSA rsa = new RSA();
        System.out.println(rsa.getPrivateKeyBase64());
        System.out.println(rsa.getPublicKeyBase64());
    }
}
