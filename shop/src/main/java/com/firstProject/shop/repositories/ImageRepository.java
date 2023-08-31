package com.firstProject.shop.repositories;

import com.firstProject.shop.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
