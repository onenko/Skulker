package net.nenko.libs;

public final class BytesHelper {

    public static byte[] concatenate(byte[] a1, byte[] a2) {
        byte[] a1a2 = new byte[a1.length + a2.length];
        System.arraycopy(a1, 0, a1a2, 0, a1.length);
        System.arraycopy(a2, 0, a1a2, a1.length, a2.length);
        return a1a2;
    }

}
