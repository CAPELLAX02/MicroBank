package com.microbank.document.service.impl;

import com.microbank.document.exception.CustomException;
import com.microbank.document.service.MinIOService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinIOServiceImpl implements MinIOService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinIOServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadFile(String fileName, InputStream fileStream, String contentType) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(fileStream, fileStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
//                            .expiry(60 * 60 * 24) // 24 hours
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            throw new CustomException("Error while uploading file to MinIO");
        }
    }
}
