package org.youcancook.gobong.domain.bookmarkrecipe.service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.youcancook.gobong.domain.recipe.dto.request.CreateRecipeRequest;
import org.youcancook.gobong.domain.recipe.dto.response.CreateRecipeResponse;
import org.youcancook.gobong.domain.recipedetail.dto.request.UploadRecipeDetailRequest;
import org.youcancook.gobong.global.util.acceptance.AcceptanceTest;
import org.youcancook.gobong.global.util.acceptance.AcceptanceUtils;

import java.util.List;

public class BookmarkRecipeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("북마크를 성공적으로 등록한다.")
    public void addBookmark(){
        String token1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "GOOGLE", "123");
        String token2 = AcceptanceUtils.signUpAndGetToken("배고파요", "KAKAO", "1234");

        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "주먹밥을 만들어요", List.of("밥", "김"), "쉬워요", null,
                List.of(new UploadRecipeDetailRequest("주먹모양으로 쥐세요", null, 15, List.of())));
        Long recipeId = AcceptanceUtils.createDummyRecipe(token1, request).as(CreateRecipeResponse.class).getId();

        RestAssured.given().log().all()
                .auth().oauth2(token2)
                .when().post("/api/bookmarks/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("북마크를 성공적으로 제거한다.")
    public void removeBookmark(){
        String token1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "GOOGLE", "123");
        String token2 = AcceptanceUtils.signUpAndGetToken("배고파요", "KAKAO", "1234");

        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "주먹밥을 만들어요", List.of("밥", "김"), "쉬워요", null,
                List.of(new UploadRecipeDetailRequest("주먹모양으로 쥐세요", null, 15, List.of())));
        Long recipeId = AcceptanceUtils.createDummyRecipe(token1, request).as(CreateRecipeResponse.class).getId();

        RestAssured.given().log().all()
                .auth().oauth2(token2)
                .when().post("/api/bookmarks/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        RestAssured.given().log().all()
                .auth().oauth2(token2)
                .when().delete("/api/bookmarks/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
