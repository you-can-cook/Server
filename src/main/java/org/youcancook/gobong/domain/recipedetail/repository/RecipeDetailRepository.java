package org.youcancook.gobong.domain.recipedetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.youcancook.gobong.domain.recipedetail.entity.RecipeDetail;

import java.util.List;

public interface RecipeDetailRepository extends JpaRepository<RecipeDetail, Long> {

    List<RecipeDetail> findAllByRecipeId(Long recipeId);

    @Query("SELECT rd FROM RecipeDetail rd WHERE rd.recipe.id =:recipeId ORDER BY rd.step ASC")
    List<RecipeDetail> findAllByRecipeIdOrderByStep(Long recipeId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM RecipeDetail as rd where rd.recipe.id =:recipeId")
    void deleteAllByRecipeId(Long recipeId);
}
