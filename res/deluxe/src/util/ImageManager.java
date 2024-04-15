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

public class ImageManager {
    private static final Map<String, TextureRegion> images = new HashMap<>();
    static final Queue<ImageData> queue = new LinkedList<>();

    public static void loadImages(){
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        final Map<String, Pixmap> pixmaps = new HashMap<>();
        for (ImageData imageData: queue) {
            executorService.submit(() -> {
                Pixmap pixmap = loadImageBuffer(imageData);
                pixmaps.put(imageData.getFile(), pixmap);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated() || !pixmaps.isEmpty()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            for (ImageData imageData: queue) {
                String key = imageData.getFile();
                if (!pixmaps.containsKey(key)) continue;
                Pixmap pixmap = pixmaps.get(key);
                pixmaps.remove(key);
                Texture texture = new Texture(pixmap);
                pixmap.dispose();
                TextureRegion region = new TextureRegion(texture);
                images.put(key, region);
            }
        }
        queue.clear();
    }

    public static Pixmap loadImageBuffer(ImageData imageData){
        return loadImageBuffer(imageData.getFile(), imageData.getFormat());
    }

    public static Pixmap loadImageBuffer(String path, Pixmap.Format format){
        FileHandle file = Gdx.files.local("assets/" + path + ".png");
        int[] width = new int[1];
        int[] height = new int[1];
        ByteBuffer bytes = STBImage.stbi_load(file.path(), width, height, new int[1], 0);
        if (bytes == null) throw new RuntimeException("Image not loaded: " + file.path());
        Pixmap pixmap = new Pixmap(width[0], height[0], format);
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
        add(path, Pixmap.Format.RGB888);
    }

    public static void add(String path, Pixmap.Format format){
        ImageData imageData = new ImageData(path, format);
        queue.add(imageData);
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