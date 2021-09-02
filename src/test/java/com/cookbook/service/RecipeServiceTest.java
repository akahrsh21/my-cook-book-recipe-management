package com.cookbook.service;

import com.cookbook.model.Recipe;
import com.cookbook.model.RecipeControllerResponse;
import com.cookbook.repository.RecipeRepository;
import com.cookbook.services.RecipeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    RecipeRepository recipeRepository;

    @Autowired
    @InjectMocks
    RecipeService recipeService;

    Recipe mockRecipe1;
    Recipe mockRecipe2;
    Recipe updateRecipe;
    List<Recipe> mockListRecipe;

    @BeforeEach
    public void setup() {
        Date date = new Date();

        SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("dd‐MM‐yyyy HH:mm");
        String createdDate = CREATE_DATE_FORMAT.format(date);
        mockRecipe1 = new Recipe(1, "Veg Spicy Soup", "akahrsh@test.com",
                true,
                1,
                createdDate,
                "1 1/2 pounds Yukon Gold or baby potatoes,1 1/2 tsp neutral oil like grape seed oil, vegetable oil or avocado oil,3/4 tsp fine sea salt,1/2 tsp fresh ground black pepper,1/2 tsp smoked paprika,Fresh herbs like chopped chives or parsley or mint for serving as required",
                "Heat the oven to 425 degrees Fahrenheit and line a large baking sheet with parchment paper or a silicone baking mat.,Chop the potatoes into 1-inch chunks and then add to a medium bowl along with the oil salt pepper and smoked paprika. Toss well and then tumble the seasoned potatoes onto the prepared baking sheet. Spread the potatoes out into one layer.,Roast the potatoes until golden brown on the outside and tender on the inside for 25 to 35 minutes. After 20 minutes check the potatoes and use a flat spatula to stir them for the most even browning.,Scatter fresh herbs over the warm potatoes and serve.");

        mockRecipe2 = new Recipe(2, "Banana Smoothie", "akahrsh@test.com",
                true,
                1,
                createdDate,
                "1 1/2 pounds Yukon Gold or baby potatoes,1 1/2 tsp neutral oil like grape seed oil, vegetable oil or avocado oil,3/4 tsp fine sea salt,1/2 tsp fresh ground black pepper,1/2 tsp smoked paprika,Fresh herbs like chopped chives or parsley or mint for serving as required",
                "Heat the oven to 425 degrees Fahrenheit and line a large baking sheet with parchment paper or a silicone baking mat.,Chop the potatoes into 1-inch chunks and then add to a medium bowl along with the oil salt pepper and smoked paprika. Toss well and then tumble the seasoned potatoes onto the prepared baking sheet. Spread the potatoes out into one layer.,Roast the potatoes until golden brown on the outside and tender on the inside for 25 to 35 minutes. After 20 minutes check the potatoes and use a flat spatula to stir them for the most even browning.,Scatter fresh herbs over the warm potatoes and serve.");

        updateRecipe = new Recipe(2, "Strawberry Icecream", "akahrsh@test.com",
                true,
                2,
                createdDate,
                "1 1/2 pounds Yukon Gold or baby potatoes,1 1/2 tsp neutral oil like grape seed oil, vegetable oil or avocado oil,3/4 tsp fine sea salt,1/2 tsp fresh ground black pepper,1/2 tsp smoked paprika,Fresh herbs like chopped chives or parsley or mint for serving as required",
                "Heat the oven to 425 degrees Fahrenheit and line a large baking sheet with parchment paper or a silicone baking mat.,Chop the potatoes into 1-inch chunks and then add to a medium bowl along with the oil salt pepper and smoked paprika. Toss well and then tumble the seasoned potatoes onto the prepared baking sheet. Spread the potatoes out into one layer.,Roast the potatoes until golden brown on the outside and tender on the inside for 25 to 35 minutes. After 20 minutes check the potatoes and use a flat spatula to stir them for the most even browning.,Scatter fresh herbs over the warm potatoes and serve.");


        mockListRecipe = new ArrayList<>(Arrays.asList(mockRecipe1, mockRecipe2));

    }

    @AfterEach
    public void cleanUp() {
        mockRecipe1 = mockRecipe2 = updateRecipe = null;
        mockListRecipe = null;
    }

    @Test
    @DisplayName("Given The User needs to retrieve the recipe Records, When The Entities are retrieved, Then validate the size of the returned list")
    void testGetAllRecipesService() {

        Mockito.when(recipeRepository.findAll()).thenReturn(mockListRecipe);

        List<Recipe> recipes = recipeRepository.findAll();

        Assertions.assertEquals(2, recipes.size());

    }

    @Test
    @DisplayName("Given The User needs to retrieve the recipe Records by author, When The Entity is retrieved, Then validate the size of the returned list")
    void testGetRecipebyAuthor() {

        Mockito.when(recipeRepository.findByRecipeAuthor("akahrsh@test.com")).thenReturn(mockListRecipe);

        List<Recipe> recipes = recipeService.findByRecipeAuthor("akahrsh@test.com");

        Assertions.assertEquals(2, recipes.size());

    }

    @Test
    @DisplayName("Given The User needs to save a recipe, When The Entity is saved, " +
            "Then validate the returned Response: should be RECIPE_SAVE_STATUS_SUCCESS ")
    void testSaveRecipe() {

        Mockito.when(recipeRepository.save(mockRecipe1))
                .thenReturn(mockRecipe1);

        RecipeControllerResponse response = recipeService.persistRecipeData(mockRecipe1);

        Assertions.assertEquals("RECIPE_SAVE_STATUS_SUCCESS", "RECIPE_SAVE_STATUS_SUCCESS");

    }

    @Test
    @DisplayName("Given The User needs to save recipe Records a list, When The Entity is saved" +
            "Then validate the returned Response: should be RECIPE_SAVE_STATUS_SUCCESS")
    void testSaveRecipeAsList() {

        Mockito.when(recipeRepository.saveAll(mockListRecipe))
                .thenReturn(mockListRecipe);

        RecipeControllerResponse response = recipeService.persistListOfRecipe(mockListRecipe);

        Assertions.assertEquals("RECIPE_SAVE_STATUS_SUCCESS", "RECIPE_SAVE_STATUS_SUCCESS");

    }

    @Test
    @DisplayName("Given The User needs to delete recipe Records,When The Recipe Record is deleted,Then Verify the number of invocations")
    void testDeleteRecipeRecord() {
        Optional<Recipe> optionalRecipe = Optional.of(mockRecipe1);
        Mockito.when(recipeRepository.findById(mockRecipe1.getRecipeID())).thenReturn(optionalRecipe);

        recipeService.deleteRecipeByID(mockRecipe1.getRecipeID());

        Mockito.verify(recipeRepository, Mockito.times(1)).deleteById(mockRecipe1.getRecipeID());


    }

    @Test
    @DisplayName("Given The User needs to update a recipe Record, When The Recipe Record is updated, Then Verify the number of invocations ")
    void testUpdateRecipeRecord() {

        Optional<Recipe> optionalRecipe = Optional.of(mockRecipe1);
        Mockito.when(recipeRepository.findById(mockRecipe1.getRecipeID())).thenReturn(optionalRecipe);

        recipeService.updateRecipeRecord(mockRecipe1.getRecipeID(), mockRecipe1);

        Mockito.verify(recipeRepository, Mockito.times(1)).save(mockRecipe1);


    }


}
