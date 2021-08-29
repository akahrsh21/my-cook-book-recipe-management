package com.cookbook.controllers;


import com.cookbook.exception.ServiceException;
import com.cookbook.model.AuthenticationRequest;
import com.cookbook.model.AuthenticationResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import com.cookbook.recipeOperations.ServiceOperations;
import com.cookbook.services.LoadUserService;
import com.cookbook.utils.SecurityUtility;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/v1/recipes/")
@Api(value = "Recipe Management Controller", description = "The Controllers for various Recipe Management actions")
public class RecipeController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    ServiceOperations serviceOperations;
    @Autowired
    private SecurityUtility securityUtility;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoadUserService userService;

    @ApiOperation(value = "Save the Recipe created by the User to Backend")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 201, message = "RECIPE INSERTION SUCCESS"),
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION")
    })
    @PostMapping(value = "/saveRecipe", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveRecipe(@Valid @RequestBody Recipe recipe) {
        RecipeControllerResponse serviceResponse = null;
        try {
            logger.info("Inside saveRecipe controller");
            serviceResponse = serviceOperations.persistRecipeData(recipe);

        } catch (ServiceException serviceException) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_FAILURE, serviceException.getErrorMessage()), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_FAILURE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        }

        logger.info("Returing the save Recipe Response");
        return new ResponseEntity<>(serviceResponse, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Retrieve the Recipe by its Author")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "RESOURCE NOT FOUND"),
            @ApiResponse(code = 200, message = "RECIPE RETRIEVAL SUCCESS")
    })
    @GetMapping(value = "/getAllRecipesByAuthor/{recipeAuthor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecipeByUser(@PathVariable("recipeAuthor") String recipeAuthor) {
        List<Recipe> recipeList = new ArrayList<>();
        try {
            logger.info("In getRecipeByUser...");
            recipeList = serviceOperations.findRecipeByOwner(recipeAuthor);
        } catch (ServiceException serviceException) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_RETRIEVAL_STATUS_FAILURE, serviceException.getErrorMessage()), HttpStatus.BAD_REQUEST);
        }
        logger.info("Returning all the available recipes for an author");
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }


    @ApiOperation(value = "Delete Recipe using mongoID")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE DELETION SUCCESS")
    })
    @DeleteMapping(value = "/deleteRecipe/{recipeID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRecipebyID(@PathVariable("recipeID") String recipeID) {
        RecipeControllerResponse serviceResponse = null;
        try {
            logger.info("Inside deleteRecipebyID");
            serviceResponse = serviceOperations.deleteRecipeByID(recipeID);
        } catch (ServiceException serviceException) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_DELETION_STATUS_FAILURE, serviceException.getErrorMessage()), HttpStatus.BAD_REQUEST);
        }
        logger.info("Returning the DeleteRecipeResponse");
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }


    @ApiOperation(value = "Retrieve all the available recipes from the DB")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE RETRIEVAL SUCCESS"),
            @ApiResponse(code = 404, message = "RECIPES LIST EMPTY")
    })
    @GetMapping(value = "/getAllRecipes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRecipes() {
        logger.info("Calling findAllRecipes");
        List<Recipe> recipeList = new ArrayList<>();
        try {
            logger.info("Inside getAllRecipes");
            recipeList = serviceOperations.findAllRecipes();
        } catch (ServiceException exception) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_RETRIEVAL_STATUS_FAILURE, exception.getErrorMessage()), HttpStatus.NOT_FOUND);

        }
        logger.info("Returning the RecipeList");
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }


    @ApiOperation(value = "Update a particular Recipe using mongoID")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE UPDATE SUCCESS"),
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION")
    })
    @PutMapping(value = "/updateRecipe/{recipeID}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRecipeById(@PathVariable("recipeID") String recipeID, @Valid @RequestBody Recipe recipe) {

        Recipe updatedRecipe;
        try {
            logger.info("Inside updateRecipeById method ");
            updatedRecipe = serviceOperations.updateRecipeCollection(recipeID, recipe);
        } catch (ServiceException serviceException) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_UPDATE_STATUS_FAILURE, serviceException.getErrorMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_UPDATE_STATUS_FAILURE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        }

        logger.info("Returning the update response");
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);


    }

    @ApiOperation(value = "Save multiple Recipes as a list to the Backend")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 201, message = "RECIPE INSERTION SUCCESS"),
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION")
    })
    @PostMapping(value = "/saveRecipeList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeControllerResponse> saveRecipe(@Valid @RequestBody List<Recipe> recipeList) {
        RecipeControllerResponse serviceResponse;
        try {
            logger.info("Inside saveRecipeList method");
            serviceResponse = serviceOperations.persistListOfRecipe(recipeList);
        } catch (ServiceException serviceException) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_FAILURE, serviceException.getErrorMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_FAILURE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        logger.info("Returning the response after saving list of recipes");
        return new ResponseEntity<>(serviceResponse, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Authenticate the User and get the authentication token")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE INSERTION SUCCESS"),
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION")
    })
    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest authRequest) throws Exception {

        try {
            logger.info("Authenticating the user");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Login Failed: Credentials Invalid", e);
        }

        final UserDetails userDetails
                = userService.loadUserByUsername(authRequest.getUsername());


        final String token = securityUtility.generateToken(userDetails);
        logger.info("Returning the Authentication Token");
        return new AuthenticationResponse(token);
    }
}
