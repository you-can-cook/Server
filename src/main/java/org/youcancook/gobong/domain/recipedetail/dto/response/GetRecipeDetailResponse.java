package org.youcancook.gobong.domain.recipedetail.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRecipeDetailResponse {

    private Long id;

    private String content;
    private String imageURL;
    private int cookTimeInSeconds;
    private List<String> cookwares;

    private int step;

}
