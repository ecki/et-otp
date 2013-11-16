/*
 * Base32.java - et-otp: GPL Java TOTP soft token by Bernd Eckenfels.
 */
package net.eckenfels.etotp;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Encodes arbitrary byte arrays as case-insensitive BASE32 strings.
 * <P>
 * This is based on the Google code from Steve Weis and Neal Gafter
 * http://code.google.com/p/google-authenticator/source/browse/mobile/android/src/com/google/android/apps/authenticator/Base32String.java
 */
public class Base32
{
    private static final Base32 INSTANCE = new Base32();

    private final char[] DIGITS;
    private final int MASK;
    private final int SHIFT;
    private final Map<Character, Integer> CHAR_MAP;

    protected Base32()
    {
        DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray(); // RFC 4668/3548
        MASK = DIGITS.length - 1;
        SHIFT = Integer.numberOfTrailingZeros(DIGITS.length);
        CHAR_MAP = new HashMap<Character, Integer>(DIGITS.length);
        for (int i = 0; i < DIGITS.length; i++)
        {
            CHAR_MAP.put(DIGITS[i], i);
        }
    }

    public static Base32 getInstance()
    {
        return INSTANCE;
    }

    public static byte[] decode(String encoded)
        throws DecodingException
    {
        return getInstance().decodeInternal(encoded);
    }

    protected byte[] decodeInternal(String encodedArg)
        throws DecodingException
    {
        // Remove all '-', ' ', '.' (seperators, trimming). Might throw NPE if encodedArg is null
        String encoded = encodedArg.replaceAll("-? ?\\.?", "");
        // Canonicalize to all upper case
        encoded = encoded.toUpperCase(Locale.ENGLISH);
        encoded = encoded.replaceAll("8", "B").replaceAll("1", "I");

        if (encoded.length() == 0) {
            return new byte[0];
        }

        int encodedLength = encoded.length();
        int outLength = encodedLength * SHIFT / 8;
        byte[] result = new byte[outLength];
        int buffer = 0;
        int next = 0;
        int bitsLeft = 0;
        for (char c : encoded.toCharArray())
        {
            Integer i = CHAR_MAP.get(c);
            if (i==null) {
                throw new DecodingException("Illegal character: " + c);
            }
            buffer <<= SHIFT;
            buffer |= i.intValue() & MASK;
            bitsLeft += SHIFT;
            if (bitsLeft >= 8) {
                result[next++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        // We'll ignore leftover bits for now.
        // if (next != outLength || bitsLeft >= SHIFT) {
        //  throw new DecodingException("Bits left: " + bitsLeft);}
        return result;
    }

    public static String encode(byte[] data)
    {
        return getInstance().encodeInternal(data);
    }

    protected String encodeInternal(byte[] data)
    {
        if (data.length == 0) {
            return "";
        }

        // SHIFT is the number of bits per output character, so the length of the
        // output is the length of the input multiplied by 8/SHIFT, rounded up.
        if (data.length >= (1 << 28)) {
            // The computation below will fail, so don't do it.
            throw new IllegalArgumentException();
        }

        int outputLength = (data.length * 8 + SHIFT - 1) / SHIFT;
        StringBuilder result = new StringBuilder(outputLength);

        int buffer = data[0];
        int next = 1;
        int bitsLeft = 8;
        while (bitsLeft > 0 || next < data.length) {
            if (bitsLeft < SHIFT) {
                if (next < data.length) {
                    buffer <<= 8;
                    buffer |= (data[next++] & 0xff);
                    bitsLeft += 8;
                } else {
                    int pad = SHIFT - bitsLeft;
                    buffer <<= pad;
                    bitsLeft += pad;
                }
            }
            int index = MASK & (buffer >> (bitsLeft - SHIFT));
            bitsLeft -= SHIFT;
            result.append(DIGITS[index]);
        }
        return result.toString();
    }

    static class DecodingException extends Exception
    {
        /** field <code>serialVersionUID</code> */
        private static final long serialVersionUID = 20111119L;

        public DecodingException(String message)
        {
            super(message);
        }
    }
}

