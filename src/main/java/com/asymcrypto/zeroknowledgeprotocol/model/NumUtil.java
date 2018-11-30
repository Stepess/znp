package com.asymcrypto.zeroknowledgeprotocol.model;

import java.math.BigInteger;
import java.util.Random;

public class NumUtil {

//    public static void main(String[] args) {
//
//        final BigInteger modulus = new BigInteger("88DB9682EB47EAB9AFFA378EBD26944368126765630E5315EA8038CDFC922ED6757A056A8346CBBD4B73001685DA9659AC3D6781FA2D844E287220BFC6846A661F33D7CA8F8DE2E690DAC1F0A24EA87CC65512A813C5C97FD0365EBBA152310E23A1CB6156E8198E12CDEB2B9CDE0B4559E44BFF9080E496C2AFD557FAA04DDFB5874EA538294B256245EEE2920BE11C67D0CBA0F4A873C18204C9DB7E79CD9EF35E4B2EF73415A4CA31EF700CEE9E4FFEAD657F697AD55D7008CEDF7190BA841821C78DA5924AF3549112433E7C7DAE4DCC0DADCB949B8E36D4664F0397B8933DC210F7CC200C581743138EDCE6A64F4D6BA90276094CA666112B1CED3E7D79", 16);
//        BigInteger t = generateRandomBigInteger(modulus);
//        System.out.println("t =" + t.toString(16));
//        BigInteger y = t.modPow(BigInteger.valueOf(2), modulus);
//        System.out.println(y.toString(16));
//
//
//        BigInteger z = new BigInteger("7BC24BB6BBADAB3B264057D6816586583272AB7B34EDB6454FA5AF6038F795878681C1934C1EC542812C14051F0467B8FBDD596311B0641992DFC53345990494F35AF0BD972DCAE136C95E2D1F625DFA4F6B92E51C1DB1A83B53D912CDBFBE6539E8B4BAA47E9BF90BEB7D5F1B02C25CF321BE2A997E0F73AB0E327555E6FF89B1EF170F883A62E3F44B00135068FC5402DCB20F5AF6777065D8844743B3BFCA270B1F650339C6492F665A2DA01F180FD32E1C8C1B4853C695BCDF9B2F55EE10C584D0D51969958557F833D564E3A362C019BC6E5ED56A3477E5869E88A5BC0192A3614692087F62AF6F47C7F7FDB7B37DBB4B053D0D5619268A0DF03318DFD1", 16);
//
//
//        System.out.println("z = " + z.toString(16));
//        System.out.println("z = " + z.subtract(modulus).toString(16));
//
//        t = new BigInteger("179a6ebe4c3f4f3420d0d011c0a362fd01e6cac31e0e65de0900770d48f5389d1145f2dbdc529f26d9c4ca2879b06d6f4fac3ed07d1dfa58bd5e3f69b38b18e633b69efa8b7b2095a76aee03d2f4d6079fbed43a0597584a64e111", 16);
//
//        BigInteger p = t.add(z).gcd(modulus);
//        BigInteger q = modulus.divide(p);
//
//        System.out.println("modulus = " + modulus.toString(16));
//        System.out.println("p = " + p.toString(16));
//        System.out.println("q = " + p.toString(16));
//        System.out.println("p*q = " + p.multiply(q).toString(16));
//    }

    public static BigInteger generateRandomBigInteger(BigInteger boundary) {
        Random random = new Random();
        int bitLength = random.nextInt(boundary.bitLength());
        return new BigInteger(bitLength, random);
    }

    public static BigInteger generateQuadraticDeduction(BigInteger modulus) {
        BigInteger shot = generateRandomBigInteger(modulus);

        while (calculateJacobiSymbol(shot, modulus) != 1) {
            shot = generateRandomBigInteger(modulus);
        }

        return shot;

    }

    public static int calculateJacobiSymbol(BigInteger numerator, BigInteger denominator) {
        final BigInteger eight = BigInteger.valueOf(8);
        final BigInteger five = BigInteger.valueOf(5);
        final BigInteger four = BigInteger.valueOf(4);
        final BigInteger three = BigInteger.valueOf(3);
        final BigInteger two = BigInteger.valueOf(2);

        if (!numerator.gcd(denominator).equals(BigInteger.ONE)) {
            return 0;
        }
        int r = 1;

        if (numerator.compareTo(BigInteger.ZERO) == -1) {
            numerator = numerator.multiply(BigInteger.valueOf(-1));
            if (denominator.mod(four).equals(three)) {
                r = -r;
            }
        }

        while (!numerator.equals(BigInteger.ZERO)) {
            int t = 0;
            while (numerator.mod(two).equals(BigInteger.ZERO)) {
                t++;
                numerator = numerator.divide(two);
            }
            if ((t & 1) == 1) {
                BigInteger buff = denominator.mod(eight);
                if (buff.equals(three) || buff.equals(five)) {
                    r = -r;
                }
            }

            if (numerator.mod(four).equals(denominator.mod(four)) && numerator.mod(four).equals(three)) {
                r = -r;
            }
            BigInteger c = numerator;
            numerator = denominator.mod(c);
            denominator = c;
        }

        return r;
    }
}
