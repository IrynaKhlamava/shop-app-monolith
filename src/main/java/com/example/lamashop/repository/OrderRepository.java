package com.example.lamashop.repository;

import com.example.lamashop.model.Order;
import com.example.lamashop.model.enumType.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByUserId(String userId);

    Stream<Order> streamByStatusIn(List<OrderStatus> statuses);

}
