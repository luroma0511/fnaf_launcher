package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TextureHandler {
    private final Map<String, TextureRegion> images = new HashMap<>();
    final Queue<String> queue = new LinkedList<>();

    public void load(String dir){
        Map<String, Pixmap> pixmaps = new HashMap<>();
//        queue.forEach(key -> pixmaps.put(key, loadImageBuffer(key)));
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            queue.forEach(key -> executorService.submit(() -> pixmaps.put(key, loadImageBuffer(dir, key))));
            executorService.shutdown();
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (String key: queue) {
            Pixmap pixmap = pixmaps.containsKey(key) ? pixmaps.get(key) : loadImageBuffer(dir, key);
            pixmaps.remove(key);
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            TextureRegion region = new TextureRegion(texture);
            images.put(key, region);
        }
        queue.clear();
    }

    public Pixmap loadImageBuffer(String dir, String path){
        path = dir + "/" + path + ".png";
        int[] width = new int[1];
        int[] height = new int[1];
        ByteBuffer bytes = STBImage.stbi_load(path, width, height, new int[1], 0);
        if (bytes == null) throw new RuntimeException("Image not loaded: " + path);
        Pixmap pixmap = new Pixmap(width[0], height[0], Pixmap.Format.RGBA8888);
        pixmap.setPixels(bytes);
        STBImage.stbi_image_free(bytes);
        return pixmap;
    }

    public TextureRegion getRegion(String path, int width, int fileIndex){
        if (!images.containsKey(path)) return null;
        TextureRegion region = images.get(path);
        region.setRegion(fileIndex * width + fileIndex + 1, 0, width, region.getTexture().getHeight());
        return region;
    }

    public boolean isAlpha(Pixmap pixmap, int mx, int my){
        return new Color(pixmap.getPixel(mx, my)).a != 0;
    }

    public void add(String path){
        queue.add(path);
    }

    public void addImages(String dir, String path){
        Gdx.files.local(path).readString().lines().forEach(line -> add(dir + line));
    }

    public TextureRegion get(String path){
        return images.get(path);
    }

    public void dispose(){
        images.values().forEach(texture -> texture.getTexture().dispose());
        images.clear();
    }
}
