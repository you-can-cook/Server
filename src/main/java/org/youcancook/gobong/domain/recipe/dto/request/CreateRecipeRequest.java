package org.youcancook.gobong.domain.recipe.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.youcancook.gobong.domain.recipedetail.dto.request.UploadRecipeDetailRequest;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class CreateRecipeRequest {

    @NotBlank(message = "레시피 이름은 필수 입력 사항입니다.")
    private String title;

    private String introduction;
    private List<String> ingredients;

    @NotBlank(message = "난이도는 필수 입력 사항입니다.")
    private String difficulty;
    private String thumbnailURL;

    @NotNull(message = "단계별 레시피는 필수 항목입니다.")
    private List<@Valid UploadRecipeDetailRequest> recipeDetails;

}
