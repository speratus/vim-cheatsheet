package space.luchuktech.vimcheatsheet;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel;
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanelProvider;
import org.intellij.plugins.markdown.ui.preview.jcef.JCEFHtmlPanelProvider;
import org.jetbrains.annotations.NotNull;
import space.luchuktech.vimcheatsheet.api.Category;
import space.luchuktech.vimcheatsheet.api.Motion;
import space.luchuktech.vimcheatsheet.service.Cheatsheet;

import javax.swing.*;

final public class CheatsheetWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CheatsheetToolWindow windowContent = new CheatsheetToolWindow();
        Content content = ContentFactory.getInstance().createContent(windowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class CheatsheetToolWindow {
        private final JPanel contentPanel = new JPanel();

        MarkdownHtmlPanelProvider panelProvider = new JCEFHtmlPanelProvider();
        MarkdownHtmlPanel mdPanel;

        public CheatsheetToolWindow() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            mdPanel = panelProvider.createHtmlPanel();

            insertContents(contentPanel);
        }

        private void insertContents(JPanel contentPanel) {
            Cheatsheet cheatsheet = ApplicationManager.getApplication().getService(Cheatsheet.class);

            StringBuffer contentBuffer = new StringBuffer();

            for (Category category : cheatsheet.getCategories()) {
                createCategoryLabel(category, contentBuffer);

                var motions = cheatsheet.getMotionsInCategory(category);

                for (Motion motion : motions) {
                    createMotionLabel(motion, contentBuffer);
                }
            }

            mdPanel.setHtml(contentBuffer.toString(), 0);
            contentPanel.add(mdPanel.getComponent());
        }

        private void createMotionLabel(Motion motion, StringBuffer textBuffer) {
            textBuffer.append("<p>")
                    .append("<code>")
                    .append(motion.motion())
                    .append("</code>")
                    .append(":&nbsp;")
                    .append(motion.description())
                    .append("</p>");
        }

        private void createCategoryLabel(Category category, StringBuffer textBuffer) {
            textBuffer.append("<h2>")
                    .append(category.name())
                    .append("</h2>");
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }

    }

}
