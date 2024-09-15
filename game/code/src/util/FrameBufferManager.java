package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FrameBufferManager {
    public static void end(SpriteBatch batch, FrameBuffer fbo, int width, int height){
        batch.flush();
        fbo.end(0, 0, width, height);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public static void render(SpriteBatch batch, FrameBuffer fbo, boolean linear){
        render(batch, fbo, linear, -1, -1);
    }

    public static void render(SpriteBatch batch, FrameBuffer fbo, boolean linear, int width, int height){
        Texture texture = fbo.getColorBufferTexture();
        if (linear) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        if (width != -1 && height != -1) {
            region.setRegion(0, 0, width, height);
            region.flip(false, true);
            batch.draw(region, CameraManager.getX(), CameraManager.getY(), 1280, 720);
        } else {
            region.flip(false, true);
            batch.draw(region, CameraManager.getX(), CameraManager.getY());
        }
    }

    public static FrameBuffer newFrameBuffer(){
        return newFrameBuffer(1280, 720);
    }

    public static FrameBuffer newFrameBuffer(int width, int height){
        return new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
    }
}