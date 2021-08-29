package com.cookbook.recipeOperations;


import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceOperations {

    RecipeControllerResponse persistRecipeData(Recipe recipe) ;

    List<Recipe> findAllRecipes();

    List<Recipe> findRecipeByOwner(String recipeOwner) ;

    RecipeControllerResponse deleteRecipeByID(String recipeID) ;

    Recipe updateRecipeCollection(String recipeID, Recipe recipe) ;

    RecipeControllerResponse persistListOfRecipe(List<Recipe> recipeList) ;
}
