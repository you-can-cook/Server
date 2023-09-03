package org.youcancook.gobong.domain.follow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.youcancook.gobong.domain.follow.dto.response.FindFolloweeResponse;
import org.youcancook.gobong.domain.follow.dto.response.FindFollowerResponse;
import org.youcancook.gobong.domain.follow.service.FollowService;
import org.youcancook.gobong.global.resolver.LoginUserId;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class FollowController {

    private final FollowService followService;

    @PostMapping("follow/{folloeeId}")
    public ResponseEntity<Void> follow(@LoginUserId Long loginUserId,
                                       @PathVariable Long folloeeId) {
        followService.follow(loginUserId, folloeeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("unfollow/{folloeeId}")
    public ResponseEntity<Void> unfollow(@LoginUserId Long loginUserId,
                                         @PathVariable Long folloeeId) {
        followService.unfollow(loginUserId, folloeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("follower")
    public ResponseEntity<List<FindFollowerResponse>> findFollowerList(@LoginUserId Long loginUserId) {
        List<FindFollowerResponse> response = followService.findFollowerList(loginUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("following")
    public ResponseEntity<List<FindFolloweeResponse>> findFollowingList(@LoginUserId Long loginUserId) {
        List<FindFolloweeResponse> response = followService.findFolloeeList(loginUserId);
        return ResponseEntity.ok(response);
    }
}
