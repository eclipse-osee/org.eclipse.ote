package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public class RECORDING_COMPLETE extends OteEventMessage {

   public static String TOPIC = "ote/message/recordingComplete";
   
	public RECORDING_COMPLETE() {
		super("RECORDING_COMPLETE", TOPIC, 0);
	}

}
