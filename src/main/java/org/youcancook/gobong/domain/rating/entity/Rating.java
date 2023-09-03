package org.youcancook.gobong.domain.rating.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.domain.rating.exception.IllegalRatingException;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rating",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "recipe_id"})
})
public class Rating {

    private static final Integer MIN_RATING = 1;
    private static final Integer MAX_RATING = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(nullable = false)
    private Integer score;

    @Builder
    public Rating(User user, Recipe recipe, Integer score) {
        validateRatingRange(score);
        this.user = user;
        this.recipe = recipe;
        this.score = score;
        recipe.updateRatingDelta(score);
        recipe.increaseTotalRatedNum();
    }
    public void updateScore(Integer score) {
        validateRatingRange(score);
        recipe.updateRatingDelta(-this.score);
        this.score = score;
        recipe.updateRatingDelta(this.score);
    }

    public void validateRatingRange(Integer rating){
        if (rating < MIN_RATING || rating > MAX_RATING){
            throw new IllegalRatingException();
        }
    }
}
