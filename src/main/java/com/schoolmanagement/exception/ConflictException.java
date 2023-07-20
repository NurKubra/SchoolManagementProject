package com.schoolmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)                     //Exception ile birlikte Http Status code gonderiyoruz. ResponseEntity'nin http status koduyla farkli seyler!!
public class ConflictException extends RuntimeException{ //RuntimeException'dan extends etmek --> bu clasin custom bir exception classi oldugunu anlar


    public ConflictException(String message) {
        super(message);
    }
}
