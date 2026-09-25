package com.kunal.simplecrudapp.dao;

public class HybridEncryptedData {

    private String encryptedKey;
    private String iv;
    private String payload;

    public HybridEncryptedData() {
    }

    public HybridEncryptedData(String encryptedKey,
                               String iv,
                               String payload) {

        this.encryptedKey = encryptedKey;
        this.iv = iv;
        this.payload = payload;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
