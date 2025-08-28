package space.luchuktech.vimcheatsheet;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.HTMLEditorKitBuilder; // added
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

    private static class CheatsheetToolWindow implements LafManagerListener {
        private final JPanel contentPanel = new JPanel();
        private JEditorPane editorPane;
        private JScrollPane scrollPane;
        private HTMLEditorKit editorKit;
        private StyleSheet customSheet;

        public CheatsheetToolWindow() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            // Create JEditorPane for HTML content
            editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");

            // Install IntelliJ's preconfigured kit and attach our custom child stylesheet
            installEditorKitAndAttachStyles();

            // Add the editor pane to a scroll pane for scrolling capability
            scrollPane = new JScrollPane(editorPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            contentPanel.add(scrollPane);

            // Register for theme changes
            ApplicationManager.getApplication().getMessageBus().connect()
                    .subscribe(LafManagerListener.TOPIC, this);

            // Apply initial styling based on current theme
            applyThemeStyles();

            // Load and display the content
            loadContent();
        }

        @Override
        public void lookAndFeelChanged(@NotNull LafManager source) {
            // Rebuild the kit so we get the correct theme defaults, then reattach our custom sheet
            installEditorKitAndAttachStyles();
            loadContent();
        }

        private void installEditorKitAndAttachStyles() {
            // Build a fresh kit which already includes JetBrains' default root StyleSheet
            editorKit = new HTMLEditorKitBuilder().build();
            editorPane.setEditorKit(editorKit);

            // Create (or rebuild) our custom linked sheet whose rules should NOT override platform defaults
            customSheet = new StyleSheet();

            boolean isDarkTheme = !JBColor.isBright();
            // Put our base rules into the linked sheet
            customSheet.addRule("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 10px; }");
            customSheet.addRule("p { margin: 5px 0; }");
            if (isDarkTheme) {
                customSheet.addRule("body { background-color: #2B2B2B; color: #A9B7C6; }");
                customSheet.addRule("h2 { color: #A9B7C6; font-size: 18px; margin-top: 20px; margin-bottom: 10px; }");
                customSheet.addRule("code { font-family: 'JetBrains Mono', monospace; background-color: #3C3F41; color: #CC7832; padding: 2px 4px; }");
            } else {
                customSheet.addRule("body { background-color: #FFFFFF; color: #000000; }");
                customSheet.addRule("h2 { color: #2C3E50; font-size: 18px; margin-top: 20px; margin-bottom: 10px; }");
                customSheet.addRule("code { font-family: 'JetBrains Mono', monospace; background-color: #F5F5F5; color: #0000FF; padding: 2px 4px; }");
            }

            // Attach as a linked sheet to the platform root; platform defaults will override our base on conflicts
            StyleSheet platformRoot = editorKit.getStyleSheet();
            platformRoot.addStyleSheet(customSheet);
        }

        private void applyThemeStyles() {
            // Kept for compatibility; installEditorKitAndAttachStyles() handles theme
            installEditorKitAndAttachStyles();
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
