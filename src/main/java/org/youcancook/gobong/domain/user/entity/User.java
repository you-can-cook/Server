package org.youcancook.gobong.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.domain.recipe.entity.Recipe;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"oauth_id", "oauth_provider"})
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, name = "oauth_id")
    private String oAuthId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "oauth_provider")
    private OAuthProvider oAuthProvider;

    private String profileImageURL;

    @OneToMany(mappedBy = "user")
    private List<Recipe> recipes = new ArrayList<>();

    @Builder
    public User(String nickname, String oAuthId, OAuthProvider oAuthProvider, String profileImageURL) {
        this.nickname = nickname;
        this.oAuthId = oAuthId;
        this.oAuthProvider = oAuthProvider;
        this.profileImageURL = profileImageURL;
    }

    public void updateNicknameAndProfileImageURL(String nickname, String profileImageURL) {
        this.nickname = nickname;
        this.profileImageURL = profileImageURL;
    }
}
