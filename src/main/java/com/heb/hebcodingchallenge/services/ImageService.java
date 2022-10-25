package com.heb.hebcodingchallenge.services;

import com.heb.hebcodingchallenge.dao.ImageObjectRepository;
import com.heb.hebcodingchallenge.dao.ImageRepository;
import com.heb.hebcodingchallenge.models.Image;
import com.heb.hebcodingchallenge.models.ImageObject;
import com.heb.hebcodingchallenge.models.json.Root;
import com.heb.hebcodingchallenge.models.json.Tag;
import com.heb.hebcodingchallenge.utils.ImageUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired ImageObjectRepository imageObjectRepository;

    public List<Image> getAllImages() {
        List<Image> images = new ArrayList<>();
        imageRepository.findAll().forEach(images::add);
        return images;
    }

    public Image getImageById(Long id) {
        Image image = new Image();
        Optional<Image> optionalImage = imageRepository.findById(id);
        if(optionalImage.isPresent())
        {
            image = optionalImage.get();
        }
        return image;
    }

    public List<Image> getImageByObject(List<String> objects) {
        List<Image> images = new ArrayList<>();
        for(String o:objects)
        {
            images.addAll(imageRepository.getImagesByImageObjects_Name(o));
        }
        return images;
    }

    public Image processImage(MultipartFile multipartFile, Image image) throws IOException, NoSuchAlgorithmException {

        File tempImage = File.createTempFile(multipartFile.getOriginalFilename(), null);
        FileOutputStream fos = new FileOutputStream(tempImage);
        fos.write(multipartFile.getBytes());

        String sha256Hex = getImageSha256Hex(multipartFile.getBytes());
        Optional<Image> existingImage = imageRepository.getImageBySha256hexEquals(sha256Hex);

        //Check if image is already in the db
        if(existingImage.isPresent())
        {
            return existingImage.get();
        }

        Image imageToInsert = image;
        imageToInsert.setSha256hex(sha256Hex);
        imageToInsert.setFileName(multipartFile.getOriginalFilename());

        if(null != imageToInsert.getLabel() || !imageToInsert.getLabel().isEmpty())
        {
            ImageObject imageObjectFromLabel = new ImageObject();
            imageObjectFromLabel.setName(imageToInsert.getLabel());
            imageToInsert.getImageObjects().add(imageObjectFromLabel);
        }

        if(imageToInsert.isEnableObjectDetection())
        {
            imageToInsert = detectObjectsInImage(imageToInsert, tempImage);
        }

        imageToInsert = associateKnownObjects(imageToInsert);

        imageToInsert.setSourceImageLocation(uploadImageToFolder(tempImage, multipartFile.getName()).toString());

        return imageRepository.save(imageToInsert);

    }

    public static Image detectObjectsInImage(Image imageToInsert, File imageFile) throws IOException {


        ResponseEntity<Root> responseEntity = callExternalImageAnalyzer(imageFile);

        Root responseRoot = responseEntity.getBody();

        List<Tag> tags = responseRoot.getResult().getTags();

        Set<ImageObject> imageObjects = new HashSet<>();
        List<String> processedImageObjects = new ArrayList<>();

        if(!imageToInsert.getImageObjects().isEmpty())
        {
            for(ImageObject obj: imageToInsert.getImageObjects())
            {
                processedImageObjects.add(obj.getName());
            }

        }

        for(Tag t: tags)
        {
            if(t.getConfidence() > 20)
            {
                ImageObject imageObject = new ImageObject();
                imageObject.setName(t.getTag().getEn());
                if(!processedImageObjects.contains(imageObject.getName())) {
                    imageObjects.add(imageObject);
                }
            }
        }
        imageToInsert.getImageObjects().addAll(imageObjects);
        return imageToInsert;
    }

    public static ResponseEntity<Root> callExternalImageAnalyzer(File image) throws FileNotFoundException {
        String imageApiAuthorization = "Basic YWNjXzNhYzY2ZDUwM2QzN2JiZDpiNjI1OTIyZTE0MGM1ZTdiNjMwM2E0NzcyNDFiZGFiZQ==";


        WebClient client = WebClient.create();
        ResponseEntity<Root> root = client
                .post()
                .uri("https://api.imagga.com/v2/tags")
                .header("Authorization", imageApiAuthorization)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("image", new FileSystemResource(image)))
                .retrieve()
                .toEntity(Root.class)
                .block();

        return root;
    }

    public Image associateKnownObjects(Image image){
        Image imageToInsert = image;
        Set<ImageObject> imageObjectDeDuped = new HashSet<>();

        for (ImageObject imgObj:image.getImageObjects())
        {
            ImageObject foundImageObject = imageObjectRepository.getImageObjectByNameEqualsIgnoreCase(imgObj.getName());
            if(null != foundImageObject)
            {
                imageObjectDeDuped.add(foundImageObject);
            }
            else
            {
                imageObjectDeDuped.add(imgObj);
            }
        }
        imageToInsert.setImageObjects(imageObjectDeDuped);

        return imageToInsert;
    }

    public static String getImageSha256Hex(byte[] file_bytes) throws IOException, NoSuchAlgorithmException {
        return ImageUtils.getSha256(file_bytes);
    }

    public static Path uploadImageToFolder(File file, String fileName) throws IOException {
        Path imageFilePath = Paths.get(ImageUtils.getApplicationLocationPath().toString(),"images" , fileName);
        return Files.copy(file.toPath(), imageFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public Image processImageFromUrl(String imageURL, Image image) throws IOException, NoSuchAlgorithmException {

        URL image_url = new URL(imageURL.trim());
        String fileName = new File(image_url.getPath()).getName();
        File tempImage = File.createTempFile(image_url.getFile(),null);

        InputStream is = image_url.openStream();
        Files.copy(is, tempImage.toPath(), StandardCopyOption.REPLACE_EXISTING);

        String sha256Hex = getImageSha256Hex(Files.readAllBytes(tempImage.toPath()));
        Optional<Image> existingImage = imageRepository.getImageBySha256hexEquals(sha256Hex);

        //Check if image is already in the db
        if(existingImage.isPresent())
        {
            return existingImage.get();
        }

        Image imageToInsert = image;
        imageToInsert.setSha256hex(sha256Hex);
        imageToInsert.setFileName(fileName);

        if(null != imageToInsert.getLabel() || !imageToInsert.getLabel().isEmpty())
        {
            ImageObject imageObjectFromLabel = new ImageObject();
            imageObjectFromLabel.setName(imageToInsert.getLabel());
            imageToInsert.getImageObjects().add(imageObjectFromLabel);
        }

        if(imageToInsert.isEnableObjectDetection())
        {
            imageToInsert = detectObjectsInImage(imageToInsert, tempImage);
        }

        imageToInsert = associateKnownObjects(imageToInsert);

        imageToInsert.setSourceImageLocation(uploadImageToFolder(tempImage, fileName).toString());

        return imageRepository.save(imageToInsert);

    }
}
