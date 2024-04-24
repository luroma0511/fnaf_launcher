package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SoundManager {
    private static final Map<String, AudioClass> audios = new HashMap<>();
    static final Queue<String> queue = new LinkedList<>();

    public static void loadSounds(){
        for (String key: queue) {
            if (audios.containsKey(key)) continue;
            AudioClass audioClass = new AudioClass(key);
            audios.put(key, audioClass);
        }
        queue.clear();
    }

    public static void add(String path){
        queue.add(path);
    }

    public static void addSounds(String path){
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line;
            while ((line = reader.readLine()) != null) add(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void play(String path){
        if (audios.containsKey(path)) audios.get(path).play();
    }

    public static void stop(String path){
        if (audios.containsKey(path)) audios.get(path).stop();
    }

    public static void stopAllSounds(){
        for (AudioClass audioClass: audios.values()) audioClass.stop();
    }

    public static void setLoop(String path, boolean loop){
        if (audios.containsKey(path)) audios.get(path).setLoop(loop);
    }

    public static float getVolume(String path){
        if (!audios.containsKey(path)) return -1;
        return audios.get(path).getVolume();
    }

    public static void setVolume(String path, float volume){
        if (audios.get(path) != null) audios.get(path).setVolume(volume);
    }

    public static float getPitch(String path){
        if (!audios.containsKey(path)) return -1;
        return audios.get(path).getPitch();
    }

    public static void setPitch(String path, float pitch) {
        if (audios.get(path) != null) audios.get(path).setPitch(pitch);
    }

    public static void dispose(){
        for (AudioClass audioClass: audios.values()) audioClass.dispose();
        audios.clear();
    }
}
