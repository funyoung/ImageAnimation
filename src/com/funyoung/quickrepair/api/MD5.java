package com.funyoung.quickrepair.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final String ALGORITHM_MD5 = "MD5";

	public static String toMd5Upper(byte[] bytes) {
		return toMd5(bytes).toUpperCase();
	} 

	public static String toMd5(byte[] bytes) {
		try {
            MessageDigest algorithm = getAlgorithm(bytes);
			return toHexString(algorithm.digest(), "");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	} 
	private static String toHexString(byte[] bytes, String separator) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			if (Integer.toHexString(0xFF & b).length() == 1)  
				hexString.append("0").append(Integer.toHexString(0xFF & b));  
	            else  
	            	hexString.append(Integer.toHexString(0xFF & b));  
		}
		return hexString.toString();
	}

    private static MessageDigest getAlgorithm(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest algorithm = MessageDigest.getInstance(ALGORITHM_MD5);
        algorithm.reset();
        algorithm.update(bytes);
        return algorithm;
    }

	public static String md5Base64(byte[] bytes) {
		try {
            MessageDigest algorithm = getAlgorithm(bytes);
			return new String(Base64.encode(algorithm.digest(), Base64.NO_WRAP));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
