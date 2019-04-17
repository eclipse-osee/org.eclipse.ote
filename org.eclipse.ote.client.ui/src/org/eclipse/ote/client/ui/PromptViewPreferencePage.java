/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.client.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Andrew M. Finkbeiner
 */
public class PromptViewPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String PROMPT_DISPLAYED = "PROMPT_DISPLAYED";
	
	private Button promptClose;
	
	public PromptViewPreferencePage() {
	}

	public PromptViewPreferencePage(String title) {
		super(title);
	}

	public PromptViewPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {
	   // INTENTIONALLY EMPTY BLOCK
	}

	@Override
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		promptClose = new Button(parent, SWT.CHECK);
		promptClose.setText("Keep prompt view displayed.");
		promptClose.setSelection(OteClientUiPlugin.getDefault().getPreferenceStore().getBoolean(PromptViewPreferencePage.PROMPT_DISPLAYED));
		promptClose.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
		      // INTENTIONALLY EMPTY BLOCK
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				OteClientUiPlugin.getDefault().getPreferenceStore().setValue(PROMPT_DISPLAYED, promptClose.getSelection());
			}
		});
		return parent;
	}
	
}
