package org.youcancook.gobong.domain.recipe.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.youcancook.gobong.domain.bookmarkrecipe.entity.BookmarkRecipe;
import org.youcancook.gobong.domain.bookmarkrecipe.repository.BookmarkRecipeRepository;
import org.youcancook.gobong.domain.follow.entity.Follow;
import org.youcancook.gobong.domain.follow.repository.FollowRepository;
import org.youcancook.gobong.domain.follow.service.FollowService;
import org.youcancook.gobong.domain.recipe.dto.response.GetFeedResponse;
import org.youcancook.gobong.domain.recipe.dto.response.GetRecipeSummaryResponse;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.recipedetail.repository.RecipeDetailRepository;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.service.ServiceTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
class GetRecipeServiceTest {

    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    GetRecipeService getRecipeService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeDetailRepository recipeDetailRepository;
    @Autowired
    BookmarkRecipeRepository bookmarkRecipeRepository;
    @Autowired
    FollowService followService;
    @Autowired
    FollowRepository followRepository;

    @Test
    @DisplayName("전체 피드를 성공적으로 가져온다.")
    public void getFeed() {
        User user = new User("name", "abc", OAuthProvider.GOOGLE, null);
        Long userId = userRepository.save(user).getId();

        Recipe recipe1 = new Recipe(user, "주먹밥1", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe2 = new Recipe(user, "주먹밥2", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe3 = new Recipe(user, "주먹밥3", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe4 = new Recipe(user, "주먹밥4", "주먹밥을 만들자", "밥", Difficulty.EASY, null);

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);
        recipeRepository.save(recipe4);

        GetFeedResponse feedResponse = getRecipeService.getAllFeed(userId, Long.MAX_VALUE, 3);

        assertThat(feedResponse.getFeed()).hasSize(3);
        assertThat(feedResponse.isHasNext()).isTrue();

        Long lastRecipeId = feedResponse.getFeed().get(2).getId();
        GetFeedResponse actual = getRecipeService.getAllFeed(userId, lastRecipeId, 3);
        assertThat(actual.getFeed()).hasSize(1);
        assertThat(actual.isHasNext()).isFalse();

    }

    @Test
    @DisplayName("북마크 피드를 성공적으로 가져온다.")
    public void getBookmarkedFeed() {
        User user = new User("name", "abc", OAuthProvider.GOOGLE, null);
        User user2 = new User("name1", "abcdd", OAuthProvider.GOOGLE, null);
        userRepository.save(user);
        Long user2Id = userRepository.save(user2).getId();

        Recipe recipe1 = new Recipe(user, "주먹밥1", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe2 = new Recipe(user, "주먹밥2", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe3 = new Recipe(user, "주먹밥3", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe4 = new Recipe(user, "주먹밥4", "주먹밥을 만들자", "밥", Difficulty.EASY, null);

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);
        recipeRepository.save(recipe4);

        BookmarkRecipe bookmarkRecipe1 = new BookmarkRecipe(user2, recipe1);
        BookmarkRecipe bookmarkRecipe2 = new BookmarkRecipe(user2, recipe2);
        bookmarkRecipeRepository.save(bookmarkRecipe1);
        bookmarkRecipeRepository.save(bookmarkRecipe2);

        GetFeedResponse feedResponse = getRecipeService.getBookmarkedFeed(user2Id, Long.MAX_VALUE, 3);

        assertThat(feedResponse.getFeed()).hasSize(2);
        assertThat(feedResponse.isHasNext()).isFalse();

        List<String> titles = feedResponse.getFeed().stream().map(GetRecipeSummaryResponse::getTitle).collect(Collectors.toList());
        assertThat(titles).hasSize(2);
        assertThat(titles).containsAll(List.of("주먹밥1", "주먹밥2"));
    }

    @Test
    @DisplayName("팔로우 중인 유저들의 피드를 확인한다.")
    public void getFollowingFeed() {
        User user1 = new User("name1", "11", OAuthProvider.GOOGLE, null);
        User user2 = new User("name2", "22", OAuthProvider.GOOGLE, null);
        User user3 = new User("name3", "33", OAuthProvider.GOOGLE, null);

        Long user1Id = userRepository.save(user1).getId();
        Long user2Id = userRepository.save(user2).getId();
        userRepository.save(user3);

        Recipe recipe1 = new Recipe(user1, "주먹밥1", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe2 = new Recipe(user1, "주먹밥2", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe3 = new Recipe(user2, "주먹밥3", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe4 = new Recipe(user2, "주먹밥4", "주먹밥을 만들자", "밥", Difficulty.EASY, null);

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);
        recipeRepository.save(recipe4);

        followRepository.save(new Follow(user2, user1));
        followRepository.save(new Follow(user2, user1));

        GetFeedResponse response = getRecipeService.getFollowingFeed(user2.getId(), Long.MAX_VALUE, 5);
        assertThat(response.getFeed()).hasSize(4);

    }

    @Test
    @DisplayName("내가 작성한 게시물의 피드를 확인한다.")
    public void getMyFeed() {
        User user1 = new User("name1", "11", OAuthProvider.GOOGLE, null);
        userRepository.save(user1);

        Recipe recipe1 = new Recipe(user1, "주먹밥1", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe2 = new Recipe(user1, "주먹밥2", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe3 = new Recipe(user1, "주먹밥3", "주먹밥을 만들자", "밥", Difficulty.EASY, null);

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);

        GetFeedResponse response = getRecipeService.getMyFeed(user1.getId(), Long.MAX_VALUE, 5);
        assertThat(response.getFeed()).hasSize(3);
    }

    @Test
    @DisplayName("특정 유저가 작성한 게시물의 피드를 확인한다.")
    public void getUserFeed() {
        User user1 = new User("name1", "11", OAuthProvider.GOOGLE, null);
        User user2 = new User("name2", "12", OAuthProvider.GOOGLE, null);
        userRepository.save(user1);
        userRepository.save(user2);

        Recipe recipe1 = new Recipe(user1, "주먹밥1", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe2 = new Recipe(user1, "주먹밥2", "주먹밥을 만들자", "밥", Difficulty.EASY, null);
        Recipe recipe3 = new Recipe(user1, "주먹밥3", "주먹밥을 만들자", "밥", Difficulty.EASY, null);

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);

        GetFeedResponse response = getRecipeService.getUserFeed(user1.getId(), user2.getId(), Long.MAX_VALUE, 5);
        assertThat(response.getFeed()).hasSize(3);
    }
}
