package util.deluxe;

public class Candys3Data {
    public StarData[] mainCastStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] ratAndCatTheaterStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] shadowCastStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] theaterTraumaStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] hellCastStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};

    public void update(Candys3Data candys3Data){
        mainCastStars = candys3Data.mainCastStars;
        shadowCastStars = candys3Data.shadowCastStars;
        hellCastStars = candys3Data.hellCastStars;
        ratAndCatTheaterStars = candys3Data.ratAndCatTheaterStars;
        theaterTraumaStars = candys3Data.theaterTraumaStars;
    }
}
