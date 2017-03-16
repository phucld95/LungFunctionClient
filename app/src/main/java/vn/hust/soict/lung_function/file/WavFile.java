package vn.hust.soict.lung_function.file;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class WavFile {
    private static final int BUFFER_SIZE = 1024;

    private Context mContext;

    private FileInputStream in;
    private FileOutputStream out;

    private String mFileName;

    private int sampleRate;
    private int numberChannels;
    private int bitPerSample;

    public WavFile(Context context, String pathRawFile, int sampleRate, int numberChannels, int bitPerSample) throws FileNotFoundException {
        mContext = context;
        in = new FileInputStream(pathRawFile);
        this.sampleRate = sampleRate;
        this.numberChannels = numberChannels;
        this.bitPerSample = bitPerSample;
    }

    public boolean saveWaveFile() throws IOException {
        String filename = mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()
                + "/RECORD_" + (new Date().getTime()) + ".wav";
        return saveWaveFile(filename);
    }

    public String getFileName() {
        return mFileName;
    }

    public boolean saveWaveFile(String outFilename) throws IOException {
        if (in == null) return false;
        mFileName = outFilename;
        out = new FileOutputStream(outFilename);
        byte[] data = new byte[BUFFER_SIZE];

        writeWaveFileHeader();

        while (in.read(data) != -1) {
            out.write(data);
        }

        in.close();
        out.close();
        return true;
    }

    private void writeWaveFileHeader() throws IOException {

        byte[] header = new byte[44];

        // ChunkID 4byte
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // ChunkSize 4byte
        // 36 + SubChunk2Size, or more precisely:
        // 4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
        long chunkSize = 36 + in.getChannel().size();
        header[4] = (byte) (chunkSize);
        header[5] = (byte) (chunkSize >> 8);
        header[6] = (byte) (chunkSize >> 16);
        header[7] = (byte) (chunkSize >> 24);

        // Format 4byte
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // Subchunk1ID 4byte
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        // Subchunk1Size 4byte
        // 16 for PCM.  This is the size of the rest of the Subchunk which follows this number.
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        // AudioFormat 2byte
        // PCM = 1 (i.e. Linear quantization) Values other than 1 indicate some form of compression.
        header[20] = 1;
        header[21] = 0;

        // NumChannels 2byte
        // Mono = 1, Stereo = 2, etc.
        header[22] = (byte) numberChannels;
        header[23] = 0;

        // SampleRate 4byte
        header[24] = (byte) (sampleRate);
        header[25] = (byte) (sampleRate >> 8);
        header[26] = (byte) (sampleRate >> 16);
        header[27] = (byte) (sampleRate >> 24);

        // ByteRate 4byte
        // == SampleRate * NumChannels * BitsPerSample/8
        long byteRate = sampleRate * numberChannels * bitPerSample / 8;
        header[28] = (byte) (byteRate);
        header[29] = (byte) (byteRate >> 8);
        header[30] = (byte) (byteRate >> 16);
        header[31] = (byte) (byteRate >> 24);

        // BlockAlign   2byte
        // == NumChannels * BitsPerSample/8
        int blockAlign = numberChannels * bitPerSample / 8;
        header[32] = (byte) (blockAlign); // block align
        header[33] = (byte) (blockAlign >> 8);

        // BitsPerSample  2byte
        // 8 bits = 8, 16 bits = 16, etc.
        header[34] = (byte) (bitPerSample); // bits per sample
        header[35] = (byte) (bitPerSample >> 8);

        // Subchunk2ID 4byte
        // Contains the letters "data"
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        //Subchunk2Size  4byte
        // == NumSamples * NumChannels * BitsPerSample/8
        long subchunk2Size = in.getChannel().size();
        header[40] = (byte) (subchunk2Size);
        header[41] = (byte) (subchunk2Size >> 8);
        header[42] = (byte) (subchunk2Size >> 16);
        header[43] = (byte) (subchunk2Size >> 24);

        out.write(header, 0, 44);
    }

    public boolean deleteFile() {
        if (mFileName == null) return false;
        return new File(mFileName).delete();
    }
}
