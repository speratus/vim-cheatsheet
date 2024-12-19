package space.luchuktech.vimcheatsheet;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.intellij.plugins.markdown.ui.preview.BrowserPipe;
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
        JLabel loader;
        BrowserPipe browserPipe;

        public CheatsheetToolWindow() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            mdPanel = panelProvider.createHtmlPanel();

            var useLoader = true;

            try {
                browserPipe = mdPanel.getBrowserPipe();
                browserPipe.subscribe("documentReady", this::swapContents);
            } catch (Exception e) {
                useLoader = false;
            }

            if (useLoader) {
                loader = new JLabel("Loading...", new AnimatedIcon.Default(), SwingConstants.LEFT);
                contentPanel.add(loader);
            }

            insertContents(contentPanel, useLoader);
        }

        private void swapContents(String s) {
            contentPanel.remove(loader);
            contentPanel.add(mdPanel.getComponent());
        }

        private void insertContents(JPanel contentPanel, boolean useLoader) {
            Cheatsheet cheatsheet = ApplicationManager.getApplication().getService(Cheatsheet.class);

            StringBuffer contentBuffer = new StringBuffer();
            contentBuffer.append("<!DOCTYPE html>" +
                    "<html>" +
                    "<head></head>" +
                    "<body>"
            );

            for (Category category : cheatsheet.getCategories()) {
                createCategoryLabel(category, contentBuffer);

                var motions = cheatsheet.getMotionsInCategory(category);

                for (Motion motion : motions) {
                    createMotionLabel(motion, contentBuffer);
                }
            }

            contentBuffer.append("</body></html>");

            mdPanel.setHtml(contentBuffer.toString(), 0);
            if (!useLoader) {
                contentPanel.add(mdPanel.getComponent());
            }
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
