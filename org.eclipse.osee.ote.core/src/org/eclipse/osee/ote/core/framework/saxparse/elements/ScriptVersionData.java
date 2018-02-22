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
package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class ScriptVersionData {

   private final String lastAuthor;
   private final String lastModified;
   private final String modifiedFlag;
   private final String repositoryType;
   private final String revision;
   private final String url;

   ScriptVersionData(String lastAuthor, String lastModified, String modifiedFlag, String repositoryType, String revision, String url) {
      this.lastAuthor = lastAuthor;
      this.lastModified = lastModified;
      this.modifiedFlag = modifiedFlag;
      this.repositoryType = repositoryType;
      this.revision = revision;
      this.url = url;
   }

   /**
    * @return the lastAuthor
    */
   public String getLastAuthor() {
      return lastAuthor;
   }

   /**
    * @return the lastModified
    */
   public String getLastModified() {
      return lastModified;
   }

   /**
    * @return the modifiedFlag
    */
   public String getModifiedFlag() {
      return modifiedFlag;
   }

   /**
    * @return the repositoryType
    */
   public String getRepositoryType() {
      return repositoryType;
   }

   /**
    * @return the revision
    */
   public String getRevision() {
      return revision;
   }

   /**
    * @return the url
    */
   public String getUrl() {
      return url;
   }
}
