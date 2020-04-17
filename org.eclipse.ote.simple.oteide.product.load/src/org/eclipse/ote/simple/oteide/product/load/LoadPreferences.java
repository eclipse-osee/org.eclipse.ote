/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.simple.oteide.product.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.ui.IStartup;

/**
 * Loads a custom Eclipse .epf preferences file on startup.
 * 
 * @author Dominic Guss
 */
public class LoadPreferences implements IStartup {

	private final String PREFERENCES_FILE_PATH = System.getProperty("user.dir") + File.separator
			+ "OSEE_Java_Preferences.epf";

	@Override
	public void earlyStartup() {

		IPreferenceFilter[] ipFilters = new IPreferenceFilter[1];

		ipFilters[0] = new IPreferenceFilter() {
			@Override
			public String[] getScopes() {
				return new String[] { InstanceScope.SCOPE, ConfigurationScope.SCOPE };
			}

			@Override
			public Map<String, PreferenceFilterEntry[]> getMapping(String scope) {
				return null;
			}
		};

		IPreferencesService service = Platform.getPreferencesService();
		InputStream inputStream = null;
		IExportedPreferences prefs = null;

		try {
			inputStream = new FileInputStream(new File(PREFERENCES_FILE_PATH));
		} catch (IOException ex) {
			OseeCoreException.wrap(ex);
		}
		try {
			prefs = service.readPreferences(inputStream);
		} catch (CoreException ex) {
			OseeCoreException.wrap(ex);
		}
		try {
			service.applyPreferences(prefs, ipFilters);
		} catch (CoreException ex) {
			OseeCoreException.wrap(ex);
		}
	}
}
