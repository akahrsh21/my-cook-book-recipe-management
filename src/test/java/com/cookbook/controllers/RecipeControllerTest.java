
package com.cookbook.controllers;

import com.cookbook.model.*;
import com.cookbook.recipeOperations.ServiceOperations;
import com.cookbook.repository.RecipeRepository;
import com.cookbook.services.LoadUserService;
import com.cookbook.utils.SecurityUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @MockBean
    private ServiceOperations serviceOperations;
    @MockBean
    private RecipeRepository recipeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityUtility securityUtility;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private LoadUserService userService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Value("${auth.mySecretKey}")
    private static String mySecretKey;

    public static final long TOKEN_VALIDITY = 300000;
    Date date = new Date();

    public static SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("dd‐MM‐yyyy HH:mm");
    String createdDate = CREATE_DATE_FORMAT.format(date);

    Recipe mockRecipe1 = new Recipe("Veg Spicy Soup", "testEmail@test.com", RecipeType.VEGETARIAN, 1, createdDate,
            Arrays.asList(new RecipeIngredients("Veggies", "1 ltr"),
                    new RecipeIngredients("Spices", "50g")),
            new RecipeInstructions(Arrays.asList("Bring broth to a low boil.", "Add curry powder and salt.", "Cook lentils for 20 minutes.")));


    Recipe mockRecipe2 = new Recipe("Veg Creamy Soup", "testEmail@test.com", RecipeType.VEGETARIAN, 1, createdDate,
            Arrays.asList(new RecipeIngredients("Veggies", "1 ltr"),
                    new RecipeIngredients("Spices", "50g")),
            new RecipeInstructions(Arrays.asList("Bring broth to a low boil.", "Add curry powder and salt.", "Cook lentils for 20 minutes.")));

    List<Recipe> mockListRecipe = new ArrayList<>(Arrays.asList(mockRecipe1, mockRecipe2));
    private String URI = "/rest/v1/recipes/";
    private String testMongoId = "612684481df3dd0b6bdbae28";
    private String testRecipeOwner = "akahrsh@gmail.com";


    @BeforeEach
    public void setup() {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void getRecipeTest() throws Exception {


        Mockito.when(serviceOperations.findAllRecipes()).thenReturn(mockListRecipe);

        mockMvc.perform(MockMvcRequestBuilders.get(URI + "getAllRecipes"))

                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].recipeName", Matchers.is("Veg Creamy Soup")));

    }

    @Test
    public void getRecipeByAuthorTest() throws Exception {

        Mockito.when(serviceOperations.findRecipeByOwner(testRecipeOwner)).thenReturn(mockListRecipe);

        // MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/rest/v1/recipes/getAllRecipesByAuthor/" + testRecipeOwner);
        mockMvc.perform(MockMvcRequestBuilders
                .get(URI + "getAllRecipesByAuthor/" + testRecipeOwner))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].recipeName", Matchers.is("Veg Creamy Soup")));

    }

    @Test
    public void saveRecipeTest() throws Exception {

        Mockito.when(serviceOperations.persistRecipeData(mockRecipe1)).thenReturn(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS, "Recipe Record Saved Successfully"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI + "saveRecipe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe1));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_SAVE_STATUS_SUCCESS")));

    }


    @Test
    void deleteRecipeByID() throws Exception {

        Mockito.when(serviceOperations.deleteRecipeByID(testMongoId)).thenReturn(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_DELETION_STATUS_SUCCESS, "Recipe Record Deleted Successfully"));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(URI + "deleteRecipe/" + testMongoId);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_DELETION_STATUS_SUCCESS")));
    }

    @Test
    void updateRecipeById() throws Exception {

        Mockito.when(serviceOperations.updateRecipeCollection(testMongoId, mockRecipe2)).thenReturn(mockRecipe2);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(URI + "updateRecipe/" + testMongoId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe2));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("recipeName", Matchers.is("Veg Creamy Soup")));
    }

    @Test
    void saveRecipeList() throws Exception {

        Mockito.when(serviceOperations.persistListOfRecipe(mockListRecipe)).thenReturn(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS, "Recipe Record Saved Successfully"));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI + "saveRecipeList")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockListRecipe));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    void updateValidationFailures() throws Exception {

        Recipe mockRecipe = Recipe.builder()
                .recipeAuthor("TEST RECIPE")
                .recipeType(RecipeType.VEGETARIAN)
                .createdDate("28‐08‐2021 11:48")
                .recipeSuitableServing(2)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put(URI + "updateRecipe/" + testMongoId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe));


        mockMvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("Input validation failed")));


    }

    @Test
    void saveValidationFailures() throws Exception {

        Recipe mockRecipe = Recipe.builder()
                .recipeAuthor("TEST RECIPE")
                .recipeType(RecipeType.VEGETARIAN)
                .recipeSuitableServing(2)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(URI + "saveRecipe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe));


        mockMvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("Input validation failed")));


    }


}

