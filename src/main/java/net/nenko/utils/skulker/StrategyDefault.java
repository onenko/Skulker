package net.nenko.utils.skulker;

import net.nenko.libs.BytesHelper;
import net.nenko.libs.NanoVLQ;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StrategyDefault extends Strategy {

	@Override
	protected byte[] getSkulkedPathBytes(String filePath) {
		Charset charset = StandardCharsets.UTF_8;
		byte[] filePathBytes = filePath.getBytes(charset);

		NanoVLQ vlq = new NanoVLQ(filePathBytes.length);
		byte[] lenPrefix = vlq.getBytes();

		return BytesHelper.concatenate(lenPrefix, filePathBytes);
	}

	/**
	 * does the encryption, based on the password
	 * @param source
	 * @param length number of bytes to encrypt
	 * @return encrypted sequence of bytes, only first "length" bytes are encrypted, this may be the copy of input array
	 */
	@Override
	protected byte[] encrBytes(byte[] source, int length, String password) {
		return source;
	}

















	@Override
	protected String decrFilePath(byte[] filePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object decrExtension(byte[] extensionData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OutputStream decrContent(InputStream encryptedContent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InputStream decrFull(InputStream fullStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] encrExtension(Object extensionData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InputStream encrContent(String filePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InputStream encrFull(InputStream fullStream) {
		// TODO Auto-generated method stub
		return null;
	}





}
