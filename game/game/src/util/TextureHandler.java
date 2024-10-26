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

public class TextureHandler {
    private final Map<String, TextureRegion> images = new HashMap<>();
    private final Map<String, Pixmap> pixmaps = new HashMap<>();
    final Queue<String> queue = new LinkedList<>();
    private ExecutorService executorService;
    public int currentPercent;
    public int maxPercent;
    public boolean loading;
    private long time;

    public void load(String dir){
        if (!loading){
            time = System.currentTimeMillis();
            currentPercent = 0;
            pixmaps.clear();
            executorService = Executors.newFixedThreadPool(3);
            queue.forEach(key -> executorService.submit(() -> {
                pixmaps.put(key, loadImageBuffer(dir, key));
                currentPercent++;
            }));
            executorService.shutdown();
            loading = true;
        } else {
            if (executorService.isTerminated()) {
                for (String key : queue) {
                    if (!pixmaps.containsKey(key)) {
                        continue;
                    }
                    Pixmap pixmap = pixmaps.get(key);
                    pixmaps.remove(key);
                    Texture texture = new Texture(pixmap);
                    pixmap.dispose();
                    TextureRegion region = new TextureRegion(texture);
                    images.put(key, region);
                    if (!pixmaps.isEmpty()) continue;
                    loading = false;
                    System.out.println(System.currentTimeMillis() - time + "ms");
                    queue.clear();
                    break;
                }
            }
        }
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

    public TextureRegion getRegion(TextureRegion region, int width, int fileIndex){
        region.setRegion(fileIndex * width + fileIndex + 1, 0, width, region.getTexture().getHeight());
        return region;
    }

    public void setRegion(TextureRegion region, int width, int fileIndex){
        region.setRegion(fileIndex * width + fileIndex + 1, 0, width, region.getTexture().getHeight());
    }

    public void setFilter(String name){
        setFilter(get(name).getTexture());
    }

    public void setFilter(Texture texture){
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public boolean isAlpha(Pixmap pixmap, int mx, int my){
        return new Color(pixmap.getPixel(mx, my)).a != 0;
    }

    public void add(String path){
        queue.add(path);
        maxPercent++;
    }

    public void addImages(String dir, String path){
        String content = Loader.loadFile("res/data/" + path);
        assert content != null;
        content.lines().forEach(line -> add(dir + line));
    }

    public TextureRegion get(String path){
        return images.get(path);
    }

    public void dispose(){
        currentPercent = 0;
        maxPercent = 0;
        images.values().forEach(texture -> texture.getTexture().dispose());
        images.clear();
    }
}
