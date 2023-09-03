package org.youcancook.gobong.domain.rating.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class RateRecipeRequest {

    @NotNull(message = "레시피 ID는 필수 항목입니다.")
    private Long recipeId;

    @NotNull(message = "평점은 필수 항목입니다.")
    @Min(value = 1, message = "최소 평점은 1점입니다.")
    @Max(value = 5, message = "최대 평점은 5점입니다.")
    private Integer score;

}
