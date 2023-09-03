package org.youcancook.gobong.domain.recipe.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.domain.BaseTime.BaseTime;
import org.youcancook.gobong.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe")
public class Recipe extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    private String introduction;

    private String ingredients;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String thumbnailURL;

    private long cookwares;
    private int totalBookmarkCount;
    private int totalCookTimeInSeconds;
    private int totalRating;
    private int totalRatedNum;

    @Builder
    public Recipe(User user, String title, String introduction, String ingredients, Difficulty difficulty,
                  String thumbnailURL) {
        this.user = user;
        this.title = title;
        this.introduction = introduction;
        this.ingredients = ingredients;
        this.difficulty = difficulty;
        this.thumbnailURL = thumbnailURL;
        cookwares = 0L;
        totalCookTimeInSeconds = 0;
        totalBookmarkCount = 0;
    }

    public Double getAverageRating(){
        if (totalRatedNum == 0) return null;
        return (double) totalRating / (double) totalRatedNum;
    }

    public void updateProperties(String title, String introduction, String ingredients,
                                 Difficulty difficulty, String thumbnailURL) {
        this.title = title;
        this.introduction = introduction;
        this.ingredients = ingredients;
        this.difficulty = difficulty;
        this.thumbnailURL = thumbnailURL;
    }

    public void addCookware(Long cookware) {
        this.cookwares |= cookware;
    }

    public void addCookTime(Integer cookTimeInSeconds) {
        this.totalCookTimeInSeconds += cookTimeInSeconds;
    }

    public void clearDetails() {
        this.cookwares = 0L;
        this.totalCookTimeInSeconds = 0;
    }

    public void updateRatingDelta(int delta) {
        this.totalRating += delta;
    }

    public void increaseTotalRatedNum(){
        this.totalRatedNum++;
    }

    public void decreaseTotalRatedNum(){
        this.totalRatedNum--;
    }

    public void increaseBookmarkCount() {
        this.totalBookmarkCount++;
    }

    public void decreaseBookmarkCount(){
        this.totalBookmarkCount--;
    }
}
