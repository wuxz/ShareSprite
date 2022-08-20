package com.zhuaiwa.dd.tool;

import java.io.PrintStream;
import java.math.BigInteger;

public class DivideRing {
    public static void printUsage(PrintStream out) {
        out.println("Usage: " + DivideRing.class.getSimpleName() + " <num>");
    }
	public static void main(String[] args) throws Exception {
	    if (args.length < 1) {
            printUsage(System.out);
            return;
        }
	    
	    String num = args[1];
//	    String num = "9";
	    
	    BigInteger two = new BigInteger("2");
	    BigInteger max = two.pow(127);
	    
	    BigInteger avr = max.divide(new BigInteger(num));
	    
	    for (int i = 0; i < Integer.valueOf(num); i++) {
	        System.out.println(String.format("%1$3s/%2$3s: %3$s", i, num, avr.multiply(new BigInteger(String.valueOf(i)))));
	    }
	    System.out.println(String.format("%1$3s/%2$3s: %3$s", num, num, max));
	}
}
