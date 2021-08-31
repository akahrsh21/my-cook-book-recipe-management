package com.cookbook.services;


import com.cookbook.controllers.RecipeController;
import com.cookbook.exception.ServiceException;
import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import com.cookbook.recipeOperations.ServiceOperations;
import com.cookbook.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RecipeService implements ServiceOperations {
    Logger logger = LoggerFactory.getLogger(RecipeService.class);

    public static SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("dd‐MM‐yyyy HH:mm");
    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    Date date = new Date();

    @Override
    public RecipeControllerResponse persistListOfRecipe(List<Recipe> recipeList) {
        if (recipeList.isEmpty()) {
            throw new ServiceException("Input list cannot be empty");
        }

        for (Recipe recipeRecord : recipeList) {

            if (!ifRecipeRecordIsValid(recipeRecord)) {
                throw new ServiceException("Please validate the input parameters. Validation Failed");
            }
            recipeRecord.setCreatedDate(CREATE_DATE_FORMAT.format(date));
            try {
                logger.info("Started Inserting the Data to Database");
                recipeRepository.insert(recipeRecord);

            } catch (IllegalArgumentException e) {
                logger.debug("Please Check the input record: " + e.getMessage());
                throw new ServiceException("Please Check the input record: " + e.getMessage());
            } catch (Exception e) {
                logger.debug("Unhandled Exception in RECIPESERVICE persistListOfRecipe(): " + e.getMessage());
                throw new ServiceException("Unhandled Exception in RECIPESERVICE persistListOfRecipe(): " + e.getMessage());
            }
        }
        logger.info("Data inserted to the database");
        return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS, "Recipe List Persisted");
    }


    private boolean ifRecipeRecordIsValid(Recipe recipeRecord) {
        if (recipeRecord.getRecipeName() == null || recipeRecord.getRecipeType() == null || recipeRecord.getRecipeAuthor() == null
                || recipeRecord.getRecipeIngredients().isEmpty() || recipeRecord.getRecipeInstructions().getRecipeInstructions().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public RecipeControllerResponse persistRecipeData(Recipe recipe) {


        Recipe savedRecipe = null;

        recipe.setCreatedDate(CREATE_DATE_FORMAT.format(date));
        try {
            logger.info("Started Inserting the Data to Database");
            savedRecipe = recipeRepository.insert(recipe);
        } catch (IllegalArgumentException e) {
            logger.error("Saving data failed for the user: " + recipe.getRecipeAuthor() + " for the Recipe of: " + recipe.getRecipeName() + e.getMessage());
            throw new ServiceException("Please Check the input record: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Saving data failed for the user: " + recipe.getRecipeAuthor() + " for the Recipe of: " + recipe.getRecipeName() + e.getMessage());
            throw new ServiceException("Unhandled Exception in RECIPESERVICE findRecipeByOwner(): " + e.getMessage());
        }

        if (savedRecipe == null) {
            logger.error("Saving data failed. Response is Null for the user: " + recipe.getRecipeAuthor() + " for the Recipe of: " + recipe.getRecipeName());
            throw new ServiceException("PERSISTENCE FAILED");
        }

        logger.info("Started Inserting the Data to Database");
        return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS,
                "Recipe Record Saved Successfully");

    }


    @Override
    public List<Recipe> findAllRecipes() {
        logger.info("inside service layer");
        List<Recipe> serviceResponseList = new ArrayList<>();
        try {
            logger.info("Retrieving the data from DataBase");
            serviceResponseList = recipeRepository.findAll();
        } catch (Exception exception) {
            logger.debug("Unhandled Exception: " + exception.getMessage());
            throw new ServiceException("Exception!");
        }
        if (serviceResponseList.isEmpty()) {
            logger.debug("No Records found");
            throw new ServiceException("No Records found");
        }
        logger.info("returning the Recipe data from DataBase");
        return serviceResponseList;
    }


    @Override
    public List<Recipe> findRecipeByOwner(String recipeAuthor) {

        if (recipeAuthor.trim().isEmpty()) {

            throw new ServiceException("The search criteria cannot be empty!");
        }
        List<Recipe> responseList = new ArrayList<>();

        try {
            logger.info("Retrieving the Recipe data from DataBase based on the Author");
            responseList = recipeRepository.findByRecipeAuthor(recipeAuthor);
        } catch (IllegalArgumentException e) {
            logger.debug(("Validation Failed: " + e.getMessage()));
            throw new ServiceException("Please validate the input record: " + e.getMessage());
        } catch (Exception e) {
            logger.debug("Unhandled Exception in RECIPESERVICE findRecipeByOwner(): " + e.getMessage());
            throw new ServiceException("Unhandled Exception in RECIPESERVICE findRecipeByOwner(): " + e.getMessage());
        }

        if (responseList.isEmpty()) {
            throw new NoSuchElementException("No Recipes found for the given Author");
        }
        logger.info("Returning the Recipe Data of the user");
        return responseList;
    }


    @Override
    public RecipeControllerResponse deleteRecipeByID(String recipeID) {
        Recipe deletedRecipe = null;
        Optional<Recipe> recipeRecord;
        if (recipeID.trim().isEmpty()) {
            throw new ServiceException("The delete ID cannot be empty!");
        }

        try {
            recipeRecord = recipeRepository.findById(recipeID);
        } catch (Exception e) {
            logger.debug("Unhandled Exception in RECIPESERVICE deleteRecipeByID(): " + e.getMessage());
            throw new ServiceException("Unhandled Exception in RECIPESERVICE deleteRecipeByID(): " + e.getMessage());
        }

        if (recipeRecord.isEmpty()) {
            throw new ServiceException("No Recipes found for the given ID");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(recipeID));
        try {
            logger.info("Deleting the Recipe Record");
            deletedRecipe = mongoTemplate.findAndRemove(query, Recipe.class);
        } catch (IllegalArgumentException e) {
            logger.debug(("Validation Failed: " + e.getMessage()));
            throw new ServiceException("Please validate the delete parameters: " + e.getMessage());
        } catch (Exception e) {
            logger.debug("Unhandled Exception in RECIPESERVICE deleteRecipeByID(): " + e.getMessage());
            throw new ServiceException("Unhandled Exception in RECIPESERVICE deleteRecipeByID(): " + e.getMessage());
        }

        if (!ifRecipeRecordIsValid(deletedRecipe)) {
            throw new ServiceException("Recipe was not deleted");
        }
        logger.info("Deleted the Recipe Record");
        return new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_DELETION_STATUS_SUCCESS,
                "Recipe Record Deleted Successfully");

    }

    @Override
    public Recipe updateRecipeCollection(String recipeID, Recipe recipe) {
        Optional<Recipe> recipeRecord;
        if (recipeID.trim().isEmpty()) {
            throw new ServiceException("The search criteria cannot be empty!");
        }
        try {
            recipeRecord = recipeRepository.findById(recipeID);
        } catch (Exception e) {
            throw new ServiceException("Unhandled Exception in RECIPESERVICE updateRecipeCollection(): " + e.getMessage());
        }
        if (recipeRecord.isEmpty()) {
            throw new ServiceException("No Recipes found for the given ID");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(recipeID));
        Update update = buildUpdateParameters(recipe);
        try {
            logger.info("Updating the Recipe Data");
            return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Recipe.class);
        } catch (IllegalArgumentException e) {
            logger.debug(("Validation Failed: " + e.getMessage()));
            throw new ServiceException("Please validate the input record: " + e.getMessage());
        } catch (Exception e) {
            logger.debug("Unhandled Exception in RECIPESERVICE updateRecipeCollection(): " + e.getMessage());
            throw new ServiceException("Unhandled Exception in RECIPESERVICE updateRecipeCollection(): " + e.getMessage());
        }


    }

    private Update buildUpdateParameters(Recipe recipe) {
        Update update = new Update();
        update.set("recipeName", recipe.getRecipeName());
        update.set("recipeOwner", recipe.getRecipeAuthor());
        update.set("recipeType", recipe.getRecipeType());
        update.set("recipeSuitableServing", recipe.getRecipeSuitableServing());
        update.set("recipeIngredients", recipe.getRecipeIngredients());
        update.set("recipeInstructions", recipe.getRecipeInstructions());
        return update;
    }


}
