package org.youcancook.gobong.domain.recipe.dto.feedfilterdto;

import org.youcancook.gobong.domain.recipe.exception.IllegalQueryTypeException;

import java.util.Arrays;

public enum QueryType {
    TITLE, AUTHOR_NAME;

    public static QueryType from(String value){
        return Arrays.stream(values())
                .filter(queryType -> queryType.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(IllegalQueryTypeException::new);
    }
}
