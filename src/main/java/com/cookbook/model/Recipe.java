package com.cookbook.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Recipe {


    @NotNull(message = "recipeName should have a valid entry")
    @NotBlank
    private String recipeName;

    @Indexed
    @Email(message = "recipeOwner should be a valid Email Address")
    @ApiModelProperty(notes = "Author's emailAddress")
    private String recipeAuthor;

    @NotNull(message = "recipeType should be valid (VEGETARIAN/NONVEGETARIAN)")
    private RecipeType recipeType;

    @NotNull
    @Min(value = 1, message = "recipeSuitableServing should be minimum 1")
    private int recipeSuitableServing;

    private String createdDate;

    @NotNull(message = "recipeIngredients should be added")
    @Size(min = 2, max = 20)
    private List<RecipeIngredients> recipeIngredients;

    @NotNull(message = "recipeInstructions should be added")

    private RecipeInstructions recipeInstructions;

}
