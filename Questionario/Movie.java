public class Movie {
    private String title;
    private String genre;
    private String time;
    private String device;

    public Movie(String title, String genre, String time, String device) {
        this.title = title;
        this.genre = genre;
        this.time = time;
        this.device = device;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getTime() {
        return time;
    }

    public String getDevice() {
        return device;
    }
}
