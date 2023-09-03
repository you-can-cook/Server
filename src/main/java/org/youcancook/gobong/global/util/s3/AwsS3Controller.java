package org.youcancook.gobong.global.util.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/images")
public class AwsS3Controller {

    private final AwsS3ImageUploader awsS3ImageUploader;

    @PostMapping("upload")
    public ResponseEntity<AwsS3ImageUploadResponse> upload(@RequestParam("image") MultipartFile file){
        if (file.isEmpty()) throw new FileUploadException();
        String imageUrl = awsS3ImageUploader.uploadImage(file);
        AwsS3ImageUploadResponse response = new AwsS3ImageUploadResponse(imageUrl);

        return ResponseEntity.ok(response);
    }
}
