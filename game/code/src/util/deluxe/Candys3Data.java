package util.deluxe;

public class Candys3Data {
    public int[] mainCastStars = new int[5];
    public int[] ratAndCatTheaterStars = new int[5];
    public int[] shadowCastStars = new int[5];
    public int[] theaterTraumaStars = new int[5];
    public int[] hellCastStars = new int[5];

    public void update(Candys3Data candys3Data){
        mainCastStars = candys3Data.mainCastStars;
        shadowCastStars = candys3Data.shadowCastStars;
        hellCastStars = candys3Data.hellCastStars;
    }
}
