package org.eclipse.ote.ui.eviewer.test;
import org.eclipse.osee.ote.message.elements.BiSci16Element;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.elements.Dec32Element;
import org.eclipse.osee.ote.message.elements.FixedPointElement;
import org.eclipse.osee.ote.message.elements.Float32Element;
import org.eclipse.osee.ote.message.elements.Float64Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.ote.ui.eviewer.tohex.ToHexFactory;
import org.junit.Assert;
import org.junit.Test;


public class ElementViewerToHexTest {


   private static ToHexFactory toHexFactory;
   @Test
   public void toHexTest() {
      
      toHexFactory = new ToHexFactory();
      
      ElementViewerTestMessage testMessage = new ElementViewerTestMessage();
      byte[] data = "blank data".getBytes();
      ElementViewerTestMessageData testMessageData = new ElementViewerTestMessageData(data, data.length, 0);
      
      IntegerElement integerElement = new IntegerElement(testMessage, "element", testMessageData, 0, 32);
      integerElement.setValue(10);
      String intToHex = toHexFactory.getHexConverter(integerElement).toHex(integerElement).toUpperCase();
      String intToHexPredicate = "A";
      Assert.assertEquals(intToHexPredicate, intToHex);
      
      LongIntegerElement longElement = new LongIntegerElement(testMessage, "element", testMessageData, 0, 64);
      longElement.setValue((long) 98765);
      String longToHex = toHexFactory.getHexConverter(longElement).toHex(longElement).toUpperCase();
      String longToHexPredicate = "181CD";
      Assert.assertEquals(longToHexPredicate, longToHex);
      
      Float32Element float32Element = new Float32Element(testMessage, "element", testMessageData, 0, 32);
      float32Element.setValue(10.5);
      String float32ToHex = toHexFactory.getHexConverter(float32Element).toHex(float32Element).toUpperCase();
      String float32ToHexPredicate = "41280000";
      Assert.assertEquals(float32ToHexPredicate, float32ToHex);
      
      Float64Element float64Element = new Float64Element(testMessage, "element", testMessageData, 0, 64);
      float64Element.setValue(55.625);
      String float64ToHex = toHexFactory.getHexConverter(float64Element).toHex(float64Element).toUpperCase();
      String float64ToHexPredicate = "404BD00000000000";
      Assert.assertEquals(float64ToHexPredicate, float64ToHex);
      
      Dec32Element decElement = new Dec32Element(testMessage, "element", testMessageData, 0, 32);
      decElement.setValue(10.5);
      String decToHex = toHexFactory.getHexConverter(decElement).toHex(decElement);
      String decToHexPredicate = "42280000";
      Assert.assertEquals(decToHexPredicate, decToHex);
      
      FixedPointElement fixedElement = new FixedPointElement(testMessage, "element", testMessageData, 1, true, 0, 0, 11);
      fixedElement.setValue((double) -2900.0);
      String fixedtoHex = toHexFactory.getHexConverter(fixedElement).toHex(fixedElement).toUpperCase();
      String fixedToHexPredicate = "4AC";
      Assert.assertEquals(fixedToHexPredicate, fixedtoHex);
      
      BiSci16Element biSci16Element = new BiSci16Element(testMessage, "element", testMessageData, 0, 16);
      biSci16Element.setValue((long) 65535);
      String biSci16ToHex = toHexFactory.getHexConverter(biSci16Element).toHex(biSci16Element).toUpperCase();
      String biSci16ToHexPredicate = "1002";
      Assert.assertEquals(biSci16ToHexPredicate, biSci16ToHex);
      
      StringElement stringElement = new StringElement(testMessage, "element", testMessageData, 0, 64);
      stringElement.setValue("ABCD");
      String stringToHex = toHexFactory.getHexConverter(stringElement).toHex(stringElement);
      String stringToHexPredicate = "ABCD";
      Assert.assertEquals(stringToHexPredicate, stringToHex);
      
      CharElement charElement = new CharElement(testMessage, "element", testMessageData, 0, 8);
      charElement.setValue('B');
      String charToHex = toHexFactory.getHexConverter(charElement).toHex(charElement);
      String charToHexPredicate = "42";
      Assert.assertEquals(charToHexPredicate, charToHex);
      
      
      
   }

}
