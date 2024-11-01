package space.luchuktech.vimcheatsheet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import space.luchuktech.vimcheatsheet.api.Category;
import space.luchuktech.vimcheatsheet.api.Motion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConcreteCheatsheet implements Cheatsheet {

    private static final String MOTIONS_PATH = "/data/motions.json";

    private HashMap<Category, List<Motion>> allMotions = new HashMap<>();

    private HashMap<String, Category> categoriesById = new HashMap<>();

    private List<Category> categories = new ArrayList<>();

    private boolean hasLoaded = false;

    @Override
    public List<Category> getCategories() {
        loadAllMotions();
        return categories;
    }

    @Override
    public List<Motion> getMotionsInCategory(Category category) {
        loadAllMotions();
        return allMotions.get(category);
    }

    private void loadAllMotions() {
        if (hasLoaded) return;

        ObjectMapper mapper = new ObjectMapper();

        try {
            var root = mapper.readTree(getClass().getResourceAsStream(MOTIONS_PATH));
            List<Category> categories = mapper.treeToValue(root.get("categories"), new TypeReference<List<Category>>() { });

            this.categories = categories;

            for (var category : categories) {
                categoriesById.put(category.id(), category);
            }

            List<Motion> motions = mapper.treeToValue(root.get("motions"), new TypeReference<List<Motion>>() { });

            for (var motion : motions) {
                addMotion(motion);
            }

            hasLoaded = true;

        } catch (JsonProcessingException e) {
            createNotification("Failed to process JSON: " + e.getMessage());
        } catch (IOException e) {
            createNotification("Failed to load motions file: " + e.getMessage());
        }

    }

    private void addMotion(Motion motion) {
        var category = categoriesById.get(motion.categoryId());

        if (!allMotions.containsKey(category)) {
            allMotions.put(category, new ArrayList<Motion>());
        }

        var categoryMotions = allMotions.get(category);
        categoryMotions.add(motion);
    }

    private void createNotification(String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Vim Cheatsheet Error")
                .createNotification(content, NotificationType.ERROR)
                .notify(null);
    }


}
