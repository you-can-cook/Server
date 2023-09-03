package org.youcancook.gobong.domain.recipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.bookmarkrecipe.service.BookmarkRecipeService;
import org.youcancook.gobong.domain.follow.service.FollowService;
import org.youcancook.gobong.domain.recipe.dto.feedfilterdto.FilterType;
import org.youcancook.gobong.domain.recipe.dto.feedfilterdto.ModifiedFeedFilterDto;
import org.youcancook.gobong.domain.rating.entity.Rating;
import org.youcancook.gobong.domain.rating.repository.RatingRepository;
import org.youcancook.gobong.domain.recipe.dto.recipeDto.RecipeDto;
import org.youcancook.gobong.domain.recipe.dto.response.GetFeedResponse;
import org.youcancook.gobong.domain.recipe.dto.response.GetRecipeResponse;
import org.youcancook.gobong.domain.recipe.dto.response.GetRecipeSummaryResponse;
import org.youcancook.gobong.domain.recipe.dto.response.RecipeAuthorResponse;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.exception.RecipeNotFoundException;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.recipedetail.dto.response.GetRecipeDetailResponse;
import org.youcancook.gobong.domain.recipedetail.service.RecipeDetailService;
import org.youcancook.gobong.domain.user.service.UserInformationService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetRecipeService {

    private final FollowService followService;
    private final RecipeDetailService recipeDetailService;
    private final RecipeRepository recipeRepository;
    private final BookmarkRecipeService bookmarkRecipeService;
    private final UserInformationService userInformationService;
    private final RatingRepository ratingRepository;

    public GetRecipeResponse getRecipe(Long userId, Long recipeId) {
        Recipe recipe = recipeRepository.fetchFindById(recipeId).orElseThrow(RecipeNotFoundException::new);
        RecipeDto recipeDto = RecipeDto.from(recipe);
        List<GetRecipeDetailResponse> recipeDetails = recipeDetailService.getRecipeDetails(recipe);
        GetRecipeSummaryResponse summaryResponse = getSummary(userId, recipeDto);

        int myRate = getMyRate(userId, recipeId);
        return new GetRecipeResponse(recipeDto.getId(), summaryResponse,
                recipeDto.getIntroduction(), recipeDto.getIngredients(), recipeDetails, myRate);
    }

    private int getMyRate(Long userId, Long recipeId) {
        Optional<Rating> ratingOptional = ratingRepository.findByUserIdAndRecipeId(userId, recipeId);
        if (ratingOptional.isPresent()) {
            return ratingOptional.get().getScore();
        }
        else {
            return 0;
        }
    }

    public GetRecipeSummaryResponse getSummary(Long userId, RecipeDto recipeDto) {
        RecipeAuthorResponse authorResponse = getAuthor(userId, recipeDto);
        boolean isBookmarked = bookmarkRecipeService.checkBookmark(userId, recipeDto.getId());
        return new GetRecipeSummaryResponse(
                recipeDto.getId(), recipeDto.getTitle(), recipeDto.getThumbnailURL(), authorResponse,
                recipeDto.getTotalBookmarkCount(), recipeDto.getTotalCookTimeInSeconds(), recipeDto.getCookwares(),
                recipeDto.getDifficulty(), recipeDto.getAverageRating(), isBookmarked
        );
    }

    public RecipeAuthorResponse getAuthor(Long userId, RecipeDto recipeDto) {
        Long authorId = recipeDto.getAuthorId();
        String authorNickname = recipeDto.getAuthorName();
        String profileImageURL = userInformationService.getMyInformation(authorId).getProfileImageURL();
        boolean isFollowing = followService.isFollowing(userId, authorId);
        boolean isMyself = Objects.equals(userId, authorId);

        return new RecipeAuthorResponse(authorId, authorNickname, profileImageURL, isFollowing, isMyself);
    }

    public GetFeedResponse getAllFeed(Long userId, long recipeId, int count) {
        Slice<Recipe> feedRecipes = recipeRepository.getAllFeed(recipeId,
                PageRequest.of(0, count, Sort.by("id").descending()));
        return getFeedResponse(userId, feedRecipes);
    }

    public GetFeedResponse getBookmarkedFeed(Long userId, long recipeId, int count) {
        Slice<Recipe> feedRecipes = recipeRepository.getAllBookmarkedFeed(userId, recipeId,
                PageRequest.of(0, count, Sort.by("id").descending()));
        return getFeedResponse(userId, feedRecipes);
    }

    public GetFeedResponse getFollowingFeed(Long userId, long recipeId, int count) {
        Slice<Recipe> feedRecipes = recipeRepository.getAllFollowingFeed(userId, recipeId,
                PageRequest.of(0, count, Sort.by("id").descending()));
        return getFeedResponse(userId, feedRecipes);
    }

    public GetFeedResponse getMyFeed(Long userId, long recipeId, int count) {
        Slice<Recipe> feedRecipes = recipeRepository.getAllUserFeed(userId, recipeId,
                PageRequest.of(0, count, Sort.by("id").descending()));
        return getFeedResponse(userId, feedRecipes);
    }

    public GetFeedResponse getFilteredFeedByName(Long userId, int page, int count, ModifiedFeedFilterDto filter){
        String sortProperty = filter.getFilterType() == FilterType.RECENT ? "id" : "totalBookmarkCount";

        Slice<Recipe> feedRecipes = recipeRepository.getFilteredFeedByRecipeTitle(filter.getQuery(),
                filter.getDifficulties(), filter.getMaxTotalCookTime(), filter.getMinRating(),
                filter.getCookwares(), PageRequest.of(page, count, Sort.by(sortProperty).descending()));
        return getFeedResponse(userId, feedRecipes);
    }

    public GetFeedResponse getFilteredFeedByRecipeTitle(Long userId, int page, int count, ModifiedFeedFilterDto filter){
        String sortProperty = filter.getFilterType() == FilterType.RECENT ? "id" : "totalBookmarkCount";

        Slice<Recipe> feedRecipes = recipeRepository.getFilteredFeedByAuthorName(filter.getQuery(),
                filter.getDifficulties(), filter.getMaxTotalCookTime(), filter.getMinRating(),
                filter.getCookwares(), PageRequest.of(page, count, Sort.by(sortProperty).descending()));
        return getFeedResponse(userId, feedRecipes);
    }

    public GetFeedResponse getUserFeed(Long userId, Long myId, long recipeId, int count){
        Slice<Recipe> feedRecipes = recipeRepository.getAllUserFeed(userId, recipeId,
                PageRequest.of(0, count, Sort.by("id").descending()));
        return getFeedResponse(myId, feedRecipes);
    }


    private GetFeedResponse getFeedResponse(Long userId, Slice<Recipe> feedRecipes) {
        List<GetRecipeSummaryResponse> summaries = feedRecipes.getContent().stream()
                .map(recipe -> getSummary(userId, RecipeDto.from(recipe)))
                .toList();
        return new GetFeedResponse(summaries, feedRecipes.hasNext());
    }
}
