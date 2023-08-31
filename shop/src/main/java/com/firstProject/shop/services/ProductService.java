package com.firstProject.shop.services;

import com.firstProject.shop.entities.Image;
import com.firstProject.shop.entities.Product;
import com.firstProject.shop.entities.User;
import com.firstProject.shop.repositories.ProductRepository;
import com.firstProject.shop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    public List<Product> productsList(String title){
        if (title != null){
            return productRepository.findByTitle(title);
        }
        return productRepository.findAll();
    }
    public void saveProduct(Principal principal, Product product,
                            MultipartFile file1, MultipartFile file2, MultipartFile file3
                            ) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        Image image1;
        Image image2;
        Image image3;

        if (file1.getSize() != 0){
            image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);
        }
        if (file2.getSize() != 0) {
            image2 = toImageEntity(file2);
            product.addImageToProduct(image2);
        }
        if (file3.getSize() != 0) {
            image3 = toImageEntity(file3);
            product.addImageToProduct(image3);
        }
        log.info("Saving new product with name {}", product.getTitle());
        Product productFromDb = productRepository.save(product);
        productFromDb.setPreviewImageId(product.getImages().get(0).getId());
        productRepository.save(product);
    }
    public User getUserByPrincipal(Principal principal){
        if (principal == null){
            return new User();
        }
        return userRepository.findByUsername(principal.getName());
    }
    public Image toImageEntity(MultipartFile file) throws IOException{
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
