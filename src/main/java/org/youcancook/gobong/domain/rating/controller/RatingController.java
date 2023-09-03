package org.youcancook.gobong.domain.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.youcancook.gobong.domain.rating.dto.request.RateRecipeRequest;
import org.youcancook.gobong.domain.rating.service.RatingService;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.resolver.LoginUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> addRating(@LoginUserId Long userId,
                                          @RequestBody @Valid RateRecipeRequest request){
        ratingService.createRating(userId, request.getRecipeId(), request.getScore());
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> editRating(@LoginUserId Long userId,
                                           @RequestBody @Valid RateRecipeRequest request){
        ratingService.updateRating(userId, request.getRecipeId(), request.getScore());
        return ResponseEntity.ok().build();
    }

}
