package org.youcancook.gobong.domain.recipe.dto.feedfilterdto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedFilterDto {

    private String query;

    @NotNull(message = "정렬 기준은 필수 항목입니다.")
    private String filterType;

    private String difficulty;

    private Integer maxTotalCookTime;

    private Integer minRating;

    private List<String> cookwares;

}
