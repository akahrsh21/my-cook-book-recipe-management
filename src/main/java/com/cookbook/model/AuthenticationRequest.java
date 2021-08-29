package com.cookbook.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AuthenticationRequest {
    private String username;
    private String password;
}
