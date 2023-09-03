package org.youcancook.gobong.domain.recipe.dto.recipeDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.domain.recipe.entity.Cookware;
import org.youcancook.gobong.domain.recipe.entity.Recipe;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeDto {

    private Long id;
    private Long authorId;
    private String authorName;
    private String title;
    private String introduction;
    private List<String> ingredients;
    private String difficulty;
    private String thumbnailURL;
    private List<String> cookwares;
    private int totalCookTimeInSeconds;
    private int totalBookmarkCount;
    private Double averageRating;

    public static RecipeDto from(Recipe recipe){
        return new RecipeDto(recipe.getId(), recipe.getUser().getId(), recipe.getUser().getNickname(), recipe.getTitle(),
                recipe.getIntroduction(), Arrays.stream(recipe.getIngredients().split(",")).toList(),
                recipe.getDifficulty().getDescription(), recipe.getThumbnailURL(), Cookware.bitToList(recipe.getCookwares()),
                recipe.getTotalCookTimeInSeconds(), recipe.getTotalBookmarkCount(), recipe.getAverageRating());
    }
}
