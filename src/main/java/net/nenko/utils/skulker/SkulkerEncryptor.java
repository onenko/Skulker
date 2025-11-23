package net.nenko.utils.skulker;

import java.util.Arrays;

/**
 * SkulkerEncryptor - transforms sequence of bytes (encrypts and decrypts)
 *
 * NOTE1: this class serves as interface definition and as default hollow implementation
 * NOTE2: the sizes of input and output arrays always equal - the opposite is not supported by skulker format
 * NOTE3: the conversion of data during method invocation MAY depend on previous invocations
 * NOTE4: this API limits the granularity of handled data to bytes
 */
public class SkulkerEncryptor {

    public byte[] encrypt(byte[] in) {
        return encrypt(in, in.length);
    }

    public byte[] encrypt(byte[] in, int len) {
        return Arrays.copyOf(in, len);
    }

    public byte[] decrypt(byte[] in) {
        return decrypt(in, in.length);
    }

    public byte[] decrypt(byte[] in, int len) {
        return Arrays.copyOf(in, len);
    }

}
