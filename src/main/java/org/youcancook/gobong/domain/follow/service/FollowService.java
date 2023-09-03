package org.youcancook.gobong.domain.follow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.follow.dto.response.FindFolloweeResponse;
import org.youcancook.gobong.domain.follow.dto.response.FindFollowerResponse;
import org.youcancook.gobong.domain.follow.entity.Follow;
import org.youcancook.gobong.domain.follow.exception.AlreadyFollowingException;
import org.youcancook.gobong.domain.follow.exception.NotFollowingException;
import org.youcancook.gobong.domain.follow.repository.FollowRepository;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.exception.UserNotFoundException;
import org.youcancook.gobong.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(UserNotFoundException::new);
        User followee = userRepository.findById(followeeId)
                .orElseThrow(UserNotFoundException::new);

        validateAlreadyFollowing(follower, followee);

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
        followRepository.save(follow);
    }

    private void validateAlreadyFollowing(User follower, User followee) {
        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new AlreadyFollowingException();
        }
    }

    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(UserNotFoundException::new);
        User followee = userRepository.findById(followeeId)
                .orElseThrow(UserNotFoundException::new);

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(NotFollowingException::new);
        followRepository.delete(follow);
    }

    public List<FindFollowerResponse> findFollowerList(Long loginUserId) {
        User user = userRepository.findById(loginUserId)
                .orElseThrow(UserNotFoundException::new);
        List<Long> userFollowedMe = followRepository.findByFollower(user).stream()
                .map(follow -> follow.getFollowee().getId())
                .toList();

        return followRepository.findByFollowee(user).stream()
                .map(follow -> FindFollowerResponse.of(
                        follow.getFollower().getId(),
                        follow.getFollower().getNickname(),
                        follow.getFollower().getProfileImageURL(),
                        userFollowedMe.contains(follow.getFollower().getId())
                ))
                .toList();
    }

    public List<FindFolloweeResponse> findFolloeeList(Long loginUserId) {
        User user = userRepository.findById(loginUserId)
                .orElseThrow(UserNotFoundException::new);

        return followRepository.findByFollower(user).stream()
                .map(follow -> FindFolloweeResponse.of(
                        follow.getFollowee().getId(),
                        follow.getFollowee().getNickname(),
                        follow.getFollowee().getProfileImageURL()
                ))
                .toList();
    }

    public boolean isFollowing(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(UserNotFoundException::new);
        User followee = userRepository.findById(followeeId)
                .orElseThrow(UserNotFoundException::new);

        return followRepository.existsByFollowerAndFollowee(follower, followee);
    }

}
