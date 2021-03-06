package ch.uzh.ifi.seal.soprafs20.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotAvailable extends RuntimeException {
    public PlayerNotAvailable(String message) {
        super(message);
    }
}