// Copyright (C) 2022-... Oleksandr Kolodkin <alexandr.kolodkin@gmail.com>
// Licensed under the MIT license. See LICENSE file in the project root for details.

package io.github.kolod

import org.apache.logging.log4j.core.*
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.config.plugins.*
import org.apache.logging.log4j.core.layout.PatternLayout
import javax.swing.*
import javax.swing.text.BadLocationException

@Suppress("unused")
@Plugin(name = "TextAreaAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
class TextAreaAppender protected constructor(
    name: String,
    layout: Layout<*>?,
    filter: Filter?,
    private val maxLines: Int,
    ignoreExceptions: Boolean,
) : AbstractAppender(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY) {

    /**
     * Used by log4j
     *
     * @param event - The logging event
     */
    override fun append(event: LogEvent) {
        val message = String(layout.toByteArray(event))

        // Append formatted message to text area using the Thread.
        try {
            SwingUtilities.invokeLater {
                textAreas.forEach { textArea ->
                    try {
                        with (textArea) {
                            if (text.isEmpty()) {
                                text = message
                            } else {
                                append("\n$message")
                                if ((maxLines > 0) && (lineCount > (maxLines + 1))) {
                                    val endIdx = document.getText(0, document.length).indexOf("\n")
                                    document.remove(0, endIdx + 1)
                                }
                            }
                            val content = text
                            text = content.substring(0, content.length - 1)
                        }
                    } catch (ex: BadLocationException) {
                        LOGGER.catching(ex)
                    }
                }
            }
        } catch (ex: IllegalStateException) {
            LOGGER.catching(ex)
        }
    }

    companion object {
        @JvmStatic private val textAreas = mutableListOf<JTextArea>()

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
        @JvmStatic
        @PluginFactory
        fun createAppender(
            @PluginAttribute("name") name: String?,
            @PluginAttribute("maxLines") maxLines: Int,
            @PluginAttribute("ignoreExceptions") ignoreExceptions: Boolean,
            @PluginElement("Layout") layout: Layout<*>?,
            @PluginElement("Filters") filter: Filter,
        ): TextAreaAppender? = if (name == null) {
            LOGGER.error("No name provided for JTextAreaAppender")
            null
        } else TextAreaAppender(
            name,
            layout ?: PatternLayout.createDefaultLayout(),
            filter,
            maxLines,
            ignoreExceptions
        )

        /**
         * Add the target JTextArea to be populated and updated by the logging information.
         *
         * @param textArea - The target JTextArea
         */
        @JvmStatic
        fun addTextArea(textArea: JTextArea) {
            textAreas.add(textArea)
        }
    }
}
