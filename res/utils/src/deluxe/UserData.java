package deluxe;

import java.io.Serializable;

public interface UserData extends Serializable {
    byte[] mainCastStar = new byte[4];
    byte[] shadowCastStar = new byte[4];
    byte[] hellCastStar = new byte[4];
    byte[] nightmareDuo = new byte[4];
    byte[] theaterTraumatizationStar = new byte[4];
}