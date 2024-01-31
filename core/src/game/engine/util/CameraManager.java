package game.engine.util;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraManager {
    OrthographicCamera camera;

    public CameraManager(short width, short height){
        camera = new OrthographicCamera(width, height);
    }
}
