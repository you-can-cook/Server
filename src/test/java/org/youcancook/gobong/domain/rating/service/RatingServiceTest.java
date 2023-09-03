package org.youcancook.gobong.domain.rating.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.youcancook.gobong.domain.rating.entity.Rating;
import org.youcancook.gobong.domain.rating.exception.RatingMyRecipeException;
import org.youcancook.gobong.domain.rating.repository.RatingRepository;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.service.ServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ServiceTest
class RatingServiceTest {
    @Autowired
    RatingService ratingService;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;

    @Test
    @DisplayName("성공적으로 평점을 등록한다.")
    public void rateRecipeTest(){
        User user = User.builder().nickname("쩝쩝박사").oAuthId("123")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        User user2 = User.builder().nickname("쩝쩝박사123").oAuthId("1234")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        User user3 = User.builder().nickname("쩝쩝학사").oAuthId("12345")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        Recipe recipe = Recipe.builder().user(user)
                .title("주먹밥").difficulty(Difficulty.EASY).build();

        userRepository.save(user);
        Long user2Id = userRepository.save(user2).getId();
        Long user3Id = userRepository.save(user3).getId();

        Long recipeId = recipeRepository.save(recipe).getId();

        ratingService.createRating(user2Id, recipeId, 3);
        ratingService.createRating(user3Id, recipeId, 5);

        List<Rating> actual = ratingRepository.findAll();

        assertThat(actual).isNotEmpty();
        assertThat(actual.stream().mapToDouble(Rating::getScore).average().orElse(0.0)).isEqualTo(4.0);

        List<Recipe> recipeActual = recipeRepository.findAll();
        assertThat(recipeActual).isNotEmpty();
        assertThat(recipeActual.get(0).getAverageRating()).isEqualTo(4.0);

    }

    @Test
    @DisplayName("성공적으로 평점을 수정한다.")
    public void updateRateTest(){
        User user = User.builder().nickname("쩝쩝박사").oAuthId("123")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        User user2 = User.builder().nickname("쩝쩝박사123").oAuthId("1234")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        User user3 = User.builder().nickname("쩝쩝학사").oAuthId("12345")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        Recipe recipe = Recipe.builder().user(user)
                .title("주먹밥").difficulty(Difficulty.EASY).build();

        userRepository.save(user);
        Long user2Id = userRepository.save(user2).getId();
        Long user3Id = userRepository.save(user3).getId();

        Long recipeId = recipeRepository.save(recipe).getId();

        ratingService.createRating(user2Id, recipeId, 3);
        ratingService.createRating(user3Id, recipeId, 5);

        ratingService.updateRating(user2Id, recipeId, 5);

        List<Rating> actual = ratingRepository.findAll();

        assertThat(actual).isNotEmpty();
        assertThat(actual.stream().mapToDouble(Rating::getScore).average().orElse(0.0)).isEqualTo(5.0);

        List<Recipe> recipeActual = recipeRepository.findAll();
        assertThat(recipeActual).isNotEmpty();
        assertThat(recipeActual.get(0).getAverageRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("자신의 레시피에 평점을 남기려고 하면 예외를 발생한다.")
    public void rateMyRecipe(){
        User user = User.builder().nickname("쩝쩝박사").oAuthId("123")
                .oAuthProvider(OAuthProvider.GOOGLE).build();
        Recipe recipe = Recipe.builder().user(user)
                .title("주먹밥").difficulty(Difficulty.EASY).build();

        Long userId = userRepository.save(user).getId();
        Long recipeId = recipeRepository.save(recipe).getId();

        assertThrows(RatingMyRecipeException.class, () -> ratingService.createRating(userId, recipeId, 5));
    }

    @AfterEach
    public void tearDown(){
        ratingRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();
    }
}
