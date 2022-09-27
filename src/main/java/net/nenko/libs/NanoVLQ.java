package net.nenko.libs;

/**
 * Variable Length Quantity datatype implementation
 *
 * @author alex.nenko@gmail.com
 *
 */
public class NanoVLQ {

	private long value;
	public static long MIN_NANOVLQ = 0L;
	public static long MAX_NANOVLQ = (1L << 56) - 1;

	public NanoVLQ(long value) {
		if(value < MIN_NANOVLQ || value > MAX_NANOVLQ) {
			throw new IllegalArgumentException("NanoVLQ(" + value + "): arg is out of supported range of numbers");
		}
		this.value = value;
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[8];
		int length = calculateByte(bytes, 0, value);
		byte[] result = new byte[length];
		for(int i = 0; i < length; i++) {
			result[i] = bytes[length - i - 1];
			if(i < length - 1) {
				result[i] |= 0x80;			// this is NOT terminating byte
			}
		}
		return result;
	}

	public byte[] getReverseBytes() {
		byte[] bytes = new byte[8];
		int length = calculateByte(bytes, 0, value);
		byte[] result = new byte[length];
		for(int i = 0; i < length; i++) {
			result[i] = bytes[i];
			if(i > 0) {
				result[i] |= 0x80;			// this is NOT terminating byte
			}
		}
		return result;
	}

	public static long value(byte[] bytes) {
		long value = 0L;
		for(int i = 0;; i++) {
			value = value * 128 + (bytes[i] & 0x7F);
			if((bytes[i] & 0x00000080) == 0) {	// check for terminating byte
				break;
			}
			if(i == bytes.length - 1) {
				throw new IllegalArgumentException("NanoVLQ.value() - no terminating byte found in the VLQ sequence");
			}
			if(i == 7) {
				throw new IllegalArgumentException("NanoVLQ.value() - too long sequence of bytes");
			}
		}
		return value;
	}

	public static long valueFromReverse(byte[] bytes) {
		long value = 0L;
		for(int i = bytes.length - 1;; --i) {
			value = value * 128 + (bytes[i] & 0x7F);
			if((bytes[i] & 0x00000080) == 0) {	// check for terminating byte
				break;
			}
			if(i == 0) {
				throw new IllegalArgumentException("NanoVLQ.valueFromReverse() - no terminating byte found in the VLQ sequence");
			}
			if(i < bytes.length - 7) {
				throw new IllegalArgumentException("NanoVLQ.valueFromReverse() - too long sequence of bytes");
			}
		}
		return value;
	}

	public static String bytesArrayToHexString(byte[] bytes) {
		int len = bytes.length;
		StringBuilder sb = new StringBuilder(7 + 4 * len);
		sb.append("byte[");
		for(int i = 0; i < len; i++) {
			int byteValue = 0x000000FF & bytes[i];
			sb.append("0x").append(Integer.toHexString(byteValue));
			if(i < len - 1) {
				sb.append(',');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @return number of used bytes in the input array, actually the length of output bytes sequence
	 */
	private int calculateByte(byte[] bytes, int index, long rest) {
		if(index >= bytes.length) {
			throw new IllegalArgumentException("This should not happen:" + index + ", " + rest);
		}
		bytes[index] = (byte)(rest % 128);
		rest = rest / 128;
		if(rest == 0) {
			return index + 1;
		} else {
			return calculateByte(bytes, index + 1, rest);
		}
	}

}
