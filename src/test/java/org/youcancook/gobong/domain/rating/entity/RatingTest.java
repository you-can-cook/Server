package org.youcancook.gobong.domain.rating.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.youcancook.gobong.domain.rating.exception.IllegalRatingException;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.user.entity.User;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RatingTest {
    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    @DisplayName("평점이 범위를 넘어갈 때 예외를 던진다.")
    public void testValidRange(int score){
        User user = User.builder().nickname("쩝쩝박사").build();
        Recipe recipe = Recipe.builder().title("주먹밥").build();
        assertThrows(IllegalRatingException.class, ()->
                new Rating(user, recipe, score));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    @DisplayName("평점이 범위를 넘어가도록 수정할 때 예외를 던진다.")
    public void testUpdateValidRange(int score){
        User user = User.builder().nickname("쩝쩝박사").build();
        Recipe recipe = Recipe.builder().title("주먹밥").build();
        Rating rating = new Rating(user, recipe, 1);
        assertThrows(IllegalRatingException.class, ()->
                rating.updateScore(score));
    }
}
