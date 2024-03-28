package util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ByteArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageManager {
    private static final Map<String, TextureRegion> images = new HashMap<>();
    static final Queue<String> queue = new LinkedList<>();

    public static void loadImages(){
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Pixmap[] pixmapArr = new Pixmap[queue.size()];
        int i = 0;
        for (String file: queue) {
            int finalI = i;
            executorService.submit(() -> {
                pixmapArr[finalI] = loadImageBuffer(file);
            });
            i++;
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(5_000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        i = 0;
        for (String file: queue){
            Texture texture = new Texture(pixmapArr[i]);
            pixmapArr[i].dispose();
            TextureRegion region = new TextureRegion(texture);
            images.put(file, region);
            i++;
        }
        queue.clear();
    }

    public static Pixmap loadImageBuffer(String path){
        File file = new File("assets/" + path + ".png");
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer imageBuffer = STBImage.stbi_load(file.getAbsolutePath(), width, height, channels, 0);
        if (imageBuffer == null) throw new RuntimeException("Image not loaded: " + file.getAbsolutePath());
        Pixmap pixmap = new Pixmap(width.get(), height.get(), Pixmap.Format.RGBA8888);
        pixmap.setPixels(imageBuffer);
        STBImage.stbi_image_free(imageBuffer);
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