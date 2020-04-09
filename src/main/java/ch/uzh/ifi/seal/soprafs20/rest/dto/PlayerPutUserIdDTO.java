package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.Date;

public class PlayerPutUserIdDTO {

    private String token;
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username=username;
    }
}
