package org.youcancook.gobong.domain.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.youcancook.gobong.domain.rating.entity.Rating;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndRecipeId(Long userId, Long recipeId);

    @Modifying
    @Query("DELETE FROM Rating r where r.recipe.id =:recipeId")
    void deleteAllByRecipeId(Long recipeId);
}
