package org.youcancook.gobong.domain.BaseTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.youcancook.gobong.domain.recipe.entity.Difficulty;
import org.youcancook.gobong.domain.recipe.entity.Recipe;
import org.youcancook.gobong.domain.recipe.repository.RecipeRepository;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BaseTimeTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("레시피를 데이터베이스에 저장 시, 타임스탬프가 저장된다.")
    public void createRecipe(){
        User user1 = new User("name1", "oauth1", OAuthProvider.GOOGLE, null);
        Recipe recipe1 = Recipe.builder().user(user1).difficulty(Difficulty.EASY).title("주먹밥1").build();
        userRepository.save(user1);
        recipeRepository.save(recipe1);

        assertThat(recipe1.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("레시피를 수정 시, 수정 타임스탬프가 저장된다.")
    public void modifyRecipe(){
        User user1 = new User("name1", "oauth1", OAuthProvider.GOOGLE, null);
        User user2 = new User("name2", "oauth2", OAuthProvider.KAKAO, null);
        Recipe recipe1 = Recipe.builder().user(user1).difficulty(Difficulty.EASY).title("주먹밥1").build();
        userRepository.save(user1);
        userRepository.save(user2);
        recipeRepository.save(recipe1);
        recipe1.updateProperties("주먹밥", "", "김,밥", Difficulty.EASY, null);
        entityManager.flush();

        LocalDateTime createdAt = recipe1.getCreatedAt();
        LocalDateTime modifiedAt = recipe1.getModifiedAt();
        assertThat(modifiedAt).isNotNull();
        assertThat(createdAt).isNotNull();
        assertThat(modifiedAt).isAfter(createdAt);
    }
}