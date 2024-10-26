package util.deluxe;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class DeluxeGuestStoreTable implements InstanceCreator<DeluxeGuestStoreTable> {
    private Candys3Score mainCastScore;
    private Candys3Score shadowCastScore;
    private Candys3Score hellCastScore;

    private Candys2Score candysShowdown;
    private Candys2Score ratAndCatTheater2Score;
    private Candys2Score shadowShowdown;
    private Candys2Score theaterTraumatization2Score;

    public static class Candys3Score {
        private String flashPoints = "0 Points";
        private String timeScore = "0:00 seconds";

        public String getFlashPoints() {
            return flashPoints;
        }

        public String getTimeScore() {
            return timeScore;
        }
    }

    public static class Candys2Score {
        private String cameraFlashes = "0 Flashes";
        private String timeScore = "0:00 seconds";

        public String getCameraFlashes() {
            return cameraFlashes;
        }

        public String getTimeScore() {
            return timeScore;
        }
    }

    public void init(){
        if (mainCastScore == null) mainCastScore = new Candys3Score();
        if (shadowCastScore == null) shadowCastScore = new Candys3Score();
        if (hellCastScore == null) hellCastScore = new Candys3Score();

        if (candysShowdown == null) candysShowdown = new Candys2Score();
        if (shadowShowdown == null) shadowShowdown = new Candys2Score();
        if (ratAndCatTheater2Score == null) ratAndCatTheater2Score = new Candys2Score();
        if (theaterTraumatization2Score == null) theaterTraumatization2Score = new Candys2Score();
    }

    public Candys2Score getCandys2Score(int id) {
        if (id == 0) return candysShowdown;
        else if (id == 1) return ratAndCatTheater2Score;
        else if (id == 2) return shadowShowdown;
        return theaterTraumatization2Score;
    }

    public Candys3Score getCandys3Score(int id) {
        if (id == 0) return mainCastScore;
        else if (id == 1) return shadowCastScore;
        return hellCastScore;
    }

    public void setCandys2Score(int id, String cameraFlashes, String timeScore){
        Candys2Score score = getCandys2Score(id);
        score.cameraFlashes = cameraFlashes;
        score.timeScore = timeScore;
    }

    public void setCandys3Score(int id, String flashPoints, String timeScore){
        Candys3Score score = getCandys3Score(id);
        score.flashPoints = flashPoints;
        score.timeScore = timeScore;
    }

    @Override
    public DeluxeGuestStoreTable createInstance(Type type) {
        return new DeluxeGuestStoreTable();
    }
}