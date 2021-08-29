package com.cookbook.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDeniedResponse {

    private String errorMessage;
    private LocalDateTime timestamp;

}
