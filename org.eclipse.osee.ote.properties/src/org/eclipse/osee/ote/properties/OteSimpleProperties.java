package org.eclipse.osee.ote.properties;

public interface OteSimpleProperties {
   String getKey();
   void setValue(String value);  
   String getValue();
   String getValue(String defaultValue);
}
