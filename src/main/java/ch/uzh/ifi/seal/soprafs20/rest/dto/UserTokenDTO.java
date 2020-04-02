package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class UserTokenDTO {

    private String token;
    private long id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id=id;
    }
}