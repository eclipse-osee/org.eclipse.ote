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

package org.eclipse.ote.test.manager.uut.selector;

import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionViewerConverter implements XViewerConverter {

   private UutSelectionTable table;

   public UutSelectionViewerConverter(UutSelectionTable table) {
      this.table = table;
   }

   public List<String> getParticipants() {
      List<String> participants = UutSuTestList.getTestSuList();
      Collections.sort(participants);
      return participants;
   }

   @Override
   public void setInput(Control c, CellEditDescriptor ced, Object selObject) {

      if (!(selObject instanceof UutItemPath)) {
         c.dispose();
         return;
      }
      else {
         UutItemPath item = (UutItemPath) selObject;
         String elementName = ced.getInputField();
         if (c instanceof Text) {
            Text text = (Text) c;
            if (elementName == UutSelectionViewerFactory.PATH.getName()) {
               text.setText(item.getPath());
               FontData[] fontData = text.getFont().getFontData();
               fontData[0].setHeight(fontData[0].getHeight()-1);
               text.setFont(new Font(null, fontData));
            }
         }
         else if (c instanceof Combo) {
            Combo combo = (Combo) c;
            if (elementName == UutSelectionViewerFactory.PARTITION.getName()) {
               for (String s : getParticipants()) {
                  combo.add(s);
               }
               combo.select(combo.indexOf(item.getPartition()));
            }
            else if (elementName == UutSelectionViewerFactory.RATE.getName()) {
                  combo.add("N/A");
            }
         }
      }
   }

   @Override
   public Object getInput(Control c, CellEditDescriptor ced, Object selObject) {
      if (selObject instanceof UutItemPath) {
         UutItemPath item = (UutItemPath) selObject;
         String elementName = ced.getInputField();
         String value;
         if (c instanceof Text) {
            value = ((Text) c).getText();
         }
         else if (c instanceof Combo) {
            value = ((Combo) c).getText();
         } else {
            throw new IllegalArgumentException("Unhandled control type "+c.toString());
         }
         if (elementName == UutSelectionViewerFactory.PARTITION.getName()) {
            value = value.trim();
            if (!value.isEmpty()) {
               table.getContentProvider().updatePartition(item, value);
               table.setItemSelected(item);
            }
         }
         else if (elementName == UutSelectionViewerFactory.PATH.getName()) {
            item.setPath(value);
            table.refresh();
         }
         else if (elementName == UutSelectionViewerFactory.RATE.getName()) {
            item.setRate(value);
            table.refresh();
         }
      }
      return null;
   }

   @Override
   public boolean isValid(CellEditDescriptor ced, Object selObject) {
      return true;
   }

}
