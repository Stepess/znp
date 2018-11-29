package com.asymcrypto.zeroknowledgeprotocol.model;



import lombok.*;

import java.math.BigInteger;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerKey {
    private String modulus;

    @Override
    public String toString() {
        return "ServerKey{" +
                "modulus=" + modulus +
                '}';
    }
}
