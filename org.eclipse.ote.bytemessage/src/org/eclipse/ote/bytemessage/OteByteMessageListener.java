package org.eclipse.ote.bytemessage;

public interface OteByteMessageListener<T extends OteByteMessage> {
   
   void onDataAvailable(T message);

}
