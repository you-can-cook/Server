package org.youcancook.gobong.domain.recipe.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.youcancook.gobong.domain.rating.entity.Rating;
import org.youcancook.gobong.domain.rating.repository.RatingRepository;
import org.youcancook.gobong.domain.recipe.entity.Cookware;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecipeRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    RatingRepository ratingRepository;

    @Test
    @DisplayName("조리기구 필터링이 잘 작동한다.")
    public void filterByCookware(){
        User user = new User("쩝쩝박사", "123", OAuthProvider.GOOGLE, null);
        Long id = userRepository.save(user).getId();

        Recipe recipe1 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe1.addCookware(Cookware.MICROWAVE.getValue() | Cookware.AIR_FRYER.getValue());
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe2.addCookware(Cookware.AIR_FRYER.getValue());
        recipeRepository.save(recipe2);

        Recipe recipe3 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe3.addCookware(Cookware.MICROWAVE.getValue() | Cookware.PAN.getValue());
        recipeRepository.save(recipe3);

        Slice<Recipe> actual = recipeRepository.getFilteredFeedByRecipeTitle("", List.of(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
                Integer.MAX_VALUE, 0, Cookware.MICROWAVE.getValue(), PageRequest.of(0, 3, Sort.by("id").descending()));

        Assertions.assertThat(actual.getContent()).hasSize(2);

    }

    @Test
    @DisplayName("난이도 필터링이 잘 작동한다.")
    public void filterByDifficulty(){
        User user = new User("쩝쩝박사", "123", OAuthProvider.GOOGLE, null);
        Long id = userRepository.save(user).getId();

        Recipe recipe1 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe1.addCookware(Cookware.MICROWAVE.getValue() | Cookware.AIR_FRYER.getValue());
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.NORMAL, null);
        recipe2.addCookware(Cookware.AIR_FRYER.getValue());
        recipeRepository.save(recipe2);

        Recipe recipe3 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.NORMAL, null);
        recipe3.addCookware(Cookware.MICROWAVE.getValue() | Cookware.PAN.getValue());
        recipeRepository.save(recipe3);

        Slice<Recipe> actual = recipeRepository.getFilteredFeedByRecipeTitle("", List.of(Difficulty.EASY),
                Integer.MAX_VALUE, 0, 0, PageRequest.of(0, 3, Sort.by("id").descending()));

        Assertions.assertThat(actual.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("최대 조리시간 필터링이 잘 작동한다.")
    public void filterByMaxCookTime(){
        User user = new User("쩝쩝박사", "123", OAuthProvider.GOOGLE, null);
        Long id = userRepository.save(user).getId();

        Recipe recipe1 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe1.addCookware(Cookware.MICROWAVE.getValue() | Cookware.AIR_FRYER.getValue());
        recipe1.addCookTime(10);
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.NORMAL, null);
        recipe2.addCookware(Cookware.AIR_FRYER.getValue());
        recipe1.addCookTime(20);
        recipeRepository.save(recipe2);

        Recipe recipe3 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.NORMAL, null);
        recipe3.addCookware(Cookware.MICROWAVE.getValue() | Cookware.PAN.getValue());
        recipe1.addCookTime(30);
        recipeRepository.save(recipe3);

        Slice<Recipe> actual = recipeRepository.getFilteredFeedByRecipeTitle("", List.of(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
                25, 0, 0, PageRequest.of(0, 3, Sort.by("id").descending()));

        Assertions.assertThat(actual.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("최소 평점 필터링이 잘 작동한다.")
    public void filterByMinRating(){
        User user = new User("쩝쩝박사", "123", OAuthProvider.GOOGLE, null);
        Long id = userRepository.save(user).getId();

        User guest1 = new User("guest1", "1234", OAuthProvider.GOOGLE, null);
        userRepository.save(guest1);
        User guest2 = new User("guest2", "12354", OAuthProvider.GOOGLE, null);
        userRepository.save(guest2);

        Recipe recipe1 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        ratingRepository.save(new Rating(guest1, recipe1, 5));
        ratingRepository.save(new Rating(guest2, recipe1, 1));
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.NORMAL, null);
        ratingRepository.save(new Rating(guest1, recipe2, 4));
        ratingRepository.save(new Rating(guest2, recipe2, 2));
        recipeRepository.save(recipe2);

        Recipe recipe3 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.NORMAL, null);
        ratingRepository.save(new Rating(guest1, recipe3, 1));
        ratingRepository.save(new Rating(guest2, recipe3, 3));
        recipeRepository.save(recipe3);

        Slice<Recipe> actual = recipeRepository.getFilteredFeedByRecipeTitle("", List.of(Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD),
                Integer.MAX_VALUE, 3, 0, PageRequest.of(0, 3, Sort.by("id").descending()));

        Assertions.assertThat(actual.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("이름 기준 필터링이 잘 작동한다.")
    public void filterByName(){
        User user = new User("쩝쩝박사", "123", OAuthProvider.GOOGLE, null);
        Long id = userRepository.save(user).getId();

        Recipe recipe1 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe1.addCookware(Cookware.MICROWAVE.getValue() | Cookware.AIR_FRYER.getValue());
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.EASY, null);
        recipe2.addCookware(Cookware.AIR_FRYER.getValue());
        recipeRepository.save(recipe2);

        Recipe recipe3 = new Recipe(user, "김밥", "김밥이에요", "김,밥", Difficulty.HARD, null);
        recipe3.addCookware(Cookware.MICROWAVE.getValue() | Cookware.PAN.getValue());
        recipeRepository.save(recipe3);

        Slice<Recipe> actual = recipeRepository.getFilteredFeedByAuthorName("쩝쩝박사", List.of(Difficulty.EASY),
                Integer.MAX_VALUE, 0, 0, PageRequest.of(0, 3, Sort.by("id").descending()));

        Assertions.assertThat(actual.getContent()).hasSize(2);

    }
}
