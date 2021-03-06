package com.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdNotValidException extends RuntimeException {
    public IdNotValidException() {
        super("Player id not valid");
    }
}