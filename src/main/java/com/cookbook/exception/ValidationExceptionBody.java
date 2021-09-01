package com.cookbook.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationExceptionBody {
    private String message;
    private List<String> exceptionDetails;
}
