package org.youcancook.gobong.domain.recipe.entity;

import lombok.Getter;
import org.youcancook.gobong.domain.recipe.exception.IllegalCookwareException;
import org.youcancook.gobong.global.util.clock.bitwise.Bitwise;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Cookware {
    MICROWAVE(0), AIR_FRYER(1), OVEN(2), GAS_RANGE(3),
    MIXER(4), ELECTRIC_KETTLE(5), PAN(6),
    ;

    private final long value;

    Cookware(int power) {
        this.value = 1L << power;
    }

    public static List<String> bitToList(long cookwaresBit){
        return Stream.of(Cookware.values())
                .filter(cookware -> (cookware.value & cookwaresBit) != 0)
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static Cookware from(String cookware){
        return Stream.of(Cookware.values())
                .filter(c -> c.name().equals(cookware))
                .findFirst()
                .orElseThrow(IllegalCookwareException::new);
    }

    public static long namesToCookwareBit(List<String> cookwares){
        return cookwares.stream()
                .map(Cookware::from)
                .mapToLong(Cookware::getValue)
                .reduce(0L, Bitwise::or);
    }

}
