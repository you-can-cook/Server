package org.youcancook.gobong.domain.recipedetail.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.exception.RecipeNotFoundException;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.recipedetail.dto.request.UploadRecipeDetailRequest;
import org.youcancook.gobong.domain.recipedetail.entity.RecipeDetail;
import org.youcancook.gobong.domain.recipedetail.repository.RecipeDetailRepository;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.service.ServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
class RecipeDetailServiceTest {

    @Autowired
    RecipeDetailService recipeDetailService;
    @Autowired
    RecipeDetailRepository recipeDetailRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("단계별 레시피 하나를 성공적으로 등록한다.")
    public void uploadOneDetail(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        userRepository.save(user);
        recipeRepository.save(recipe);

        String content = "밥을 비벼주세요";
        recipeDetailService.uploadRecipeDetail(recipe.getId(), content, "",
                30, 1L, 1);
        List<RecipeDetail> actual = recipeDetailRepository.findAll();

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("여러 단계별 레시피를 성공적으로 등록한다.")
    public void uploadMultipleDetails(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        userRepository.save(user);
        Long recipeId = recipeRepository.save(recipe).getId();

        List<UploadRecipeDetailRequest> requests = List.of(
            new UploadRecipeDetailRequest("Content1", "", 30, List.of("MICROWAVE")),
            new UploadRecipeDetailRequest("Content2", "", 15, List.of("AIR_FRYER"))
        );

        recipeDetailService.uploadRecipeDetails(recipeId, requests);

        List<RecipeDetail> actual = recipeDetailRepository.findAllByRecipeId(recipeId);
        assertThat(actual).hasSize(2);

        List<Recipe> actualRecipe = recipeRepository.findAll();
        assertThat(actualRecipe).isNotEmpty();
        assertThat(actualRecipe.get(0).getTotalCookTimeInSeconds()).isEqualTo(45);
        assertThat(actualRecipe.get(0).getCookwares()).isEqualTo(3L);
    }

    @Test
    @DisplayName("단계별 레시피의 대상 레시피가 존재하지 않을 경우, 예외를 발생한다.")
    public void recipeNotFound(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();

        userRepository.save(user);
        Long recipeId = recipeRepository.save(recipe).getId();

        List<UploadRecipeDetailRequest> requests = List.of(
                new UploadRecipeDetailRequest("Content1", "", 30, List.of("MICROWAVE")),
                new UploadRecipeDetailRequest("Content2", "", 15, List.of("OVEN")));

        assertThrows(RecipeNotFoundException.class, ()->
                recipeDetailService.uploadRecipeDetails(recipeId + 100, requests));
    }

    @Test
    @DisplayName("단계별 레시피들이 성공적으로 제거된다.")
    public void clearDetails(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();
        userRepository.save(user);
        recipeRepository.save(recipe);

        recipeDetailRepository.save(new RecipeDetail(recipe, "Content1", "", 30, 1L, 0));
        recipeDetailRepository.save(new RecipeDetail(recipe, "Content2", "", 15, 2L, 1));
        recipeDetailRepository.save(new RecipeDetail(recipe, "Content3", "", 10, 4L, 2));

        recipeDetailService.clearRecipeDetails(recipe);
        List<RecipeDetail> actual = recipeDetailRepository.findAll();
        assertThat(actual).isEmpty();
    }
    @Test
    @Transactional
    @DisplayName("단계별 레시피 업데이트 시, 기존 항목들은 삭제된다.")
    public void checkDeleted(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().user(user).difficulty(Difficulty.EASY).title("주먹밥").build();
        userRepository.save(user);
        Long recipeId = recipeRepository.save(recipe).getId();

        recipeDetailRepository.save(new RecipeDetail(recipe, "Content1", "", 30, 1L, 0));
        recipeDetailRepository.save(new RecipeDetail(recipe, "Content2", "", 15, 2L, 1));
        recipeDetailRepository.save(new RecipeDetail(recipe, "Content3", "", 10, 3L, 2));

        List<UploadRecipeDetailRequest> requests = List.of(
                new UploadRecipeDetailRequest("Content4", "", 30, List.of("MICROWAVE")),
                new UploadRecipeDetailRequest("Content5", "", 15, List.of("OVEN"))
        );

        recipeDetailService.uploadRecipeDetails(recipeId, requests);

        List<RecipeDetail> actual = recipeDetailRepository.findAll();
        assertThat(actual).hasSize(2);

        List<Recipe> recipeActual = recipeRepository.findAll();
        assertThat(recipeActual).hasSize(1);

        assertThat(recipeActual.get(0).getTotalCookTimeInSeconds()).isEqualTo(45);
        assertThat(recipeActual.get(0).getCookwares()).isEqualTo(5L);
    }

    @AfterEach
    void teardown(){
        recipeDetailRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();
    }

}
