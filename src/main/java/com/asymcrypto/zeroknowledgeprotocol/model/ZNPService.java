package com.asymcrypto.zeroknowledgeprotocol.model;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Getter
@Service
public class ZNPService {
    private BigInteger serverKey;

    public void setServerKey(String serverKey) {
        this.serverKey = new BigInteger(serverKey, 16);
    }
}
