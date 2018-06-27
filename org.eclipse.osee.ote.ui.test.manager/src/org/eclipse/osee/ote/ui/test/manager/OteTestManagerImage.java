/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public enum OteTestManagerImage implements KeyedImage {
   ADD("add.gif"),
   ALERT_OBJ("alert_obj.gif"),
   CHECK("check.gif"),
   CHECKBOX_ENABLED("chkbox_enabled.gif"),
   CHECKBOX_DISABLED("chkbox_disabled.gif"),
   DELETE("delete.gif"),
   ENVIRONMENT("environment.gif"),
   ERROR("error.gif"),
   ERROR_STACK("error_stack.gif"),
   FILE("file.gif"),
   FILE_DELETE("file_delete.gif"),
   FLDR_OBJ("fldr_obj.gif"),
   LOAD_CONFIG("loadConfig.gif"),
   OFP("ofp.gif"),
   PROJECT_SET_IMAGE("import_wiz.gif"),
   SAVE_EDIT("save_edit.gif"),
   SAVEAS_EDIT("saveas_edit.gif"),
   SEL_ABORT_STOP("sel_abort_stop.gif"),
   SEL_BATCH_ABORT_STOP("sel_batch_abort_stop.gif"),
   SEL_RUN_EXEC("sel_run_exec.gif"),
   SCRIPT_CANCELLED("scriptCancelled.gif"),
   SCRIPT_CANCELLED_SM("scriptCancelled_sm.gif"),
   SCRIPT_CANCELLING("scriptCancelling.gif"),
   SCRIPT_CANCELLING_SM("scriptCancelling_sm.gif"),
   SCRIPT_COMPLETE("scriptComplete.gif"),
   SCRIPT_COMPLETE_SM("scriptComplete_sm.gif"),
   SCRIPT_IN_QUEUE("scriptInQueue.gif"),
   SCRIPT_IN_QUEUE_SM("scriptInQueue_sm.gif"),
   SCRIPT_OUTPUT("scriptOutput.gif"),
   SCRIPT_OUTPUT_SM("scriptOutput_sm.gif"),
   SCRIPT_READY("scriptReady.gif"),
   SCRIPT_READY_SM("scriptReady_sm.gif"),
   SCRIPT_RUNNING("scriptRunning.gif"),
   TEST("test.gif"),
   TEST_BATCH_IMAGE("file.gif"),
   TEST_MANAGER("tm.gif"),
   UNSEL_ABORT_STOP("unsel_abort_stop.gif"),
   UNSEL_BATCH_ABORT_STOP("unsel_batch_abort_stop.gif"),
   UNSEL_RUN_EXEC("unsel_run_exec.gif");

   private final String fileName;

   private OteTestManagerImage(String fileName) {
      this.fileName = fileName;
   }

   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(TestManagerPlugin.PLUGIN_ID, "images", fileName);
   }

   @Override
   public String getImageKey() {
      return TestManagerPlugin.PLUGIN_ID + ".images." + fileName;
   }
}
