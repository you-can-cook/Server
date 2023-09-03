package org.youcancook.gobong.domain.recipe.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.youcancook.gobong.domain.recipe.exception.IllegalCookwareException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CookwareTest {
    @Test
    @DisplayName("조리기구 비트를 조리기구 리스트로 변환한다.")
    public void convertBitToCookwares(){
        long cookwareBit = Cookware.OVEN.getValue() | Cookware.MICROWAVE.getValue() | Cookware.ELECTRIC_KETTLE.getValue();
        List<String> actual = Cookware.bitToList(cookwareBit);

        assertThat(actual).hasSize(3);
        assertThat(actual).hasSameElementsAs(
                List.of(Cookware.OVEN.name(), Cookware.MICROWAVE.name(), Cookware.ELECTRIC_KETTLE.name()));
    }

    @Test
    @DisplayName("조리기구 리스트를 조리기구 비트로 변환한다.")
    public void convertCookwareNameListToBit(){
        List<String> cookwareNames = List.of("OVEN", "MICROWAVE", "PAN");
        long actual = Cookware.namesToCookwareBit(cookwareNames);

        assertThat(actual).isEqualTo(Cookware.OVEN.getValue() | Cookware.MICROWAVE.getValue() | Cookware.PAN.getValue());
    }

    @Test
    @DisplayName("잘못된 조리기구가 비트로 변환되려 할 경우 예외를 발생한다.")
    public void invalidCookware(){
        List<String> cookwareNames = List.of("OVEN", "MICROWAVE", "PAN", "PINEAPPLE");
        assertThrows(IllegalCookwareException.class, () -> Cookware.namesToCookwareBit(cookwareNames));
    }
}