

package com.cookbook.controllers;

import com.cookbook.DTO.RecipeDTO;
import com.cookbook.mapper.DTOMapper;
import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import com.cookbook.repository.RecipeRepository;
import com.cookbook.services.IRecipeService;
import com.cookbook.services.LoadUserService;
import com.cookbook.utils.SecurityUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {


    @MockBean
    private RecipeRepository recipeRepository;

    @MockBean
    private IRecipeService recipeService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DTOMapper mapper;

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

    Recipe mockRecipe1 = new Recipe(1, "Veg Spicy Soup", "testEmail@test.com",
            true,
            1,
            createdDate,
            "1 1/2 pounds Yukon Gold or baby potatoes,1 1/2 tsp neutral oil like grape seed oil, vegetable oil or avocado oil,3/4 tsp fine sea salt,1/2 tsp fresh ground black pepper,1/2 tsp smoked paprika,Fresh herbs like chopped chives or parsley or mint for serving as required",
            "Heat the oven to 425 degrees Fahrenheit and line a large baking sheet with parchment paper or a silicone baking mat.,Chop the potatoes into 1-inch chunks and then add to a medium bowl along with the oil salt pepper and smoked paprika. Toss well and then tumble the seasoned potatoes onto the prepared baking sheet. Spread the potatoes out into one layer.,Roast the potatoes until golden brown on the outside and tender on the inside for 25 to 35 minutes. After 20 minutes check the potatoes and use a flat spatula to stir them for the most even browning.,Scatter fresh herbs over the warm potatoes and serve.");

    Recipe mockRecipe2 = new Recipe(2, "Banana Smoothie", "testEmail@test.com",
            true,
            1,
            createdDate,
            "1 1/2 pounds Yukon Gold or baby potatoes,1 1/2 tsp neutral oil like grape seed oil, vegetable oil or avocado oil,3/4 tsp fine sea salt,1/2 tsp fresh ground black pepper,1/2 tsp smoked paprika,Fresh herbs like chopped chives or parsley or mint for serving as required",
            "Heat the oven to 425 degrees Fahrenheit and line a large baking sheet with parchment paper or a silicone baking mat.,Chop the potatoes into 1-inch chunks and then add to a medium bowl along with the oil salt pepper and smoked paprika. Toss well and then tumble the seasoned potatoes onto the prepared baking sheet. Spread the potatoes out into one layer.,Roast the potatoes until golden brown on the outside and tender on the inside for 25 to 35 minutes. After 20 minutes check the potatoes and use a flat spatula to stir them for the most even browning.,Scatter fresh herbs over the warm potatoes and serve.");


    List<Recipe> mockListRecipe = new ArrayList<>(Arrays.asList(mockRecipe1, mockRecipe2));
    private String URI = "/rest/recipe-management/";

    private String testRecipeOwner = "testEmail@test.com";


    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @DisplayName("Given The User needs to update the recipe Record, When The request fails the Input validation, Then The response should return the code '400' ")
    void testValidationFailuresUpdateRecipe() throws Exception {

        Recipe mockRecipe = Recipe.builder()
                .recipeAuthor("TEST RECIPE")
                .veganRecipe(true)
                .createdDate("28‐08‐2021 11:48")
                .recipeSuitableServing(2)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .put(URI + "recipe/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe));

        mockMvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest());


    }

    @Test
    @DisplayName("Given The User needs to save the recipe Record, When The request fails the Input validation, Then The response should return the code '400' ")
    void testValidationFailuresSaveRecipe() throws Exception {

        Recipe mockRecipe = Recipe.builder()
                .recipeAuthor("TEST RECIPE")
                .veganRecipe(true)
                .recipeSuitableServing(2)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(URI + "recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRecipe));


        mockMvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isBadRequest());


    }

    @Test
    @DisplayName("Given The User needs to save the recipe Record, When The request is valid, Then The response should return the response code '201'")
    void testSuccessResponseSaveRecipe() throws Exception {

        RecipeDTO dto = new RecipeDTO(1, "TEST RECIPE", "akahrsh@gmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI + "recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Given The User needs to save the recipe Record, When The request is not valid , Then The response should return the code '400'")
    void testValidationFailureResponseSaveRecipe() throws Exception {


        RecipeDTO dto = new RecipeDTO(1, "R", "akahrshgmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI + "recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Given The User needs to retrieve all the Recipe Records, When The request is valid, Then The response should return the code '200'")
    void testSuccessResponseGetAllRecipes() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URI + "recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Given The User needs to retrieve all the Recipe Records by Author Name, When The request is valid, Then The response should return the code '200'")
    void testSuccessResponseGetRecipeByAuthor() throws Exception {


        Mockito.when(recipeService.findByRecipeAuthor(testRecipeOwner)).thenReturn(mockListRecipe);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URI + "recipes/" + testRecipeOwner)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    @DisplayName("Given The User needs to retrieve all the Recipe Records by Author Name" +
            "When The request is valid And there are no records for the user, Then the Response should return the code 404")
    void testNoDataFoundResponseGetRecipeByAuthor() throws Exception {

        Mockito.when(recipeService.findByRecipeAuthor(testRecipeOwner)).thenReturn(mockListRecipe);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URI + "recipes/" + "INVALIDEMAILADDRESS")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("Given The User needs to Delete a Recipe Record, When The request is valid, Then the Response should return the code 200")
    void testSuccessResponseDeleteRecipe() throws Exception {

        Mockito.when(recipeService.deleteRecipeByID(1))
                .thenReturn(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_DELETION_STATUS_SUCCESS, "Recipe Record Deleted Successfully"));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(URI + "recipe/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_DELETION_STATUS_SUCCESS")));

    }

    @Test
    @DisplayName("Given The User needs to update a Recipe Record, When The request is valid, Then the Response should return the code 200 ")
    void testSuccessResponseUpdateRecipe() throws Exception {

        RecipeDTO dto = new RecipeDTO(1, "TEST RECIPE", "akahrsh@gmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(URI + "recipe/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("Given The User needs to Save Recipe records as a list, When The request is valid, Then the Response should return the code 201 ")
    void testSaveRecipeListSuccess() throws Exception {


        RecipeDTO dto1 = new RecipeDTO(1, "TEST RECIPE", "akahrsh@gmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        RecipeDTO dto2 = new RecipeDTO(2, "TEST RECIPE2", "akahrsh@test.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));


        List<Recipe> recipeList = new ArrayList<>(Arrays.asList(mapper.convertDTOtoEntity(dto1), mapper.convertDTOtoEntity(dto2)));

        Mockito.when(recipeService.persistListOfRecipe(recipeList)).thenReturn(new RecipeControllerResponse(RecipeControllerResponse.StatusCode.RECIPE_SAVE_STATUS_SUCCESS, "Recipe Record Saved Successfully"));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI + "recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeList));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }


}


