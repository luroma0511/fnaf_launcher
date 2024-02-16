package game.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private final Map<String, AudioClass> audioClasses;

    public SoundManager(){
        audioClasses = new HashMap<>();
    }

    public boolean loadingSounds(Request request){
        if (request.soundsIsEmpty()) return false;
        String path = request.getSound(0);
        if (!audioClasses.containsKey(path)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + path + ".wav"));
            AudioClass audioClass = new AudioClass(sound);
            audioClasses.put(path, audioClass);
        }
        request.removeSound(0);
        return true;
    }

    public void play(String path){
        if (!audioClasses.containsKey(path)) return;
        audioClasses.get(path).play();
    }

    public void stop(String path){
        if (!audioClasses.containsKey(path)) return;
        audioClasses.get(path).stop();
    }

    public void stopAllSounds(){
        for (AudioClass audioClass: audioClasses.values()){
            audioClass.stop();
        }
    }

    public void setLoop(String path, boolean loop){
        if (!audioClasses.containsKey(path)) return;
        audioClasses.get(path).setLoop(loop);
    }

    public float getVolume(String path){
        if (!audioClasses.containsKey(path)) return 0;
        return audioClasses.get(path).getVolume();
    }

    public void setVolume(String path, float volume){
        if (!audioClasses.containsKey(path)) return;
        audioClasses.get(path).setVolume(volume);
    }

    public float getPitch(String path){
        if (!audioClasses.containsKey(path)) return 0;
        return audioClasses.get(path).getPitch();
    }

    public void setPitch(String path, float pitch){
        if (!audioClasses.containsKey(path)) return;
        audioClasses.get(path).setPitch(pitch);
    }

    public void dispose(){
        for (AudioClass audioClass: audioClasses.values()) audioClass.dispose();
        audioClasses.clear();
    }
}
