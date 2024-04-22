package data;

import java.io.Serializable;

public class UserData implements Serializable {
    private final byte[] mainCastStar;
    private final byte[] shadowCastStar;
    private final byte[] hellCastStar;
    private final byte[] puppetTheaterStar;
    private final byte[] theaterTraumatizationStar;

    public UserData(){
        mainCastStar = new byte[5];
        shadowCastStar = new byte[5];
        hellCastStar = new byte[5];
        puppetTheaterStar = new byte[5];
        theaterTraumatizationStar = new byte[5];
    }

    public byte[] getMainCastStar() {
        return mainCastStar;
    }

    public byte[] getShadowCastStar() {
        return shadowCastStar;
    }

    public byte[] getHellCastStar() {
        return hellCastStar;
    }

    public byte[] getPuppetTheaterStar() {
        return puppetTheaterStar;
    }

    public byte[] getTheaterTraumatizationStar() {
        return theaterTraumatizationStar;
    }
}
