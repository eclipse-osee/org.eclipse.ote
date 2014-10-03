package org.eclipse.ote.connect.server.internal;

import java.rmi.RemoteException;
import java.util.UUID;

import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;

public class FakeUserSession implements IRemoteUserSession {

   @Override
   public void initiateYesNoPrompt(IYesNoPromptResponse prompt) throws Exception {
      // TODO Auto-generated method stub
      System.out.println("initiateYesNoPrompt");
   }

   @Override
   public void cancelPrompts() throws Exception {
      // TODO Auto-generated method stub
      System.out.println("cancelPrompts");
   }

   @Override
   public String getAddress() throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("getAddress");
      return "127.0.0.1";
   }

   @Override
   public OSEEPerson1_4 getUser() throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("getUser");
      return new OSEEPerson1_4("na", "na", "na");
   }

   @Override
   public byte[] getFile(String workspacePath) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("getFile");
      return new byte[0];
   }

   @Override
   public long getFileDate(String workspacePath) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("getFileDate");
      return 0;
   }

   @Override
   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiatePassFailPrompt");
   }

   @Override
   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiateUserInputPrompt");
   }

   @Override
   public void initiateResumePrompt(IResumeResponse prompt) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiateResumePrompt");
   }

   @Override
   public void initiateInformationalPrompt(String message) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiateInformationalPrompt");
   }

   @Override
   public boolean isAlive() throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("isAlive");
      return false;
   }

   @Override
   public void sendMessageToClient(Message message) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("sendMessageToClient");
   }

   @Override
   public UUID getUserId() throws Exception {
      // TODO Auto-generated method stub
      System.out.println("get id");
      return UUID.randomUUID();
   }

}
