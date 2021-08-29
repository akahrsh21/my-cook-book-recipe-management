package com.cookbook.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AuthenticationResponse {
    private String authToken;
}
