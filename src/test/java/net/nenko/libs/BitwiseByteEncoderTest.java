package net.nenko.libs;

import net.nenko.lib.BitwiseByteEncoder;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BitwiseByteEncoderTest {

    private static final String String1 = "1234567890ABCDabcd_А також українською мовою";

    @Test
    public void byteEnDecryptTest() {
        BitwiseByteEncoder bwbe = new BitwiseByteEncoder("ABC");
        byte[] bytes = {1, 11, 111, 127, -128, -111, -11, -1, 0 };
        for(byte b: bytes) {
            byte encrypted = bwbe.encrypt(b);
            assertEquals(b, bwbe.decrypt(encrypted));
        }
    }

    @Test
    public void byteArrayEnDecryptTest() {
        BitwiseByteEncoder bwbe = new BitwiseByteEncoder("ABC");
        byte[] bytes = {1, 11, 111, 127, -128, -111, -11, -1, 0 };
        byte[] toBeEncrypted = Arrays.copyOf(bytes, bytes.length);
        bwbe.encrypt(toBeEncrypted);
        bwbe.decrypt(toBeEncrypted);
        assertArrayEquals(bytes, toBeEncrypted);
    }

    @Test
    public void localizedStringEnDecryptTest() {
        BitwiseByteEncoder bwbe = new BitwiseByteEncoder("локальний пароль");
        byte[] bytes = String1.getBytes(StandardCharsets.UTF_8);
        byte[] toBeEncrypted = Arrays.copyOf(bytes, bytes.length);
        bwbe.encrypt(toBeEncrypted);
        bwbe.decrypt(toBeEncrypted);
        assertEquals(String1, new String(toBeEncrypted, StandardCharsets.UTF_8));
    }

}