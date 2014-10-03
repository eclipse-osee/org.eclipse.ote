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
      
   }

   @Override
   public void initiateYesNoPrompt(IYesNoPromptResponse prompt) throws Exception {
      System.out.println("ServerSideRemoteUserSession initiateYesNoPrompt " + prompt.getPromptMessage());
   }

   @Override
   public void cancelPrompts() throws Exception {
      System.out.println("ServerSideRemoteUserSession cancelPrompts");
   }

   @Override
   public String getAddress() throws RemoteException {
      return "ServerSideRemoteUserSession mine";
   }

   @Override
   public OSEEPerson1_4 getUser() throws RemoteException {
      System.out.println("ServerSideRemoteUserSession getUser");
      return new OSEEPerson1_4("na", "na", "na");
   }

   @Override
   public byte[] getFile(String workspacePath) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession getFile" + workspacePath);
      return null;
   }

   @Override
   public long getFileDate(String workspacePath) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession getFileDate" + workspacePath);
      return 0;
   }

   @Override
   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession initiatePassFailPrompt " + prompt.toString());
   }

   @Override
   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession initiateUserInputPrompt " + prompt.toString());
   }

   @Override
   public void initiateResumePrompt(IResumeResponse prompt) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession initiateResumePrompt " + prompt.toString());
   }

   @Override
   public void initiateInformationalPrompt(String message) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession initiateInformationalPrompt " + message.toString());
   }

   @Override
   public boolean isAlive() throws RemoteException {
      System.out.println("ServerSideRemoteUserSession isAlive ");
      return true;
   }

   @Override
   public void sendMessageToClient(Message message) throws RemoteException {
      System.out.println("ServerSideRemoteUserSession sendMessageToClient " + message.toString());
   }

   @Override
   public UUID getUserId() throws Exception {
      System.out.println("ServerSideRemoteUserSession getUserId");
      return UUID.randomUUID();
   }

}
