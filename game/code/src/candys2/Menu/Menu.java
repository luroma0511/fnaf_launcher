package candys2.Menu;

import core.Engine;

public class Menu {
    private float staticScreenFrame;

    private boolean loaded;

    public void load(Engine engine){
        loaded = true;
    }

    public void update(Engine engine){
        if (engine.appHandler.getRenderHandler().lock) return;
    }

    public void render(Engine engine){

    }
}
