import java.util.List;

public class UserPreferences {
    private List<Integer> favoriteGenres;
    private int preferredTime;
    private int preferredDevice;
    private String favoriteActors;
    private boolean prefersNewMovies;
    private String avoidGenres;

    public UserPreferences(List<Integer> favoriteGenres, int preferredTime, int preferredDevice, 
                           String favoriteActors, boolean prefersNewMovies, String avoidGenres) {
        this.favoriteGenres = favoriteGenres;
        this.preferredTime = preferredTime;
        this.preferredDevice = preferredDevice;
        this.favoriteActors = favoriteActors;
        this.prefersNewMovies = prefersNewMovies;
        this.avoidGenres = avoidGenres;
    }

    public List<Integer> getFavoriteGenres() {
        return favoriteGenres;
    }

    public int getPreferredTime() {
        return preferredTime;
    }

    public int getPreferredDevice() {
        return preferredDevice;
    }

    public String getFavoriteActors() {
        return favoriteActors;
    }

    public boolean prefersNewMovies() {
        return prefersNewMovies;
    }

    public String getAvoidGenres() {
        return avoidGenres;
    }
}
