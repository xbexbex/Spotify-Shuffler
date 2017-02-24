package spotifysystem.logic;
/**
     * Custom playlist class for saving data
     *
     */

public class Playlist {
    private String name;
    private String id;
    
    public Playlist(String n, String i) {
        name = n;
        id = i;
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
}
