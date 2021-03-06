package org.tockit.docco;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CliMessages {
	private static final String BUNDLE_NAME = "messages/commandLineMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private CliMessages() {
		// nothing to do, just hiding constructor
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
