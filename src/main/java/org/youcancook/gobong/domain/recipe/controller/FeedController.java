package org.youcancook.gobong.domain.recipe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.youcancook.gobong.domain.recipe.dto.feedfilterdto.FeedFilterDto;
import org.youcancook.gobong.domain.recipe.dto.feedfilterdto.ModifiedFeedFilterDto;
import org.youcancook.gobong.domain.recipe.dto.feedfilterdto.QueryType;
import org.youcancook.gobong.domain.recipe.dto.response.GetFeedResponse;
import org.youcancook.gobong.domain.recipe.service.GetRecipeService;
import org.youcancook.gobong.global.resolver.LoginUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/feed")
public class FeedController {

    private final GetRecipeService getRecipeService;

    @GetMapping("all")
    public ResponseEntity<GetFeedResponse> getAllFeed(@LoginUserId Long userId,
                                                      @RequestParam(name = "last", required = false) Long lastRecipeId,
                                                      @RequestParam(name = "count") int count){
        lastRecipeId = lastRecipeId == null ? Long.MAX_VALUE : lastRecipeId;
        GetFeedResponse response = getRecipeService.getAllFeed(userId, lastRecipeId, count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("bookmarked")
    public ResponseEntity<GetFeedResponse> getBookmarkedFeed(@LoginUserId Long userId,
                                                   @RequestParam(name = "last", required = false) Long lastRecipeId,
                                                   @RequestParam(name = "count") int count){
        lastRecipeId = lastRecipeId == null ? Long.MAX_VALUE : lastRecipeId;
        GetFeedResponse response = getRecipeService.getBookmarkedFeed(userId, lastRecipeId, count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("following")
    public ResponseEntity<GetFeedResponse> getFollowingFeed(@LoginUserId Long userId,
                                                             @RequestParam(name = "last", required = false) Long lastRecipeId,
                                                             @RequestParam(name = "count") int count){
        lastRecipeId = lastRecipeId == null ? Long.MAX_VALUE : lastRecipeId;
        GetFeedResponse response = getRecipeService.getFollowingFeed(userId, lastRecipeId, count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("my")
    public ResponseEntity<GetFeedResponse> getMyFeed(@LoginUserId Long userId,
                                                     @RequestParam(name = "last", required = false) Long lastRecipeId,
                                                     @RequestParam(name = "count") int count) {
        lastRecipeId = lastRecipeId == null ? Long.MAX_VALUE : lastRecipeId;
        GetFeedResponse response = getRecipeService.getMyFeed(userId, lastRecipeId, count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{userId}")
    public ResponseEntity<GetFeedResponse> getUserFeed(@LoginUserId Long myId,
                                                       @PathVariable Long userId,
                                                       @RequestParam(name = "last", required = false) Long lastRecipeId,
                                                       @RequestParam(name = "count") int count) {
        lastRecipeId = lastRecipeId == null ? Long.MAX_VALUE : lastRecipeId;
        GetFeedResponse response = getRecipeService.getUserFeed(userId, myId, lastRecipeId, count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("filter")
    public ResponseEntity<GetFeedResponse> getFilteredFeedByTitle(@LoginUserId Long userId,
                                                           @RequestParam(name = "page") int page,
                                                           @RequestParam(name = "count") int count,
                                                           @RequestBody @Valid FeedFilterDto filter){
        ModifiedFeedFilterDto modifiedFilter = ModifiedFeedFilterDto.from(filter);
        GetFeedResponse response = modifiedFilter.getQueryType() == QueryType.TITLE ?
                getRecipeService.getFilteredFeedByName(userId, page, count, modifiedFilter) :
                getRecipeService.getFilteredFeedByRecipeTitle(userId, page, count, modifiedFilter);
        return ResponseEntity.ok(response);
    }


}
