package game.engine.util;

import game.deluxe.state.StateManager;

public class Engine {
    private final StateManager stateManager;
    private float deltaTime;
    private boolean lock;
    private long previousTime;

    public Engine(){
        stateManager = new StateManager();
    }

    public void update(){
        stateManager.update();
        previousTime = System.currentTimeMillis();
        lock = false;
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
