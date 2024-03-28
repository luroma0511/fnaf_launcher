package util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraManager {
    static Viewport viewport;
    private static float x;
    private static float y;

    public static void createCamera(int width, int height){
        final OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false);
        viewport = new FitViewport(width, height, camera);
        viewport.getCamera().update();
        viewport.update(width, height);
        viewport.apply();
    }

    public static void move(float x, float y){
        if (x == CameraManager.x && y == CameraManager.y) return;
        translate(x - CameraManager.x, y - CameraManager.y);
    }

    public static void translate(float x, float y){
        viewport.getCamera().translate(x, y, 0);
        CameraManager.x += x;
        CameraManager.y += y;
        viewport.getCamera().update();
    }

    public static void setOrigin(){
        if (x != 0 || y != 0) translate(-x, -y);
    }

    public static Viewport getViewport() {
        return viewport;
    }

    public static float getX() {
        return x;
    }

    public static float getY() {
        return y;
    }
}
