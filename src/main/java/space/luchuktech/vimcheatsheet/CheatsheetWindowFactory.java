package space.luchuktech.vimcheatsheet;

import com.intellij.ide.customize.transferSettings.models.EditorColorScheme;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBFont;
import org.jetbrains.annotations.NotNull;
import space.luchuktech.vimcheatsheet.api.Category;
import space.luchuktech.vimcheatsheet.api.Motion;
import space.luchuktech.vimcheatsheet.service.Cheatsheet;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

final public class CheatsheetWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CheatsheetToolWindow windowContent = new CheatsheetToolWindow();
        Content content = ContentFactory.getInstance().createContent(windowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class CheatsheetToolWindow {
        //TODO: Swap out JTextPane for a JEditorPane so that HTML can be rendered.
        private final JPanel contentPanel = new JPanel();

        private final JEditorPane editorPane = new JEditorPane();

        public CheatsheetToolWindow() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            editorPane.setContentType("text/html");

            insertContents(contentPanel);
        }

        private void insertContents(JPanel contentPanel) {
            Cheatsheet cheatsheet = ApplicationManager.getApplication().getService(Cheatsheet.class);

            StringBuffer contentBuffer = new StringBuffer();
            contentBuffer.append("<html lang=\"en\"><head>" +
                    "<style>" +
                    "body, h2, p {" +
                    "   font-family: '" + JBFont.regular().getFamily() + "';" +
                    "}" +
                    "p {" +
                    "   font-size: " + JBFont.regular().getSize() + "px;" +
                    "}" +
                    "h2 {" +
                    "   font-size: " + JBFont.h2().getSize() + "px;" +
                    "   padding-bottom: 0.75em;" +
                    "}" +
                    "ul {" +
                    "   list-style: none;" +
                    "}" +
                    "span.code {" +
                    "   background-color: rgb(128, 128, 128);" +
                    "   display: inline-block;" +
                    "   border-radius: 5px;" +
                    "   padding-left: 3px;" +
                    "   padding-right: 3px;" +
                    "   font-family: 'JetBrains Mono';" +
                    "}" +
                    "body {" +
                    "   padding-left: 20px;" +
                    "}" +
                    "</style>" +
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

            editorPane.setText(contentBuffer.toString());
            editorPane.setEditable(false);
            contentPanel.add(editorPane);
        }

        private void createMotionLabel(Motion motion, StringBuffer textBuffer) {
            textBuffer.append("<p><span class=\"code\">")
                    .append(motion.motion())
                    .append("</span>:&nbsp;")
                    .append(motion.description())
                    .append("</p>");
        }

        private void createCategoryLabel(Category category, StringBuffer textBuffer) {
            textBuffer.append("<h1>")
                    .append(category.name())
                    .append("</h1>");
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }

    }

}
