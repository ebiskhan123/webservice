package com.csye6225.application.repository;

import com.csye6225.application.objects.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends CrudRepository<Image, String> {
    Image findByuserId(String user_id);

    void deleteById(String user_id);
}
