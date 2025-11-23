package net.nenko.lib;

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

	public static byte[] longToBytes(long val) {
		return new NanoVLQ(val).getBytes();
	}

	public static long value(byte[] bytes) {
		return value(bytes, bytes.length);
	}

	/**
	 * Recovers VLQ from the sequence of bytes
	 *
	 * NOTE: this method uses several bytes from input array to reconstruct VLQ,
	 * 	but the caller can't figure out, how many exactly was the length of VLQ.
	 * 	If this is a problem, use value() method with producer
	 *
	 * @param bytes input sequence of bytes, that contains VLQ and may be some trailing bytes
	 * @param len maximum known length of data in 'bytes' array
	 * @return recovered VLQ
	 */
	public static long value(byte[] bytes, int len) {
		long value = 0L;
		for(int i = 0;; i++) {
			value = value * 128 + (bytes[i] & 0x7F);
			if((bytes[i] & 0x00000080) == 0) {	// check for terminating byte
				break;
			}
			if(i == len - 1) {
				throw new IllegalArgumentException("NanoVLQ.value([], len) - no terminating byte found in the VLQ sequence");
			}
			if(i == 7) {
				throw new IllegalArgumentException("NanoVLQ.value([], len) - too long sequence of bytes");
			}
		}
		return value;
	}

	/**
	 * Recovers VLQ from the sequence of bytes
	 *
	 * Bytes are produced by a caller in ByteProducer, and in produceByte() the caller can control number of
	 * used bytes, etc. Check unit tests for the example.
	 *
	 * @param byteProducer the source of input bytes to reconstruct VLQ
	 * @return VLQ reconstructed from byte sequence
	 */
	public static long value(ByteProducer byteProducer) throws Exception {
		long value = 0L;
		for(int i = 0;; i++) {
			byte b = byteProducer.produceByte();
			value = value * 128 + (b & 0x7F);
			if((b & 0x00000080) == 0) {	// check for terminating byte
				break;
			}
			if(i == 7) {
				throw new IllegalArgumentException("NanoVLQ.value(producer) - too long sequence of bytes");
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
				throw new IllegalArgumentException("NanoVLQ.valueFromReverse([]) - no terminating byte found in the VLQ sequence");
			}
			if(i < bytes.length - 7) {
				throw new IllegalArgumentException("NanoVLQ.valueFromReverse([]) - too long sequence of bytes");
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

	@FunctionalInterface
	public interface ByteProducer {
		byte produceByte() throws Exception;
	}

	public static class ByteProducerFromByteArray implements ByteProducer {
		public int index = 0;
		private final byte[] data;
		public ByteProducerFromByteArray(byte[] data) {
			this.data = data;
		}
		public byte produceByte() {
			return data[index++];
		}
	}

}
