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

package org.eclipse.osee.ote.core.environment.interfaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.ote.tools.util.Message;
import org.eclipse.ote.tools.util.ToMessage;

/**
 * The TestPoint interface should be implemented by objects that store pass/fail data.
 *
 * @author Robert A. Fisher
 */
public interface ITestPoint extends Xmlizable, XmlizableStream, ToMessage {
   @JsonProperty
   public boolean isPass();

   /**
    * @return True only if in batch mode and test point is result of an interactive pass/fail
    */
   default boolean isInteractive() {
      return false;
   }

   /**
    * Sets the requirement on the Test Point for coverage.
    */
   default void setRequirements(Set<String> requirementIds) {
      //Implement in implemented objects
   }

   /**
    * A default implementation for the {@link ToMessage#toMessage} implementation which generates a {@link Message}
    * showing the results of the {@link ItestPoint} implementaiton's {@link #isPass} and {@link #isInteractive} method
    * results.
    *
    * @return a {@link Message} describing the {@link ITestPoint} implementation.
    */

   @Override
   public default Message toMessage(int indent, Message message) {
      message = Objects.nonNull(message) ? message : new Message();
      //@formatter:off
      message
         .indent( indent )
         .title( "ITestPoint" )
         .indentInc()
         .segment( "Is Pass", this.isPass() )
         .segment( "Is Interactive", this.isInteractive() )
         .indentDec()
         .toString()
         ;
      //@formatter:on
      return message;
   }

}
