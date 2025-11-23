package net.nenko.utils.skulker;

import net.nenko.lib.NanoLog;
import net.nenko.lib.NanoVLQ;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * SkulkedFile - defines the format of skulked file and its handling
 *
 * TODO: add use case when carrierFullName = skulkedFullName, and thus we avoid file copying,
 *      but this may work only if we have write permissions for carrier file
 */

public class SkulkedFile {
    private static final int VAL_FILLED_BY_INFO = 0b0000000000000000;

    private static final NanoLog log = App.log;
    private final static int BUFFER_SIZE = 1000000;
    private final static int TAIL_WITH_LEN_LEN = 10;
    private final byte[] BUFFER = new byte[BUFFER_SIZE];

    private String carrierFullName;             // existing file that will be preserved
    private long carrierLength = 0L;
    private String skulkedFullName;             // new file that will be written (carrier + source)
    private long skulkedLength = 0L;
    private String sourceFullName;              // existing file that will be skulked
    private long sourceLength = 0L;
    private byte[] extraData = new byte[0];     // normally it is zero sized array
    private InputStream toBeSkulked;            // opened for read and at the beginning
    private FileOutputStream skulkedOutputStream;
    private SkulkerEncryptor encryptor = new SkulkerEncryptor();    // default = hollow

    public SkulkedFile(String carrierFullName, String sourceFullName, String skulkedFullName) {
        this.carrierFullName = carrierFullName;
        this.sourceFullName = sourceFullName;
        this.skulkedFullName = skulkedFullName;
    }

    public SkulkedFile(String carrierFullName, String sourceFullName, String skulkedFullName, SkulkerEncryptor encr) {
        this(carrierFullName, sourceFullName, skulkedFullName);
        this.encryptor = encr;
    }

    /**
     * skulk() - builds skulked file from carrier file and source file
     */
    public void skulk() throws IOException {
        try {
            copyCarrier();                  // here skulkedOutputStream became opened
            writeString(sourceFullName);
            writeLenAndBytes(extraData);
            copySource();
            writeReverseCarrierLength();
        } finally {
            skulkedOutputStream.close();
        }
    }

    /**
     * Fabric method, that attempt to create SkulkedFile object from a file
     *
     * @param skulkedFullName file that should be deskulked
     * @param encr decryptor
     * @return SkulkedFile object with fields set from parsed file, or null if the file has wrong format (or is not skulked file)
     */
    public static SkulkedFile deskulk(String skulkedFullName, SkulkerEncryptor encr) throws FileNotFoundException, SkulkedFileException {
        SkulkedFile skulkedFile = new SkulkedFile(null, null, skulkedFullName, encr);
        // Check file length and retrieve reverse carrier length (cut off too small files)
        File skulkedFileFile = new File(skulkedFullName);
        if(!skulkedFileFile.exists()) {
            throw new FileNotFoundException(skulkedFullName + " not found.");
        }
        skulkedFile.skulkedLength = skulkedFileFile.length();
        if(skulkedFile.skulkedLength < TAIL_WITH_LEN_LEN) {
            throw new SkulkedFileException(SKULKED_ERR_CODE.TOO_SHORT_FILE, skulkedFile.skulkedLength);
        }
        // Retrieve carrier file length from the tail of skulked file
        byte[] lastBytes = new byte[TAIL_WITH_LEN_LEN];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(skulkedFileFile, "r")) {
            randomAccessFile.seek(skulkedFile.skulkedLength - TAIL_WITH_LEN_LEN);
            randomAccessFile.readFully(lastBytes);
            skulkedFile.carrierLength = NanoVLQ.valueFromReverse(lastBytes);
            // Skip carrier and retrieve source file name, this must be done with decryption
            randomAccessFile.seek(skulkedFile.carrierLength);
            skulkedFile.sourceFullName = skulkedFile.readLenAndString(randomAccessFile);
            skulkedFile.extraData = skulkedFile.readLenAndBytes(randomAccessFile);
            int tailVLQlen = new NanoVLQ(skulkedFile.carrierLength).getBytes().length;
            skulkedFile.sourceLength = skulkedFile.skulkedLength - tailVLQlen - randomAccessFile.getFilePointer();
        } catch(IOException e) {
            log.error("SkulkedFile.deskulk(" + skulkedFullName + ", ...) IOException:", e);
            return null;
        } catch(Exception e) {
            log.error("SkulkedFile.deskulk(" + skulkedFullName + ", ...) ex:", e);
            return null;
        }
        return skulkedFile;
    }

    /**
     * Having filled up SkulkedFile object, for example, by deskulk(), Xtracts original source file
     * Skulked file remains unchanged.
     *
     * @param customOutputFullName if null, the source is deskulked in its original source file name
     * @return String description of the problem or null if everything fine
     * TODO: keep exceptions or returned value
     */
    public String extract(String customOutputFullName) throws IOException {
        validate(VAL_FILLED_BY_INFO);
        int tailVLQlen = new NanoVLQ(this.carrierLength).getBytes().length;
        long off = this.skulkedLength - tailVLQlen - this.sourceLength;
        if(off < 4) {
            return "extract(): structural error: expected offset should be > 3.";
        }
        File skulkedFileFile = new File(skulkedFullName);
        if(!skulkedFileFile.exists()) {
            throw new FileNotFoundException(skulkedFullName + " not found.");
        }

        if(customOutputFullName == null) {
            customOutputFullName = this.sourceFullName;
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(skulkedFileFile, "r")) {
            randomAccessFile.seek(off);
            createDirsForSourceFile(customOutputFullName);
            try(BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(customOutputFullName))) {
                long remaining = this.sourceLength;
                while(remaining > 0L) {
                    if(remaining >= BUFFER_SIZE) {
                        randomAccessFile.readFully(BUFFER);
                        outputStream.write(encryptor.decrypt(BUFFER));
                        remaining -= BUFFER_SIZE;
                    } else {
                        randomAccessFile.readFully(BUFFER, 0, (int) remaining);
                        outputStream.write(encryptor.decrypt(BUFFER, (int) remaining));
                        remaining = 0;
                    }
                }
            }
        }
        return null;
    }

    // Applied to skulked file, and reconstruct carrier file, also invalidates this object
    public String truncate() throws IOException {
        File skulkedFileFile = new File(skulkedFullName);
        try (FileOutputStream fos = new FileOutputStream(skulkedFileFile, true);
                 FileChannel fileChannel = fos.getChannel()) {
            fileChannel.truncate(carrierLength);
            log.debug("File {} truncated to {} bytes.", skulkedFullName, carrierLength);
            // Destroy this object data, because it does not represent skulked file
            this.sourceLength = 0;
            this.sourceFullName = null;
            this.skulkedFullName = null;
        } catch (IOException e) {
            log.error("truncate(): unxpxp: ", e);
            return "truncate(): unxpxp: " + e.getMessage();
        }
        return null;
    }

    // Helper to write data bytes to the output with preceding length
    // All written bytes are encrypted
    private void writeLenAndBytes(byte[] bytes, int len) throws IOException {
        byte[] vsnPSNL = NanoVLQ.longToBytes(len);
        byte[] encodedVsnPSNL = encryptor.encrypt(vsnPSNL);
        skulkedOutputStream.write(encodedVsnPSNL);
        skulkedOutputStream.write(encryptor.encrypt(bytes, len));
    }

    private void writeLenAndBytes(byte[] bytes) throws IOException {
        writeLenAndBytes(bytes, bytes.length);
    }

    private void writeString(String str) throws IOException {
        writeLenAndBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    // Helper to read VLQ from RandomAccessFile
    // All input bytes are encrypted
    // RandomAccessFile should be positioned on the 1st byte of data (length)
    // SkulkedEncryptor also should be in the proper state to descrypt these bytes
    private long readVLQ(RandomAccessFile randomAccessFile) throws Exception {
        final SkulkerEncryptor decryptor = this.encryptor;
        byte[] bytes = new byte[1];
        NanoVLQ.ByteProducer producer = new NanoVLQ.ByteProducer() {
            @Override
            public byte produceByte() throws Exception {
                randomAccessFile.readFully(bytes);
                return decryptor.decrypt(bytes)[0];
            }
        };
        return NanoVLQ.value(producer);
    }

    // Helper to read bytes array from the file with preceding length
    // All input bytes are encrypted
    // RandomAccessFile should be positioned on the 1st byte of data (length)
    // After the completion of the method the position is shifted forward
    // SkulkedEncryptor also should be in the proper state to descrypt these bytes
    private byte[] readLenAndBytes(RandomAccessFile randomAccessFile) throws Exception {
        long len = readVLQ(randomAccessFile);
        if(len > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Bad file format");
        }
        byte[] output = new byte[(int)len];
        randomAccessFile.readFully(output);
        return encryptor.decrypt(output);
    }

    // Helper to read UTF-8 string from the file with preceding length
    // All input bytes are encrypted
    // RandomAccessFile should be positioned on the 1st byte of data (length)
    // After the completion of the method the position is shifted forward
    // SkulkedEncryptor also should be in the proper state to descrypt these bytes
    private String readLenAndString(RandomAccessFile randomAccessFile) throws Exception {
        byte[] bytes = readLenAndBytes(randomAccessFile);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * copyCarrier() - opens and reads carrier file, copy all data to output skulked file
     *
     * After the completion of the method the skulked (output) file remains open
     */
    private void copyCarrier() throws IOException {
        skulkedOutputStream = new FileOutputStream(skulkedFullName);
        try(FileInputStream fis = new FileInputStream(carrierFullName)) {
            boolean goOn = true;
            while(goOn) {
                int bytes = fis.read(BUFFER);
                if (bytes < 0) {
                    goOn = false;
                } else {
                    skulkedOutputStream.write(BUFFER, 0, bytes);
                    carrierLength += bytes;
                }
            }
        }
    }

    /**
     * copySource() - opens and reads source file, copy all data to output skulked file
     *
     * After the completion of the method the skulked (output) file remains open
     */
    private void copySource() throws IOException {
        try(FileInputStream fis = new FileInputStream(sourceFullName)) {
            boolean goOn = true;
            while(goOn) {
                int bytes = fis.read(BUFFER);
                if (bytes < 0) {
                    goOn = false;
                } else {
                    byte[] encryptedChunk = encryptor.encrypt(BUFFER, bytes);
                    skulkedOutputStream.write(encryptedChunk);
                }
            }
        }
    }

    private void writeReverseCarrierLength() throws IOException {
        byte[] carrierLengthVLQ = new NanoVLQ(carrierLength).getReverseBytes();
        skulkedOutputStream.write(carrierLengthVLQ);
    }

    private void createDirsForSourceFile(String fullFileName) {
        File file = new File(fullFileName);
        File parentDir = file.getParentFile();
        if (parentDir != null && ! parentDir.exists()) {
            log.info("Creating directories path for '{}'", fullFileName);
            parentDir.mkdirs();
        }
    }

//    private static long getFileLength(String fileName) {
//        File file = new File(fileName);
//        return file.exists() ? file.length() : -1;
//        }
//    }

    /**
     * validate() runs validation of this object, the set is defined by bitmask
     *
     * @param  bitMaskOfChecks defines which checks to perform
     * @throws IllegalStateException with the description of the error, or null if everything OK
     */
    private void validate(int bitMaskOfChecks) throws IllegalStateException {
    }

    private static long retrieveCarrierLength(String skulkedFullName) {
        byte[] lastBytes = new byte[TAIL_WITH_LEN_LEN];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(skulkedFullName), "r")) {
            long fileLength = randomAccessFile.length();
            long position = Math.max(0, fileLength - TAIL_WITH_LEN_LEN);
            randomAccessFile.seek(position);
            randomAccessFile.readFully(lastBytes);
            return 0;
        } catch(IOException e) {
            return -1;
        }
    }

    public static enum SKULKED_ERR_CODE {
        TOO_SHORT_FILE
    }

    public static class SkulkedFileException extends Exception {
        public final SKULKED_ERR_CODE errCode;
        public final long valueOfError;  // some additional data about error if reasonable
        public SkulkedFileException(SKULKED_ERR_CODE errCode, long valueOfError) {
            this.errCode = errCode;
            this.valueOfError = valueOfError;
        }
    }

    public String getCarrierFullName() {
        return carrierFullName;
    }

    public String getSkulkedFullName() {
        return skulkedFullName;
    }

    public String getSourceFullName() {
        return sourceFullName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("Skulked: ").append(skulkedFullName).append('(').append(skulkedLength).append(" bytes), ");
        sb.append("Carrier: ").append(carrierFullName).append('(').append(carrierLength).append(" bytes), ");
        sb.append("Source: ").append(sourceFullName).append('(').append(sourceLength).append(" bytes)");
        return sb.toString();
    }

    public String infoToString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("Skulked: ").append(skulkedFullName).append(" (").append(skulkedLength).append(" bytes), ");
        sb.append("Carrier (").append(carrierLength).append(" bytes), ");
        sb.append("Source: ").append(sourceFullName).append(" (").append(sourceLength).append(" bytes)");
        return sb.toString();
    }

}
