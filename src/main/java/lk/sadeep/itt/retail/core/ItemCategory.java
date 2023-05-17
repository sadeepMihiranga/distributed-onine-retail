package lk.sadeep.itt.retail.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemCategory {

    private Integer categoryId;
    private String categoryName;

    private static List<ItemCategory> itemCategories = Arrays.asList(
            new ItemCategory(1, "Exercise Book"),
            new ItemCategory(2, "Ball Pont Pen"),
            new ItemCategory(3, "Eraser"),
            new ItemCategory(4, "Pencil"),
            new ItemCategory(5, "Ruler")
    );

    public ItemCategory(Integer categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static Optional<ItemCategory> findById(Integer categoryId) {
        return itemCategories.stream()
                .filter(category -> category.getCategoryId().equals(categoryId))
                .findFirst();
    }

    public static List<ItemCategory> getCategories() {
        return itemCategories;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
