package game.engine.util;

import java.util.HashMap;
import java.util.Map;

import game.deluxe.state.StateManager;

public class Engine {
    private final StateManager stateManager;
    private final Map<String, String> requests;
    private float deltaTime;
    private boolean lock;
    private long previousTime;

    public Engine(){
        stateManager = new StateManager();
        requests = new HashMap<>();
    }

    public void update(){
        stateManager.update(requests);
        previousTime = System.currentTimeMillis();
        lock = false;
    }

    public Map<String, String> getRequests(){
        return requests;
    }

    public void clearRequest(){
        requests.clear();
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
