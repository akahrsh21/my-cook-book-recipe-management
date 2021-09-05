package com.cookbook.controllers;

import com.cookbook.DTO.RecipeDTO;
import com.cookbook.model.AuthenticationRequest;
import com.cookbook.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecipeControllerTests {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    RecipeRepository recipeRepository;

    private static String authJWTToken = "";

    @Autowired
    private ObjectMapper objectMapper;

    String URI = "/rest/recipe-management/";

    @Value("${auth.token.expiry.milliseconds}")
    private long TOKEN_VALIDITY;

    @Value("${auth.mySecretKey}")
    private String mySecretKey;


    @Before
    public void tokenGenerator() {


        Map<String, Object> claims = new HashMap<>();
        authJWTToken = Jwts.builder().setClaims(claims).setSubject("admin").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, mySecretKey).compact();


    }

    RecipeDTO dto1 = new RecipeDTO(1, "TEST RECIPE", "akahrsh@gmal.com", true, 2, "01‐09‐2021 14:24",
            Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

    RecipeDTO dto2 = new RecipeDTO(2, "TEST RECIPE2", "akahrsh@test.com", true, 2, "01‐09‐2021 14:24",
            Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));


    List<RecipeDTO> recipeList = new ArrayList<>(Arrays.asList(dto1, dto2));


    @Test
    @DisplayName("Given The User needs to save the recipe Record, When The request is valid, Then The response should return the response code '201'")
    public void a_givenRecipe_whenSaveRecipe_thenStatus201()
            throws Exception {

        RecipeDTO dto = new RecipeDTO(1, "TEST RECIPE", "akahrsh@gmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/recipe-management/recipe").header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_SAVE_STATUS_SUCCESS")));

    }

    @Test
    @DisplayName("Given The User needs to save the recipe Record, When The request has no Valid Token , Then The response should return the code '403'")
    public void b_givenRecipe_whenSaveRecipe_andInvalidToken_thenStatus403()
            throws Exception {

        RecipeDTO dto = new RecipeDTO(1, "TEST RECIPE", "akahrsh@gmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/recipe-management/recipe")
                .header("Authorization", "BearerXXXX " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());


    }

    @Test
    @DisplayName("Given The User needs to retrieve all the Recipe Records, When The request is valid, Then The response should return the code '200'")
    public void c_givenRecipe_whenGetAllRecipes_thenStatus200() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(URI + "recipes")
                .header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Given The User needs to Save Recipe records as a list, When The request is valid, Then the Response should return the code 201 ")
    public void d_givenRecipe_whenSaveRecipeAsList_thenStatus200() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI + "recipes")
                .header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeList));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_SAVE_STATUS_SUCCESS")));

    }

    @Test
    @DisplayName("Given The User needs to update a Recipe Record, When The request is valid, Then the Response should return the code 200 ")
    public void e_givenRecipe_whenUpdateRecipe_thenStatus200() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(URI + "recipe/" + 1)
                .header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_UPDATE_STATUS_SUCCESS")));

    }

    @Test
    @DisplayName("Given The User needs to update a Recipe Record, When no records are found to update, Then the Response should return the code 404 ")
    public void f_givenRecipe_whenUpdateRecipeWithInvalidID_thenStatus404() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(URI + "recipe/" + 15)
                .header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("NO DATA FOUND FOR THE SEARCH PARAMETER")));

    }

    @Test
    @DisplayName("Given The User needs to delete a Recipe Record, When the request is valid, Then the Response should return the code 200 ")
    public void g_givenRecipe_whenDeleteRecipe_thenStatus200() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(URI + "recipe/" + 1)
                .header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("statusCode", Matchers.is("RECIPE_DELETION_STATUS_SUCCESS")));

    }

    @Test
    @DisplayName("Given The User needs to delete a Recipe Record, When no records are found to delete, Then the Response should return the code 404 ")
    public void h_givenRecipe_whenDeleteRecipeWithInvalidID_thenStatus404() throws Exception {


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(URI + "recipe/" + 15)
                .header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2));


        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("NO DATA FOUND FOR THE SEARCH PARAMETER")));

    }

    @Test
    @DisplayName("Given The User needs to save the recipe Record, When The request fails the Input validation, Then The response should return the code '400' ")
    public void i_givenInvalidRecipeInput_whenSaveRecipe_thenStatus400()
            throws Exception {

        RecipeDTO invalidDto = new RecipeDTO(1, "TE", "akahrshgmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        mockMvc.perform(MockMvcRequestBuilders.post(URI + "recipe").header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("Input validation failed")));

    }

    @Test
    @DisplayName("Given The User needs to update the recipe Record, When The request fails the Input validation, Then The response should return the code '400' ")
    public void j_givenInvalidRecipeInput_whenUpdateRecipe_thenStatus400()
            throws Exception {

        RecipeDTO invalidDto = new RecipeDTO(1, "TE", "akahrshgmal.com", true, 2, "01‐09‐2021 14:24",
                Arrays.asList("ing 1", "ing2", "ing3"), Arrays.asList("ins1", "ins2", "ins3"));

        mockMvc.perform(MockMvcRequestBuilders.put(URI + "recipe/" + invalidDto.getRecipeID()).header("Authorization", "Bearer " + authJWTToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("Input validation failed")));

    }

    @Test
    @DisplayName("Given The User needs to authenticate, When The credentials are correct, Then The response should return the code '200' ")
    public void k_givenValidCredentials_whenAuthenticate_thenStatus200()
            throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "password");
        mockMvc.perform(MockMvcRequestBuilders.post(URI + "authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("authToken").exists());


    }

    @Test
    @DisplayName("Given The User needs to authenticate, When The credentials are invalid, Then The response should return the code '403' ")
    public void l_givenInValidCredentials_whenAuthenticate_thenStatus403()
            throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "incorrectpassword");
        mockMvc.perform(MockMvcRequestBuilders.post(URI + "authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());


    }

}
