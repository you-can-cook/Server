package org.youcancook.gobong.domain.recipe.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.youcancook.gobong.domain.bookmarkrecipe.entity.BookmarkRecipe;
import org.youcancook.gobong.domain.bookmarkrecipe.repository.BookmarkRecipeRepository;
import org.youcancook.gobong.domain.rating.entity.Rating;
import org.youcancook.gobong.domain.rating.repository.RatingRepository;
import org.youcancook.gobong.domain.recipe.dto.request.CreateRecipeRequest;
import org.youcancook.gobong.domain.recipe.dto.request.UpdateRecipeRequest;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.exception.RecipeAccessDeniedException;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.recipedetail.dto.request.UploadRecipeDetailRequest;
import org.youcancook.gobong.domain.recipedetail.entity.RecipeDetail;
import org.youcancook.gobong.domain.recipedetail.repository.RecipeDetailRepository;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.service.ServiceTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
class RecipeServiceTest {
    @Autowired
    RecipeService recipeService;
    @Autowired
    RecipeDetailRepository recipeDetailRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    BookmarkRecipeRepository bookmarkRecipeRepository;


    @Test
    @DisplayName("레시피를 성공적으로 등록한다.")
    public void createRecipe(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Long userId = userRepository.save(user).getId();
        String title = "주먹밥";
        List<UploadRecipeDetailRequest> details = new ArrayList<>();
        recipeService.createRecipe(userId, new CreateRecipeRequest(title, "주먹밥을 만들어요",
                List.of("밥", "김"), "쉬워요", null, details));
        List<Recipe> actual = recipeRepository.findAll();

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("레시피 내용을 성공적으로 업데이트한다.")
    public void updateRecipe(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        Long userId = userRepository.save(user).getId();
        Long recipeId = recipeRepository.save(recipe).getId();
        String title = "비빔밥";
        List<UploadRecipeDetailRequest> details = new ArrayList<>();

        recipeService.updateRecipe(userId, recipeId, new UpdateRecipeRequest(title, "주먹밥을 만들어요",
                List.of("밥", "김"), "보통이에요", null, details));

        List<Recipe> actual = recipeRepository.findAll();
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("작성자가 아닌 사람이 업데이트 시도 시 예외를 반환한다.")
    public void unauthorizedTest(){
        User user1 = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        User user2 = User.builder().nickname("쩝쩝학사").oAuthProvider(OAuthProvider.KAKAO).oAuthId("125").build();
        Recipe recipe = Recipe.builder().user(user1).difficulty(Difficulty.EASY).title("주먹밥").build();

        userRepository.save(user1);
        Long user2Id = userRepository.save(user2).getId();
        Long recipeId = recipeRepository.save(recipe).getId();
        String title = "비빔밥";
        List<UploadRecipeDetailRequest> details = new ArrayList<>();
        assertThrows(RecipeAccessDeniedException.class, ()->
            recipeService.updateRecipe(user2Id, recipeId, new UpdateRecipeRequest(title, "주먹밥을 만들어요",
                List.of("밥", "김"), "어려워요", null, details)));
    }

    @Test
    @DisplayName("단계별 레시피가 추가될 때, 시간이 자동으로 합산된다.")
    public void addDetailsCheckTime(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        new RecipeDetail(recipe, "Content", "", 1, 1L, 1);
        new RecipeDetail(recipe, "Content", "", 2, 1L, 1);
        new RecipeDetail(recipe, "Content", "", 3, 1L, 1);
        assertThat(recipe.getTotalCookTimeInSeconds()).isEqualTo(6);
    }

    @Test
    @DisplayName("단계별 레시피가 추가될 때, 조리도구가 자동으로 합산된다.")
    public void addDetailsCheckCookwares(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        new RecipeDetail(recipe, "Content", "", 1, 1L, 1);
        new RecipeDetail(recipe, "Content", "", 2, 2L, 1);
        new RecipeDetail(recipe, "Content", "", 3, 4L, 1);
        assertThat(recipe.getCookwares()).isEqualTo(7L);
    }

    @Test
    @DisplayName("레시피를 성공적으로 삭제한다.")
    public void deleteRecipe(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        Long userId = userRepository.save(user).getId();
        Long recipeId = recipeRepository.save(recipe).getId();

        recipeService.deleteRecipe(userId, recipeId);

        List<Recipe> actual = recipeRepository.findAll();
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("레시피를 삭제하면, 단계별 레시피도 함께 삭제된다.")
    public void deleteDetailWhenRecipeDeleted(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        Long userId = userRepository.save(user).getId();
        Long recipeId = recipeRepository.save(recipe).getId();

        RecipeDetail r1 = new RecipeDetail(recipe, "Content", "", 1, 1L, 1);
        RecipeDetail r2 = new RecipeDetail(recipe, "Content", "", 2, 1L, 1);
        RecipeDetail r3 = new RecipeDetail(recipe, "Content", "", 3, 1L, 1);
        recipeDetailRepository.save(r1);
        recipeDetailRepository.save(r2);
        recipeDetailRepository.save(r3);

        recipeService.deleteRecipe(userId, recipeId);

        List<RecipeDetail> actual = recipeDetailRepository.findAll();
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("레시피를 삭제하면, 해당 레시피에 작성된 평점도 함께 삭제된다.")
    public void deleteRatingWhenRecipeDeleted(){
        User user1 = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        User user2 = User.builder().nickname("쩝쩝학사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("125").build();

        Recipe recipe = Recipe.builder().user(user1).difficulty(Difficulty.EASY).title("주먹밥").build();

        Long userId = userRepository.save(user1).getId();
        userRepository.save(user2);
        Long recipeId = recipeRepository.save(recipe).getId();

        Rating rating = new Rating(user2, recipe, 5);
        ratingRepository.save(rating);

        recipeService.deleteRecipe(userId, recipeId);

        List<Rating> actual = ratingRepository.findAll();
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("레시피를 삭제하면, 해당 레시피를 저장한 북마크도 함께 삭제된다.")
    public void deleteBookmarksWhenRecipeDeleted(){
        User user1 = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        User user2 = User.builder().nickname("쩝쩝학사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("125").build();
        User user3 = User.builder().nickname("쩝쩝석사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("127").build();
        Recipe recipe = Recipe.builder().user(user1).difficulty(Difficulty.EASY).title("주먹밥").build();

        Long userId = userRepository.save(user1).getId();
        userRepository.save(user2);
        userRepository.save(user3);
        Long recipeId = recipeRepository.save(recipe).getId();

        BookmarkRecipe bookmark1 = new BookmarkRecipe(user2, recipe);
        BookmarkRecipe bookmark2 = new BookmarkRecipe(user3, recipe);
        bookmarkRecipeRepository.save(bookmark1);
        bookmarkRecipeRepository.save(bookmark2);

        recipeService.deleteRecipe(userId, recipeId);
        List<BookmarkRecipe> actual = bookmarkRecipeRepository.findAll();
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("유저가 작성한 레시피의 경우, 검증이 통과한다.")
    public void validateUserOk(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        userRepository.save(user);
        recipeRepository.save(recipe);

        assertDoesNotThrow(() -> recipeService.validateUserRecipe(user, recipe));
    }

    @Test
    @DisplayName("유저가 작성하지 않은 레시피의 경우, 검증에서 예외를 발생한다.")
    public void validateUserNotOk(){
        User user1 = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        User user2 = User.builder().nickname("쩝쩝학사").oAuthProvider(OAuthProvider.KAKAO).oAuthId("125").build();
        Recipe recipe = Recipe.builder().user(user1).difficulty(Difficulty.EASY).title("주먹밥").build();

        userRepository.save(user1);
        userRepository.save(user2);
        recipeRepository.save(recipe);
        assertThrows(RecipeAccessDeniedException.class, () -> recipeService.validateUserRecipe(user2, recipe));
    }

    @AfterEach
    void teardown(){
        recipeDetailRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();
    }
}
