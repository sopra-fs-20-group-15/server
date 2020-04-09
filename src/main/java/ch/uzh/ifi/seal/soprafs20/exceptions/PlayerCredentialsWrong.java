package ch.uzh.ifi.seal.soprafs20.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PlayerCredentialsWrong extends RuntimeException {
    public PlayerCredentialsWrong(String message) {
        super(message);
    }
}