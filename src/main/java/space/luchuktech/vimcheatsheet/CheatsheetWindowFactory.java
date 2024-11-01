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
        private final JPanel contentPanel = new JPanel();

        private final JTextPane textPane = new JTextPane();

        private final SimpleAttributeSet header = new SimpleAttributeSet();
        private final SimpleAttributeSet body = new SimpleAttributeSet();
        private final SimpleAttributeSet code = new SimpleAttributeSet();

        private final StyledDocument document;

        public CheatsheetToolWindow() {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            document = textPane.getStyledDocument();

            StyleConstants.setFontSize(header, 24);
            StyleConstants.setBold(header, true);
            StyleConstants.setLineSpacing(header, 2.0f);
            StyleConstants.setLeftIndent(header, 3.0f);

            StyleConstants.setFontSize(body, 16);
            StyleConstants.setLeftIndent(body, 5.0f);

            StyleConstants.setFontFamily(code, "Monospaced");
            StyleConstants.setFontSize(code, 16);
            StyleConstants.setBackground(code, JBColor.gray);

            insertContents(contentPanel);
        }

        private void insertContents(JPanel contentPanel) {
            Cheatsheet cheatsheet = ApplicationManager.getApplication().getService(Cheatsheet.class);

            try {
                for (Category category : cheatsheet.getCategories()) {
                    createCategoryLabel(category);

                    var motions = cheatsheet.getMotionsInCategory(category);

                    for (Motion motion : motions) {
                        createMotionLabel(motion);
                    }
                }
            } catch (BadLocationException e) {
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("Vim Cheatsheet Error")
                        .createNotification("Bad Location error: " + e.getMessage(), NotificationType.ERROR)
                        .notify(null);
            }

            textPane.setEditable(false);
            contentPanel.add(Box.createRigidArea(new Dimension(30, 0)));
            contentPanel.add(textPane);
        }

        private void createMotionLabel(Motion motion) throws BadLocationException {
            document.insertString(document.getLength(), motion.motion(), code);
            document.insertString(document.getLength(), ": " + motion.description() + "\n", body);
        }

        private void createCategoryLabel(Category category) throws BadLocationException {
            document.insertString(document.getLength(), category.name() + "\n", header);
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }

    }

}
