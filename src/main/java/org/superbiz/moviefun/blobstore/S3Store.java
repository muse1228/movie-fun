package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client s3Client;
    private final String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {

        ObjectMetadata om = new ObjectMetadata();
        om.setContentType(blob.contentType);
        om.setContentLength(blob.inputStream.available());

        s3Client.putObject(photoStorageBucket, blob.name, blob.inputStream, om);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        S3Object s3obj = s3Client.getObject(photoStorageBucket, name);

        InputStream inputstrem = s3obj.getObjectContent();
        ObjectMetadata om = s3obj.getObjectMetadata();
        Blob blob = new Blob(name, inputstrem, om.getContentType());

        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

    }
}
