package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputManager extends InputAdapter {
    float x;
    float y;
    float scroll;
    boolean pressed;
    boolean lock;

    public void update(){
        pressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        lock = false;
    }

    public void fullscreen(Window window){
        if (!keyTyped(Input.Keys.F11) || lock) return;
        if (Gdx.graphics.isFullscreen()) Gdx.graphics.setWindowedMode(window.getWidth(), window.getHeight());
        else Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }

    public boolean keyTyped(int key){
        return Gdx.input.isKeyJustPressed(key);
    }

    public void reset(){
        scroll = 0;
    }

    public void setLock(){
        lock = !lock;
    }

    public boolean mouseOver(float x, float y, float width, float height){
        return mouseOverExact(x, y, x + width, y + height);
    }

    public boolean mouseOverExact(float x, float y, float x2, float y2){
        return this.x >= x && this.x <= x2 && this.y >= y && this.y <= y2;
    }

    public void readjust(){
        if (x < 0) x = 0;
        if (x > 1280) x = 1280;
        if (y < 0) y = 0;
        if (y > 720) y = 720;
    }

    @Override
    public boolean scrolled(float amountX, float amountY){
        scroll = -amountY;
        return false;
    }

    public float getScrolled() {
        return scroll;
    }

    public boolean isPressed() {
        return pressed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
