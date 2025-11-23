package net.nenko.libs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.nenko.lib.NanoVLQ;
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
	public void testOnRange() {
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
	public void testReversedOnRange() {
		System.out.println("" + NanoVLQ.MIN_NANOVLQ + " - " + NanoVLQ.MAX_NANOVLQ);
		for(long value = NanoVLQ.MIN_NANOVLQ; value <= NanoVLQ.MAX_NANOVLQ; value += 10000000099L) {
			NanoVLQ vlq = new NanoVLQ(value);
			byte[] bytes = vlq.getReverseBytes();
			System.out.println("" + value + ", Reversed: " + NanoVLQ.bytesArrayToHexString(bytes));
			long value2 = NanoVLQ.valueFromReverse(bytes);
			assertTrue(value2 == value);
		}
	}

	@Test
	public void test0WithProducer() throws Exception {
		NanoVLQ vlq = new NanoVLQ(0);
		byte[] bytes = vlq.getReverseBytes();
		assertTrue(bytes.length == 1);
		assertTrue(bytes[0] == 0);
		NanoVLQ.ByteProducer producer = new NanoVLQ.ByteProducerFromByteArray(bytes);
		long value2 = NanoVLQ.value(producer);
		assertEquals(0, value2);
	}

	@Test
	public void testMaxWithProducer() throws Exception {
		NanoVLQ vlq = new NanoVLQ(NanoVLQ.MAX_NANOVLQ);
		byte[] bytes = vlq.getBytes();
		NanoVLQ.ByteProducer producer = new NanoVLQ.ByteProducerFromByteArray(bytes);
		long value2 = NanoVLQ.value(producer);
		assertEquals(NanoVLQ.MAX_NANOVLQ, value2);
	}

	@Test
	public void testRestWithProducer() throws Exception {
		for(long value = NanoVLQ.MIN_NANOVLQ; value <= NanoVLQ.MAX_NANOVLQ; value += 10000000099L) {
			NanoVLQ vlq = new NanoVLQ(value);
			byte[] bytes = vlq.getBytes();
			System.out.println("" + value + ", " + NanoVLQ.bytesArrayToHexString(bytes));
			NanoVLQ.ByteProducer producer = new NanoVLQ.ByteProducerFromByteArray(bytes);
			long value2 = NanoVLQ.value(producer);
			assertEquals(value, value2);
		}
	}

}
