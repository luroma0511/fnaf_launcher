package deluxe;

import java.io.Serializable;

public interface UserData extends Serializable {
    byte[] mainCastStar = new byte[5];
    byte[] shadowCastStar = new byte[5];
    byte[] hellCastStar = new byte[5];
    byte[] puppetTheaterStar = new byte[5];
    byte[] theaterTraumatizationStar = new byte[5];
}