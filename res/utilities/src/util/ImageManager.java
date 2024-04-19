package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageManager {
    private static final Map<String, TextureRegion> images = new HashMap<>();
    static final Queue<String> queue = new LinkedList<>();

    public static void loadImages(){
        final Map<String, Pixmap> pixmaps = new HashMap<>();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            for (String key : queue) {
                executorService.submit(() -> {
                    Pixmap pixmap = loadImageBuffer(key);
                    pixmaps.put(key, pixmap);
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String key: queue) {
            Pixmap pixmap;
            if (!pixmaps.containsKey(key)) pixmap = loadImageBuffer(key);
            else {
                pixmap = pixmaps.get(key);
                pixmaps.remove(key);
            }
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            TextureRegion region = new TextureRegion(texture);
            images.put(key, region);
        }
        queue.clear();
    }

    public static Pixmap loadImageBuffer(String path){
        FileHandle file = Gdx.files.local("assets/" + path + ".png");
        int[] width = new int[1];
        int[] height = new int[1];
        ByteBuffer bytes = STBImage.stbi_load(file.path(), width, height, new int[1], 0);
        if (bytes == null) throw new RuntimeException("Image not loaded: " + file.path());
        Pixmap pixmap = new Pixmap(width[0], height[0], Pixmap.Format.RGBA8888);
        pixmap.setPixels(bytes);
        STBImage.stbi_image_free(bytes);
        return pixmap;
    }

    public static TextureRegion getRegion(String path, int width, int fileIndex){
        if (!images.containsKey(path)) return null;
        int x = fileIndex * width + fileIndex + 1;
        TextureRegion region = images.get(path);
        region.setRegion(x, 0, width, region.getTexture().getHeight());
        return region;
    }

    public static boolean isAlpha(Pixmap pixmap, int mx, int my){
        Color color = new Color(pixmap.getPixel(mx, my));
        return color.a != 0;
    }

    public static void add(String path){
        queue.add(path);
    }

    public static TextureRegion get(String path){
        return images.get(path);
    }

    public static void pixmapDispose(Pixmap pixmap){
        pixmap.dispose();
    }

    public static void dispose(){
        if (images.isEmpty()) return;
        for (TextureRegion texture: images.values()) texture.getTexture().dispose();
        images.clear();
    }
}