package game.deluxe.state;

import java.util.List;
import java.util.Map;

public class Menu {
    private byte nightSelection = 0;
    private float staticAnimation;
    private boolean loaded;

    public Menu(){

    }

    public void update(Map<String, String> requests){
        if (!loaded){
            requests.put("LoadSpriteSheet", "Static");
            requests.put("PlayVideo", "Menu");
            loaded = true;
        }
    }
}
