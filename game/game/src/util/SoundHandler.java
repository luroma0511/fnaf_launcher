package util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SoundHandler implements Constants {
    private final Map<String, Sound> sounds = new HashMap<>();
    final Queue<String> queue = new LinkedList<>();

    public void load(String dir){
        queue.forEach(name -> load(dir, name));
        queue.clear();
    }

    public void load(String dir, String name){
        if (!has(name)) sounds.put(name, new Sound(dir, name));
    }

    public void add(String path){
        if (!has(path)) queue.add(path);
    }

    public void addAll(String path){
        String content = Loader.loadFile(path);
        assert content != null;
        content.lines().forEach(this::add);
    }

    public boolean has(String key){
        return sounds.containsKey(key);
    }

    public void play(String key){
        if (sounds.containsKey(key)) sounds.get(key).play();
    }

    public boolean isPlaying(String key){
        return has(key) && sounds.get(key).isPlaying();
    }

    public void stop(String key){
        if (has(key)) sounds.get(key).stop();
    }

    public void stopAllSounds(){
        sounds.keySet().forEach(this::stop);
    }

    public float getSoundEffect(int effectType, String key){
        if (!has(key)) return -1;
        if (effectType == VOLUME) return sounds.get(key).getVolume();
        else if (effectType == PITCH) return sounds.get(key).getPitch();
        else if (effectType == LOOP) return sounds.get(key).isLoop() ? 1 : 0;
        else if (effectType == MUFFLE) return sounds.get(key).getMuffle();
        return -1;
    }

    public void setAllSoundEffect(int effectType, float value){
        sounds.keySet().forEach(key -> setSoundEffect(effectType, key, value));
    }

    public void setSoundEffect(int effectType, String key, float value){
        if (!has(key)) return;
        if (effectType == VOLUME) sounds.get(key).setVolume(value);
        else if (effectType == PITCH) sounds.get(key).setPitch(value);
        else if (effectType == LOOP) sounds.get(key).setLoop((int) value == 1);
        else if (effectType == MUFFLE) sounds.get(key).setMuffle(value);
    }

    public void setSoundEffect(int effectType, String key, float v1, float v2){
        if (!has(key)) return;
        if (effectType == PAN) sounds.get(key).setPan(v1, v2);
    }

    public void dispose(){
        sounds.values().forEach(Sound::dispose);
        sounds.clear();
    }
}
