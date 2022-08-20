package com.zhuaiwa.dd.tool;

import java.io.PrintStream;
import java.math.BigInteger;

public class CalcMiddlePoint {
    public static void printUsage(PrintStream out) {
        out.println("Usage: " + CalcMiddlePoint.class.getSimpleName() + " <min point token> <max point token>");
    }
    
    public static final BigInteger TWO = new BigInteger("2");
    
    public static BigInteger midpoint(BigInteger left, BigInteger right, int sigbits)
    {
        BigInteger midpoint;
        boolean remainder;
        if (left.compareTo(right) < 0)
        {
            BigInteger sum = left.add(right);
            remainder = sum.testBit(0);
            midpoint = sum.shiftRight(1);
        }
        else
        {
            BigInteger max = TWO.pow(sigbits);
            // wrapping case
            BigInteger distance = max.add(right).subtract(left);
            remainder = distance.testBit(0);
            midpoint = distance.shiftRight(1).add(left).mod(max);
        }
        return midpoint;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage(System.out);
            return;
        }
        
        String minPointToken = args[0];
        String maxPointToken = args[1];
        
        BigInteger minInteger = new BigInteger(minPointToken);
        BigInteger maxInteger = new BigInteger(maxPointToken);
        
        BigInteger midpoint = midpoint(minInteger, maxInteger, 127);
        System.out.println(midpoint);
    }

}
