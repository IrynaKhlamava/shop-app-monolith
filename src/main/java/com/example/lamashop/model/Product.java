package com.example.lamashop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;


@Document(collection = "products")
@CompoundIndexes({
        @CompoundIndex(name = "category_index", def = "{'categories': 1}")
})
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    @Indexed(name = "price_index")
    private BigDecimal price;

    private int amountLeft;

    private String image;

    private List<String> categories;

    private boolean available = true;

}
