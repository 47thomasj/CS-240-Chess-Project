package models;

import java.util.UUID;

public class Authtoken {
    private String authToken;
    public Authtoken() {
        this.authToken = UUID.randomUUID().toString();
    }
    
    public String getAuthToken() {
        return authToken;
    }
}
