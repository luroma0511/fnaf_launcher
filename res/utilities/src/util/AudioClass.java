package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class AudioClass {
    private final Sound sound;
    private long soundID;
    private float pitch;
    private float volume;

    public AudioClass(String path) {
        FileHandle file = Gdx.files.local("assets/sounds/" + path + ".wav");
        this.sound = Gdx.audio.newSound(file);
        resetValues();
    }

    public void resetValues(){
        soundID = -1;
        pitch = 1;
        volume = 1;
    }

    public void play(){
        if (soundID != -1) stop();
        resetValues();
        soundID = sound.play(volume);
    }

    public void stop(){
        if (soundID != -1) sound.stop(soundID);
    }

    public void setLoop(boolean loop){
        if (soundID != -1) sound.setLooping(soundID, loop);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        if (soundID == -1 || this.pitch == pitch) return;
        this.pitch = pitch;
        sound.setPitch(soundID, pitch);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        if (soundID == -1 || this.volume == volume) return;
        this.volume = volume;
        sound.setVolume(soundID, volume);
    }

    void dispose(){
        sound.dispose();
    }
}