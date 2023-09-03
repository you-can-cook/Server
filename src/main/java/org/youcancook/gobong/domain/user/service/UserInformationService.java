package org.youcancook.gobong.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.follow.repository.FollowRepository;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.user.dto.response.MyInformationResponse;
import org.youcancook.gobong.domain.user.dto.response.UserInformationResponse;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.exception.DuplicationNicknameException;
import org.youcancook.gobong.domain.user.exception.UserNotFoundException;
import org.youcancook.gobong.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInformationService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final RecipeRepository recipeRepository;

    public MyInformationResponse getMyInformation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Long followeeNumber = followRepository.countByFollower(user);
        Long followerNumber = followRepository.countByFollowee(user);
        Long recipeNumber = recipeRepository.countByUser(user);
        return MyInformationResponse.of(user, followerNumber, followeeNumber, recipeNumber);
    }

    public UserInformationResponse getUserInformation(Long myId, Long userId) {
        User me = userRepository.findById(myId)
                .orElseThrow(UserNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Long followeeNumber = followRepository.countByFollower(user);
        Long followerNumber = followRepository.countByFollowee(user);
        Long recipeNumber = recipeRepository.countByUser(user);
        boolean isFollowed = followRepository.existsByFollowerAndFollowee(me, user);
        return UserInformationResponse.of(user, followerNumber, followeeNumber, recipeNumber, isFollowed);
    }

    @Transactional
    public void updateInformation(Long userId, String nickname, String profileImageURL) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        if (!user.getNickname().equals(nickname)) {
            validateDuplicateNickname(nickname);
        }

        user.updateNicknameAndProfileImageURL(nickname, profileImageURL);
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicationNicknameException();
        }
    }

}
