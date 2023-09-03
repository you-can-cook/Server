package org.youcancook.gobong.domain.recipe.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.youcancook.gobong.domain.recipe.exception.IllegalDifficultyDescriptionException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    EASY("쉬워요"), NORMAL("보통이에요"), HARD("어려워요");

    private final String description;

    public static Difficulty from(String description){
        return Arrays.stream(values())
                .filter(it -> it.getDescription().equals(description))
                .findFirst()
                .orElseThrow(IllegalDifficultyDescriptionException::new);
    }

}
