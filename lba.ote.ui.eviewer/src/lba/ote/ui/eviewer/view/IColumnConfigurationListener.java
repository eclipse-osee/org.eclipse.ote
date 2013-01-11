/*
 * Created on Oct 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.Collection;

/**
 * @author Ken J. Aguilar
 */
public interface IColumnConfigurationListener {

   void changed();

   void activeStateChanged(Collection<ColumnDetails> columns);
}
