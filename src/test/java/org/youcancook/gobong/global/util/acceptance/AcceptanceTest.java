package org.youcancook.gobong.global.util.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.youcancook.gobong.global.util.service.DatabaseCleanupExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(DatabaseCleanupExtension.class)
public class AcceptanceTest {

    @LocalServerPort
    public int port;

    @BeforeEach
    void setUp(){
        RestAssured.port = port;
    }
}
