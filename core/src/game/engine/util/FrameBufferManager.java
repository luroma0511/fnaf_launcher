package game.engine.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class FrameBufferManager {
    private final FrameBuffer frameBuffer;

    public FrameBufferManager(){
        frameBuffer = newFrameBuffer();
    }

    public void begin(){
        frameBuffer.begin();
    }

    public void createShapes(SpriteBatch batch, ImageManager imageManager) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        batch.enableBlending();
        batch.begin();
        begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, 1280, 720);
        shapeRenderer.flush();
        shapeRenderer.end();
        end(batch);

        imageManager.add("rect", new TextureRegion(frameBuffer.getColorBufferTexture()));
        shapeRenderer.dispose();
    }

    public Texture getTexture(){
        return frameBuffer.getColorBufferTexture();
    }

    public void end(SpriteBatch batch){
        batch.flush();
        frameBuffer.end(0, 0, 1280, 720);
        batch.end();
        ScreenUtils.clear(0, 0, 0, 1);
    }

    public FrameBuffer newFrameBuffer(){
        return new FrameBuffer(Pixmap.Format.RGBA8888, 1280, 720, true);
    }
}
