package game.engine.util;

import game.deluxe.state.StateManager;

public class Engine {
    private final StateManager stateManager;
    private SpriteRequest spriteRequest;
    private float deltaTime;
    private boolean lock;
    private long previousTime;

    public Engine(){
        stateManager = new StateManager();
    }

    public void update(){
        stateManager.update(this);
        previousTime = System.currentTimeMillis();
        lock = false;
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

    public void next(){
        spriteRequest = spriteRequest.getNext();
    }

    public StateManager getStateManager() {
        return stateManager;
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
