package org.youcancook.gobong.domain.recipe.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.youcancook.gobong.domain.recipe.dto.feedfilterdto.FeedFilterDto;
import org.youcancook.gobong.domain.recipe.dto.request.CreateRecipeRequest;
import org.youcancook.gobong.domain.recipe.dto.request.UpdateRecipeRequest;
import org.youcancook.gobong.domain.recipe.dto.response.CreateRecipeResponse;
import org.youcancook.gobong.domain.recipedetail.dto.request.UploadRecipeDetailRequest;
import org.youcancook.gobong.global.util.acceptance.AcceptanceTest;
import org.youcancook.gobong.global.util.acceptance.AcceptanceUtils;

import java.util.List;

public class RecipeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("레시피를 성공적으로 등록한다.")
    public void createRecipe() {
        String accessToken = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "쉽게 만드는 주먹밥", List.of("밥", "김"), "쉬워요", null, List.of(
                new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of()),
                new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of())
        ));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .body(request)
                .when().post("/api/recipes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("레시피를 성공적으로 삭제한다.")
    public void deleteRecipe() {
        String accessToken = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "쉽게 만드는 주먹밥", List.of("밥", "김"), "쉬워요", null, List.of(
                new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of()),
                new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of())
        ));
        Long recipeId = AcceptanceUtils.createDummyRecipe(accessToken, request).as(CreateRecipeResponse.class).getId();

        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/api/recipes/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("권한없는 레시피 삭제 요청 시 올바른 에러코드를 반환한다.")
    public void noAuth() {
        String accessToken1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        String accessToken2 = AcceptanceUtils.signUpAndGetToken("쩝쩝학사", "GOOGLE", "123a");
        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "쉽게 만드는 주먹밥", List.of("밥", "김"), "쉬워요", null, List.of(
                new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of()),
                new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of())
        ));
        Long recipeId = AcceptanceUtils.createDummyRecipe(accessToken1, request).as(CreateRecipeResponse.class).getId();

        RestAssured.given().log().all()
                .auth().oauth2(accessToken2)
                .when().delete("/api/recipes/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .extract();
    }

    @Test
    @DisplayName("레시피를 성공적으로 수정한다.")
    public void updateRecipe() {
        String accessToken = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "쉽게 만드는 주먹밥", List.of("밥", "김"), "쉬워요", null, List.of(
                new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of()),
                new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of())
        ));
        Long recipeId = AcceptanceUtils.createDummyRecipe(accessToken, request).as(CreateRecipeResponse.class).getId();
        UpdateRecipeRequest updateRequest = new UpdateRecipeRequest("고소한 주먹밥", "쉽게 만드는 주먹밥",
                List.of("밥", "김"), "쉬워요", null, List.of(
                new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of()),
                new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of())));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .auth().oauth2(accessToken)
                .when().put("/api/recipes/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("레시피 단건 조회를 성공한다.")
    public void getRecipe() {
        String accessToken = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "쉽게 만드는 주먹밥", List.of("밥", "김"), "쉬워요", null, List.of(
                new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of("MICROWAVE")),
                new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of("PAN"))
        ));
        Long recipeId = AcceptanceUtils.createDummyRecipe(accessToken, request).as(CreateRecipeResponse.class).getId();

        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/recipes/" + recipeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("전체 피드를 조회한다.")
    public void getFeed() {
        String accessToken = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        for (int i = 1; i <= 10; i++) {
            CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "1번 레시피", List.of("밥", "김"), "쉬워요", null, List.of(
                    new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of("MICROWAVE")),
                    new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of("PAN"))
            ));
            AcceptanceUtils.createDummyRecipe(accessToken, request).as(CreateRecipeResponse.class);
        }

        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/feed/all?count=3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

    }

    @Test
    @DisplayName("북마크한 피드를 조회한다.")
    public void getBookmarkedFeed() {
        String accessToken1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        String accessToken2 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사2", "KAKAO", "1234");
        for (int i = 1; i <= 10; i++) {
            CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "1번 레시피", List.of("밥", "김"), "쉬워요", null, List.of(
                    new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of("MICROWAVE")),
                    new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of("PAN"))
            ));
            Long recipeId = AcceptanceUtils.createDummyRecipe(accessToken1, request).as(CreateRecipeResponse.class).getId();
            if (i < 5) AcceptanceUtils.bookmarkRecipe(accessToken2, recipeId);
        }

        RestAssured.given().log().all()
                .auth().oauth2(accessToken2)
                .when().get("/api/feed/bookmarked?count=3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("팔로우 피드를 조회한다.")
    public void getFollowingFeed() {
        String accessToken1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        String accessToken2 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사2", "KAKAO", "1234");
        String accessToken3 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사3", "KAKAO", "12345");

        Long user1Id = AcceptanceUtils.getUserIdFromToken(accessToken1);
        AcceptanceUtils.followUser(accessToken2, user1Id);

        for (int i = 1; i <= 10; i++) {
            CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "1번 레시피", List.of("밥", "김"), "쉬워요", null, List.of(
                    new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of("MICROWAVE")),
                    new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of("PAN"))
            ));

            if (i == 10) AcceptanceUtils.createDummyRecipe(accessToken3, request);
            else AcceptanceUtils.createDummyRecipe(accessToken1, request);
        }

        RestAssured.given().log().all()
                .auth().oauth2(accessToken2)
                .when().get("/api/feed/following?count=3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("나의 피드를 조회한다.")
    public void getMyFeed() {
        String accessToken = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");

        for (int i = 1; i <= 10; i++) {
            CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "1번 레시피", List.of("밥", "김"), "쉬워요", null, List.of(
                    new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of("MICROWAVE")),
                    new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of("PAN"))
            ));
            AcceptanceUtils.createDummyRecipe(accessToken, request);
        }

        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/feed/my?count=3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("다른 사람의 피드를 조회한다.")
    public void getUserFeed() {
        String accessToken1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        String accessToken2 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사123", "KAKAO", "121233");

        Long userId = AcceptanceUtils.getUserIdFromToken(accessToken2);

        for (int i = 1; i <= 10; i++) {
            CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "1번 레시피", List.of("밥", "김"), "쉬워요", null, List.of(
                    new UploadRecipeDetailRequest("소금간을 해 주세요", null, 30, List.of("MICROWAVE")),
                    new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, 15, List.of("PAN"))
            ));
            AcceptanceUtils.createDummyRecipe(i%2 == 0 ? accessToken1 : accessToken2, request);
        }

        RestAssured.given().log().all()
                .auth().oauth2(accessToken2)
                .when().get("/api/feed/" + userId +"?count=3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @Test
    @DisplayName("필터링한 피드를 조회한다.")
    public void getFilteredFeed() {
        String accessToken1 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사", "KAKAO", "123");
        String accessToken2 = AcceptanceUtils.signUpAndGetToken("쩝쩝박사123", "KAKAO", "121233");

        AcceptanceUtils.getUserIdFromToken(accessToken2);

        for (int i = 1; i <= 10; i++) {
            CreateRecipeRequest request = new CreateRecipeRequest("주먹밥", "레시피", List.of("밥", "김"), "쉬워요", null, List.of(
                    new UploadRecipeDetailRequest("소금간을 해 주세요", null, i, List.of("MICROWAVE")),
                    new UploadRecipeDetailRequest("주먹을 쥐어 밥을 뭉쳐주세요", null, i, List.of("PAN"))
            ));
            AcceptanceUtils.createDummyRecipe(accessToken1, request);
        }

        FeedFilterDto feedFilterDto = new FeedFilterDto("주먹", "recent", "쉬워요", 13, null, null);

        RestAssured.given().log().all()
                .auth().oauth2(accessToken2)
                .contentType(ContentType.JSON)
                .body(feedFilterDto)
                .when().post("/api/feed/filter?page=0&count=10")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
