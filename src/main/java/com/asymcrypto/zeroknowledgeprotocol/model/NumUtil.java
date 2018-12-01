package com.asymcrypto.zeroknowledgeprotocol.model;

import java.math.BigInteger;
import java.util.Random;

public class NumUtil {
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
