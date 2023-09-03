package org.youcancook.gobong.domain.recipe.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.domain.recipedetail.dto.response.GetRecipeDetailResponse;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRecipeResponse {

    private Long id;
    private GetRecipeSummaryResponse summary;
    private String introduction;
    private List<String> ingredients;
    private List<GetRecipeDetailResponse> recipeDetails;
    private int myRate;

}
