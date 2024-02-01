package game.engine.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class FrameBufferManager {
    private FrameBuffer shapeFrameBufferManager;

    public FrameBufferManager(){

    }

    public FrameBuffer getShape() {
        return shapeFrameBufferManager;
    }

    public void createShape(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.enableBlending();
        batch.begin();
        shapeFrameBufferManager = newFrameBuffer();
        shapeFrameBufferManager.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, 1280, 720);
        shapeRenderer.flush();
        shapeRenderer.end();
        batch.flush();
        shapeFrameBufferManager.end(0, 0, 1280, 720);
        batch.end();
        ScreenUtils.clear(0, 0, 0, 1);
    }

    public FrameBuffer newFrameBuffer(){
        return new FrameBuffer(Pixmap.Format.RGBA8888, 1280, 720, true);
    }

    public void dispose(){
        shapeFrameBufferManager.dispose();
    }
}
