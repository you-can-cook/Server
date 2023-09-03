package org.youcancook.gobong.global.util.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.youcancook.gobong.domain.recipe.dto.request.CreateRecipeRequest;
import org.youcancook.gobong.domain.user.dto.request.SignupRequest;
import org.youcancook.gobong.domain.user.dto.response.TemporaryTokenIssueResponse;
import org.youcancook.gobong.domain.user.dto.response.MyInformationResponse;
import org.youcancook.gobong.global.util.token.TokenDto;

public class AcceptanceUtils {

    public static String getTemporaryToken(){
        return RestAssured.given().log().all()
                .when().post("/api/users/temporary-token")
                .then().log().all()
                .extract()
                .as(TemporaryTokenIssueResponse.class)
                .getTemporaryToken();
    }

    public static String signUpAndGetToken(String name, String oAuthProvider, String oAuthId) {
        String temporaryToken = getTemporaryToken();
        SignupRequest request = new SignupRequest(name, oAuthProvider, oAuthId, temporaryToken, null);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/users/signup")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TokenDto.class)
                .getAccessToken();
    }

    public static ExtractableResponse<Response> createDummyRecipe(String token, CreateRecipeRequest request){
        return RestAssured.given().log().all()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/recipes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void bookmarkRecipe(String token, Long recipeId){
        RestAssured.given().log().all()
            .auth().oauth2(token)
            .when().post("/api/bookmarks/" + recipeId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static Long getUserIdFromToken(String token){
        return RestAssured.given().log().all()
                .auth().oauth2(token)
                .when().get("/api/users")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(MyInformationResponse.class)
                .getId();
    }

    public static void followUser(String token, Long userId){
        RestAssured.given().log().all()
                .auth().oauth2(token)
                .when().post("/api/follow/" + userId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
