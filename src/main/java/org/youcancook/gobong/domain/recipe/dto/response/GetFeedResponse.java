package org.youcancook.gobong.domain.recipe.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetFeedResponse {
    private List<GetRecipeSummaryResponse> feed;
    private boolean hasNext;
}
