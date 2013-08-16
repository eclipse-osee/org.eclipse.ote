package org.eclipse.ote.message.lookup;

import java.util.List;

public interface MessageAssociationLookup {
   List<String> lookupAssociatedMessages(String classname);
}
