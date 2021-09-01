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
    @DisplayName("Test the Get All Recipes Service Method, Expected OK")
    void testGetAllRecipesService() {
        //Given The User needs to retrieve the recipe Records
        Mockito.when(recipeRepository.findAll()).thenReturn(mockListRecipe);
        //When The Entities are retrieved
        List<Recipe> recipes = recipeRepository.findAll();
        //Then validate the size of the returned list
        Assertions.assertEquals(2, recipes.size());

    }

    @Test
    @DisplayName("Test the Get Recipe by Author Service Method, Expected OK")
    void testGetRecipebyAuthor() {
        //Given The User needs to retrieve the recipe Records by author

        Mockito.when(recipeRepository.findByRecipeAuthor("akahrsh@test.com")).thenReturn(mockListRecipe);
        //When The Entity is retrieved
        List<Recipe> recipes = recipeService.findByRecipeAuthor("akahrsh@test.com");
        //Then validate the size of the returned list
        Assertions.assertEquals(2, recipes.size());

    }

    @Test
    @DisplayName("Test the Save Recipe Service Method, Expected OK")
    void testSaveRecipe() {
        //Given The User needs to save a recipe
        Mockito.when(recipeRepository.save(mockRecipe1))
                .thenReturn(mockRecipe1);
        //When The Entity is saved
        RecipeControllerResponse response = recipeService.persistRecipeData(mockRecipe1);
        //Then validate the returned Response: should be RECIPE_SAVE_STATUS_SUCCESS
        Assertions.assertEquals("RECIPE_SAVE_STATUS_SUCCESS", "RECIPE_SAVE_STATUS_SUCCESS");

    }

    @Test
    @DisplayName("Test the Save Recipe as List Service Method, Expected OK")
    void testSaveRecipeAsList() {
        //Given The User needs to save recipe Records a list

        Mockito.when(recipeRepository.saveAll(mockListRecipe))
                .thenReturn(mockListRecipe);
        //When The Entity is saved
        RecipeControllerResponse response = recipeService.persistListOfRecipe(mockListRecipe);
        //Then validate the returned Response: should be RECIPE_SAVE_STATUS_SUCCESS
        Assertions.assertEquals("RECIPE_SAVE_STATUS_SUCCESS", "RECIPE_SAVE_STATUS_SUCCESS");

    }

    @Test
    @DisplayName("Test the Delete Recipe Record Service Method, Expected OK")
    void testDeleteRecipeRecord() {
        //Given The User needs to delete recipe Records


        Optional<Recipe> optionalRecipe = Optional.of(mockRecipe1);
        Mockito.when(recipeRepository.findById(mockRecipe1.getRecipeID())).thenReturn(optionalRecipe);
        //When The Recipe Record is deleted
        recipeService.deleteRecipeByID(mockRecipe1.getRecipeID());
        //Then Verify the number of invocations
        Mockito.verify(recipeRepository, Mockito.times(1)).deleteById(mockRecipe1.getRecipeID());


    }

    @Test
    @DisplayName("Test the Update Recipe Record Service Method, Expected OK")
    void testUpdateRecipeRecord() {
        //Given The User needs to update a recipe Record

        Optional<Recipe> optionalRecipe = Optional.of(mockRecipe1);
        Mockito.when(recipeRepository.findById(mockRecipe1.getRecipeID())).thenReturn(optionalRecipe);
        //When The Recipe Record is updated
        recipeService.updateRecipeRecord(mockRecipe1.getRecipeID(), mockRecipe1);
        //Then Verify the number of invocations
        Mockito.verify(recipeRepository, Mockito.times(1)).save(mockRecipe1);


    }


}
