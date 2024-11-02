package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import util.deluxe.CandysUser;

public class InputManager extends InputAdapter {
    float x;
    float y;
    float scroll;
    boolean lock;
    int currentKey = -1;

    public void fullscreen(Window window, CandysUser user){
        if (!keyTyped(user.fullscreenKey) || lock) return;
        if (Gdx.graphics.isFullscreen()) Gdx.graphics.setWindowedMode(window.width(), window.height());
        else Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }

    public boolean keyTyped(int key){
        return Gdx.input.isKeyJustPressed(key);
    }

    public boolean keyPressed(int key){
        return Gdx.input.isKeyPressed(key);
    }

    private boolean buttonType(int button){
        return Gdx.input.isButtonJustPressed(button);
    }

    public boolean isLeftPressed(){
        return buttonType(Input.Buttons.LEFT);
    }

    public boolean isRightPressed(){
        return buttonType(Input.Buttons.RIGHT);
    }

    public int getCurrentKey() {
        return currentKey;
    }

    public void reset(){
        currentKey = -1;
        scroll = 0;
        lock = false;
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
    public boolean keyUp(int keycode) {
        currentKey = -1;
        return super.keyUp(keycode);
    }

    @Override
    public boolean keyDown(int keycode) {
        currentKey = keycode;
        return super.keyDown(keycode);
    }

    @Override
    public boolean scrolled(float amountX, float amountY){
        scroll = -amountY;
        return false;
    }

    public float getScrolled() {
        return scroll;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
