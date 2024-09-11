package util;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WaveData {
    final int format;
    final int sampleRate;
    final int totalBytes;
    final int bytesPerFrame;
    final ByteBuffer data;

    private final AudioInputStream audioStream;
    private final byte[] dataArray;

    private WaveData(AudioInputStream stream) {
        this.audioStream = stream;
        AudioFormat audioFormat = stream.getFormat();
        format = getOpenAlFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
        this.sampleRate = (int) audioFormat.getSampleRate();
        this.bytesPerFrame = audioFormat.getFrameSize();
        this.totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
        this.data = BufferUtils.createByteBuffer(totalBytes);
        this.dataArray = new byte[totalBytes];
        loadData();
    }

    protected void dispose() {
        try {
            audioStream.close();
            data.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData() {
        try {
            int bytesRead = audioStream.read(dataArray, 0, totalBytes);
            data.clear();
            data.put(dataArray, 0, bytesRead);
            data.flip();
        } catch (IOException e) {
            System.err.println("Couldn't readUser deluxe from audio stream!");
            throw new RuntimeException(e);
        }
    }


    public static WaveData create(String file){
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(Paths.get(file)));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            return new WaveData(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static int getOpenAlFormat(int channels, int bitsPerSample) {
        if (channels == 1) return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
        return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
    }
}