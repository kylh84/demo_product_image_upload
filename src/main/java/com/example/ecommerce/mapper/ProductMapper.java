package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.ProductRequestDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductRequestDTO request);

    @Mapping(target = "imageIds", source = "images", qualifiedByName = "mapToImageIds")
    ProductResponseDTO toResponseDTO(Product product);

    @Named("mapToImageIds")
    default List<Long> mapToImageIds(List<ProductImage> images) {
        return images.stream().map(ProductImage::getId).toList();
    }
}