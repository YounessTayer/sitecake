package com.sitecake.commons.client.util;

import java.math.BigInteger;

public class RSA {
	
	public static final BigInteger PUBLIC_EXPONENT = new BigInteger("65537");
	
	public static BigInteger encode(BigInteger exponent, BigInteger moduli, BigInteger message) {
		return message.modPow(exponent, moduli);
	}
	
	public static String encode(String moduli, String message) {
		return toString((encode(PUBLIC_EXPONENT, fromString(moduli), fromString(message))));
	}

	public static String encode(BigInteger moduli, String message) {
		return toString((encode(PUBLIC_EXPONENT, moduli, fromString(message))));
	}

	public static String encode(String exponent, String moduli, String message) {
		return toString((encode(fromString(exponent), fromString(moduli), fromString(message))));
	}
	
	public static BigInteger fromString(String value) {
		byte[] bytes = toByteArray(value);
		BigInteger number = new BigInteger(bytes);
		return number;
	}

	public static BigInteger fromHexString(String value) {
		return new BigInteger(value, 16);
	}
	
	public static String toString(BigInteger value) {
		byte[] bytes = value.toByteArray();
		String str = fromByteArray(bytes);
		return str;
	}
	
	private static byte[] toByteArray(String value) {
		char[] chars = value.toCharArray();
		byte[] bytes = new byte[chars.length];
		
		for ( int i=0; i<chars.length; i++ ) {
			bytes[i] = (byte)chars[i];
		}
		return bytes;
	}

	private static String fromByteArray(byte[] value) {
		byte[] bytes = value;
		char[] chars = new char[bytes.length];
		
		for ( int i=0; i<bytes.length; i++ ) {
			chars[i] = (char)bytes[i];
		}
		
		return String.valueOf(chars);
	}
	
}
