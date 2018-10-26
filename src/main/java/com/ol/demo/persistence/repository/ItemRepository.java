package com.ol.demo.persistence.repository;

import com.ol.demo.persistence.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
