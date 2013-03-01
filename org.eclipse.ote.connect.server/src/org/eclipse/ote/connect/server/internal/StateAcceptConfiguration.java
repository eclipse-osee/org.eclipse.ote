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
package org.eclipse.ote.connect.server.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.BundleConfigurationReport;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.OpMode;
import org.eclipse.ote.connect.messages.RequestStatus;
import org.eclipse.ote.connect.messages.ServerConfigurationResponse;
import org.eclipse.ote.connect.messages.ServerConfigurationStatus;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.BaseState;

public class StateAcceptConfiguration extends BaseState {

   private InputStartingConfiguration inputStartingConfiguration;
   private InputAcceptingUpdatedConfiguration inputAcceptingUpdatedConfiguration;
   private IRuntimeLibraryManager runtimeLibraryManager;
   private OteSendByteMessage sender;

   public StateAcceptConfiguration(OteSendByteMessage sender, InputAcceptingUpdatedConfiguration inputAcceptingUpdatedConfiguration, InputStartingConfiguration inputStartingConfiguration, IRuntimeLibraryManager runtimeLibraryManager){
      this.inputStartingConfiguration = inputStartingConfiguration;
      this.runtimeLibraryManager = runtimeLibraryManager;
      this.inputAcceptingUpdatedConfiguration =inputAcceptingUpdatedConfiguration;
      this.sender = sender;
   }

   private void startConfigurationInNewThread(List<BundleDescription> bundles, UUID uuid) {
      Thread th = new Thread(new BundleConfiguration(runtimeLibraryManager, uuid, sender, inputAcceptingUpdatedConfiguration, bundles));
      th.setName("Configuring OTE Bundles");
      th.start();
   }
  
   private static class BundleConfiguration implements Runnable {
      private OteSendByteMessage sender;
      private List<BundleDescription> bundles;
      private IRuntimeLibraryManager runtimeLibraryManager;
      private UUID uuid;
      
      public BundleConfiguration(IRuntimeLibraryManager runtimeLibraryManager, UUID uuid, OteSendByteMessage sender, InputAcceptingUpdatedConfiguration inputAcceptingUpdatedConfiguration, List<BundleDescription> bundles){
         this.sender = sender;
         this.uuid = uuid;
         this.runtimeLibraryManager = runtimeLibraryManager;
         this.bundles = bundles;
      }
      
      @Override
      public void run(){
         ServerConfigurationStatus status = new ServerConfigurationStatus();
         status.setSessionUUID(uuid);
         System.out.println(status.getSessionUUID());
         
         status.TOTAL_UNITS_OF_WORK.setValue(bundles.size());
         status.UNITS_WORKED_SO_FAR.setValue(bundles.size());
         try {
            List<BundleDescription> toload = new ArrayList<BundleDescription>();
            int count = 0;
            boolean fail = false;
            for(BundleDescription bundle:bundles){
               count++;
               toload.clear();
               toload.add(bundle);
               runtimeLibraryManager.loadBundles(toload);
               BundleConfigurationReport report = runtimeLibraryManager.checkBundleConfiguration(toload);
               if(report.getMissing().size() > 0 || report.getVersionMismatch().size() > 0){
                  fail = true;
                  status.MESSAGE.setValue("Failed to load " + bundle.getSymbolicName() + " " + bundle.getVersion());
               } else {
                  status.MESSAGE.setValue("");
               }
               status.STATUS.setValue(OpMode.inProgress);
               status.UNITS_WORKED_SO_FAR.setValue(count);
               sender.asynchSend(status);
            }
            if(fail){            
               status.STATUS.setValue(OpMode.fail);   
            } else {
               status.STATUS.setValue(OpMode.success);
            }
            sender.asynchSend(status);
         } catch (Exception e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
            status.STATUS.setValue(OpMode.fail);
            sender.asynchSend(status);
         } finally {
            //TODO determine when to allow updated configurations
//          inputAcceptingUpdatedConfiguration.addToStateMachineQueue();
         }
      }
   }

   @Override
   public void run(BaseInput input) {
      if(input.getType() == InputServerConfigurationRequest.TYPE){
         try{
            ServerConfigurationResponse response = new ServerConfigurationResponse();
            InputServerConfigurationRequest request = (InputServerConfigurationRequest)input;
            response.setSessionUUID(request.get().getSessionUUID());
            List<BundleDescription> bundles = Arrays.asList( request.get().getBundleConfiguration());
            BundleConfigurationReport report = runtimeLibraryManager.checkBundleConfiguration(bundles);
            if(report.getVersionMismatch().size() > 0){
               response.STATUS.setValue(RequestStatus.no);
            } else {
               response.STATUS.setValue(RequestStatus.yes);
               startConfigurationInNewThread(bundles, request.get().getSessionUUID());
            }
            sender.asynchSend(response);
            inputStartingConfiguration.addToStateMachineQueue();
         } catch (Exception ex){
            ex.printStackTrace();
         }
      }
   }

   @Override
   public void entry() {
   }

}
