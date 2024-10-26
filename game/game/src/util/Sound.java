package util;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

public class Sound {
    private final int buffer;
    private final int source;
    private final int filter;
    private float volume;
    private float pitch;
    private boolean loop;
    private float[] pan = new float[2];
    private float muffle;
    private boolean playing;

    public Sound(String dir, String path) {
        buffer = AL11.alGenBuffers();
        source = AL11.alGenSources();
        WaveData waveData = WaveData.create(dir + "/sounds/" + path + ".wav");
        AL11.alBufferData(buffer, waveData.format, waveData.data, waveData.sampleRate);
        waveData.dispose();
        AL11.alSourcei(source, AL11.AL_BUFFER, buffer);

        filter = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(filter, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        EXTEfx.alFilterf(filter, EXTEfx.AL_LOWPASS_GAINHF, 1);
        AL11.alSource3i(source, EXTEfx.AL_AUXILIARY_SEND_FILTER, 0, 0, filter);

        AL11.alSourcef(source, AL11.AL_REFERENCE_DISTANCE, 1.0f);  // Starts attenuating at 1 unit
        AL11.alSourcef(source, AL11.AL_MAX_DISTANCE, 100.0f);      // Stops attenuating after 100 units
        AL11.alSourcef(source, AL11.AL_ROLLOFF_FACTOR, 1.0f);

        resetValues();
    }

    public void resetValues(){
        setPan(0, 0);
        setVolume(1);
        setPitch(1);
        setLoop(false);
        setMuffle(1);
        playing = false;
    }

    public void play(){
        if (playing) stop();
        AL11.alSourcePlay(source);
        playing = true;
    }

    public boolean isPlaying(){
        return playing;
    }

    public void stop(){
        if (playing) AL11.alSourceStop(source);
        resetValues();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop){
        this.loop = loop;
        AL11.alSourcei(source, AL11.AL_LOOPING, loop ? 1 : 0);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        AL11.alSourcef(source, AL11.AL_PITCH, pitch);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        AL11.alSourcef(source, AL11.AL_GAIN, volume);
    }

    public float[] getPan() {
        return pan;
    }

    public void setPan(float panX, float panZ) {
        pan[0] = panX;
        pan[1] = panZ;
        AL11.alSource3f(source, AL11.AL_POSITION, pan[0], 0, pan[1]);
    }

    public float getMuffle() {
        return muffle;
    }

    public void setMuffle(float muffle) {
        this.muffle = muffle;
        EXTEfx.alFilterf(filter, EXTEfx.AL_LOWPASS_GAINHF, muffle);
        AL11.alSourcei(source, EXTEfx.AL_DIRECT_FILTER, filter);
    }

    void dispose(){
        EXTEfx.alDeleteFilters(filter);
        AL11.alDeleteSources(source);
        AL11.alDeleteBuffers(buffer);
    }
}
