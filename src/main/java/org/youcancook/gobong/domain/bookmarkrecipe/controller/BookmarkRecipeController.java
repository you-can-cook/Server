package org.youcancook.gobong.domain.bookmarkrecipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.youcancook.gobong.domain.bookmarkrecipe.dto.response.AddBookmarkResponse;
import org.youcancook.gobong.domain.bookmarkrecipe.service.BookmarkRecipeService;
import org.youcancook.gobong.global.resolver.LoginUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bookmarks/{recipeId}")
public class BookmarkRecipeController {

    private final BookmarkRecipeService bookmarkRecipeService;

    @PostMapping
    public ResponseEntity<AddBookmarkResponse> addBookmark(@LoginUserId Long userId,
                                                           @PathVariable Long recipeId){
        AddBookmarkResponse response = bookmarkRecipeService.addBookmark(userId, recipeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(@LoginUserId Long userId, @PathVariable Long recipeId){
        bookmarkRecipeService.removeBookmark(userId, recipeId);
        return ResponseEntity.ok().build();
    }
}
