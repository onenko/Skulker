package net.nenko.utils.skulker;

import net.nenko.lib.NanoVLQ;

import java.io.*;

public abstract class Strategy {

	protected int READ_CHUNK_BYTES = 1000000;
	protected byte[] CHUNK = new byte[READ_CHUNK_BYTES];

	protected abstract String decrFilePath(byte[] filePath);
	protected abstract Object decrExtension(byte[] extensionData);
	protected abstract OutputStream decrContent(InputStream encryptedContent);
	protected abstract InputStream decrFull(InputStream fullStream);

	public InputStream decrMultiplex(byte[] part1, byte[] part2, InputStream part3, byte[] par44) {
		return null;
	}

	public InputStream readData(String carrierFilePath) {
		return null;
	}


	protected abstract byte[] encrExtension(Object extensionData);
	protected abstract InputStream encrContent(String filePath);
	protected abstract InputStream encrFull(InputStream fullStream);

	public InputStream encrMultiplex(byte[] part1, byte[] part2, InputStream part3, byte[] par44) {
		return null;
	}

	public void writeData(String carrierFilePath, InputStream encrFullStream) {
	}

	protected abstract byte[] encrBytes(byte[] part1, int length, String password);

	protected abstract byte[] getSkulkedPathBytes(String filePath);

	public void encrypt(String skulkedFile, String carrierFile) {

//		ByteSink byteSink = MoreFiles.asByteSink(outputFile.toPath(),
//				StandardOpenOption.CREATE,
//				StandardOpenOption.WRITE);
//		byteSink.write(dataForWriting);

//		String outputPath = cntx.carrierPath + ".JPG";
//		File output = new File(outputPath);
//		File carrier = new File(cntx.carrierPath);
//		File skulked = new File(cntx.skulkedPath);

//		File outputFile = new File(cntx.carrierPath);
////		File skulked = new File(cntx.skulkedPath);;
//		ByteSink byteSink = Files.asByteSink(outputFile);
//		byteSink.write(dataForWriting);
		FileOutputStream out = null;
		FileInputStream in = null;
		try {
			long carrierLength = new File(carrierFile).length();
			out = new FileOutputStream(carrierFile, true);		// to append = true
			out.write(getSkulkedPathBytes(skulkedFile));

			in = new FileInputStream(skulkedFile);
			for(;;) {
				int readCount =	in.read(CHUNK);
				if(readCount < 0) {
					break;
				}
				byte[] encryptedChunk = encrBytes(CHUNK, readCount, null);
				out.write(encryptedChunk, 0, readCount);
				if(readCount < READ_CHUNK_BYTES) {
					break;
				}
			}
			byte[] carrierLengthVLQ = new NanoVLQ(carrierLength).getReverseBytes();
			out.write(encrBytes(carrierLengthVLQ, carrierLengthVLQ.length, null));
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			this.close(in);
			this.close(out);
		}
	}

	private void close(Closeable stream) {
		if(stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
