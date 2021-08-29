package com.cookbook.repository;

import com.cookbook.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> findByRecipeAuthor(String recipeAuthor);



}
