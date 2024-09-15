package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraManager {
    static Viewport viewport;
    private static float x;
    private static float y;
    static ShaderProgram shader;

    public static void initShader(){
        if (shader == null) shader = new ShaderProgram(Gdx.files.local("game/shaders/perspective.vert"), Gdx.files.local("game/shaders/perspective.frag"));
        if (!shader.isCompiled()) Gdx.app.error("shader", "compilation failed:\n" + shader.getLog());
    }

    public static void createCamera(int width, int height){
        OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false);
        viewport = new FitViewport(width, height, camera);
        viewport.getCamera().update();
        apply(width, height);
    }

    public static void move(float x, float y){
        if (x != CameraManager.x || y != CameraManager.y) translate(x - CameraManager.x, y - CameraManager.y);
    }

    public static void translate(float x, float y){
        viewport.getCamera().translate(x, y, 0);
        CameraManager.x += x;
        CameraManager.y += y;
        viewport.getCamera().update();
    }

    public static void apply(int width, int height){
        viewport.update(width, height);
        viewport.apply();
    }

    public static void applyShader(SpriteBatch batch){
        batch.setShader(shader);
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