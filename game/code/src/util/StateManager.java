package util;

public abstract class StateManager {
    private int state;
    private int prevState;

    public StateManager(){
        prevState = -1;
    }

    public void transitionState(){
        prevState = state;
    }

    public boolean sameStates() {
        return prevState == state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
