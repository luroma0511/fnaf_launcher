package util.deluxe;

import com.badlogic.gdx.Input;

public class Candys2Data {
    public StarData[] newCandysShowdownStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] shadowShowdownStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] ratAndCatTheaterStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] theaterTraumaStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};

    public int flashKey = Input.Keys.SPACE;
    public boolean infiniteNight;
    public boolean perspective;

    public void update(Candys2Data candys2Data){
        newCandysShowdownStars = candys2Data.newCandysShowdownStars;
        shadowShowdownStars = candys2Data.shadowShowdownStars;
        ratAndCatTheaterStars = candys2Data.ratAndCatTheaterStars;
        theaterTraumaStars = candys2Data.theaterTraumaStars;

        flashKey = candys2Data.flashKey;
        infiniteNight = candys2Data.infiniteNight;
        perspective = candys2Data.perspective;
    }
}
