/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.ote.ui.message.util;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.FrameworkUtil;

public enum Images {
	ADD("icons/add.png"),
	DELETE_ALL("icons/deleteAll.gif"),
	DELETE("icons/remove.png"),
	RECORD("icons/record_action.png");
	
	private final String path;

	private static ImageRegistry ir = new ImageRegistry();
	/**
	 * @param path
	 */
	private Images(String path) {
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	public Image getImage() {
		Image image = ir.get(path);
		if (image == null) {
			image = ImageDescriptor.createFromURL(getFile(path)).createImage();
			ir.put(path, image);
		}
		return image;
	}
	
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = ir.getDescriptor(path);
		if (descriptor == null) {
			descriptor = ImageDescriptor.createFromURL(getFile(path));
			ir.put(path, descriptor);
		}
		return descriptor;
	}
	
	private static URL getFile(String path) {
		URL url = FrameworkUtil.getBundle(Images.class).getEntry(path);
		if (url == null) {
			return null;
		}
		try {
			return FileLocator.resolve(url);
		} catch (IOException e) {
			throw new RuntimeException("could not resolve URL from path: " + path, e);
		}
	}
}
