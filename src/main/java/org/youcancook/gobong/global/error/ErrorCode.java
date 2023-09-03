package org.youcancook.gobong.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "C001", "엔티티 조회에 실패하였습니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "잘못된 HTTP 메서드입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C004", "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "서버 내부에서 에러가 발생하였습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C006", "Not Found"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C007", "Bad Request"),

    // Authentication
    EMPTY_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A001", "Authorization Header가 빈 값입니다."),
    NOT_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "A002", "인증 타입이 Bearer 타입이 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A005", "Unauthorized"),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "A006", "잘못된 토큰 타입입니다."),

    // OAuth
    OAUTH_PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O004", "OAuth provider를 찾을 수 없습니다."),

    // User
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "U001", "이미 등록된 닉네임입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U004", "유저를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U001", "이미 등록된 유저입니다."),

    // Recipe
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "R004", "레시피를 찾을 수 없습니다."),

    // Recipe Details,
    RECIPE_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "R104", "단계별 레시피를 찾을 수 없습니다."),

    // Rating
    RATING_RANGE_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "R201", "평점 범위는 1부터 5 사이입니다."),
    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "R204", "평점을 찾을 수 없습니다."),
    RATE_ON_MY_RECIPE(HttpStatus.BAD_REQUEST, "R202", "나의 레시피에 평점을 남길 수 없습니다."),

    // Refresh Token
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "R304", "refresh 토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "R301", "유효하지 않은 refresh 토큰입니다."),

    // Bookmarks
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "B004", "북마크에 담긴 레시피가 아닙니다."),
    ALREADY_BOOKMARKED_RECIPE(HttpStatus.BAD_REQUEST, "B001", "이미 북마크로 등록한 레시피입니다."),

    // Temporary Token
    TEMPORARY_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "T004", "임시 토큰을 찾을 수 없습니다."),

    // Difficulty Argument
    INVALID_DIFFICULTY_ARGUMENT(HttpStatus.BAD_REQUEST, "D003", "난이도는 '쉬워요', '보통이에요', '어려워요' 중 하나여야 합니다."),

    // S3 File upload
    FAILED_TO_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일 업로드를 실패했습니다."),

    // Follow
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "F101", "이미 팔로우한 사용자입니다."),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, "F102", "팔로우하지 않은 사용자입니다."),

    // Cookwares
    ILLEGAL_COOKWARE(HttpStatus.NOT_FOUND, "C104", "존재하지 않는 조리기구입니다."),

    // FilterType
    INVALID_FILTER_TYPE(HttpStatus.BAD_REQUEST, "F201", "필터 타입은 'recent', 'popular' 중 하나여야 합니다."),
    INVALID_QUERY_TYPE(HttpStatus.BAD_REQUEST, "F202", "쿼리 필터 타입은 'name', 'authorName' 중 하나여야 합니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
