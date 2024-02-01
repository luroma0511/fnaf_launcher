package game.engine.util;

import game.deluxe.state.StateManager;

public class Engine {
    private final StateManager stateManager;
    private SpriteRequest spriteRequest;
    private float deltaTime;
    private long previousTime;
    private boolean lock;
    private boolean pressed;

    public Engine(){
        stateManager = new StateManager();
    }

    public void update(){
        if (pressed){
            System.out.println("Passed");
        }
        stateManager.update(this);
        previousTime = System.currentTimeMillis();
        lock = false;
        pressed = false;
    }

    public SpriteRequest createSpriteRequest(SpriteRequest currentRequest, String name, short width){
        if (currentRequest == null){
            return new SpriteRequest(name, width);
        }
        SpriteRequest tempRequest = currentRequest;
        currentRequest = new SpriteRequest(name, width);
        currentRequest.setNext(tempRequest);
        return currentRequest;
    }

    public SpriteRequest getSpriteRequest(){
        return spriteRequest;
    }

    public void setSpriteRequest(SpriteRequest request){
        spriteRequest = request;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    private void updateDeltaTime(){
        deltaTime = (float) (System.currentTimeMillis() - previousTime) / 1_000;
    }

    public float getDeltaTime() {
        if (!lock) {
            updateDeltaTime();
            lock = true;
        }
        return deltaTime;
    }
}
