package game.engine.util;

import com.badlogic.gdx.InputAdapter;

public class InputManager extends InputAdapter {
    float x;
    float y;
    boolean pressed;

    public InputManager(){

    }

    public boolean mouseOver(short x, short y, short width, short height){
        return mouseOverExact(x, y, (short) (x + width), (short) (y + height));
    }

    public boolean mouseOverExact(short x, short y, short x2, short y2){
        return this.x >= x && this.x <= x2 && this.y >= y && this.y <= y2;
    }

    public void readjust(){
        x = Math.min(x, 1280);
        x = Math.max(x, 0);

        y = Math.min(y, 720);
        y = Math.max(y, 0);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
