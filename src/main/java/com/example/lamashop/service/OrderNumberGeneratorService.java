package com.example.lamashop.service;

import com.example.lamashop.exception.OrderNumberGenerationException;
import com.example.lamashop.model.OrderSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNumberGeneratorService {

    private final MongoTemplate mongoTemplate;

    public long getNextOrderNumber() {
        Query query = new Query(Criteria.where("_id").is("order"));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

        OrderSequence counter = mongoTemplate.findAndModify(query, update, options, OrderSequence.class);

        if (counter == null) {
            throw new OrderNumberGenerationException();
        }

        return counter.getSeq();
    }
}