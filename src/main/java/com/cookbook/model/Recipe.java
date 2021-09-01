package com.cookbook.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Recipe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recipeID;
    private String recipeName;
    private String recipeAuthor;
    private boolean veganRecipe;
    private int recipeSuitableServing;
    private String createdDate;
    @Column(length = 10000)
    private String recipeIngredients;
    @Column(length = 10000)
    private String recipeInstructions;

}
