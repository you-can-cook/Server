package org.youcancook.gobong.global.util.s3;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AwsS3ImageUploadResponse {
    private String imageUrl;
}
