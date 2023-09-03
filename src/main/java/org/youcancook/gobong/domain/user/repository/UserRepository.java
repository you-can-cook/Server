package org.youcancook.gobong.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.oAuthProvider =:oAuthProvider AND u.oAuthId =:oAuthId")
    Optional<User> findByOAuthProviderAndOAuthId(OAuthProvider oAuthProvider, String oAuthId);

    boolean existsByNickname(String nickname);

    @Query("SELECT exists(SELECT 1 FROM User u WHERE u.oAuthProvider =:oAuthProvider AND u.oAuthId =:oAuthId)")
    boolean existsByOAuthProviderAndOAuthId(OAuthProvider oAuthProvider, String oAuthId);
}
