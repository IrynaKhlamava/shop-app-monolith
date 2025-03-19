package com.example.lamashop.repository;

import com.example.lamashop.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategoriesContaining(String category);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

}
