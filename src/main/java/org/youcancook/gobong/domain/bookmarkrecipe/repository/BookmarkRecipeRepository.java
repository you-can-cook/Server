package org.youcancook.gobong.domain.bookmarkrecipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.youcancook.gobong.domain.bookmarkrecipe.entity.BookmarkRecipe;

import java.util.Optional;

public interface BookmarkRecipeRepository extends JpaRepository<BookmarkRecipe, Long> {
    Optional<BookmarkRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);

    Boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);

    @Modifying
    @Query("DELETE FROM BookmarkRecipe bm where bm.recipe.id =:recipeId")
    void deleteAllByRecipeId(Long recipeId);

}
