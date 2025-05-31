package space.luchuktech.vimcheatsheet;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import space.luchuktech.vimcheatsheet.api.Category;
import space.luchuktech.vimcheatsheet.api.Motion;
import space.luchuktech.vimcheatsheet.service.Cheatsheet;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

final public class CheatsheetWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CheatsheetToolWindow windowContent = new CheatsheetToolWindow();
        Content content = ContentFactory.getInstance().createContent(windowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class CheatsheetToolWindow {
        private final JPanel contentPanel = new JPanel();
        private JEditorPane editorPane;
        private JScrollPane scrollPane;

        public CheatsheetToolWindow() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            // Create JEditorPane for HTML content
            editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");

            // Set up the HTML editor kit with custom styling
            HTMLEditorKit editorKit = new HTMLEditorKit();
            editorPane.setEditorKit(editorKit);

            // Apply CSS styling to match markdown appearance
            StyleSheet styleSheet = editorKit.getStyleSheet();
            styleSheet.addRule("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 10px; }");
            styleSheet.addRule("h2 { color: #2C3E50; font-size: 18px; margin-top: 20px; margin-bottom: 10px; }");
            styleSheet.addRule("p { margin: 5px 0; }");
            styleSheet.addRule("code { font-family: 'Courier New', monospace; background-color: #F5F5F5; padding: 2px 4px; border-radius: 3px; }");

            // Add the editor pane to a scroll pane for scrolling capability
            scrollPane = new JScrollPane(editorPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            contentPanel.add(scrollPane);

            // Load and display the content
            loadContent();
        }

        private void loadContent() {
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

            // Set the HTML content to the editor pane
            editorPane.setText(contentBuffer.toString());
        }

        private void insertContents(JPanel contentPanel) {
            // This method is now replaced by loadContent()
            // Keeping it for backward compatibility but not using it
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
