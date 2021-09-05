package com.cookbook.controllers;


import com.cookbook.DTO.RecipeDTO;
import com.cookbook.mapper.DTOMapper;
import com.cookbook.model.AuthenticationRequest;
import com.cookbook.model.AuthenticationResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import com.cookbook.services.IRecipeService;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/recipe-management")
@Api(value = "Recipe Management Controller", description = "The Controllers for various Recipe Management actions")
public class RecipeController {
    Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    IRecipeService recipeService;


    @Autowired
    DTOMapper mapper;

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
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION"),
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED")
    })
    @PostMapping(value = "/recipe", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<?> saveRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {

        Recipe recipeRecord = mapper.convertDTOtoEntity(recipeDTO);
        RecipeControllerResponse serviceResponse = recipeService.persistRecipeData(recipeRecord);
        logger.info("Returning the save Recipe Response");
        return new ResponseEntity<>(serviceResponse, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Retrieve the Recipe by its Author")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "RESOURCE NOT FOUND"),
            @ApiResponse(code = 200, message = "RECIPE RETRIEVAL SUCCESS"),
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED")
    })
    @GetMapping(value = "/recipes/{recipeAuthor}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecipeByUser(@PathVariable("recipeAuthor") String recipeAuthor) {

        List<Recipe> recipeList = recipeService.findByRecipeAuthor(recipeAuthor);
        List<RecipeDTO> recipeDTOList = recipeList.stream().map(x -> mapper.convertToDTO(x)).collect(Collectors.toList());
        return new ResponseEntity<>(recipeDTOList, HttpStatus.OK);
    }


    @ApiOperation(value = "Retrieve all the available recipes from the DB")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE RETRIEVAL SUCCESS"),
            @ApiResponse(code = 404, message = "RECIPES LIST EMPTY"),
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED")
    })
    @GetMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRecipes() {

        List<Recipe> recipeList = recipeService.findAllRecipes();
        List<RecipeDTO> recipeDTOList = recipeList.stream().map(x -> mapper.convertToDTO(x)).collect(Collectors.toList());
        return new ResponseEntity<>(recipeDTOList, HttpStatus.OK);
    }


    @ApiOperation(value = "Delete Recipe using RecipeID")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE DELETION SUCCESS"),
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED")
    })
    @DeleteMapping(value = "/recipe/{recipeID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRecipeByID(@PathVariable("recipeID") Integer recipeID) {

        RecipeControllerResponse recipeControllerResponse = recipeService.deleteRecipeByID(recipeID);

        return new ResponseEntity<>(recipeControllerResponse, HttpStatus.OK);
    }


    @ApiOperation(value = "Update a particular Recipe using RecipeID")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 200, message = "RECIPE UPDATE SUCCESS"),
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION"),
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED")
    })
    @PutMapping(value = "/recipe/{recipeID}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRecipeById(@PathVariable("recipeID") Integer recipeID, @Valid @RequestBody RecipeDTO recipeDTO) {

        Recipe recipeToBeUpdated = mapper.convertDTOtoEntity(recipeDTO);
        RecipeControllerResponse recipeControllerResponse = recipeService.updateRecipeRecord(recipeID, recipeToBeUpdated);
        return new ResponseEntity<>(recipeControllerResponse, HttpStatus.OK);


    }


    @ApiOperation(value = "Save multiple Recipes as a list to the Backend")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "BAD REQUEST"),
            @ApiResponse(code = 201, message = "RECIPE INSERTION SUCCESS"),
            @ApiResponse(code = 500, message = "UNHANDLED EXCEPTION"),
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED")
    })
    @PostMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeControllerResponse> saveRecipe(@Valid @RequestBody List<RecipeDTO> inputRecipeList) {

        List<Recipe> recipeList = inputRecipeList.stream().map(x -> mapper.convertDTOtoEntity(x)).collect(Collectors.toList());
        RecipeControllerResponse recipeControllerResponse =  recipeService.persistListOfRecipe(recipeList);
        return new ResponseEntity<>(recipeControllerResponse, HttpStatus.CREATED);
    }


    @ApiOperation(value = "Authenticate the User and get the authentication token")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "AUTHENTICATION FAILED"),
            @ApiResponse(code = 200, message = "TOKEN GENERATED")
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
            logger.info("Auth Failed for the user: " + authRequest.getUsername());
            throw new Exception("Login Failed: Credentials Invalid", e);
        }

        final UserDetails userDetails
                = userService.loadUserByUsername(authRequest.getUsername());


        final String token = securityUtility.generateToken(userDetails);
        logger.info("Returning the Authentication Token");
        return new AuthenticationResponse(token);
    }


}
