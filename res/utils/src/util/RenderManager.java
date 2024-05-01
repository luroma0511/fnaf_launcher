package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderManager {
    public static final SpriteBatch batch = new SpriteBatch();
    public static final ShapeDrawer shapeDrawer;
    public static final FrameBuffer screenBuffer = FrameBufferManager.newFrameBuffer();
    public static boolean lock = true;
    public static float screenAlpha;

    static {
        FrameBuffer fbo = FrameBufferManager.newFrameBuffer();
        fbo.begin();
        ScreenUtils.clear(1, 1, 1, 1);
        FrameBufferManager.end(batch, fbo, 1280, 720);
        TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
        shapeDrawer = new ShapeDrawer(batch, region);
    }

    public static boolean requests(){
        lock = !ImageManager.queue.isEmpty() || !SoundManager.queue.isEmpty();
        if (!lock) return false;
        Time.lock = true;
        long time = System.currentTimeMillis();
        ImageManager.dispose();
        SoundManager.load();
        ImageManager.load();
        System.out.println(System.currentTimeMillis() - time + "ms");
        return true;
    }

    public static void viewportAdjust(InputManager inputManager){
        Vector3 v3 = CameraManager.viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        inputManager.x = v3.x - CameraManager.getX();
        inputManager.y = v3.y - CameraManager.getY();
        inputManager.readjust();
    }

    public static void dispose(){
        screenBuffer.dispose();
        batch.dispose();
    }
}
