package org.youcancook.gobong.global.util.s3;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.youcancook.gobong.global.util.service.ServiceTest;

import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.when;

@ServiceTest
class AwsS3ImageUploaderTest {

    @MockBean
    AwsS3ImageUploader awsS3ImageUploader;

    @Test
    @DisplayName("이미지를 성공적으로 업로드한다.")
    public void uploadImage() throws IOException {
        String fileName = "testimage.png";
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/" + fileName);
        MockMultipartFile multipartFile = new MockMultipartFile("testimage", fileName, "png", fileInputStream);
        when(awsS3ImageUploader.uploadImage(multipartFile)).thenReturn("testimage.png");

        String actual = awsS3ImageUploader.uploadImage(multipartFile);
        Assertions.assertThat(actual).isEqualTo(fileName);
    }
}