package util;

import deluxe.state.Game.Game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SoundManager {
    private static final Map<String, AudioClass> audios = new HashMap<>();
    static final Queue<String> queue = new LinkedList<>();

    public static boolean loadSounds(){
        if (queue.isEmpty()) return false;
        String path = queue.remove();
        if (audios.containsKey(path)) return true;
        AudioClass audioClass = new AudioClass(path);
        audios.put(path, audioClass);
        return true;
    }

    public static void add(String path){
        queue.add(path);
    }

    public static void addGameSounds(){
        for (String path: Game.gameSoundData) add(path);
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
