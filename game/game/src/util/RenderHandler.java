package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderHandler {
    public final SpriteBatch batch;
    public final ShapeDrawer shapeDrawer;
    public final FrameBuffer screenBuffer;
    public boolean lock = true;
    public float screenAlpha;
    private boolean batchProcessing;

    public RenderHandler() {
        batch = new SpriteBatch();
        screenBuffer = FrameBufferManager.newFrameBuffer();
        FrameBuffer fbo = FrameBufferManager.newFrameBuffer();
        fbo.begin();
        ScreenUtils.clear(1, 1, 1, 1);
        FrameBufferManager.end(batch, fbo, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
        shapeDrawer = new ShapeDrawer(batch, region);
    }

    public void batchBegin(){
        if (batchProcessing) return;
        batch.begin();
        batchProcessing = true;
    }

    public void batchEnd(){
        if (!batchProcessing) return;
        batch.end();
        batchProcessing = false;
    }

    public boolean requests(AppHandler appHandler, String game){
        lock = !appHandler.getTextureHandler().queue.isEmpty() || !appHandler.soundHandler.queue.isEmpty();
        if (!lock) return false;
        Time.lock = true;
        long time = System.currentTimeMillis();
        appHandler.getTextureHandler().dispose();
        appHandler.soundHandler.load("assets/" + game);
        appHandler.getTextureHandler().load("assets/" + game);
        System.out.println(System.currentTimeMillis() - time + "ms");
        return true;
    }

    public void viewportAdjust(InputManager inputManager){
        Vector3 v3 = CameraManager.viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        inputManager.x = v3.x - CameraManager.getX();
        inputManager.y = v3.y - CameraManager.getY();
        inputManager.readjust();
    }

    public void dispose(){
        screenBuffer.dispose();
        batch.dispose();
    }
}
