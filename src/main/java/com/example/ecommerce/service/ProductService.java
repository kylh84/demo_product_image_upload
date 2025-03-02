package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductImage;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductImageRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productMapper = ProductMapper.INSTANCE;
    }

//    public List<ProductResponseDTO> getAllProducts() {
//        List<Product> products = productRepository.findAll();
//        return products.stream().map(this::convertToProductResponseDTO).collect(Collectors.toList());
//    }

//    private ProductResponseDTO convertToProductResponseDTO(Product product) {
//        ProductResponseDTO dto = new ProductResponseDTO();
//        dto.setId(product.getId());
//        dto.setDescription(product.getDescription());
//        dto.setName(product.getName());
//        dto.setPrice(product.getPrice());
//        dto.setStock(product.getStock());
//        dto.setImageIds(product.getImages().stream().map(ProductImage::getId).collect(Collectors.toList()));
//        return dto;
//    }

    // Using MapperStruct
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toResponseDTO).toList();
    }


    @Transactional
    public ProductResponseDTO createProductWithImages(ProductRequestDTO request, List<MultipartFile> files) throws IOException {
//        Product product = new Product();
//        product.setDescription(request.getDescription());
//        product.setName(request.getName());
//        product.setPrice(request.getPrice());
//        product.setStock(request.getStock());
        // Using MapperStruct
        Product product = productMapper.toEntity(request);

        // Build images and set into product BEFORE save
        List<ProductImage> images = uploadProductImages(files);
        images.forEach(image -> image.setProduct(product));
        product.setImages(images);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    private List<ProductImage> uploadProductImages(List<MultipartFile> files) throws IOException {
        String uploadDir = "uploads/products/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        List<ProductImage> images = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ProductImage image = new ProductImage();
            image.setImageUrl(uploadDir + fileName);
            images.add(image);
        }

        return images;
    }


    public Resource loadProductImage(Long productId, Long imageId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!product.getImages().contains(image)) {
            throw new RuntimeException("Image not found in product");
        }

        Path imagePath = Paths.get(image.getImageUrl());
        if (!Files.exists(imagePath)) {
            throw new FileNotFoundException("Image file not found");
        }

        return new UrlResource(imagePath.toUri());
    }

    public String getImageContentType(Resource resource) throws IOException {
        Path imagePath = Paths.get(resource.getFile().getAbsolutePath());
        String contentType = Files.probeContentType(imagePath);
        return contentType != null ? contentType : "application/octet-stream";
    }
}