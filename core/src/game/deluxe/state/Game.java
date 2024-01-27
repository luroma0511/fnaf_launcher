package game.deluxe.state;

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
        nightState = -1;
    }

    public void setNightState(byte nightState){
        this.nightState = nightState;
    }

    public void update(){

    }
}
