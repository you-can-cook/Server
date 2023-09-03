package org.youcancook.gobong.domain.bookmarkrecipe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class RemoveBookmarkRecipeRequest {
    @NotNull(message = "레시피 ID는 필수 항목입니다.")
    private Long recipeId;
}
