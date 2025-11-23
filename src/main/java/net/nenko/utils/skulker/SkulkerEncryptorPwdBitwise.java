package net.nenko.utils.skulker;

import java.nio.charset.StandardCharsets;

/**
 * SkulkerEncryptorPwdBitwise - encrypts and decrypts bytes sequences using password
 *
 * NOTE1: if password is empty or missing, encryptor uses simple algorithm to apply previous byte state to current byte
 * NOTE2: if password is not empty, it is converted in sequence of bytes, and used in loop to encrypt bytes of data
 * NOTE3: the conversion state is initialized in constructor, and the object is used only to encrypt/decrypt single
 *  sequence of bytes
 */
public class SkulkerEncryptorPwdBitwise extends SkulkerEncryptor {
    private final byte[] pwd;
    private int pwdIx = 0;      // index of byte to use in password
    private byte worker = 0;

    public SkulkerEncryptorPwdBitwise(String pwd) {
        if(pwd != null && ! pwd.isEmpty()) {
            this.pwd = pwd.getBytes(StandardCharsets.UTF_8);
            this.worker = (byte) pwd.length();
        } else {
            this.pwd = new byte[1000];      // zeroes are used instead of missing password
        }
    }

    /**
     * do not check for null or empty argument, do not spend time to detect silly data
     */
    @Override
    public byte[] encrypt(byte[] in, int len) {
        byte[] out = new byte[len];
        for(int i = 0; i < len; i++) {
            int temp = worker ^ pwd[pwdIx++];
            out[i] = worker = (byte) (temp ^ in[i]);
            if (pwdIx >= pwd.length) {
                pwdIx = 0;
            }
        }
        return out;
    }

    /**
     * do not check for null or empty argument, do not spend time to detect silly data
     */
    @Override
    public byte[] decrypt(byte[] in, int len) {
        byte[] out = new byte[len];
        for(int i = 0; i < len; i++) {
            int temp = worker ^ pwd[pwdIx++];
            out[i] = (byte) (temp ^ in[i]);
            worker = in[i];
            if (pwdIx >= pwd.length) {
                pwdIx = 0;
            }
        }
        return out;
    }

}
