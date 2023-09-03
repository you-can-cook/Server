package org.youcancook.gobong.domain.follow.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.youcancook.gobong.domain.follow.entity.Follow;
import org.youcancook.gobong.domain.follow.exception.AlreadyFollowingException;
import org.youcancook.gobong.domain.follow.exception.NotFollowingException;
import org.youcancook.gobong.domain.follow.repository.FollowRepository;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Test
    @DisplayName("팔로우 성공")
    void followSuccess() {
        // given
        User follower = createTestUser(1L);
        User followee = createTestUser(2L);

        when(userRepository.findById(follower.getId()))
                .thenReturn(Optional.of(follower));
        when(userRepository.findById(followee.getId()))
                .thenReturn(Optional.of(followee));
        when(followRepository.existsByFollowerAndFollowee(follower, followee))
                .thenReturn(false);
        when(followRepository.save(any(Follow.class)))
                .thenReturn(new Follow(follower, followee));

        // when
        followService.follow(follower.getId(), followee.getId());

        // then
        ArgumentCaptor<Follow> argument = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository, times(1)).save(argument.capture());
        assertThat(argument.getValue().getFollower()).isEqualTo(follower);
        assertThat(argument.getValue().getFollowee()).isEqualTo(followee);
    }

    @Test
    @DisplayName("팔로우 실패 - 이미 팔로우한 유저")
    void followFailByAlreadyFollowing() {
        // given
        User follower = createTestUser(1L);
        User followee = createTestUser(2L);

        when(userRepository.findById(follower.getId()))
                .thenReturn(Optional.of(follower));
        when(userRepository.findById(followee.getId()))
                .thenReturn(Optional.of(followee));
        when(followRepository.existsByFollowerAndFollowee(follower, followee))
                .thenReturn(true);

        // when
        assertThrows(AlreadyFollowingException.class, () -> followService.follow(follower.getId(), followee.getId()));
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollowSuccess() {
        // given
        User follower = createTestUser(1L);
        User followee = createTestUser(2L);

        when(userRepository.findById(follower.getId()))
                .thenReturn(Optional.of(follower));
        when(userRepository.findById(followee.getId()))
                .thenReturn(Optional.of(followee));

        Follow follow = new Follow(follower, followee);
        when(followRepository.findByFollowerAndFollowee(follower, followee))
                .thenReturn(Optional.of(follow));
        doNothing().when(followRepository).delete(follow);

        // when
        followService.unfollow(follower.getId(), followee.getId());

        // then
        ArgumentCaptor<Follow> argument = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository, times(1)).delete(argument.capture());
        assertThat(argument.getValue().getFollower()).isEqualTo(follower);
        assertThat(argument.getValue().getFollowee()).isEqualTo(followee);
    }

    @Test
    @DisplayName("언팔로우 실패 - 팔로우하지 않은 유저")
    void unfollowFailByNotFollowing() {
        // given
        User follower = createTestUser(1L);
        User followee = createTestUser(2L);

        when(userRepository.findById(follower.getId()))
                .thenReturn(Optional.of(follower));
        when(userRepository.findById(followee.getId()))
                .thenReturn(Optional.of(followee));
        when(followRepository.findByFollowerAndFollowee(follower, followee))
                .thenReturn(Optional.empty());

        // when
        assertThrows(NotFollowingException.class, () -> followService.unfollow(follower.getId(), followee.getId()));
    }

    @Test
    @DisplayName("팔로우하지 않았다면, 여부를 정상적으로 리턴한다.")
    public void testNotFollowing(){
        User follower = createTestUser(1L);
        User followee = createTestUser(2L);
        when(userRepository.findById(follower.getId()))
                .thenReturn(Optional.of(follower));
        when(userRepository.findById(followee.getId()))
                .thenReturn(Optional.of(followee));
        when(followRepository.existsByFollowerAndFollowee(follower, followee))
                .thenReturn(false);

        assertThat(followService.isFollowing(follower.getId(), followee.getId())).isFalse();
    }

    @Test
    @DisplayName("팔로우했다면, 여부를 정상적으로 출력한다.")
    public void testFollowing(){
        User follower = createTestUser(1L);
        User followee = createTestUser(2L);
        when(userRepository.findById(follower.getId()))
                .thenReturn(Optional.of(follower));
        when(userRepository.findById(followee.getId()))
                .thenReturn(Optional.of(followee));
        when(followRepository.existsByFollowerAndFollowee(follower, followee))
                .thenReturn(true);

        assertThat(followService.isFollowing(follower.getId(), followee.getId())).isTrue();
    }

    private User createTestUser(Long userId) {
        User user = User.builder()
                .nickname("nickname" + userId)
                .oAuthProvider(OAuthProvider.KAKAO)
                .oAuthId("oauthid" + userId)
                .build();
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }
}