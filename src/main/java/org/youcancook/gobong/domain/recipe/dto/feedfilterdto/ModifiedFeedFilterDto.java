package org.youcancook.gobong.domain.recipe.dto.feedfilterdto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.domain.recipe.entity.Cookware;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifiedFeedFilterDto {

    private String query;
    private QueryType queryType;
    private FilterType filterType;
    private List<Difficulty> difficulties;
    private Integer maxTotalCookTime;
    private Integer minRating;
    private Long cookwares;

    public static ModifiedFeedFilterDto from(FeedFilterDto dto){
        ModifiedFeedFilterDto modifiedDto = new ModifiedFeedFilterDto();

        modifiedDto.query = dto.getQuery() == null ? "" : dto.getQuery();

        if (modifiedDto.getQuery().startsWith("@")){
            modifiedDto.queryType = QueryType.AUTHOR_NAME;
            modifiedDto.query = modifiedDto.query.substring(1);
        }
        else{
            modifiedDto.queryType = QueryType.TITLE;
        }

        modifiedDto.filterType = FilterType.from(dto.getFilterType());
        modifiedDto.difficulties = dto.getDifficulty() == null ? List.of(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD)
                : List.of(Difficulty.from(dto.getDifficulty()));
        modifiedDto.maxTotalCookTime = dto.getMaxTotalCookTime() == null ? Integer.MAX_VALUE : dto.getMaxTotalCookTime();
        modifiedDto.minRating = dto.getMinRating() == null ? 0 : dto.getMinRating();
        modifiedDto.cookwares = dto.getCookwares() == null ? 0 : Cookware.namesToCookwareBit(dto.getCookwares());

        return modifiedDto;
    }
}
