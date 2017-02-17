/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifysystem.logic;

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
