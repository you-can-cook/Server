package org.youcancook.gobong.domain.rating.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.youcancook.gobong.domain.rating.dto.request.RateRecipeRequest;
import org.youcancook.gobong.domain.recipe.dto.request.CreateRecipeRequest;
import org.youcancook.gobong.domain.recipe.dto.response.CreateRecipeResponse;
import org.youcancook.gobong.global.util.acceptance.AcceptanceTest;
import org.youcancook.gobong.global.util.acceptance.AcceptanceUtils;

import java.util.List;

class RatingAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("사용자가 평점을 등록한다.")
    public void addRating(){
        String token1 = AcceptanceUtils.signUpAndGetToken("name1", "Google", "1234");
        String token2 = AcceptanceUtils.signUpAndGetToken("name2", "Google", "1235");
        Long recipeId = AcceptanceUtils.createDummyRecipe(token1, new CreateRecipeRequest(
                "주먹밥", "주먹밥을 만들어요", List.of("김", "밥"), "쉬워요", null, List.of())
        ).as(CreateRecipeResponse.class).getId();

        RateRecipeRequest request = new RateRecipeRequest(recipeId, 3);

        RestAssured.given().log().all()
                .auth().oauth2(token2)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/ratings")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("사용자가 평점을 수정한다.")
    public void updateRating(){
        String token1 = AcceptanceUtils.signUpAndGetToken("name1", "Google", "123");
        String token2 = AcceptanceUtils.signUpAndGetToken("name2", "Google", "1235");
        Long recipeId = AcceptanceUtils.createDummyRecipe(token1, new CreateRecipeRequest(
                "주먹밥", "주먹밥을 만들어요", List.of("김", "밥"), "쉬워요", null, List.of())
        ).as(CreateRecipeResponse.class).getId();

        RateRecipeRequest request1 = new RateRecipeRequest(recipeId, 3);

        RestAssured.given().log().all()
                .auth().oauth2(token2)
                .contentType(ContentType.JSON)
                .body(request1)
                .when().post("/api/ratings")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RateRecipeRequest updateRequest = new RateRecipeRequest(recipeId, 5);

        RestAssured.given().log().all()
                .auth().oauth2(token2)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when().put("/api/ratings")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

}