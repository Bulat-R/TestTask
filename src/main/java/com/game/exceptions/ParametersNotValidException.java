package com.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParametersNotValidException extends RuntimeException {
    public ParametersNotValidException() {
        super("Player parameters not valid");
    }
}
