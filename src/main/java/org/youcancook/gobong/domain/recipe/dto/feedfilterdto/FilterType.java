package org.youcancook.gobong.domain.recipe.dto.feedfilterdto;

import org.youcancook.gobong.domain.recipe.exception.IllegalFilterTypeException;

import java.util.Arrays;

public enum FilterType {
    RECENT, POPULAR;

    public static FilterType from(String value){
        return Arrays.stream(values())
                .filter(filterType -> filterType.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(IllegalFilterTypeException::new);
    }
}
