package org.eclipse.osee.ote.runtimemanager;

import java.io.File;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.workspacebundleloader.IJarChangeListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Version;

public class PrecompiledListener implements IJarChangeListener<OteSystemLibsNature> {
	
	private static final Matcher VERSION_MATTCHER = Pattern.compile("(\\d+.\\d+.\\d+)").matcher("");
	private boolean firstTime = true;
	private boolean isPrecompiledInstalled = false;
	
	@Override
	public void handleBundleAdded(URL url) {
		// do nothing -- gets called for each jar in the precompiled project
	}

	@Override
	public void handleBundleChanged(URL url) {
		// do nothing -- gets called for each jar in the precompiled project
	}

	@Override
	public void handleBundleRemoved(URL url) {
		// do nothing?
	}

	@Override
	public void handlePostChange() {
		runCheckInThread();
	}

	@Override
	public void handleNatureClosed(OteSystemLibsNature nature) {
		// do nothing?
	}
	
	public void runCheckInThread() {
		Jobs.runInJob(new AbstractOperation("Precompiled Version Check", RuntimeManager.BUNDLE_ID) {

			@Override
			protected void doWork(IProgressMonitor monitor) throws Exception {
				runCheck();
			}}, false);
	}

	private synchronized void runCheck() {
		if(firstTime) {
			firstTime = false;
			URL installLocation = Platform.getInstallLocation().getURL();
			File pluginsDir = new File(installLocation.getFile() + File.separator + "plugins");
			for(File plugin : Lib.recursivelyListFiles(pluginsDir)) {
				if(plugin.getName().startsWith("ote.cdb.messages")) {
					isPrecompiledInstalled = true;
					break;
				}
			}
		}
		
		String precompileVersion = null;
		try {
			for (OteSystemLibsNature nature : OteSystemLibsNature.getWorkspaceProjects()) {
				IProject project = nature.getProject();
				if (precompileVersion == null && project.getName().contains("precompiled")) {
					IFile buildLabel = project.getFile("build_label.txt");
					Scanner s = new Scanner(buildLabel.getContents());
					try {
						while (s.hasNextLine()) {
							String line = s.nextLine().toLowerCase();
							if (line.contains("osee build")) {
								String[] tokens = line.split(":");
								if (tokens.length == 2) {
									precompileVersion = tokens[1].trim();
									break;
								}
							}
						}
					} catch (Exception ex) {
						OseeLog.log(RuntimeManager.class, Level.SEVERE, ex);
					} finally {
						Lib.close(s);
					}
				}
				
				if(precompileVersion != null) {
					break;
				}

			}
		} catch (CoreException ex) {
			OseeLog.log(RuntimeManager.class, Level.SEVERE, ex);
		}

		if (precompileVersion != null) {
			final String message;
			if(isPrecompiledInstalled) {
				message = "Conflict: Precompiled detected in workspace of non precompiled required client!\nPlease remove it from the workspace and restart.";
			} else {
				Version client = RuntimeManager.getDefault().getContext().getBundle().getVersion();
				String clientStr = "", precompileStr = "";
				Matcher matcher = VERSION_MATTCHER.reset(client.toString());
				if(matcher.find()) {
					clientStr = matcher.group(1);
				}
				matcher = VERSION_MATTCHER.reset(precompileVersion.toString());
				if(matcher.find()) {
					precompileStr = matcher.group(1);
				}
				
				if(!clientStr.equals(precompileStr)) {
					message = String.format(
						"OTE IDE / Precompiled Libraries version mismatch!\nOTE[%s] != Precompiled[%s]\nPlease get the version that matches [%s].",
						clientStr, precompileStr, clientStr);
				} else {
					message = null;
				}
				
			}
			
			if (message != null) {
				Displays.ensureInDisplayThread(new Runnable() {

					@Override
					public void run() {
						MessageDialog.openError(PlatformUI.getWorkbench().getModalDialogShellProvider().getShell(), "Precompiled Version",
								message);
					}
				}, true);
			}
		}
	}
	
}
