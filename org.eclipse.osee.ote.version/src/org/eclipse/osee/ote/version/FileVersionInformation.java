/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.version;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FileVersionInformation {
	
	Map<File, FileVersion> getFileVersions(List<File> files);
	
}
