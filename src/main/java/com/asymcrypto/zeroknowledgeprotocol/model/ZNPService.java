package com.asymcrypto.zeroknowledgeprotocol.model;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class ZNPService {
    private BigInteger serverKey;

    public void setServerKey(String serverKey) {
        this.serverKey = new BigInteger(serverKey, 16);
    }
}
