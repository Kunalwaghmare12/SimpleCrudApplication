package com.kunal.simplecrudapp.controller;

import com.kunal.simplecrudapp.dao.EmployeeDao;
import com.kunal.simplecrudapp.dao.HybridEncryptedData;
import com.kunal.simplecrudapp.service.encryptiondecryption.EncryptionDecryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/secure")
public class HybridEncryptionControllers {
    @Value("${RSA-PublicKey}")
    private String publicKey;
    @Value("${RSA-PrivateKey}")
    private String privateKey;

    @Autowired
    private EncryptionDecryptionUtils encryptionDecryptionUtils;

    @PostMapping("/encrypt-data")
    public HybridEncryptedData encryptData(@RequestParam("data") String data) throws Exception {
        String request=data;
        PublicKey key=encryptionDecryptionUtils.getPublicKey(publicKey);
        HybridEncryptedData encryptedData = encryptionDecryptionUtils.encrypt(request,key);
        return  encryptedData;
    }

    @PostMapping("/decrypt-data")
    public Map<String,Object> decryptData(@RequestBody HybridEncryptedData payload) throws Exception {
        PrivateKey key = encryptionDecryptionUtils.getPrivateKey(privateKey);
        String decryptedPayload = encryptionDecryptionUtils.decrypt(payload,key);
        Map<String,Object> map=new LinkedHashMap<>();
        map.put("status","success");
        map.put("response",decryptedPayload);
        return map;
    }




}
