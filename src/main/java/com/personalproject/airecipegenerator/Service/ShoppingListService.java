package com.personalproject.airecipegenerator.Service;

import com.personalproject.airecipegenerator.Repository.ShoppingListItemRepository;
import com.personalproject.airecipegenerator.Repository.UserRepository;
import com.personalproject.airecipegenerator.Dao.ShoppingListItem;
import com.personalproject.airecipegenerator.Dao.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//Service Handling the Shopping List workflow

@Service
public class ShoppingListService {

    private final UserRepository userRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final EmailService emailService;

    public ShoppingListService(UserRepository userRepository, ShoppingListItemRepository shoppingListItemRepository,EmailService emailService) {
        this.userRepository = userRepository;
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.emailService = emailService;
    }

    // Helper to get the currently logged-in user
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<ShoppingListItem> getShoppingList() {
        User user = getAuthenticatedUser();
        return user.getShoppingList();
    }

    public void addItemsFromRecipe(List<String> recipeIngredients) {
        User user = getAuthenticatedUser();

        Set<String> existingItems = user.getShoppingList().stream()
                .map(ShoppingListItem::getItemName)
                .collect(Collectors.toSet());

        for (String cleanItem : recipeIngredients) {
            // The item is already clean! No parsing needed.
            // Check for null or empty strings (from "optional" items)
            if (cleanItem == null || cleanItem.trim().isEmpty()) {
                continue;
            }

            String cleanItemName = cleanItem.toLowerCase().trim();

            // Only add if it's not already on the list
            if (!existingItems.contains(cleanItemName)) {
                ShoppingListItem newItem = new ShoppingListItem(cleanItemName, user);
                shoppingListItemRepository.save(newItem);
            }
        }
    }


    public ShoppingListItem updateItem(Long itemId, boolean isChecked) {
        ShoppingListItem item = shoppingListItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        // Security check: Make sure this item belongs to the logged-in user
        if (!item.getUser().getId().equals(getAuthenticatedUser().getId())) {
            throw new SecurityException("Forbidden");
        }

        item.setChecked(isChecked);
        return shoppingListItemRepository.save(item);
    }


    public void clearCompletedItems() {
        User user = getAuthenticatedUser();
        List<ShoppingListItem> itemsToDelete = user.getShoppingList().stream()
                .filter(ShoppingListItem::isChecked)
                .toList();

        shoppingListItemRepository.deleteAll(itemsToDelete);
    }
    public ShoppingListItem addItem(String itemName) {
        User user = getAuthenticatedUser();
        String cleanItemName = itemName.trim().toLowerCase();

        // Check for dany Duplicates
        Optional<ShoppingListItem> existingItem = user.getShoppingList().stream()
                .filter(item -> item.getItemName().equalsIgnoreCase(cleanItemName))
                .findFirst();

        if (existingItem.isPresent()) {
            // Item already exists, just return it
            return existingItem.get();
        }

        // Create and save the new item
        ShoppingListItem newItem = new ShoppingListItem(cleanItemName, user);
        return shoppingListItemRepository.save(newItem);
    }
    public void sendListByEmail() {
        User user = getAuthenticatedUser();

        //Get the list of items that are NOT checked
        List<ShoppingListItem> itemsToSend = user.getShoppingList().stream()
                .filter(item -> !item.isChecked())
                .toList();

        if (itemsToSend.isEmpty()) {
            return;
        }

        // Format the list into a simple text body
        String subject = "Your Shopping List";
        String body = "Here is your shopping list:\n\n" +
                itemsToSend.stream()
                        .map(item -> "• " + item.getItemName())
                        .collect(Collectors.joining("\n"));

        // Send the email
        // We use the user's username, which is their email
        emailService.sendEmail(user.getUsername(), subject, body);
    }
}