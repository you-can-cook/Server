package org.youcancook.gobong.domain.recipedetail.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class UploadRecipeDetailRequest {
    private String content;
    private String imageURL;

    @NotNull(message = "조리시간은 필수 항목입니다.")
    private Integer cookTimeInSeconds;

    @NotNull(message = "조리도구는 필수 항목입니다.")
    private List<String> cookwares;
}
