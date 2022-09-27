package net.nenko.libs;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NanoVLQTest {

	@Test
	public void test_min() {
		NanoVLQ vlq = new NanoVLQ(0);
		byte[] bytes = vlq.getBytes();
		assertTrue(bytes.length == 1);
		assertTrue(bytes[0] == 0);
	}

	@Test
	public void test_max() {
		NanoVLQ vlq = new NanoVLQ(NanoVLQ.MAX_NANOVLQ);
		byte[] bytes = vlq.getBytes();
		assertTrue(bytes.length == 8);
		long value2 = NanoVLQ.value(bytes);
		assertTrue(value2 == NanoVLQ.MAX_NANOVLQ);
	}

	@Test
	public void testRest() {
		System.out.println("" + NanoVLQ.MIN_NANOVLQ + " - " + NanoVLQ.MAX_NANOVLQ);
		for(long value = NanoVLQ.MIN_NANOVLQ; value <= NanoVLQ.MAX_NANOVLQ; value += 10000000099L) {
			NanoVLQ vlq = new NanoVLQ(value);
			byte[] bytes = vlq.getBytes();
			System.out.println("" + value + ", " + NanoVLQ.bytesArrayToHexString(bytes));
			long value2 = NanoVLQ.value(bytes);
			assertTrue(value2 == value);
		}
	}

	@Test
	public void test0Reversed() {
		NanoVLQ vlq = new NanoVLQ(0);
		byte[] bytes = vlq.getReverseBytes();
		assertTrue(bytes.length == 1);
		assertTrue(bytes[0] == 0);
	}

	@Test
	public void test_reversed_max() {
		NanoVLQ vlq = new NanoVLQ(NanoVLQ.MAX_NANOVLQ);
		byte[] bytes = vlq.getReverseBytes();
		assertTrue(bytes.length == 8);
		long value2 = NanoVLQ.valueFromReverse(bytes);
		assertTrue(value2 == NanoVLQ.MAX_NANOVLQ);
	}

	@Test
	public void testRestReversed() {
		System.out.println("" + NanoVLQ.MIN_NANOVLQ + " - " + NanoVLQ.MAX_NANOVLQ);
		for(long value = NanoVLQ.MIN_NANOVLQ; value <= NanoVLQ.MAX_NANOVLQ; value += 10000000099L) {
			NanoVLQ vlq = new NanoVLQ(value);
			byte[] bytes = vlq.getReverseBytes();
			System.out.println("" + value + ", Reversed: " + NanoVLQ.bytesArrayToHexString(bytes));
			long value2 = NanoVLQ.valueFromReverse(bytes);
			assertTrue(value2 == value);
		}
	}

	
}
