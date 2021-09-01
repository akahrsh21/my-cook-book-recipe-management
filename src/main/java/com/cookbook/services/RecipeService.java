package com.cookbook.services;


import com.cookbook.DTO.RecipeDTO;
import com.cookbook.exception.NoDataFoundException;
import com.cookbook.exception.ServiceException;
import com.cookbook.mapper.DTOMapper;
import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import com.cookbook.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService implements IRecipeService {
    Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public static SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("dd‐MM‐yyyy HH:mm");
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    DTOMapper mapper;
    Date date = new Date();


    @Override
    public RecipeControllerResponse persistListOfRecipe(List<Recipe> recipeList) {
        List<Recipe> savedList = new ArrayList<>();
        if (recipeList.isEmpty()) {
            throw new ServiceException("Input list cannot be empty");
        }
        try {
            savedList = recipeRepository.saveAll(recipeList);
        } catch (JpaSystemException exception) {
            throw new ServiceException(exception.getMessage());
        } catch (Exception e) {
            throw new ServiceException("Unhandled Exception in persistRecipeData() ");
        }
        if (savedList.isEmpty()) {
            throw new ServiceException("PERSISTENCE FAILED");
        }

        logger.info("Data inserted to the database");
        return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS, "Recipe Record Saved Successfully");
    }


    @Override
    public RecipeControllerResponse persistRecipeData(Recipe recipe) {
        Recipe savedRecipe;
        try {
            savedRecipe = recipeRepository.save(recipe);
        } catch (JpaSystemException exception) {
            throw new ServiceException(exception.getMessage());
        } catch (Exception e) {
            throw new ServiceException("Unhandled Exception in persistRecipeData() ");
        }
        if (savedRecipe == null) {
            throw new ServiceException("PERSISTENCE FAILED");
        }

        return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS,
                "Recipe Record Saved Successfully");
    }

    @Override
    public List<Recipe> findAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try {

            recipes = recipeRepository.findAll();

        } catch (JpaSystemException exception) {
            throw new ServiceException(exception.getMessage());
        }

        if (recipes.isEmpty()) {
            throw new NoDataFoundException("No Recipe Records in the DataBase");
        }
        return recipes;
    }


    @Override
    public List<Recipe> findByRecipeAuthor(String recipeAuthor) {

        List<Recipe> recipes = new ArrayList<>();
        try {

            recipes = recipeRepository.findByRecipeAuthor(recipeAuthor);

        } catch (JpaSystemException exception) {
            throw new ServiceException(exception.getMessage());
        }

        if (recipes.isEmpty()) {
            throw new NoDataFoundException("No Recipes found for the given Author");
        }
        return recipes;
    }


    @Override
    public RecipeControllerResponse deleteRecipeByID(Integer recipeID) {

        if (recipeRepository.findById(recipeID).isEmpty()) {
            throw new NoDataFoundException("No Data with the ID to delete");
        }
        try {
            recipeRepository.deleteById(recipeID);
        } catch (JpaSystemException exception) {
            throw new ServiceException(exception.getMessage());
        }
        return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_DELETION_STATUS_SUCCESS,
                "Recipe Record Deleted Successfully");

    }


    @Override
    public RecipeControllerResponse updateRecipeRecord(Integer recipeID, Recipe requestRecipe) {

        Optional<Recipe> recipeData = recipeRepository.findById(recipeID);

        System.out.println(recipeID+"---------------");
        if (recipeData.isPresent()) {

            Recipe recipeToUpdate = recipeData.get();
            recipeToUpdate.setRecipeName(requestRecipe.getRecipeName());
            recipeToUpdate.setRecipeAuthor(requestRecipe.getRecipeAuthor());
            recipeToUpdate.setRecipeSuitableServing(requestRecipe.getRecipeSuitableServing());
            recipeToUpdate.setRecipeInstructions(requestRecipe.getRecipeInstructions());
            recipeToUpdate.setRecipeIngredients(requestRecipe.getRecipeIngredients());
            recipeToUpdate.setVeganRecipe(requestRecipe.isVeganRecipe());

            try {
                recipeRepository.save(recipeToUpdate);
            } catch (JpaSystemException exception) {
                throw new ServiceException(exception.getMessage());
            }

            return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_UPDATE_STATUS_SUCCESS,
                    "Recipe Record updated Successfully");

        } else {
            throw new NoDataFoundException("No Data with the ID to update");
        }


    }


}
