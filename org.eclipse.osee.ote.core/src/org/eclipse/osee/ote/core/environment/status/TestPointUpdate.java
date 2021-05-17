/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;

public class TestPointUpdate implements Serializable, IServiceStatusDataCommand {

   private static final long serialVersionUID = 7157851807444983673L;
   private int pass;
   private int fail;
   private int interactive;
   private CommandDescription description;
   private String testClassName;

   public TestPointUpdate(int pass, int fail, CommandDescription description) {
      this.pass = pass;
      this.fail = fail;
      this.description = description;
   }

   public TestPointUpdate(int pass, int fail, int interactive, String testClassName) {
      this.pass = pass;
      this.fail = fail;
      this.interactive = interactive;
      this.testClassName = testClassName;
   }

   public TestPointUpdate() {
   }

   public int getFail() {
      return fail;
   }

   public int getPass() {
      return pass;
   }

   public int getInteractive() {
      return interactive;
   }

   public String getClassName() {
      return testClassName;
   }

   @Override
   public CommandDescription getDescription() {
      return description;
   }

   public void set(int pass, int fail, CommandDescription description) {
      this.pass = pass;
      this.fail = fail;
      this.description = description;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asTestPointUpdate(this);
      }
   }
}
