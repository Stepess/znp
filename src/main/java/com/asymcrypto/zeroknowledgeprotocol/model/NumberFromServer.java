package com.asymcrypto.zeroknowledgeprotocol.model;



import lombok.*;

import java.math.BigInteger;

@Data
@Getter
@AllArgsConstructor
public class NumberFromServer {
    private String modulus;

    @Override
    public String toString() {
        return "NumberFromServer{" +
                "modulus=" + modulus +
                '}';
    }
}
