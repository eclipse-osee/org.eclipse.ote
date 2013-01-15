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
package org.eclipse.osee.ote.ui.mux;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class MuxToolPlugin extends AbstractUIPlugin {

    private ServiceTracker oteClientServiceTracker;

    // The shared instance.
    private static MuxToolPlugin plugin;

    
    public static String PLUGIN_ID = "org.eclipse.osee.ote.ui.mux";
    /**
     * The constructor.
     */
    public MuxToolPlugin() {
	plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
	super.start(context);
	oteClientServiceTracker = new ServiceTracker(context,
		IOteClientService.class.getName(), null);
	oteClientServiceTracker.open();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
	super.stop(context);
	plugin = null;
	oteClientServiceTracker.close();
    }

    /**
     * Returns the shared instance.
     */
    public static MuxToolPlugin getDefault() {
	return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return AbstractUIPlugin.imageDescriptorFromPlugin(
		"org.eclipse.osee.ote.ui.mux", path);
    }
    
    public IOteClientService getOteClientService() {
	return (IOteClientService) oteClientServiceTracker.getService();
    }
}
