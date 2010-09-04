/*
 * Created on Apr 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.BitSet;

/**
 * @author Ken J. Aguilar
 */
public interface IUpdateListener {
   void update(SubscriptionDetails details, BitSet deltaSet);
}
