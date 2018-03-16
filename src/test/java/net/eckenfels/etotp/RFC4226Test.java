/*
 * RFC4226Test.java - et-otp: GPL Java TOTP soft token by Bernd Eckenfels.
 */
package net.eckenfels.etotp;

import static org.junit.Assert.assertEquals;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;


/**
 * JUnit tests for HOTP/TOTP test vectors.
 *
 * @author Bernd Eckenfels
 */
public class RFC4226Test
{
    @Test
    public void testRFC4226() throws InvalidKeyException,
            NoSuchAlgorithmException {
        // Test Vector 0,1 and 9 from RFC 4226
        byte[] secretBytes = "12345678901234567890".getBytes();
        String s = RFC4226.generateOTP(secretBytes, 0, 6, false, -1);
        assertEquals("755224", s);

        s = RFC4226.generateOTP(secretBytes, 1, 6, false, -1);
        assertEquals("287082", s);

        s = RFC4226.generateOTP(secretBytes, 9, 6, false, -1);
        assertEquals("520489", s);

        // RFC 6238 Test Vector 1
        s = RFC4226.generateOTP(secretBytes, 1, 8, false, -1);
        assertEquals("94287082", s);
    }
}
