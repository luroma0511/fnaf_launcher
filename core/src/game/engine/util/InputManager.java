package game.engine.util;

import com.badlogic.gdx.InputAdapter;

public class InputManager extends InputAdapter {
    float x;
    float y;
    boolean pressed;

    public InputManager(){

    }

    public void readjust(){
        if (x > 1280){
            x = 1280;
        } else if (x < 0){
            x = 0;
        }

        if (y > 720){
            y = 720;
        } else if (y < 0){
            y = 0;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
