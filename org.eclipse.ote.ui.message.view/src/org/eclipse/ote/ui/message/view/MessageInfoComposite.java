package org.eclipse.ote.ui.message.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ote.message.lookup.MessageAssociationLookup;
import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.eclipse.ote.ui.message.view.internal.MessageViewServiceUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MessageInfoComposite extends Composite {
   private static final int OFFSET = 10;
   private static final int HALF_OFFSET = 5;

   private final Color TEXT_BACKGROUND_COLOR = new Color(null, 230, 230, 230);
   private final Color NOT_FOUND_COLOR = new Color(null, 255, 180, 180);

   private StyledText labelMessage;
   private StyledText labelType;
   private StyledText labelPublishers;
   private StyledText labelSubscribers;
   private StyledText labelAssociated;
   private Text labelByteSize;
   private Text labelPhase;
   private Text labelRate;
   private Text labelScheduled;

   private List<String> associatedMsgs;
   private MessageInfoSelectionListener selectionListener;

   public MessageInfoComposite(Composite parent) {
      super(parent, SWT.NONE);
      associatedMsgs = null;
      selectionListener = null;
      createComposite(this);
   }

   private void createComposite(Composite parent) {
      FormLayout layout = new FormLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      parent.setLayout(layout);

      Composite labelBorder = new Composite(parent, SWT.NONE);
      labelBorder.setBackground(new Color(null, 0,0,0));
      FillLayout labelBorderLayout = new FillLayout();
      labelBorderLayout.marginHeight = 1;
      labelBorderLayout.marginWidth = 1;
      labelBorder.setLayout(labelBorderLayout);
      labelMessage = new StyledText(labelBorder, SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
      labelMessage.setBackground(TEXT_BACKGROUND_COLOR);
      Widgets.attachToParent(labelBorder, SWT.LEFT, 0, HALF_OFFSET);
      Widgets.attachToParent(labelBorder, SWT.RIGHT, 100, -HALF_OFFSET);
      Widgets.attachToControl(labelBorder, parent, SWT.TOP, SWT.TOP, HALF_OFFSET);

      labelType = createWideStyled(parent, "Type:", labelBorder, labelBorder);
      labelPublishers = createWideStyled(parent, "Publishers:", labelBorder, labelType);
      labelSubscribers = createWideStyled(parent, "Subscribers:", labelBorder, labelPublishers);
      labelAssociated = createWideStyled(parent, "Associated:", labelBorder, labelSubscribers);

      Composite comp = new Composite(parent, SWT.NONE);
      Widgets.attachToParent(comp, SWT.LEFT, 0, OFFSET);
      Widgets.attachToControl(comp, labelAssociated, SWT.TOP, SWT.BOTTOM, 0);
      GridLayoutFactory.swtDefaults().numColumns(4).applyTo(comp);
      labelByteSize = createGridEntry(comp, "Byte Size:");
      labelPhase = createGridEntry(comp, "Phase:");
      labelRate = createGridEntry(comp, "Rate:");
      labelScheduled = createGridEntry(comp, "Scheduled:");

      labelAssociated.addListener(SWT.MouseDown, new Listener() {
         @Override
         public void handleEvent(Event event) {
            if (event.button == 1) {
               try {
                  int offset = labelAssociated.getOffsetAtLocation(new Point (event.x, event.y));
                  StyleRange[] styleRanges = labelAssociated.getStyleRanges(true);
                  String classname = null;
                  for (int i=0; i < styleRanges.length; i++) {
                     int start = styleRanges[i].start;
                     int end = start+styleRanges[i].length;
                     if (start <= offset && end > offset) {
                        classname = associatedMsgs.get(i);
                        break;
                     }
                  }
                  if (classname != null && !classname.isEmpty() && selectionListener != null) {
                     selectionListener.associatedClassSelected(classname);
                  }
               } catch (IllegalArgumentException e) {
                  // no character under event.x, event.y
               }
            }
         }
      });

      parent.layout();
   }

   private Text createGridEntry(Composite parent, String text) {
      Label label = new Label(parent, SWT.NONE);
      label.setText(text);
      label.setAlignment(SWT.RIGHT);
      Text value = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      value.setBackground(TEXT_BACKGROUND_COLOR);
      GridDataFactory.fillDefaults().applyTo(value);
      return value;
   }


   private StyledText createWideStyled(Composite parent, String text, Control lrControl, Control topControl) {
      Label label = new Label(parent, SWT.NONE);
      label.setText(text);
      label.setAlignment(SWT.RIGHT);
      FormData formData = new FormData(SWT.DEFAULT, 30);
      label.setLayoutData(formData);
      StyledText value = new StyledText (parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
      value.setBackground(TEXT_BACKGROUND_COLOR);

      layoutWide(lrControl, topControl, label, value);

      return value;
   }

   private void layoutWide(Control lrControl, Control topControl, Control label, Control value) {
      Widgets.attachToControl(label, lrControl, SWT.LEFT, SWT.LEFT, 0);
      Widgets.attachToControl(label, topControl, SWT.TOP, SWT.BOTTOM, OFFSET);
      Widgets.attachToControl(value, label, SWT.TOP, SWT.TOP, -HALF_OFFSET);
      Widgets.attachToControl(value, label, SWT.LEFT, SWT.RIGHT, HALF_OFFSET);
      Widgets.attachToControl(value, lrControl, SWT.RIGHT, SWT.RIGHT, 0);
   }

   public void search(final String className) {
      final MessageLookup messageLookup = MessageViewServiceUtility.getService(MessageLookup.class);
      final MessageAssociationLookup associationLookup = MessageViewServiceUtility.getService(MessageAssociationLookup.class);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (messageLookup != null && associationLookup != null) {
               MessageLookupResult result =  messageLookup.lookupClass(className);
               associatedMsgs = associationLookup.lookupAssociatedMessages(className);
               if (result != null) {
                  try {
                     labelMessage.setText(result.getClassName());
                     labelMessage.setBackground(TEXT_BACKGROUND_COLOR);
                     labelType.setText(result.getMessageType());
                     labelPublishers.setText(toMessageCsv(result.getPublishers()));
                     labelSubscribers.setText(toMessageCsv(result.getSubscribers()));
                     labelAssociated.setText("");
                     labelByteSize.setText(Integer.toString(result.getByteSize()));
                     labelPhase.setText(result.getPhase());
                     labelRate.setText(result.getRate());
                     labelScheduled.setText(result.getScheduled());
                     updateAssociated();
                  }
                  catch (Exception e) {
                     setNotFound(className);
                  }
               }
               else {
                  setNotFound(className);
               }
            }
            else {
               OseeLog.log(getClass(), Level.SEVERE, new Exception("Required Services not found for message info search"));
               setNotFound(className);
            }
            layout(true, true);
         }
      });
   }

   private void updateAssociated() {
      final String associated = toMessageCsv(associatedMsgs);
      final int size = associatedMsgs.size();
      labelAssociated.setText(associated);

      StyleRange[] styles = new StyleRange[size];
      for (int i=0; i<size; i++) {
         final String msgName = getMessageName(associatedMsgs.get(i));
         StyleRange style = new StyleRange();
         style.underline = true;
         style.underlineStyle = SWT.UNDERLINE_LINK;
         style.start = associated.indexOf(msgName);
         style.length = msgName.length();
         styles[i] = style;
      }
      labelAssociated.setStyleRanges(styles);
   }

   private String toMessageCsv(List<String> associatedResult) {
      StringBuilder sb = new StringBuilder();
      final int size = associatedResult.size();
      for (int i=0; i<size; i++) {
         String msgName = getMessageName(associatedResult.get(i));
         sb.append(msgName).append(", ");
      }
      if (sb.length() > 2) {
         sb.delete(sb.length()-2, sb.length()-1);
      }
      return sb.toString();
   }

   private String getMessageName(final String classname) {
      return classname.substring(classname.lastIndexOf('.')+1);
   }

   private void setNotFound(final String className) {
      labelMessage.setText(className);
      labelMessage.setBackground(NOT_FOUND_COLOR);
      labelType.setText("");
      labelPublishers.setText("");
      labelSubscribers.setText("");
      labelAssociated.setText("");
      labelByteSize.setText("");
      labelPhase.setText("");
      labelRate.setText("");
      labelScheduled.setText("");
      associatedMsgs = null;
   }

   public void setSelectionListener(MessageInfoSelectionListener listener) {
      selectionListener = listener;
   }

   private void test() {
      labelMessage.setText("osee.test.core.message.pubsub.WEAP_IN");
      if (labelMessage.getBackground().equals(TEXT_BACKGROUND_COLOR)) {
         labelMessage.setBackground(NOT_FOUND_COLOR);
      } else {
         labelMessage.setBackground(TEXT_BACKGROUND_COLOR);
      }
      labelType.setText("PUB_SUB");
      labelPublishers.setText("IOP_P");
      labelSubscribers.setText("INSTR, IOP_O, WPS_B, AND, MANY1, MANY2, MANY3, MANY4, MANY5, MANY6, MORE");
      labelByteSize.setText("20");
      labelPhase.setText("0");
      labelRate.setText("50.0");
      labelScheduled.setText("false");
      associatedMsgs = new ArrayList<String>();
      associatedMsgs.add("osee.test.core.message.aiu.WEAP_IN_AIL_AIU_WIRE");
      associatedMsgs.add("osee.test.core.message.mux.CH5_23T06");
      associatedMsgs.add("osee.test.core.message.mux.CH5_22T06");
      associatedMsgs.add("osee.test.core.message.mux.CH5_25T06");
      associatedMsgs.add("osee.test.core.message.mux.CH5_21T06");
      associatedMsgs.add("osee.test.core.message.mux.CH5_26T06");
      associatedMsgs.add("osee.test.core.message.mux.CH5_24T06");
      associatedMsgs.add("osee.test.core.message.pats.WEAP_IN_PATS_WIRE");
      updateAssociated();
      layout(true, true);
   }

   public static void main(String[] args) {
      Display display = new Display ();
      Shell shell = new Shell (display);
      shell.setText ("Shell");
      shell.setLayout(new FormLayout());
      final MessageInfoComposite view = new MessageInfoComposite(shell);

      Button button = new Button(shell, SWT.NONE);
      button.setText("Push it real good");

      Widgets.attachToParent(view, SWT.TOP, 0, 0);
      Widgets.attachToParent(view, SWT.LEFT, 0, 0);
      Widgets.attachToParent(view, SWT.RIGHT, 100, 0);
      Widgets.attachToControl(view, button, SWT.BOTTOM, SWT.TOP, 0);
      Widgets.attachToParent(button, SWT.BOTTOM, 100, 0);
      button.addSelectionListener(new SelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            view.test();
         }
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {}
      });
      shell.layout();
      shell.pack();
      shell.open ();
      shell.setSize (600, 400);
      while (!shell.isDisposed ()) {
         if (!display.readAndDispatch ()) display.sleep ();
      }
      display.dispose ();
   }

}
