package space.luchuktech.vimcheatsheet.service;

import space.luchuktech.vimcheatsheet.api.Category;
import space.luchuktech.vimcheatsheet.api.Motion;

import java.util.List;

public interface Cheatsheet {

    public List<Category> getCategories();

    public List<Motion> getMotionsInCategory(Category category);

}
