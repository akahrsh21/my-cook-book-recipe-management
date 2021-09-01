package com.cookbook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
    @NotNull
    private int recipeID;

    @Size(min = 3, max = 25, message = "Enter a Valid Name")
    @NotEmpty(message = "Please enter the Recipe Name")
    private String recipeName;


    @Email(message = "Email must be a valid one")
    private String recipeAuthor;

    @NotNull
       private boolean veganRecipe;

    @NotNull
    @Min(value = 1, message = "recipeSuitableServing should be minimum 1")
    private int recipeSuitableServing;

    private String createdDate;

    @NotEmpty
    private List<String> recipeIngredients;

    @NotEmpty
    private List<String> recipeInstructions;

}
