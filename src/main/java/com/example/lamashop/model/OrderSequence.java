package com.example.lamashop.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counters")
@Data
public class OrderSequence {
    @Id
    private String id;
    private long seq;
}