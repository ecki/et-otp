/*
 * Main.java - et-otp: GPL Java TOTP soft token by Bernd Eckenfels.
 */
package net.eckenfels.etotp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import net.eckenfels.etotp.Base32.DecodingException;


/**
 * Demonstration for TOTP/HOTP authenticator tokens (MFA).
 * 
 * @author Bernd Eckenfels
 * @version $Revision: $
 */
public class Main
{
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws DecodingException 
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, DecodingException, InvalidKeyException, UnsupportedEncodingException
	{
		rfc4226tests();
	}
	
	static void rfc4226tests() throws InvalidKeyException, NoSuchAlgorithmException, DecodingException 
	{
		// Test Vector 0,1 and 9 from RFC 4226
		byte[] secretBytes = "12345678901234567890".getBytes();
		String s = RFC4226.generateOTP(secretBytes, 0, 6, false, -1);
		assert "755224".equals(s);

		s = RFC4226.generateOTP(secretBytes, 1, 6, false, -1);
		assert "287082".equals(s);

		s = RFC4226.generateOTP(secretBytes, 9, 6, false, -1);
		assert "520489".equals(s);
		
		// encode the secret "12345678901234567890" (0x323334...) into base32 and back
		s = Base32.encode(secretBytes);
		assert "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ".equals(s);

		secretBytes = Base32.decode("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ");
		assert secretBytes[0] == '1';
		assert secretBytes[19] == '0';

		// same base32 string with lowercase and 8 instead of b
		secretBytes = Base32.decode("GEZDGNBVGY3TQOJQGEZDGn8VGY3TQOJQ");
		assert secretBytes[0] == '1';
		assert secretBytes[19] == '0';

		System.out.println("Passed test vectors for Base32 and RFC4226");
		

	}

}



