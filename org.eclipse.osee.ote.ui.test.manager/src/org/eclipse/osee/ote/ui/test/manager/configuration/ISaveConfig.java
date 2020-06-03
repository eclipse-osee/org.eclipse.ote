/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.test.manager.configuration;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract interface ISaveConfig {

   public abstract Element toXml(Document doc);

   public abstract void saveConfig(File fileName) throws Exception;

   public abstract void printXmlTree();
}
