package space.luchuktech.vimcheatsheet;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import space.luchuktech.vimcheatsheet.api.Category;
import space.luchuktech.vimcheatsheet.service.Cheatsheet;

import javax.swing.*;

final public class CheatsheetWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    }

    private static class CheatsheetToolWindow {
        private final JPanel contentPanel = new JPanel();

        public CheatsheetToolWindow(ToolWindow toolWindow) {

        }

        private void insertContents(JPanel contentPanel) {
            Cheatsheet cheatsheet = ApplicationManager.getApplication().getService(Cheatsheet.class);

            for (Category category : cheatsheet.getCategories()) {
                contentPanel.add(createCategoryLabel(category));

                //TODO: Loop through motions and add them as labels to the content panel.
            }
        }

        private JLabel createCategoryLabel(Category category) {
            var label = new JLabel();
            label.setText(category.name());

            return label;
        }
    }

}
