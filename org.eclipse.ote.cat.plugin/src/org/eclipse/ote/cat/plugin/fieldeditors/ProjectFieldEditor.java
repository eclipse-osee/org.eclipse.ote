/*******************************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.cat.plugin.fieldeditors;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.composites.AddRemoveBox;
import org.eclipse.ote.cat.plugin.composites.ButtonBox;
import org.eclipse.ote.cat.plugin.preferencepage.Preference;
import org.eclipse.ote.cat.plugin.util.Projects;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * A {@link FieldEditor} to select the projects to be configured or unconfigured for the Compiler Applicability Tool
 * (CAT).
 */

public class ProjectFieldEditor extends FieldEditor {

   /**
    * An extension of the class {@link AddRemoveBox} for selection of Eclipse Projects ({@link IProject}) objects.
    */

   public class ProjectSelectionBox extends AddRemoveBox<IProject> {

      /**
       * Creates a new {@link AddRemoveBox} for {@link IProject} objects.
       * 
       * @param parent the {@link Composite} the selection box will be attached to.
       * @param selectionChangedAction a callback method for when the selection box selection has changed.
       */

      public ProjectSelectionBox(Composite parent, SelectionChangedAction selectionChangedAction) {
         super(parent, selectionChangedAction);
      }

   }

   /**
    * The preferred minimum number of grid columns for the control.
    */

   private static final int controlColumnCount = 2;

   /**
    * Saves the control for the "add" and "remove" buttons.
    */

   private ButtonBox buttonBox;

   /**
    * Saves the parent {@link Composite}.
    */

   private Composite parent;

   /**
    * Saves the control for the selected projects table view box.
    */

   private ProjectSelectionBox projectSelectionBox;

   /**
    * Creates a Project field editor.
    *
    * @param name the name of the preference this field editor works on
    * @param labelText the label text of the field editor
    * @param parent the parent of the field editor's control
    */

   public ProjectFieldEditor(String name, String labelText, Composite parent) {
      this.parent = parent;
      this.init(name, labelText);
      this.createControl(parent);
   }

   /**
    * Performs the action for the "Add" button by:
    * <ul>
    * <li>presenting the user with a selection box of the Eclipse Projects, and</li>
    * <li>adding the selected projects to the project selection box.</li>
    * </ul>
    */

   private void addPressed() {
      List<IProject> newProjects = this.promptForNewProjects();
      if (Objects.nonNull(newProjects) && (newProjects.size() > 0)) {
         this.getProjectSelectionBox().add(newProjects);
      }
      this.selectionChanged();
      this.setPresentsDefaultValue(false);
   }

   /**
    * Adjusts the layout data for the controls as follows:
    * <ul>
    * <li>The label is given the entire first row.</li>
    * <li>When the number of columns is greater than one, the project selection box is given all but the last column of
    * the second row. Otherwise, the project selection box is given the entire second row.</li>
    * <li>When the number of columns is greater than one, the buttons are given the last column of the second row.
    * Otherwise, the buttons are given the entire third row. The buttons are set to align with the top of the row
    * containing the buttons.</li>
    * </ul>
    * 
    * @param numColumns the number of columns in the parent {@link Composite} the controls are placed in.
    */

   @Override
   protected void adjustForNumColumns(int numColumns) {
      {
         /*
          * Use the first full grid row for the label
          */
         Control labelControl = this.getLabelControl();
         GridData labelGridData = (GridData) labelControl.getLayoutData();
         labelGridData.horizontalSpan = numColumns;
      }
      {
         /*
          * Use all but the last grid column of the second row for the table
          */
         Control tableControl = this.getProjectSelectionBox();
         GridData tableGridData = (GridData) tableControl.getLayoutData();
         tableGridData.horizontalSpan = Math.max(numColumns - 1, 1);
      }
      {
         /*
          * Use the last column of the second row for the buttons
          */
         Control buttonBoxControl = this.getButtonBox();
         GridData buttonBoxGridData = (GridData) buttonBoxControl.getLayoutData();
         buttonBoxGridData.horizontalSpan = 1;
         buttonBoxGridData.verticalAlignment = GridData.BEGINNING;
      }
   }

   /**
    * The callback method used by the {@link #buttonBox} to generate a bit mask of the buttons to be enabled as follows:
    * <ul>
    * <li>The {@link ButtonBox.AddButton} is always enabled.</li>
    * <li>The {@link ButtonBox.RemoveButton} is enabled when the {@link #projectSelectionBox} has a selection.</li>
    * </ul>
    * 
    * @return bit mask for the enabled buttons.
    */

   private int createButtonBoxEnableMask() {
      //@formatter:off
      int mask =
           ButtonBox.AddButton
         | (this.getProjectSelectionBox().hasSelection() ? ButtonBox.RemoveButton : 0);
      //@formatter:on
      return mask;
   }

   /**
    * Creates and lays out the controls for the {@link ProjectFieldEditor} {@link FieldEditor}.
    * <p>
    * {@inheritDoc}
    */

   @Override
   protected void doFillIntoGrid(Composite parent, int numColumns) {
      {
         final GridData labelGridData = new GridData();
         Control labelControl = this.getLabelControl(parent);
         labelControl.setLayoutData(labelGridData);
      }
      {
         final GridData tableGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
         Control tableControl = this.getProjectSelectionBox();
         tableControl.setLayoutData(tableGridData);
      }
      {
         final GridData buttonBoxGridData = new GridData();
         Control buttonBoxControl = this.getButtonBox();
         buttonBoxControl.setLayoutData(buttonBoxGridData);
      }
      this.adjustForNumColumns(numColumns);
      this.getButtonBox().enableButtons();
   }

   /**
    * Gets the stored preference string indicating which projects have been selected. The named projects are found and
    * loaded in to the {@link #projectSelectionBox}.
    * <p>
    * {@inheritDoc}
    */

   @Override
   protected void doLoad() {
      String jtsProjectsPreferenceString = Preference.JTS_PROJECTS.get();
      String[] projectStrings = jtsProjectsPreferenceString.split(",");
      Map<String, IProject> projectMap = Projects.getProjectMap(IProject::toString);
      List<IProject> projects = new LinkedList<>();
      for (String projectString : projectStrings) {
         IProject project = projectMap.get(projectString);
         if (project == null) {
            continue;
         }
         projects.add(project);
      }
      this.getProjectSelectionBox().add(projects);
   }

   /**
    * By default no projects are selected. This method performs no action.
    * <p>
    * {@inheritDoc}
    */

   @Override
   protected void doLoadDefault() {
   }

   /**
    * Gets the list of selected projects from the {@link #projectSelectionBox}, creates a string with all of the project
    * names, and saves the string in the preference store.
    * <p>
    * {@inheritDoc}
    */

   @Override
   protected void doStore() {
      //@formatter:off
      String jtsProjectsPreferenceString =
         this
            .getProjectSelectionBox()
            .getContents(LinkedList::new)
            .stream()
            .map( IProject::toString )
            .collect( Collectors.joining(",") );
      //@formatter:on
      Preference.JTS_PROJECTS.set(jtsProjectsPreferenceString);
   }

   /**
    * Gets the {@link #buttonBox} and creating it if necessary.
    *
    * @return the button box.
    */

   private ButtonBox getButtonBox() {
      return //
      Objects.nonNull(this.buttonBox) //
         ? this.buttonBox //
         : (this.buttonBox =
            new ButtonBox(this.parent, this::addPressed, this::removePressed, this::createButtonBoxEnableMask));
   }

   /**
    * The preferred minimum number of grid columns for the control.
    * <p>
    * {@inheritDoc}
    * 
    * @return {@value ProjectFieldEditor#controlColumnCount}
    */

   @Override
   public int getNumberOfControls() {
      return ProjectFieldEditor.controlColumnCount;
   }

   /**
    * Gets the {@link #projectSelectionBox} and creating it if necessary.
    * 
    * @return the project selection box.
    */

   private ProjectSelectionBox getProjectSelectionBox() {
      if (Objects.nonNull(this.projectSelectionBox)) {
         return this.projectSelectionBox;
      }
      this.projectSelectionBox = new ProjectSelectionBox(this.parent, this::selectionChanged);
      List<IProject> projectsWithNature = Projects.getProjectsWithNature(CatPlugin.getCatNatureIdentifier());
      this.projectSelectionBox.add(projectsWithNature);
      return this.projectSelectionBox;
   }

   /**
    * Presents the user with a list dialog to select projects to be configured for the CAT.
    *
    * @return a list of the projects selected by the user.
    */

   protected List<IProject> promptForNewProjects() {
      List<IProject> projectsWithoutNature = Projects.getProjectsWithoutNature(CatPlugin.getCatNatureIdentifier());
      ArrayContentProvider arrayContentProvider = new ArrayContentProvider();
      WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();
      Shell parentShell = this.parent.getShell();
      //@formatter:off
      ListSelectionDialog listSelectionDialog =
         new ListSelectionDialog
                (
                   parentShell,
                   projectsWithoutNature,
                   arrayContentProvider,
                   workbenchLabelProvider,
                   "Select a project to be configured for building with the CAT."
                );
      //@formatter:on
      int result = listSelectionDialog.open();
      if (result == Window.OK) {
         Object[] selectedProjects = listSelectionDialog.getResult();
         @SuppressWarnings("unchecked")
         List<IProject> newProjects = (List<IProject>) (Object) Arrays.asList(selectedProjects);
         return newProjects;
      }
      return Collections.emptyList();
   }

   /**
    * Callback method for the remove button. Removes the selected items in the table view from the selection box.
    */

   private void removePressed() {
      List<IProject> selectedProjects = this.getProjectSelectionBox().getSelected();
      this.getProjectSelectionBox().remove(selectedProjects);
   }

   /**
    * Callback method invoked when the selection in the project selection box has changed. This method enables or
    * disables the {@link #buttonBox} buttons according to the {@link #projectSelectionBox} selection state.
    * 
    * @implNote Invocation of this method does not indicate that the projects within the {@link #projectSelectionBox}
    * have changed.
    */

   private void selectionChanged() {
      this.getButtonBox().enableButtons();
   }

}
