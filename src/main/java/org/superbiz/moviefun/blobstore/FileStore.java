package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        FileCopyUtils.copy(blob.inputStream,  new FileOutputStream(targetFile));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        // ...

        Path path = Paths.get(name);
        File file = path.toFile();

        String contentType = new Tika().detect(path.toAbsolutePath());

        Blob blob = new Blob(name, new FileInputStream(file), contentType);

        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {
        // ...
    }
}