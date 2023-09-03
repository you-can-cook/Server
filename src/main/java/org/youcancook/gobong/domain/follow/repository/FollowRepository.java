package org.youcancook.gobong.domain.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.youcancook.gobong.domain.follow.entity.Follow;
import org.youcancook.gobong.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowee(User follower, User followee);

    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    @Query("select f from Follow f join fetch f.follower where f.followee = :followee")
    List<Follow> findByFollowee(User followee);

    @Query("select f from Follow f join fetch f.followee where f.follower = :follower")
    List<Follow> findByFollower(User follower);

    Long countByFollower(User user);

    Long countByFollowee(User user);
}
