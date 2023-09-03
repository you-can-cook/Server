package org.youcancook.gobong.domain.recipe.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRecipeSummaryResponse {

    private Long id;
    private String title;
    private String thumbnailURL;
    private RecipeAuthorResponse author;
    private int totalBookmarkCount;
    private int totalCookTimeInSeconds;
    private List<String> cookwares;
    private String difficulty;
    private Double averageRating;
    private boolean isBookmarked;

}
