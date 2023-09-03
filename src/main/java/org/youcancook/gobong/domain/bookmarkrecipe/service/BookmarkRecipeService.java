package org.youcancook.gobong.domain.bookmarkrecipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.bookmarkrecipe.dto.response.AddBookmarkResponse;
import org.youcancook.gobong.domain.bookmarkrecipe.entity.BookmarkRecipe;
import org.youcancook.gobong.domain.bookmarkrecipe.exception.AlreadyBookmarkedRecipeException;
import org.youcancook.gobong.domain.bookmarkrecipe.exception.BookmarkRecipeNotFoundException;
import org.youcancook.gobong.domain.bookmarkrecipe.repository.BookmarkRecipeRepository;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.exception.RecipeNotFoundException;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.exception.UserNotFoundException;
import org.youcancook.gobong.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkRecipeService {

    private final BookmarkRecipeRepository bookmarkRecipeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public AddBookmarkResponse addBookmark(Long userId, Long recipeId){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(RecipeNotFoundException::new);
        if (bookmarkRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId))
            throw new AlreadyBookmarkedRecipeException();

        BookmarkRecipe bookmarkRecipe = new BookmarkRecipe(user, recipe);

        Long id = bookmarkRecipeRepository.save(bookmarkRecipe).getId();
        return new AddBookmarkResponse(id);
    }

    @Transactional
    public void removeBookmark(Long userId, Long recipeId){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(RecipeNotFoundException::new);
        BookmarkRecipe bookmarkRecipe = bookmarkRecipeRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(BookmarkRecipeNotFoundException::new);
        recipe.decreaseBookmarkCount();
        bookmarkRecipeRepository.delete(bookmarkRecipe);
    }

    public boolean checkBookmark(Long userId, Long recipeId){
        return bookmarkRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }
}
