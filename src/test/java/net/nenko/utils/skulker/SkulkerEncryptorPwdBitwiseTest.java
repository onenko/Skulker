package net.nenko.utils.skulker;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class SkulkerEncryptorPwdBitwiseTest {

    private final static byte[] TEST1IN     = { 0x00, 0x01, 0x40};
    private final static byte[] TEST1OUT1   = { 0x00, 0x01, 0x41};
    private final static byte[] TEST1OUT2   = { 88, 56, 11};
    private final static byte[] TEST1OUT3   = { -61, 95, -49};
    private final static String PASSWORD    = "Password";
    private final static String PASSWORD2   = "Не-АСКІ-шний-Пароль";

    @Test
    public void encryptWithMissingPassword() {
        SkulkerEncryptorPwdBitwise encryptor = new SkulkerEncryptorPwdBitwise(null);
        byte[] out = encryptor.encrypt(TEST1IN);
        assertArrayEquals(TEST1OUT1, out);
        // We MUST use new encryptor !
        encryptor = new SkulkerEncryptorPwdBitwise(null);
        out = encryptor.decrypt(out);
        assertArrayEquals(TEST1IN, out);
    }

    @Test
    public void encryptWithPassword() {
        SkulkerEncryptorPwdBitwise encryptor = new SkulkerEncryptorPwdBitwise(PASSWORD);
        byte[] out = encryptor.encrypt(TEST1IN);
        assertArrayEquals(TEST1OUT2, out);
        // We MUST use new encryptor !
        encryptor = new SkulkerEncryptorPwdBitwise(PASSWORD);
        out = encryptor.decrypt(out);
        assertArrayEquals(TEST1IN, out);
    }

    @Test
    public void encryptWithNonAsciiPassword() {
        SkulkerEncryptorPwdBitwise encryptor = new SkulkerEncryptorPwdBitwise(PASSWORD2);
        byte[] out = encryptor.encrypt(TEST1IN);
        assertArrayEquals(TEST1OUT3, out);
        // We MUST use new encryptor !
        encryptor = new SkulkerEncryptorPwdBitwise(PASSWORD2);
        out = encryptor.decrypt(out);
        assertArrayEquals(TEST1IN, out);
    }

    @Test
    public void encryptDecryptWithNonAsciiStrings() {
        String toEncryptDecrypt = "Цей текст написаний українською мовою";
        byte[] in = toEncryptDecrypt.getBytes(StandardCharsets.UTF_8);
        SkulkerEncryptorPwdBitwise encryptor = new SkulkerEncryptorPwdBitwise(PASSWORD2);
        byte[] encrypted = encryptor.encrypt(in);
        encryptor = new SkulkerEncryptorPwdBitwise(PASSWORD2);
        byte[] decrypted = encryptor.decrypt(encrypted);
        assertArrayEquals(in, decrypted);
    }

}