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
package org.eclipse.osee.ote.core.environment.config;

import java.io.Serializable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.test.tags.BaseTestTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ScriptVersionConfig implements Xmlizable, XmlizableStream, Serializable {

   private static final long serialVersionUID = -4021198751318075600L;
   private String repositoryType;
   private String location;
   private String lastChangedRevision;
   private String lastAuthor;
   private String lastModificationDate;
   private String modifiedFlag;

   public ScriptVersionConfig() {
      repositoryType = "UNKNOWN";
      location = "-";
      lastChangedRevision = "-";
      lastAuthor = "-";
      lastModificationDate = "-";
      modifiedFlag = "-";
   }

   public ScriptVersionConfig(String repositoryType, String location, String lastChangedRevision, String lastAuthor, String lastModificationDate, String modifiedFlag) {
      this.repositoryType = repositoryType;
      this.location = location;
      this.lastChangedRevision = lastChangedRevision;
      this.lastAuthor = lastAuthor;
      this.lastModificationDate = lastModificationDate;
      this.modifiedFlag = modifiedFlag;
   }

   /**
    * @return the location
    */
   @JsonProperty
   public String getLocation() {
      return location;
   }

   /**
    * @param location the location to set
    */
   public void setLocation(String location) {
      this.location = location;
   }

   /**
    * @return the repositoryType
    */
   @JsonProperty
   public String getRepositoryType() {
      return repositoryType;
   }

   /**
    * @param repositoryType the repositoryType to set
    */
   public void setRepositoryType(String repositoryType) {
      this.repositoryType = repositoryType;
   }

   /**
    * @return the revision
    */
   @JsonProperty
   public String getLastChangedRevision() {
      return lastChangedRevision;
   }

   /**
    * @param lastChangedRevision the revision to set
    */
   public void setLastChangedRevision(String lastChangedRevision) {
      this.lastChangedRevision = lastChangedRevision;
   }

   /**
    * @return the lastAuthor
    */
   @JsonProperty
   public String getLastAuthor() {
      return lastAuthor;
   }

   /**
    * @param lastAuthor the lastAuthor to set
    */
   public void setLastAuthor(String lastAuthor) {
      this.lastAuthor = lastAuthor;
   }

   /**
    * @return the lastDateModified
    */
   @JsonProperty
   public String getLastModificationDate() {
      return lastModificationDate;
   }

   /**
    * @param lastModified the lastModified to set
    */
   public void setLastModificationDate(String lastModificationDate) {
      this.lastModificationDate = lastModificationDate;
   }

   /**
    * @return the modifiedFlag
    */
   @JsonProperty
   public String getModifiedFlag() {
      return modifiedFlag;
   }

   /**
    * @param modifiedFlag the modifiedFlag to set
    */
   public void setModifiedFlag(String modifiedFlag) {
      this.modifiedFlag = modifiedFlag;
   }

   @Override
   public Element toXml(Document doc) {
      Element scriptVersion = doc.createElement(BaseTestTags.SCRIPT_VERSION);
      scriptVersion.setAttribute(BaseTestTags.REVISION_FIELD, getLastChangedRevision());
      scriptVersion.setAttribute(BaseTestTags.REPOSITORY_TYPE, getRepositoryType());
      scriptVersion.setAttribute(BaseTestTags.LAST_AUTHOR_FIELD, getLastAuthor());
      scriptVersion.setAttribute(BaseTestTags.LAST_MODIFIED, getLastModificationDate());
      scriptVersion.setAttribute(BaseTestTags.MODIFIED_FIELD, getModifiedFlag());
      scriptVersion.setAttribute(BaseTestTags.URL, getLocation());
      return scriptVersion;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement(BaseTestTags.SCRIPT_VERSION);
      writer.writeAttribute(BaseTestTags.REVISION_FIELD, getLastChangedRevision());
      writer.writeAttribute(BaseTestTags.REPOSITORY_TYPE, getRepositoryType());
      writer.writeAttribute(BaseTestTags.LAST_AUTHOR_FIELD, getLastAuthor());
      writer.writeAttribute(BaseTestTags.LAST_MODIFIED, getLastModificationDate());
      writer.writeAttribute(BaseTestTags.MODIFIED_FIELD, getModifiedFlag());
      writer.writeAttribute(BaseTestTags.URL, getLocation());
      writer.writeEndElement();
   }

}
