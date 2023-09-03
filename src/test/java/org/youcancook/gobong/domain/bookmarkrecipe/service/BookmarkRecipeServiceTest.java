package org.youcancook.gobong.domain.bookmarkrecipe.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.youcancook.gobong.domain.bookmarkrecipe.entity.BookmarkRecipe;
import org.youcancook.gobong.domain.bookmarkrecipe.exception.AlreadyBookmarkedRecipeException;
import org.youcancook.gobong.domain.bookmarkrecipe.exception.BookmarkRecipeNotFoundException;
import org.youcancook.gobong.domain.bookmarkrecipe.repository.BookmarkRecipeRepository;
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
class BookmarkRecipeServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    BookmarkRecipeRepository bookmarkRecipeRepository;
    @Autowired
    BookmarkRecipeService bookmarkRecipeService;

    @Test
    @DisplayName("북마크를 성공적으로 등록한다.")
    public void addBookmarkTest(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().title("주먹밥").difficulty(Difficulty.EASY).build();
        userRepository.save(user);
        recipeRepository.save(recipe);

        bookmarkRecipeService.addBookmark(user.getId(), recipe.getId());
        List<BookmarkRecipe> actual = bookmarkRecipeRepository.findAll();
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getUser().getId()).isEqualTo(user.getId());

        List<Recipe> recipeActual = recipeRepository.findAll();
        assertThat(recipeActual).hasSize(1);
        assertThat(recipeActual.get(0).getTotalBookmarkCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("북마크를 성공적으로 제거한다.")
    public void removeBookmarkTest(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().title("주먹밥").difficulty(Difficulty.EASY).build();
        userRepository.save(user);
        recipe.increaseBookmarkCount();
        recipeRepository.save(recipe);

        BookmarkRecipe bookmarkRecipe = new BookmarkRecipe(user, recipe);
        bookmarkRecipeRepository.save(bookmarkRecipe);

        bookmarkRecipeService.removeBookmark(user.getId(), recipe.getId());

        List<BookmarkRecipe> actual = bookmarkRecipeRepository.findAll();
        assertThat(actual).isEmpty();

        List<Recipe> recipeActual = recipeRepository.findAll();
        assertThat(recipeActual).hasSize(1);
        assertThat(recipeActual.get(0).getTotalBookmarkCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 북마크를 삭제하려고 하면 예외를 발생한다.")
    public void notFoundTest(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().title("주먹밥").difficulty(Difficulty.EASY).build();
        userRepository.save(user);
        Long recipeId = recipeRepository.save(recipe).getId();

        assertThrows(BookmarkRecipeNotFoundException.class, ()->
                bookmarkRecipeService.removeBookmark(user.getId(), recipeId));
    }

    @Test
    @DisplayName("이미 북마크에 등록된 레시피를 다시 등록하면 예외를 발생한다.")
    public void concurrentTest(){
        User user = User.builder().nickname("쩝쩝박사").oAuthProvider(OAuthProvider.GOOGLE).oAuthId("123").build();
        Recipe recipe = Recipe.builder().title("주먹밥").difficulty(Difficulty.EASY).build();
        userRepository.save(user);
        recipeRepository.save(recipe);

        BookmarkRecipe bookmarkRecipe = new BookmarkRecipe(user, recipe);
        bookmarkRecipeRepository.save(bookmarkRecipe);
        assertThrows(AlreadyBookmarkedRecipeException.class, ()->
            bookmarkRecipeService.addBookmark(user.getId(), recipe.getId()));
    }

    @AfterEach
    void teardown(){
        bookmarkRecipeRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();
    }
}