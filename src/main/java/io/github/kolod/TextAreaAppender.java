// Licensed under the MIT license. See LICENSE file in the project root for details.

package io.github.kolod;

import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import static javax.swing.SwingUtilities.invokeLater;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import static org.apache.logging.log4j.core.config.Property.EMPTY_ARRAY;
import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

@Plugin(name = "TextAreaAppender", category = "Core", elementType = "appender", printObject = true)
public class TextAreaAppender extends AbstractAppender {

    private static volatile ArrayList<JTextArea> textAreas = new ArrayList<>();
    private final int maxLines;

    private TextAreaAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions, EMPTY_ARRAY);
        this.maxLines = maxLines;
    }

    /**
     * Constructor.
     *
     * @param name - The Appender name.
     * @param maxLines - The maximum number of lines. Old lines are deleted when exceeded.
     * @param ignoreExceptions - If true, exceptions will be logged and suppressed. If false errors will be logged and
     * then passed to the application.
     * @param layout - The layout to use to format the event.
     * @param filter - The Filter to associate with the Appender.
     * @return - The TextAreaAppender instance.
     */
    @SuppressWarnings("unused")
    @PluginFactory
    public static TextAreaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("maxLines") int maxLines,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginElement("Layout") Layout<?> layout,
            @PluginElement("Filters") Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for JTextAreaAppender");
            return null;
        }

        if (layout == null) {
            layout = createDefaultLayout();
        }

        return new TextAreaAppender(name, layout, filter, maxLines, ignoreExceptions);
    }

    /**
     * Add the target JTextArea to be populated and updated by the logging information.
     *
     * @param textArea - The target JTextArea
     */
    public static void addTextArea(final JTextArea textArea) {
        TextAreaAppender.textAreas.add(textArea);
    }

    /**
     * Used by log4j
     *
     * @param event - The logging event
     */
    @Override
    public void append(LogEvent event) {
        String message = new String(this.getLayout().toByteArray(event));

        // Append formatted message to text area using the Thread.
        try {
            invokeLater(() -> {
                textAreas.forEach(textArea -> {
                    try {
                        if (textArea != null) {
                            if (textArea.getText().length() == 0) {
                                textArea.setText(message);
                            } else {
                                textArea.append("\n" + message);
                                if (maxLines > 0 & textArea.getLineCount() > maxLines + 1) {
                                    int endIdx = textArea.getDocument().getText(0, textArea.getDocument().getLength()).indexOf("\n");
                                    textArea.getDocument().remove(0, endIdx + 1);
                                }
                            }
                            String content = textArea.getText();
                            textArea.setText(content.substring(0, content.length() - 1));
                        }
                    } catch (BadLocationException ex) {
                        LOGGER.catching(ex);
                    }
                });
            });
        } catch (IllegalStateException ex) {
            LOGGER.catching(ex);
        }
    }
}
