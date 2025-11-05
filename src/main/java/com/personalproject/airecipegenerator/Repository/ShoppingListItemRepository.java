package com.personalproject.airecipegenerator.Repository;

import com.personalproject.airecipegenerator.Dao.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

}