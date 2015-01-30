package uk.org.harwellcroquet.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static String getHash(String input) {

		try {
			// MD5 is the only one supported by GWT
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmonpqrstuvwxyz0123456789()";
			for (byte b : bytes) {
				int off = b & 63;
				sb.append(alphabet.charAt(off));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

	}

}
