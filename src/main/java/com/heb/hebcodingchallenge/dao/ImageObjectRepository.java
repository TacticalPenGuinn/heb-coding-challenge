package com.heb.hebcodingchallenge.dao;

import com.heb.hebcodingchallenge.models.ImageObject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageObjectRepository extends CrudRepository<ImageObject,Long> {
    public ImageObject getImageObjectByNameEqualsIgnoreCase (String Name);

}
