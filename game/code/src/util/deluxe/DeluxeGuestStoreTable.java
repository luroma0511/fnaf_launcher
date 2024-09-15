package util.deluxe;

import util.GuestScoreTable;

public class DeluxeGuestStoreTable extends GuestScoreTable {
    private final Score mainCastScore = new Score();
    private final Score shadowCastScore = new Score();
    private final Score hellCastScore = new Score();

    public static class Score {
        private String flashPoints = "0 Points";
        private String timeScore = "0:00 seconds";

        public String getFlashPoints() {
            return flashPoints;
        }

        public String getTimeScore() {
            return timeScore;
        }
    }

    public Score getScore(int id) {
        if (id == 0) return mainCastScore;
        else if (id == 1) return shadowCastScore;
        return hellCastScore;
    }

    public void setScore(int id, String flashPoints, String timeScore){
        Score score = getScore(id);
        score.flashPoints = flashPoints;
        score.timeScore = timeScore;
    }
}