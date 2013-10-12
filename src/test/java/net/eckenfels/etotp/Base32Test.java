/*
 * Base32Test.java - et-otp: GPL Java TOTP soft token by Bernd Eckenfels.
 */
package net.eckenfels.etotp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.eckenfels.etotp.Base32.DecodingException;

import org.junit.Ignore;
import org.junit.Test;

public class Base32Test
{
    private static final String ALL_ONE_BYTES  = "AAAEAIAMAQAUAYA4BABEBIBMBQBUBYB4CACECICMCQCUCYC4DADEDIDMDQDUDYD4EAEEEIEMEQEUEYE4FAFEFIFMFQFUFYF4GAGEGIGMGQGUGYG4HAHEHIHMHQHUHYH4IAIEIIIMIQIUIYI4JAJEJIJMJQJUJYJ4KAKEKIKMKQKUKYK4LALELILMLQLULYL4MAMEMIMMMQMUMYM4NANENINMNQNUNYN4OAOEOIOMOQOUOYO4PAPEPIPMPQPUPYP4QAQEQIQMQQQUQYQ4RARERIRMRQRURYR4SASESISMSQSUSYS4TATETITMTQTUTYT4UAUEUIUMUQUUUYU4VAVEVIVMVQVUVYV4WAWEWIWMWQWUWYW4XAXEXIXMXQXUXYX4YAYEYIYMYQYUYYY4ZAZEZIZMZQZUZYZ42A2E2I2M2Q2U2Y243A3E3I3M3Q3U3Y344A4E4I4M4Q4U4Y445A5E5I5M5Q5U5Y546A6E6I6M6Q6U6Y647A7E7I7M7Q7U7Y74";
    private static final String SOME_TWO_BYTES = "AAAAAEAQAIBAAMBQAQCAAUCQAYDAA4DQBAEABEEQBIFABMFQBQGABUGQBYHAB4HQCAIACEIQCIJACMJQCQKACUKQCYLAC4LQDAMADEMQDINADMNQDQOADUOQDYPAD4PQEAQAEEQQEIRAEMRQEQSAEUSQEYTAE4TQFAUAFEUQFIVAFMVQFQWAFUWQFYXAF4XQGAYAGEYQGIZAGMZQGQ2AGU2QGY3AG43QHA4AHE4QHI5AHM5QHQ6AHU6QHY7AH47QIBAAIFAQIJBAINBQIRCAIVCQIZDAI5DQJBEAJFEQJJFAJNFQJRGAJVGQJZHAJ5HQKBIAKFIQKJJAKNJQKRKAKVKQKZLAK5LQLBMALFMQLJNALNNQLROALVOQLZPAL5PQMBQAMFQQMJRAMNRQMRSAMVSQMZTAM5TQNBUANFUQNJVANNVQNRWANVWQNZXAN5XQOBYAOFYQOJZAONZQOR2AOV2QOZ3AO53QPB4APF4QPJ5APN5QPR6APV6QPZ7AP57QQCAAQGAQQKBAQOBQQSCAQWCQQ2DAQ6DQRCEARGEQRKFAROFQRSGARWGQR2HAR6HQSCIASGIQSKJASOJQSSKASWKQS2LAS6LQTCMATGMQTKNATONQTSOATWOQT2PAT6PQUCQAUGQQUKRAUORQUSSAUWSQU2TAU6TQVCUAVGUQVKVAVOVQVSWAVWWQV2XAV6XQWCYAWGYQWKZAWOZQWS2AWW2QW23AW63QXC4AXG4QXK5AXO5QXS6AXW6QX27AX67QYDAAYHAQYLBAYPBQYTCAYXCQY3DAY7DQZDEAZHEQZLFAZPFQZTGAZXGQZ3HAZ7HQ2DIA2HIQ2LJA2PJQ2TKA2XKQ23LA27LQ3DMA3HMQ3LNA3PNQ3TOA3XOQ33PA37PQ4DQA4HQQ4LRA4PRQ4TSA4XSQ43TA47TQ5DUA5HUQ5LVA5PVQ5TWA5XWQ53XA57XQ6DYA6HYQ6LZA6PZQ6T2A6X2Q633A673Q7D4A7H4Q7L5A7P5Q7T6A7X6Q737A777Q";

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

        // same base32 string with lowercase and 8 instead of B
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
        assertEquals("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ", s);
    }

    @Test
    public void testEncodeOnyByte()
    {
        byte[] oneByte = new byte[1];
        StringBuffer b = new StringBuffer(128);
        for(int i=0;i<256;i++)
        {
            oneByte[0] = (byte)i;
            b.append(Base32.encode(oneByte));
        }
        assertEquals(ALL_ONE_BYTES, b.toString());
    }
    
    @Test
    public void testEncodeTwoBytes()
    {
        byte[] twoByte = new byte[2];
        StringBuffer b = new StringBuffer(128);
        for(int i=0;i<256;i++)
        {
            twoByte[0] = (byte)i;
            twoByte[1] = (byte)i;
            b.append(Base32.encode(twoByte));
        }
        assertEquals(SOME_TWO_BYTES, b.toString());
    }

    @Test
    public void testEncodeNothing()
    {
        String s = Base32.encode(new byte[0]);
        assertEquals("", s);
    }

    @Test(expected=IllegalArgumentException.class)
    @Ignore // this will most likely produce an OutOfHeap if run
    public void testEncodeTooLong()
    {
        Base32.encode(new byte[1 << 28]);
    }

    @Test
    public void testDecodeEmpty() throws DecodingException
    {
        byte[] result = Base32.decode("");
        assertTrue(result.length == 0);
    }

    @Test(expected=DecodingException.class)
    public void testDecodeBroken() throws DecodingException
    {
        Base32.decode("_");
    }
}
