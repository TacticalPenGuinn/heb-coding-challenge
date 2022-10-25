package com.heb.hebcodingchallenge.dao;

import com.heb.hebcodingchallenge.models.Image;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    public List<Image> getImagesByImageObjects_Name (String name);

    public Optional<Image> getImageBySha256hexEquals(String sha256hex);

}
