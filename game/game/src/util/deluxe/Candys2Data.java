package util.deluxe;

public class Candys2Data {
    public StarData[] newCandysShowdownStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] shadowShowdownStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] ratAndCatTheaterStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};
    public StarData[] theaterTraumaStars = new StarData[]{new StarData(), new StarData(), new StarData(), new StarData(), new StarData()};

    public void update(Candys2Data candys2Data){
        newCandysShowdownStars = candys2Data.newCandysShowdownStars;
        shadowShowdownStars = candys2Data.shadowShowdownStars;
        ratAndCatTheaterStars = candys2Data.ratAndCatTheaterStars;
        theaterTraumaStars = candys2Data.theaterTraumaStars;
    }
}
