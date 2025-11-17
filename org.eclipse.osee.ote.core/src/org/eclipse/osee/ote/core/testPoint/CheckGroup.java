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

package org.eclipse.osee.ote.core.testPoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.ote.core.environment.interfaces.ITestGroup;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.ote.tools.util.Message;
import org.eclipse.ote.tools.util.ToMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Robert A. Fisher
 * @author Charles Shaw
 * @author Loren K. Ashley
 */
public class CheckGroup implements ITestGroup, ToMessage {
   private final String groupName;
   private final ArrayList<ITestPoint> testPoints;
   private final Operation operation;
   private Set<String> requirementIds;

   public static final CheckGroup SENTINEL = new CheckGroup(null, null) {

      @Override
      public CheckGroup add(ITestPoint testPoint) {
         throw new UnsupportedOperationException();
      }

      @Override
      public CheckGroup addAll(Collection<ITestPoint> testPoionts) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ArrayList<ITestPoint> getTestPoints() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Operation getOperation() {
         throw new UnsupportedOperationException();
      }

      @Override
      public String getGroupName() {
         return Named.SENTINEL;
      }

      /**
       * {@inheritDoc}
       *
       * @return true
       */

      @Override
      public boolean isInvalid() {
         return true;
      }

      /**
       * {@inheritDoc}
       *
       * @return false
       */

      @Override
      public boolean isValid() {
         return false;
      }

      @Override
      public int size() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isPass() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Element toXml(Document doc) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void toXml(XMLStreamWriter writer) throws XMLStreamException {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setRequirements(Set<String> requirementIds) {
         throw new UnsupportedOperationException();
      }

   };

   /**
    * Determines if the {@link CheckGroup} implementation is <code>null</code> or a sentinel implementation.
    *
    * @return <code>true</code> when the implementation is <code>null</code> or sentinel; otherwise, <code>false</code>.
    */

   public static boolean isInvalid(CheckGroup checkGroup) {
      return Objects.isNull(checkGroup) || checkGroup.isInvalid();
   }

   /**
    * Determines if the {@link CheckGroup} implementation is non-<code>null</code> and a non-sentinel implementation.
    *
    * @return <code>true</code> when the implementation is non-<code>null</code> and non-sentinel; otherwise,
    * <code>false</code>.
    */

   public static boolean isValid(CheckGroup checkGroup) {
      return Objects.nonNull(checkGroup) && checkGroup.isValid();
   }

   /**
    * CheckGroup objects are used to setup complex TestPoint structures where the pass/fail behavior can be an <b>And
    * </b>'ing or an <b>Or </b>'ing of the <b>getPass() </b> values of all the immediate children.
    * <p>
    * More complex TestPoint syntax can be obtained using the CheckGroup as a parent of other CheckGroup objects, of
    * which the <b>And </b> or <b>Or </b> setting can be set differently.
    *
    * @param operation The logical operation used for combining items within this CheckGroup.
    */
   public CheckGroup(Operation operation, String groupName) {
      super();
      testPoints = new ArrayList<>();
      this.operation = operation;
      this.groupName = groupName;
   }

   public CheckGroup add(ITestPoint testPoint) {
      this.testPoints.add(testPoint);

      return this;
   }

   public CheckGroup addAll(Collection<ITestPoint> testPoints) {
      this.testPoints.addAll(testPoints);

      return this;
   }

   @JsonProperty
   @Override
   public ArrayList<ITestPoint> getTestPoints() {
      return testPoints;
   }

   @JsonProperty
   public Operation getOperation() {
      return operation;
   }

   @JsonProperty
   public String getGroupName() {
      return groupName;
   }

   /**
    * @return The number of test points added to this check group so far
    */
   @Override
   public int size() {
      return this.testPoints.size();
   }

   /**
    * Predicate to determine if the {@link CheckGroup} implementation is sentinel.
    *
    * @return true
    */

   public boolean isInvalid() {
      return false;
   }

   /**
    * Predicate to determine if the {@link CheckGroup} implementation is non-sentinel.
    *
    * @return false
    */

   public boolean isValid() {
      return true;
   }

   @Override
   public boolean isPass() {

      /*
       * When no test points are present, all operations are defined to be failures.
       */

      if (testPoints.size() == 0) {
         return false;
      }

      boolean isAndOperation = (operation == Operation.AND) || (operation == Operation.NAND);
      boolean isNotOfOperation = (operation == Operation.NAND) || (operation == Operation.NOR);

      /*
       * For and operations start out assuming a pass until a failure is found. For or operations start out assuming a
       * fail until a pass is found.
       */

      boolean passFail = isAndOperation;

      for (ITestPoint testPoint : testPoints) {
         if (isAndOperation) {
            passFail &= testPoint.isPass();
            if (passFail == false) {
               /*
                * Short circuit failure exit
                */
               break;
            }
         } else {
            passFail |= testPoint.isPass();
            if (passFail == true) {
               /*
                * Short circuit pass exit
                */
               break;
            }
         }
      }

      /*
       * Flip result for NAND and NOR operations
       */

      if (isNotOfOperation) {
         passFail = !passFail;
      }

      return passFail;
   }

   @Override
   public Element toXml(Document doc) {
      return buildXml(doc, "CheckGroup");
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      buildXml(writer, "CheckGroup");
      writer.writeEndElement();
   }

   protected Element buildXml(Document doc, String name) {
      Element checkGroupElement = doc.createElement(name);

      checkGroupElement.setAttribute("Mode", operation.toString());
      checkGroupElement.appendChild(Jaxp.createElement(doc, "GroupName", groupName));

      // No need for an interactive result in a checkgroup
      if (this.isPass()) {
         checkGroupElement.appendChild(Jaxp.createElement(doc, "Result", "PASSED"));
      } else {
         checkGroupElement.appendChild(Jaxp.createElement(doc, "Result", "FAILED"));
      }
      if (requirementIds != null && requirementIds.size() > 0) {
         for (String req : requirementIds) {
            checkGroupElement.appendChild(Jaxp.createElement(doc, "Requirement", req));
         }
      }

      for (ITestPoint testPoint : testPoints) {
         checkGroupElement.appendChild(testPoint.toXml(doc));
      }

      return checkGroupElement;
   }

   protected void buildXml(XMLStreamWriter writer, String name) throws XMLStreamException {
      writer.writeStartElement(name);
      writer.writeAttribute("Mode", operation.toString());
      XMLStreamWriterUtil.writeElement(writer, "GroupName", groupName);

      // No need for an interactive result in a checkgroup
      if (this.isPass()) {
         XMLStreamWriterUtil.writeElement(writer, "Result", "PASSED");
      } else {
         XMLStreamWriterUtil.writeElement(writer, "Result", "FAILED");
      }
      if (requirementIds != null && requirementIds.size() > 0) {
         for (String req : requirementIds) {
            XMLStreamWriterUtil.writeElement(writer, "Requirement", req);
         }
      }

      for (ITestPoint testPoint : testPoints) {
         testPoint.toXml(writer);
      }
   }

   @Override
   public void setRequirements(Set<String> requirementIds) {
      //This is to ensure we get this by object and not be reference.
      this.requirementIds = new HashSet<String>(requirementIds);
   }

   /**
    * Generates a {@link Message} describing the contents of this {@link CheckGroup}. The generated message is for
    * debugging purposes and no contract for the contents of the returned {@link Message} is implied.
    *
    * @param indent the indent level for the message.
    * @param message when not null the message is appended to the {@link Message} provided by the {@code message}
    * parameter.
    * @return a {@link Message} describing the contents of this {@link CheckGroup} object.
    */

   @Override
   public Message toMessage(int indent, Message message) {
      message = Objects.nonNull(message) ? message : new Message();
      //@formatter:off
      message
         .indent( indent )
         .title( "CheckGroup" )
         .indentInc()
         .segment( "Group Name", this.groupName )
         .segment( "Operation", this.operation )
         .segmentIndexed( "Requirement Identifiers", this.requirementIds )
         .segmentIndexed( "Test Points", this.testPoints )
         .indentDec()
         .toString()
         ;
      //@formatter:on
      return message;
   }

   /**
    * Generates a {@link String} describing the contents of this {@link CheckGroup}. The generated string is for
    * debugging purposes and no contract for the contents of the returned {@link String} is implied.
    *
    * @return a {@link String} describing the contents of this {@link CheckGroup} object.
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}