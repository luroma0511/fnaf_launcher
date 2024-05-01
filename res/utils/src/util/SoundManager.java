package util;

import com.badlogic.gdx.Gdx;

import java.util.*;

public class SoundManager implements Constants {
    private static final Map<String, Sound> sounds = new HashMap<>();
    static final Queue<String> queue = new LinkedList<>();

    public static void load(){
        queue.forEach(key -> sounds.put(key, new Sound(key)));
        queue.clear();
    }

    public static void add(String path){
        if (!sounds.containsKey(path)) queue.add(path);
    }

    public static void addAll(String path){
        Gdx.files.local(path).readString().lines().forEach(SoundManager::add);
    }

    public static void play(String key){
        if (sounds.containsKey(key)) sounds.get(key).play();
    }

    public static void stop(String key){
        if (sounds.containsKey(key)) sounds.get(key).stop();
    }

    public static void stopAllSounds(){
        sounds.keySet().forEach(SoundManager::stop);
    }

    public static float getSoundEffect(int effectType, String key){
        if (!sounds.containsKey(key)) return -1;
        if (effectType == VOLUME) return sounds.get(key).getVolume();
        else if (effectType == PITCH) return sounds.get(key).getPitch();
        else if (effectType == LOOP) return sounds.get(key).isLoop() ? 1 : 0;
        else if (effectType == MUFFLE) return sounds.get(key).getMuffle();
        return -1;
    }

    public static void setAllSoundEffect(int effectType, float value){
        sounds.keySet().forEach(key -> setSoundEffect(effectType, key, value));
    }

    public static void setSoundEffect(int effectType, String key, float value){
        if (!sounds.containsKey(key)) return;
        if (effectType == VOLUME) sounds.get(key).setVolume(value);
        else if (effectType == PITCH) sounds.get(key).setPitch(value);
        else if (effectType == LOOP) sounds.get(key).setLoop((int) value == 1);
        else if (effectType == MUFFLE) sounds.get(key).setMuffle(value);
    }

    public static void dispose(){
        sounds.values().forEach(Sound::dispose);
        sounds.clear();
    }
}