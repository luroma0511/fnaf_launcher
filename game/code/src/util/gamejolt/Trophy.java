package util.gamejolt;

public class Trophy {
    private int id;
    private String title;
    private String difficulty;
    private String description;
    private String image_url;
    private boolean achieved;

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAchieved() {
        return achieved;
    }

    @Override
    public String toString(){
        return "Trophy {" +
                "\n\tid: " + id +
                "\n\ttitle: " + title +
                "\n\tdifficulty: " + difficulty +
                "\n\tdescription: " + description +
                "\n\timage_url: " + image_url +
                "\n\tachieved: " + achieved +
                "\n}";
    }
}
