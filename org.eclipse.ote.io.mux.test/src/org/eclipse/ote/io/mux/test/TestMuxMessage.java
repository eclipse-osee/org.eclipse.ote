/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.io.mux.test;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.entity.EntityFactory;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.mux.test.messages.TEST_LOGICIAL_MESSAGE;
import org.eclipse.ote.io.mux.test.messages.TEST_MUX_MSG_R;
import org.eclipse.ote.io.mux.test.messages.TEST_MUX_MSG_T;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael P. Masterson
 */
public class TestMuxMessage {
   

   /**
    * Test method for {@link org.eclipse.ote.io.mux.MuxMessage#isValidElement(org.eclipse.osee.ote.message.elements.Element, org.eclipse.osee.ote.message.elements.Element)}.
    */
   @Test
   public void testIsValidElement() {
      TEST_LOGICIAL_MESSAGE lm = new TEST_LOGICIAL_MESSAGE();
      TEST_MUX_MSG_R muxR = new TEST_MUX_MSG_R();
      TEST_MUX_MSG_T muxT = new TEST_MUX_MSG_T();
      lm.setMemSource(GenericOteIoType.MUX);

      // If there is only a single direction Mux message association (T in this case), the check
      // will always return true
      lm.addMessageTypeAssociation(GenericOteIoType.MUX, muxT);
      setLmAsWriter(lm);
      Assert.assertTrue(muxT.isValidElement(lm.MULTI_MSG_ELEMENT, muxT.MULTI_MSG_ELEMENT));
      Assert.assertTrue(muxR.isValidElement(lm.MULTI_MSG_ELEMENT, muxR.MULTI_MSG_ELEMENT));
      
      setLmAsReader(lm);
      Assert.assertTrue(muxT.isValidElement(lm.MULTI_MSG_ELEMENT, muxT.MULTI_MSG_ELEMENT));
      Assert.assertTrue(muxR.isValidElement(lm.MULTI_MSG_ELEMENT, muxR.MULTI_MSG_ELEMENT));

      // As soon as the the R message is added, the special logic for valid element is turned on
      lm.addMessageTypeAssociation(GenericOteIoType.MUX, muxR);
      setLmAsWriter(lm);
      Assert.assertTrue(muxT.isValidElement(lm.MULTI_MSG_ELEMENT, muxT.MULTI_MSG_ELEMENT));
      Assert.assertFalse(muxR.isValidElement(lm.MULTI_MSG_ELEMENT, muxR.MULTI_MSG_ELEMENT));
      
      setLmAsReader(lm);
      Assert.assertFalse(muxT.isValidElement(lm.MULTI_MSG_ELEMENT, muxT.MULTI_MSG_ELEMENT));
      Assert.assertTrue(muxR.isValidElement(lm.MULTI_MSG_ELEMENT, muxR.MULTI_MSG_ELEMENT));

   }

   /**
    * @param lm
    */
   private void setLmAsWriter(TEST_LOGICIAL_MESSAGE lm) {
      MessageData md = lm.getDefaultMessageData();
      md.setWriter(new DataWriter());
      md.setReader(null);
   }
   
   private void setLmAsReader(TEST_LOGICIAL_MESSAGE lm) {
      MessageData md = lm.getDefaultMessageData();
      md.setWriter(null);
      EntityFactory parentFactory = new EntityFactory() {

         @Override
         public boolean isEnabled() {
             return true;
         }

     };
      md.setReader(new DataReader(null, null, true, null, parentFactory));
   }

}
