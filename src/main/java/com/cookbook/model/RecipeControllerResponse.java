package com.cookbook.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeControllerResponse {

    public enum StatusCode {
        RECIPE_SAVE_STATUS_SUCCESS,
        RECIPE_SAVE_STATUS_FAILURE,
        RECIPE_UPDATE_STATUS_SUCCESS,
        RECIPE_UPDATE_STATUS_FAILURE,
        RECIPE_RETRIEVAL_STATUS_FAILURE,
        RECIPE_DELETION_STATUS_FAILURE,
        RECIPE_DELETION_STATUS_SUCCESS
    }

    private StatusCode statusCode;
    private String responseMessage;

}
