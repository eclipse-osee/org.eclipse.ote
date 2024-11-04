/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin.exception;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * An enumeration of error codes used specify the following for CAT Plug-In exceptions:
 * <ul stype="margin-left:3em;">
 * <li>The {@link StatusManager} style bits used to determine if an error dialog is displayed or if the error is
 * logged.</li>
 * <li>The {@link IStatus} severity of the error.</li>
 * <li>A unique integer identifier for the error.</li>
 * <li>The {@link CatErrorCode#Type} used to determine the {@link Control} used to display user support information on
 * the error dialog.</li>
 * </ul>
 * 
 * @author Loren K. Ashley
 */

public enum CatErrorCode {

   //@formatter:off
   /*
    * Status Codes
    * 
    * The lower word (short) of the status code is provided to the CatErrorCode constructor. The upper word (short) is
    * obtained from the Type. For CatErrorCode members with the same Type the lower status code word must be unique.
    * When creating new CatErrorCode members use the next status code for the type according to the table below and increment
    * the value in the table. Don't change existing status codes or reuse status code numbers below the numbers in the table.
    * 
    * Last Status Code By Type:
    * 
    *    Type.UserError:     9
    *    Type.InternalError: 3
    */
   //@formatter:on

   //@formatter:off

   CatJarFileError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0001,
         "CAT Jar File Error",
         "cat-jar-file-error.html"
      ),
      
   CatPluginStateFileError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG, 
         IStatus.ERROR, 
         (short) 0x0002, 
         "CAT Plug-In State File Error", 
         "cat-plugin-state-file-error.html"
      ),

   CatProjectInfoFileError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0003,
         "CAT Project Info File Error",
         "cat-project-info-file-error.html"
      ),
      
   CommandLineOptionError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0004,
         "CAT Plug-In Command Line Option Error",
         "command-line-option-error.html"
      ),
      
   PreferenceFileError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0005,
         "CAT Plug-In Default Preference File Error",
         "default-preference-file-error.html"
      ),
      
   InternalError
      (
         Type.InternalError, 
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0001,
         "CAT Plug-In Internal Error",
         null
      ),
      
   InvalidCatErrorCode
      (
         Type.InternalError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0002,
         "CAT Plug-In \"CatErrorCode\" Error",
         null
      ),

   NoDefaultPreferencesWarning
      (
         Type.UserError,
         StatusManager.LOG,
         IStatus.INFO,
         (short) 0x0006,
         "Default Preferences Command Line Option Warning",
         "no-default-preferences-warning.html"
      ),
   
   PleConfigurationCacheFolderError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0007,
         "PLE Configuration Cache Folder Error",
         "ple-configuration-cache-folder-error.html"
      ),
   
   PreferenceFileSaveError
      (
         Type.UserError,
         StatusManager.BLOCK | StatusManager.LOG,
         IStatus.ERROR,
         (short) 0x0008,
         "CAT Preference File Error",
         "preference-file-save-error.html"
      ),
      
    PreferencePageError
       (
          Type.InternalError,
          StatusManager.BLOCK | StatusManager.LOG,
          IStatus.ERROR,
          (short) 0x0003,
          "CAT Plug-In Preference Page Error",
          null
       ),
       
    SourceLocationMethodError
       (
          Type.UserError,
          StatusManager.BLOCK | StatusManager.LOG,
          IStatus.ERROR,
          (short) 0x0009,
          "CAT Source Location Method Error",
          "source-location-method-error.html"
       );
      
   //@formatter:on

   /**
    * The type of error. Used by the {@link CatErrorSupportProvider} to select the {@link Control} for the user support
    * area of the {@link StatusManager} error dialog.
    */

   enum Type {

      /**
       * Errors of this type are likely not resolvable by the user.
       */

      InternalError((short) 0x0001),

      /**
       * Errors of this type are possibly resolvable by the user.
       */

      UserError((short) 0x0000);

      /**
       * Saves the high word used for a {@link CatErrorCode} status code.
       */

      private short statusCodeHighWord;

      /**
       * Creates a new {@link Type} enumeration member.
       * 
       * @param statusCodeHighWord the high word for {@link CatErrorCode}s of the type.
       */

      Type(short statusCodeHighWord) {
         this.statusCodeHighWord = (short) statusCodeHighWord;
      }

      /**
       * Combines the {@link #statusCodeHighWord} with the provided <code>statusCodeLowWord</code> to produce the
       * complete status code.
       * 
       * @param statusCodeLowWord the low word of the status code.
       * @return
       */
      int createStatusCode(short statusCodeLowWord) {
         int statusCode = (this.statusCodeHighWord << 16) | statusCodeLowWord;
         return statusCode;
      }

      /**
       * Predicate to determine if the {@link Type} member is {@link Type#InternalError}.
       * 
       * @return <code>true</code> when the member is {@link Type#InternalError}; otherwise, <code>false</code>.
       */

      boolean isInternalError() {
         return this == InternalError;
      }

      /**
       * Predicate to determine if the {@link Type} member is {@link Type#UserError}.
       * 
       * @return <code>true</code> when the member is {@link Type#UserError}; otherwise, <code>false</code>.
       */

      boolean isUserError() {
         return this == UserError;
      }
   }

   /**
    * A lookup {@link Map} of {@link CatErrorCode} enumeration members by status code.
    */

   //@formatter:off
   private static Map<Integer, CatErrorCode> statusCodeMap =
      Arrays
         .stream( CatErrorCode.values() )
         .collect( Collectors.toMap( CatErrorCode::getStatusCode, Function.identity(), (f,s) -> f ) );

   /**
    * The sub-folder of the project's &quot;OSEE-INF&quot; folder that user support files are contained.
    */

   private static final String userSupportFolder = "user-support";

   /**
    * Gets the {@link CatErrorCode} enumeration member with the specified <code>statusCode</code>.
    * 
    * @param statusCode the status code of the {@link CatErrorCode} to get.
    * @return {@link CatErrorCode} associated with the <code>statusCode</code>; otherwise, {@link CatErrorCode#InternalError}.
    */

   static CatErrorCode getByStatusCode(int statusCode) {
      CatErrorCode catErrorCode = CatErrorCode.statusCodeMap.get(statusCode);
      return Objects.nonNull(catErrorCode) ? catErrorCode : InternalError;
   }

   /**
    * This method is invoked by the Plug-In activator to verify the {@link CatErrorCode} enumeration members as follows:
    * <ul>
    * <li>Verifies the status code is unique.</li>
    * <li>Verifies a title string is set.</li>
    * <li>Verifies the user support file can be accessed.</li>
    */
   
   public static void verifyStatusCodes() {

      Map<Integer, CatErrorCode> map = new HashMap<>();
      LinkedList<IStatus> statusList = new LinkedList<>();
      
      for( CatErrorCode catErrorCode : CatErrorCode.values() ) {
         
         Integer statusCode = catErrorCode.getStatusCode();
         CatErrorCode otherCatErrorCode = map.put(statusCode, catErrorCode);
         
         if( Objects.nonNull( otherCatErrorCode) ) {
            //@formatter:off
            CatPluginStatus duplicateErrorCodeStatus =
                  new CatPluginStatus
                         (
                            CatErrorCode.InvalidCatErrorCode,
                              "CatErrorCode enumeration members have the same status code."              + "\n"
                            + "   Status Code:   " + Integer.toHexString( catErrorCode.getStatusCode() ) + "\n"
                            + "   First Member:  " + otherCatErrorCode.name()                            + "\n"
                            + "   Second Member: " + catErrorCode.name()                                 + "\n"
                         );
            //@formatter:on
            statusList.add(duplicateErrorCodeStatus);
         }

         if (Objects.isNull(catErrorCode.title) || catErrorCode.title.isEmpty()) {
            //@formatter:off
            CatPluginStatus userErrorMustHaveTitleStatus =
               new CatPluginStatus
                      (
                         CatErrorCode.InvalidCatErrorCode,
                           "CatErrorCode enumeration member for a user error does not have a title." + "\n"
                         + "   Member: " + catErrorCode.name() + "\n"
                      );
            //@formatter:on
            statusList.add(userErrorMustHaveTitleStatus);
         }

         if (catErrorCode.isUserError()) {

            if (Objects.isNull(catErrorCode.userSupportFile) || catErrorCode.userSupportFile.isEmpty()) {
               //@formatter:off
               CatPluginStatus userErrorMustHaveUrlStatus =
                  new CatPluginStatus
                         (
                            CatErrorCode.InvalidCatErrorCode,
                              "CatErrorCode enumeration member for a user error does not have a user support file." + "\n"
                            + "   Member: " + catErrorCode.name() + "\n"
                         );
               //@formatter:on
               statusList.add(userErrorMustHaveUrlStatus);
            } else {

               try {
                  final String pathTail = CatErrorCode.userSupportFolder + "/" + catErrorCode.userSupportFile;
                  final File file = OseeInf.getResourceAsFile(pathTail, CatErrorCode.class);
                  if (!file.canRead()) {
                     //@formatter:off
                     CatPluginStatus userSupportFileErrorStatus =
                        new CatPluginStatus
                               (
                                  CatErrorCode.InvalidCatErrorCode,
                                  "Cannot read the user support file for a CatErrorCode." + "\n"
                                  + "   Member: " + catErrorCode.name() + "\n"
                               );
                     //@formatter:on
                     statusList.add(userSupportFileErrorStatus);
                  }
               } catch (Exception e) {
                  //@formatter:off
                  CatPluginStatus userSupportFileErrorStatus =
                     new CatPluginStatus
                            (
                               CatErrorCode.InvalidCatErrorCode,
                                 "Cannot read the user support file for a CatErrorCode." + "\n"
                               + "   Member: " + catErrorCode.name() + "\n",
                               e
                            );
                  //@formatter:on
                  statusList.add(userSupportFileErrorStatus);
               }
            }
         }
      }

      if (statusList.isEmpty()) {
         return;
      }

      //@formatter:off
      CatPluginException duplicateStatusCodeException =
         new CatPluginException
                (
                   CatErrorCode.InvalidCatErrorCode,
                   "CatErrorCode Exception",
                   "Duplicate status codes were found for \"CatErrorCode\" enumeration memebers." + "\n",
                   statusList.toArray(new IStatus[statusList.size()])
                );
      //@formatter:on
      throw duplicateStatusCodeException;

   }

   /**
    * Saves {@link IStatus} error severity level.
    */

   private final int severity;

   /**
    * Save a unique integer identifier for the error.
    */

   private final int statusCode;

   /**
    * Saves the {@link StatusManager} error dialog box style. See {@link StatusManager#BLOCK},
    * {@link StatusManager#SHOW}, and {@link StatusManager#LOG}.
    */

   private final int style;

   /**
    * Save a title string used for all errors for the {@link CatErrorCode}.
    */

   private final String title;

   /**
    * Saves the {@link Type} of the error used to determine the {@link Control} for the user support area of
    * {@link StatusManager} error dialogs.
    */

   private final Type type;

   /**
    * Saves the file name portion for the user support file. User support files are contained in the
    * {@value CatErrorCode#userSupportFolder} sub-folder of the project's &quot;OSEE-INF&quot; folder.
    */

   private final String userSupportFile;

   /**
    * Creates a new {@link CatErrorCode} member.
    * 
    * @param type the {@link Type} of the error.
    * @param style the {@link StatusManager} error dialog style.
    * @param severity the {@link IStatus} error severity.
    * @param statusCode the low word (short) of the status code. Must be unique for all {@link CatErrorCode} members
    * with the same {@link #type}.
    * @param title a title string used for all errors of the {@link CatErrorCode} kind.
    * @param userSupportFile the file name portion of the user support file.
    */

   private CatErrorCode(Type type, int style, int severity, short typeStatusCode, String title, String userSupportFile) {
      this.type = type;
      this.style = style;
      this.severity = severity;
      this.statusCode = type.createStatusCode(typeStatusCode);
      this.title = title;
      this.userSupportFile = userSupportFile;
   }

   /**
    * For {@link CatErrorCode.Type#UserError} errors, gets the URL of the error support file.
    * 
    * @return an {@link Optional} containing the {@link URL} of the error support file; otherwise, an empty
    * {@link Optional}.
    */

   Optional<URL> getErrorSupportUrl() {
      try {
         final String pathTail = CatErrorCode.userSupportFolder + "/" + this.userSupportFile;
         final URL url = OseeInf.getResourceAsUrl(pathTail, this.getClass());
         return Optional.ofNullable(url);
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Gets the {@link IStatus} severity of the error.
    * 
    * @return the error severity.
    */

   int getSeverity() {
      return this.severity;
   }

   /**
    * Get the unique status code for the error.
    * 
    * @return the error status code.
    */

   int getStatusCode() {
      return this.statusCode;
   }

   /**
    * Gets the {@link StatusManager} error dialog style to use for the error.
    * 
    * @return the error dialog style.
    */

   int getStyle() {
      return this.style;
   }

   /**
    * Gets a title string to be used at the start of the error message for errors of the {@link CatErrorCode} kind.
    * 
    * @return the title string.
    */

   String getTitle() {
      return this.title;
   }

   /**
    * Gets the {@link CatErrorCode.Type} of the error.
    * 
    * @return the error type.
    */

   Type getType() {
      return this.type;
   }

   /**
    * Predicate to determine if the error is a {@link CatErrorCode.Type#InternalError}.
    * 
    * @return <code>true</code> when the error is of the type {@link CatErrorCode.Type#InternalError}; otherwise,
    * <code>false</code>.
    */

   boolean isInternalError() {
      return this.getType().isInternalError();
   }

   /**
    * Predicate to determine if the error is a {@link CatErrorCode.Type#UserError}.
    * 
    * @return <code>true</code> when the error is of the type {@link CatErrorCode.Type#UserError}; otherwise,
    * <code>false</code>.
    */

   boolean isUserError() {
      return this.getType().isUserError();
   }

}
