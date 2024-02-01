package game.engine.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraManager {
    final Viewport viewport;
    private float x;
    private float y;

    public CameraManager(short width, short height){
        final OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false);
        viewport = new FitViewport(width, height, camera);
        viewport.getCamera().translate((float) width / 2, (float) height / 2, 0);
        viewport.getCamera().update();
        viewport.update(width, height);
        viewport.apply();
    }

    public void translate(short x, short y){
        viewport.getCamera().translate(x, y, 0);
        this.x += x;
        this.y += y;
        viewport.getCamera().update();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
