package net.nenko.libs;

import java.nio.charset.StandardCharsets;

public class BitwiseByteEncoder {
    private static final NanoLog log = new NanoLog(NanoLog.LogLevel.DEBUG, null);
    private byte[] password;
    private long byteEncryptCounter = 0L;
    private long byteDecryptCounter = 0L;

    public BitwiseByteEncoder(String password) {
        this.password = password.getBytes(StandardCharsets.UTF_8);
        log.info("BitwiseByteEncoder({}) created. Password: {}", password, bytesToString(this.password));
    }

    public byte encrypt(byte b) {
        int indexOfPasswordByte = (int)(byteEncryptCounter++ % password.length);
        b ^= password[indexOfPasswordByte];
        return b;
    }

    public byte decrypt(byte b) {
        int indexOfPasswordByte = (int)(byteDecryptCounter++ % password.length);
        b ^= password[indexOfPasswordByte];
        return b;
    }

    public void encrypt(byte[] ba) {
        for(int i = 0; i < ba.length; i++) {
            int indexOfPasswordByte = (int) (byteEncryptCounter++ % password.length);
            ba[i] ^= password[indexOfPasswordByte];
        }
    }

    public void decrypt(byte[] ba) {
        for(int i = 0; i < ba.length; i++) {
            int indexOfPasswordByte = (int) (byteDecryptCounter++ % password.length);
            ba[i] ^= password[indexOfPasswordByte];
        }
    }

    public static String bytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 4);
        sb.append("bytearray:[");
        for(int i = 0; i < bytes.length; i++) {
            sb.append(bytes[i]).append(i == bytes.length - 1 ? ']' : ',');
        }
        return sb.toString();
    }

}
