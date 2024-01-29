package game.deluxe.state;

import java.util.List;
import java.util.Map;

import game.deluxe.state.night.CustomNight;
import game.deluxe.state.night.RatCatTheater;
import game.deluxe.state.night.ShadowNight;

public class Game {
    private final ShadowNight shadowNight;
    private final CustomNight customNight;
    private final RatCatTheater ratCatTheater;
    private byte nightState;

    public Game(){
        shadowNight = new ShadowNight();
        customNight = new CustomNight();
        ratCatTheater = new RatCatTheater();
    }

    public void setNightState(byte nightState){
        this.nightState = nightState;
    }

    public void update(Map<String, String> requests){
        if (nightState == 1){
            ratCatTheater.update();
        } else if (nightState == 2){
            shadowNight.update();
        } else if (nightState == 3){
            customNight.update();
        }
    }
}
