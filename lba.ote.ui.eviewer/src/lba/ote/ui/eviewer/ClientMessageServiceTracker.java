/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer;

import java.util.logging.Level;
import lba.ote.ui.eviewer.view.ElementViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class ClientMessageServiceTracker extends ServiceTracker {

   private final ElementViewer viewer;

   public ClientMessageServiceTracker(ElementViewer viewer) {
      super(Activator.getDefault().getBundleContext(), IOteMessageService.class.getName(), null);
      this.viewer = viewer;
   }

   @Override
   public synchronized Object addingService(ServiceReference reference) {
      IOteMessageService service = (IOteMessageService) super.addingService(reference);
      try {
         viewer.serviceStarted(service);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE, "exception while notifying viewer of service", e);
      }
      return service;
   }

   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
      IOteMessageService oteMessageService = (IOteMessageService) service;
      try {
         viewer.serviceStopping(oteMessageService);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE,
            "exception while notifying viewer of service stop", e);
      }
      super.removedService(reference, service);
   }
}
