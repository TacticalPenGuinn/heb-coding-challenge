package com.heb.hebcodingchallenge.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.hebcodingchallenge.models.Image;
import com.heb.hebcodingchallenge.models.ImageObject;
import com.heb.hebcodingchallenge.services.ImageService;
import com.heb.hebcodingchallenge.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/images")
    public ResponseEntity<List<Image>> getImages(@RequestParam(required = false) String objects)
    {
        if(null == objects || objects.isEmpty())
        {
            return new ResponseEntity<>(imageService.getAllImages(), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(imageService.getImageByObject(Arrays.stream(objects.split(",")).toList()), HttpStatus.OK);
        }

    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Image> getImagesById(@PathVariable Long imageId)
    {
        return new ResponseEntity<>(imageService.getImageById(imageId), HttpStatus.OK);
    }

    @PostMapping("/images")
    public ResponseEntity<Image> uploadImage(@RequestParam(required = false) MultipartFile multipartFile, @RequestParam(required = false) String imageJson, @RequestParam(required = false) String imageURL) throws IOException, NoSuchAlgorithmException {

        if(null != multipartFile && !multipartFile.isEmpty() && null != imageURL && !imageURL.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image URL and Image File cannot both be processed at the same time");
        }

        Image image = new Image();
        if(null != imageJson && !imageJson.isEmpty())
        {
            image = new ObjectMapper().readValue(imageJson, Image.class);
        }

        if(null != multipartFile && !multipartFile.isEmpty())
        {
            return new ResponseEntity<>(imageService.processImage(multipartFile, image), HttpStatus.OK);
        }

        if(null != imageURL && !imageURL.isEmpty())
        {
            return new ResponseEntity<>(imageService.processImageFromUrl(imageURL, image), HttpStatus.OK);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }

}
