package com.cookbook.mapper;

import com.cookbook.DTO.RecipeDTO;
import com.cookbook.model.Recipe;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DTOMapper {

    @Autowired
    ModelMapper modelMapper;


    public Recipe convertDTOtoEntity(RecipeDTO recipeDTO) {
        SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("dd‐MM‐yyyy HH:mm");
        Date date = new Date();
        Recipe recipeEntity = modelMapper.map(recipeDTO, Recipe.class);

        List<String> ingredientsList = recipeDTO.getRecipeIngredients();
        String ing = ingredientsList.stream().collect(Collectors.joining(","));

        List<String> instructionsList = recipeDTO.getRecipeInstructions();
        String ins = instructionsList.stream().collect(Collectors.joining(","));

        recipeEntity.setRecipeIngredients(ing);
        recipeEntity.setRecipeInstructions(ins);
        recipeEntity.setCreatedDate(CREATE_DATE_FORMAT.format(date));

        return recipeEntity;
    }

    public RecipeDTO convertToDTO(Recipe model) {

        RecipeDTO dto = modelMapper.map(model, RecipeDTO.class);

        List<String> ingre = Stream.of(model.getRecipeIngredients().split(",", -1))
                .collect(Collectors.toList());

        List<String> instru = Stream.of(model.getRecipeInstructions().split(",", -1))
                .collect(Collectors.toList());

        dto.setRecipeIngredients(ingre);
        dto.setRecipeInstructions(instru);

        return dto;
    }
}
