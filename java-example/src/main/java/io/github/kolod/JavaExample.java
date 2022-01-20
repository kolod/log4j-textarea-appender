// Copyright (C) 2022-... Oleksandr Kolodkin <alexandr.kolodkin@gmail.com>
// Licensed under the MIT license. See LICENSE file in the project root for details.

package io.github.kolod;

import com.formdev.flatlaf.FlatLightLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.*;
import javax.swing.*;

class JavaExample {
	public static void main(String[] args) {
		// Setup look and feel
		FlatLightLaf.setup();

		// Creating the Frame
		JFrame frame = new JFrame("TextAreaAppender Java Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(800, 600));

		// Text Area at the Center
		JTextArea log = new JTextArea();
		JScrollPane scroll = new JScrollPane(log);
		log.setEditable(false);
		log.setColumns(20);
		log.setRows(5);

		// Connect text area and log4j appender
		TextAreaAppender.addTextArea(log);

		//Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.CENTER, scroll);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Send test message
		Logger logger = LogManager.getLogger();
		logger.info("Application started");
	}
}
