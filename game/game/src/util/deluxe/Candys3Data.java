package util.deluxe;

public class Candys3Data {
    public int mainCastStar;
    public int shadowCastStar;
    public int hellCastStar;
    public int mainCastStarRainbow;
    public int shadowCastStarRainbow;
    public int hellCastStarRainbow;

    public void update(Candys3Data candys3Data){
        mainCastStar = candys3Data.mainCastStar;
        shadowCastStar = candys3Data.shadowCastStar;
        hellCastStar = candys3Data.hellCastStar;
        mainCastStarRainbow = candys3Data.mainCastStarRainbow;
        shadowCastStarRainbow = candys3Data.shadowCastStarRainbow;
        hellCastStarRainbow = candys3Data.hellCastStarRainbow;
    }
}
