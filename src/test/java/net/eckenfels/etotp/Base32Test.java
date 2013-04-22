/*
 * Base32Test.java - et-otp: GPL Java TOTP soft token by Bernd Eckenfels.
 */
package net.eckenfels.etotp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import net.eckenfels.etotp.Base32.DecodingException;

import org.junit.Test;

public class Base32Test
{
    @Test
    public void testGetInstance()
    {
        Base32 i1 = Base32.getInstance();
        Base32 i2 = Base32.getInstance();
        assertNotNull(i1);
        assertSame(i1,  i2);
    }

    @Test
    public void testDecode() throws DecodingException
    {
        byte[] secretBytes = Base32.decode("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ");
        assertArrayEquals("12345678901234567890".getBytes(), secretBytes);

        // same base32 string with lowercase and 8 instead of b
        secretBytes = Base32.decode("GEZDGNBVGY3TQOJQGEZDGn8VGY3TQOJQ");
        assertArrayEquals("12345678901234567890".getBytes(), secretBytes);
    }

    
    @Test
    public void testDecodeExtra() throws DecodingException
    {
        byte[] secretBytes = Base32.decode("G E - ZD  GNBVGY3TQOJQGEZDGNBVGY3TQOJQ");
        assertArrayEquals("12345678901234567890".getBytes(), secretBytes);

        // same base32 string with lowercase and 8 instead of b
        secretBytes = Base32.decode("GEZDGNBVGY3TQOJQGEZDGn8VGY3TQOJQ-");
        assertArrayEquals("12345678901234567890".getBytes(), secretBytes);
    }

    @Test
    public void testEncode()
    {
        // encode the secret "12345678901234567890" (0x323334...) into base32 and back
        byte[] secretBytes = "12345678901234567890".getBytes();
        String s = Base32.encode(secretBytes);
        assert "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ".equals(s);
    }

    @Test(expected=DecodingException.class)
    public void testDecodeBroken() throws DecodingException
    {
        Base32.decode("_");
    }
}
