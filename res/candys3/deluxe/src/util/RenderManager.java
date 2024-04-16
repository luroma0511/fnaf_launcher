package util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import core.Candys3Deluxe;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderManager {
    public static SpriteBatch batch;
    public static ShapeDrawer shapeDrawer;
    public static FrameBuffer screenBuffer;
    public static boolean lock = true;

    public RenderManager(){
        batch = new SpriteBatch();
        FrameBuffer fbo = FrameBufferManager.newFrameBuffer();
        fbo.begin();
        ScreenUtils.clear(1, 1, 1, 1);
        FrameBufferManager.end(batch, fbo, 1280, 720);
        TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
        shapeDrawer = new ShapeDrawer(batch, region);
        screenBuffer = FrameBufferManager.newFrameBuffer();
    }

    public boolean requests(){
        lock = !(ImageManager.queue.isEmpty() && SoundManager.queue.isEmpty());
        if (!lock) return false;
        Time.lock = true;
        long time = System.currentTimeMillis();
        ImageManager.dispose();
        while (SoundManager.loadSounds()){
        }
        ImageManager.loadImages();
        System.out.println(System.currentTimeMillis() - time + "ms");
        return true;
    }

    public void viewportAdjust(){
        Vector3 v3 = CameraManager.viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Candys3Deluxe.inputManager.x = v3.x - CameraManager.getX();
        Candys3Deluxe.inputManager.y = v3.y - CameraManager.getY();
        Candys3Deluxe.inputManager.readjust();
    }

    public void dispose(){
        screenBuffer.dispose();
        batch.dispose();
    }
}
