// Copyright (C) 2022-... Oleksandr Kolodkin <alexandr.kolodkin@gmail.com>
// Licensed under the MIT license. See LICENSE file in the project root for details.

@file:JvmName("KotlinExample")
@file:Suppress("unused")

package io.github.kolod

import com.formdev.flatlaf.FlatLightLaf
import io.github.kolod.TextAreaAppender.Companion.addTextArea
import org.apache.logging.log4j.LogManager
import java.awt.*
import javax.swing.*

fun main() {
	// Setup look and feel
	FlatLightLaf.setup()

	// Creating the Frame
	val frame = JFrame("TextAreaAppender Java Example")
	frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
	frame.preferredSize = Dimension(800, 600)

	// Text Area at the Center
	val log = JTextArea()
	val scroll = JScrollPane(log)
	log.isEditable = false
	log.columns = 20
	log.rows = 5

	// Connect text area and log4j appender
	addTextArea(log)

	//Adding Components to the frame.
	frame.contentPane.add(BorderLayout.CENTER, scroll)
	frame.pack()
	frame.setLocationRelativeTo(null)
	frame.isVisible = true

	// Send test message
	val logger = LogManager.getLogger()
	logger.info("Application started")
}
