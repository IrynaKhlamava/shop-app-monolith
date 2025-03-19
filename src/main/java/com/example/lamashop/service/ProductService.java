package com.example.lamashop.service;

import com.example.lamashop.dto.ProductDto;
import com.example.lamashop.mapper.ProductMapper;
import com.example.lamashop.model.Product;
import com.example.lamashop.repository.ProductRepository;
import com.example.lamashop.exception.FileUploadException;
import com.example.lamashop.exception.NotEnoughStockException;
import com.example.lamashop.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private static final String UPLOAD_DIR = isRunningInDocker()
            ? "/app/uploads/"
            : System.getProperty("user.home") + "/lama-shop/uploads/";

    public List<ProductDto> getAllProducts() {
        logger.info("Get all products");
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }

    public ProductDto getProductById(String id) {
        logger.info("Get product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::forProduct);
        return productMapper.toDTO(product);
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        logger.info("Creating new product: {}", productDto.getName());
        return productMapper.toDTO(productRepository.save(product));
    }

    public ProductDto uploadProductImage(String productId, MultipartFile file) {
        logger.info("Uploading image for product ID={}", productId);

        Product product = getProductByIdOrThrow(productId);

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            product.setImage("/uploads/" + filename);
            productRepository.save(product);

            logger.info("Uploaded image for product {}: {}", productId, filename);
            return productMapper.toDTO(product);

        } catch (IOException e) {
            logger.error("File upload failed for product ID={}: {}", productId, e.getMessage());
            throw new FileUploadException();
        }
    }

    private Product getProductByIdOrThrow(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found: ID={}", productId);
                    return ResourceNotFoundException.forProduct();
                });
    }

    public ProductDto updateProduct(String id, ProductDto ProductDto) {
        logger.info("Updating product with id: {}", id);
        Product product = productMapper.toEntity(ProductDto);
        product.setId(id);
        return productMapper.toDTO(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        logger.warn("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }

    private static boolean isRunningInDocker() {
        return Files.exists(Paths.get("/.dockerenv"));
    }

    public Product validateProductAvailability(String productId, int requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ResourceNotFoundException::forProduct);

        if (product.getAmountLeft() < requestedQuantity) {
            throw new NotEnoughStockException();
        }

        return product;
    }

}
