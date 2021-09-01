package com.cookbook.services;

import com.cookbook.DTO.RecipeDTO;
import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRecipeService {

    RecipeControllerResponse persistRecipeData(Recipe recipe);

    List<Recipe> findAllRecipes();

    List<Recipe> findByRecipeAuthor(String recipeOwner);

    RecipeControllerResponse deleteRecipeByID(Integer recipeID);

    RecipeControllerResponse updateRecipeRecord(Integer recipeID, Recipe recipeDTO);

    RecipeControllerResponse persistListOfRecipe(List<Recipe> recipeList);
}
