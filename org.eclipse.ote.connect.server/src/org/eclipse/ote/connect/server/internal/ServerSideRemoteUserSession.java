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
import org.eclipse.ote.bytemessage.OteSendByteMessage;

public class ServerSideRemoteUserSession implements IRemoteUserSession {

   public ServerSideRemoteUserSession(UUID sessionUUID, OteSendByteMessage sender) {
      // TODO Auto-generated constructor stub
   }

   @Override
   public void initiateYesNoPrompt(IYesNoPromptResponse prompt) throws Exception {
      // TODO Auto-generated method stub

   }

   @Override
   public void cancelPrompts() throws Exception {
      // TODO Auto-generated method stub

   }

   @Override
   public String getAddress() throws RemoteException {
      // TODO Auto-generated method stub
      return "mine";
   }

   @Override
   public OSEEPerson1_4 getUser() throws RemoteException {
      // TODO Auto-generated method stub
      return new OSEEPerson1_4("na", "na", "na");
   }

   @Override
   public byte[] getFile(String workspacePath) throws RemoteException {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public long getFileDate(String workspacePath) throws RemoteException {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiatePassFailPrompt " + prompt.toString());
   }

   @Override
   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiateUserInputPrompt " + prompt.toString());
   }

   @Override
   public void initiateResumePrompt(IResumeResponse prompt) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiateResumePrompt " + prompt.toString());
   }

   @Override
   public void initiateInformationalPrompt(String message) throws RemoteException {
      // TODO Auto-generated method stub
      System.out.println("initiateInformationalPrompt " + message.toString());
   }

   @Override
   public boolean isAlive() throws RemoteException {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public void sendMessageToClient(Message message) throws RemoteException {
      System.out.println("sendMessageToClient " + message.toString());
   }

}
