package com.asymcrypto.zeroknowledgeprotocol.model;



import lombok.*;

@Data
@Getter
@AllArgsConstructor
public class Modulus {
    private String modulus;

    @Override
    public String toString() {
        return "Modulus{" +
                "modulus=" + modulus +
                '}';
    }
}
