package com.personalproject.airecipegenerator.Controllers;

import com.personalproject.airecipegenerator.Dao.AddItemRequest;
import com.personalproject.airecipegenerator.Dao.AddRecipeRequest;
import com.personalproject.airecipegenerator.Dao.ShoppingListItem;
import com.personalproject.airecipegenerator.Service.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/list")
@CrossOrigin(origins = "*") // Allow requests from the frontend
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @GetMapping
    public ResponseEntity<List<ShoppingListItem>> getList() {
        return ResponseEntity.ok(shoppingListService.getShoppingList());
    }

    @PostMapping("/add-from-recipe")
    public ResponseEntity<Void> addFromRecipe(@RequestBody AddRecipeRequest request) {
        shoppingListService.addItemsFromRecipe(request.ingredients());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<ShoppingListItem> updateItem(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        ShoppingListItem updatedItem = shoppingListService.updateItem(id, body.get("isChecked"));
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/clear-completed")
    public ResponseEntity<Void> clearCompleted() {
        shoppingListService.clearCompletedItems();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/item")
    public ResponseEntity<ShoppingListItem> addItem(@RequestBody AddItemRequest request) {
        if (request.itemName() == null || request.itemName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ShoppingListItem newItem = shoppingListService.addItem(request.itemName());

        // Return 201 Created status with the new item
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }
    @PostMapping("/send-email")
    public ResponseEntity<Void> sendListEmail() {
        try {
            shoppingListService.sendListByEmail();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Handle any errors
            return ResponseEntity.internalServerError().build();
        }
    }
}