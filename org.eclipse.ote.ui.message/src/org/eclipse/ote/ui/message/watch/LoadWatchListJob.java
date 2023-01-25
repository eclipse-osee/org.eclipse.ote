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
package org.eclipse.ote.ui.message.watch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.ui.message.internal.Activator;
import org.eclipse.ote.ui.message.tree.WatchList;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;

/**
 * 
 * @author Ken J. Aguilar
 *
 */
final class LoadWatchListJob extends Job {
	private static final Pattern elmPattern = Pattern.compile("^(osee\\.test\\.core\\.message\\.[^.]+\\..+)\\.(.+)$");
	private static final Pattern msgPattern = Pattern.compile("^(osee\\.test\\.core\\.message\\.[^.]+\\..+)$");

	private final WatchView watchView;
	private final File watchFile;

	private final WatchListConfiguration watchListConfiguration = new WatchListConfiguration();

	LoadWatchListJob(WatchView watchView, File watchFile) {
		super("Loading watch file");
		this.watchView = watchView;
		this.watchFile = watchFile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(watchFile)));
			try {
				monitor.beginTask("loading watch elements", IProgressMonitor.UNKNOWN);
				String line = reader.readLine();

				if (line != null) {
					if (line.equals("version=3.0")) {
						processVersion3FileFormat(monitor, reader);
					} else if (line.equals("version=2.0")) {
						processVersion2FileFormat(monitor, reader);
					} else {
						processLegacyFormat(monitor, reader);
					}
				}
			} finally {
				reader.close();
			}
		} catch (Throwable t) {
			OseeLog.log(Activator.class, Level.SEVERE, "error loading watch file", t);
			return org.eclipse.core.runtime.Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		return org.eclipse.core.runtime.Status.OK_STATUS;
	}

	private void processLegacyFormat(IProgressMonitor monitor, BufferedReader reader) throws IOException{

		String line = reader.readLine();
		while(line != null) {
			final Matcher elmMatch = elmPattern.matcher(line);
			final Matcher msgMatch = msgPattern.matcher(line);

			if (elmMatch.find()) {
				Displays.pendInDisplayThread(new Runnable() {
					@Override
					public void run() {
						String msg = elmMatch.group(1);
						String elm = elmMatch.group(2);
						ElementPath element = new ElementPath(msg, elm);
						AddWatchParameter parameter = new AddWatchParameter(elmMatch.group(1), element);
						watchView.addWatchMessage(parameter);
					}
				});
			} else if (msgMatch.find()) {
				Displays.pendInDisplayThread(new Runnable() {
					@Override
					public void run() {
						watchView.addWatchMessage(new AddWatchParameter(msgMatch.group(1)));
					}
				});
			}
			line = reader.readLine();
		}
		Displays.pendInDisplayThread(new Runnable() {
			@Override
			public void run() {
				watchView.saveWatchFile();
				watchView.getTreeViewer().refresh();
			}
		});

	}

	private void processVersion3FileFormat(IProgressMonitor monitor, BufferedReader reader) throws IOException{

		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith("#rec#")) {
				line = processRecordingState(line, reader);
			} else if (line.startsWith("msg:")){
				String message = line.substring(4);
				line = processMessage(message, reader);
			} else {
				line = reader.readLine();
			}
		}
		updateViewer();
	}

	private String processMessage(String message, BufferedReader reader) throws IOException{
		watchListConfiguration.addMessage(message);
		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith("@")) {
				String elementLine = line.substring(1);
				String[] result = elementLine.split("=");								
				ElementPath path = new ElementPath(result[0]);				
				watchListConfiguration.addPath(path);
				if (result.length > 1) {
					watchListConfiguration.getAddWatchParameter().setValue(path, result[1]);
				}
			} else if (line.startsWith("isWriter=")) {
				try {
					boolean isWriter = Boolean.parseBoolean(line.substring(9));
					watchListConfiguration.setIsWriter(message, isWriter);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}				
			} else if (line.startsWith("data type=")) {
				try {
					String type = line.substring(10);
					watchListConfiguration.setDataType(message, type);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}				
			} else {
				break;
			}
			
			line = reader.readLine();
		}
		return line;
	}

	private void updateViewer() {
		Displays.pendInDisplayThread(new Runnable() {
			@Override
			public void run() {
				try {
					WatchList watchList = watchView.getWatchList();
					watchView.getTreeViewer().getTree().setRedraw(false);
					watchView.addWatchMessage(watchListConfiguration.getAddWatchParameter());
					for (ElementPath path : watchListConfiguration.getRecBodyElementsToAdd()) {
						WatchedMessageNode msgNode = watchList.getMessageNode(path.getMessageName());
						if (msgNode != null) {
							msgNode.getRecordingState().addBody(path);
						}
					}
					for (ElementPath path : watchListConfiguration.getRecHeaderElementsToAdd()) {
						WatchedMessageNode msgNode = watchList.getMessageNode(path.getMessageName());
						if (msgNode != null) {
							msgNode.getRecordingState().addHeader(path);
						}
					}
					for (String msg : watchListConfiguration.getRecBodyHex()) {
						WatchedMessageNode msgNode = watchList.getMessageNode(msg);
						if (msgNode != null) {
							msgNode.getRecordingState().setBodyDump(true);
						}
					}
					for (String msg : watchListConfiguration.getRecHeaderHex()) {
						WatchedMessageNode msgNode = watchList.getMessageNode(msg);
						if (msgNode != null) {
							msgNode.getRecordingState().setHeaderDump(true);
						}
					}
				} finally {
					watchView.getTreeViewer().getTree().setRedraw(true);
					watchView.getTreeViewer().refresh();
				}
			}
		});

		Displays.pendInDisplayThread(new Runnable() {
			@Override
			public void run() {
				watchView.saveWatchFile();
			}
		});

	}

	private String processRecordingState(String line, BufferedReader reader) throws IOException{
		// #rec#,message,[body|header|bodyHex|
		// headerHex],[boolean|path]
		String[] els = line.split(",");
		if (els.length == 4) {
			String message = els[1];
			String type = els[2];
			String value = els[3];
			if (type.equals("body")) {
				watchListConfiguration.getRecBodyElementsToAdd().add(new ElementPath(value));
			} else if (type.equals("header")) {
				watchListConfiguration.getRecHeaderElementsToAdd().add(new ElementPath(value));
			} else if (type.equals("headerHex")) {
				if (Boolean.parseBoolean(value)) {
					watchListConfiguration.getRecHeaderHex().add(message);
				}
			} else if (type.equals("bodyHex")) {
				if (Boolean.parseBoolean(value)) {
					watchListConfiguration.getRecBodyHex().add(message);
				}
			}
		}
		return reader.readLine();
	}

	private void processVersion2FileFormat(IProgressMonitor monitor, BufferedReader reader) throws IOException{
		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith("#rec#")) {
				line = processRecordingState(line, reader);
			} else {
				watchListConfiguration.addPath(new ElementPath(line));
				line = reader.readLine();
			}
		}
		updateViewer();
	}
}