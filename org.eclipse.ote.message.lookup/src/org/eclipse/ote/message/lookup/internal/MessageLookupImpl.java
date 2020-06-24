/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.message.lookup.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.message.lookup.MessageLookupOperator;
import org.eclipse.ote.message.lookup.MessageLookupProvider;
import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.osgi.framework.FrameworkUtil;


/**
 * Uses an in-memory SQL database to store message information
 * @author Michael P. Masterson
 */
public class MessageLookupImpl implements MessageLookup, MessageLookupOperator {

   private static Object createLock = new Object();

   private int biggestElement;
   public Connection connection;
   private static final String DB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
   private static final String DB = "jdbc:hsqldb:file:";
   private static final String DB_NAME = "OTE_MsgLookupDb";
   private static final String USER_NAME = "sa";
   private static final String USER_PW = "";

   private HashMap<String, Integer> messageTypeMap;
   private HashMap<String, Integer> suMap;
   private List<String> descriptiveProviderNames;
   private File dbTempDir;

   private final String DROP_TABLES = "DROP TABLE messages IF EXISTS; DROP TABLE elements IF EXISTS; DROP TABLE sources IF EXISTS; DROP TABLE destinations IF EXISTS; DROP TABLE types IF EXISTS; DROP TABLE sus IF EXISTS;";
   private final String DISABLE_LOGGING = "SET FILES LOG FALSE";

   private final String MESSAGES_INSERT = "INSERT INTO messages (class, messageName, typeId, messageId, byteSize, phase, rate, scheduled, providerId) VALUES(?,?,?,?,?,?,?,?,?);";
   private final String MESSAGES_ID_SELECT = "select msg.id from messages msg where msg.class = ?;";
   private final String MESSAGES_DELETE = "DELETE FROM messages where providerId = ?;";
   private final String ELEMENTS_INSERT = "INSERT INTO elements (id, elementName, providerId) VALUES(?,?,?);";
   private final String ELEMENTS_DELETE = "DELETE FROM elements where providerId = ?;";
   private final String SOURCES_INSERT = "INSERT INTO sources (messageId, suId, providerId) VALUES(?,?,?);";
   private final String SOURCES_DELETE = "DELETE FROM sources where providerId = ?;";
   private final String DESTINATIONS_INSERT = "INSERT INTO destinations (messageId, suId, providerId) VALUES(?,?,?);";
   private final String DESTINATIONS_DELETE = "DELETE FROM destinations where providerId = ?;";
   private final String INSERT_MESSAGE_TYPE = "INSERT INTO types (messageType) VALUES(?);";
   private final String INSERT_SU_TYPE = "INSERT INTO sus (suName) VALUES(?);";

   private final String TYPE_SEARCH = "SELECT * from types where messageType = ?;";
   private final String SU_SEARCH = "SELECT * from sus where suName = ?;";

   private final String MESSAGE_SEARCH = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId from messages msg1, types type1 where msg1.messagename like ? and  msg1.typeid = type1.typeid;";
   private final String MESSAGE_SEARCH_ID = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId from messages msg1, types type1 where msg1.messageId = ? and msg1.typeid = type1.typeid;";
   private final String MESSAGE_SEARCH_TYPE = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId from messages msg1, types type1 where msg1.messagename like ? and msg1.typeid = type1.typeid and messageType in (%s);";
   private final String MESSAGE_SEARCH_CLASS = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId, msg1.byteSize, msg1.phase, msg1.rate, msg1.scheduled from messages msg1, types type1 where msg1.class = ? and msg1.typeid = type1.typeid;";

   private final String ELEMENT_SEARCH = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId, el1.elementName from elements el1, messages msg1, types type1 where el1.id = msg1.id and type1.typeId = msg1.typeId and el1.elementname like ?;";
   private final String ELEMENT_SEARCH_TYPE = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId, el1.elementName from elements el1, messages msg1, types type1 where el1.id = msg1.id and type1.typeId = msg1.typeId and el1.elementname like ? and type1.messageType in (%s);";

   private final String MESSAGE_SEARCH_ELEMENT_JOIN = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId, el1.elementName "
                                                    + "from elements el1, messages msg1, types type1 "
                                                    + "where el1.id = msg1.id and type1.typeId = msg1.typeId and msg1.messageName LIKE ?;";

   private final String MESSAGE_SEARCH_ELEMENT_JOIN_TYPE = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId,  el1.elementName "
                                                    + "from elements el1, messages msg1, types type1 "
                                                    + "where el1.id = msg1.id and type1.typeId = msg1.typeId and msg1.messageName LIKE ? and type1.messageType in (%s);";
   
   private final String ALL_MESSAGE_SEARCH = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId from messages msg1, types type1 where type1.typeId = msg1.typeId";
   private final String ALL_MESSAGE_SEARCH_TYPES = "select msg1.class, msg1.messageName, type1.messageType, msg1.messageId from messages msg1, types type1 where type1.typeId = msg1.typeId and type1.messageType in (%s)";

   private final String SOURCES_FOR_MSG_SEARCH = "select su1.suName from sus su1, sources src1 where src1.messageId = ? and su1.suId = src1.suId";
   private final String DESTINATIONS_FOR_MSG_SEARCH = "select su1.suName from sus su1, destinations dest1 where dest1.messageId = ? and su1.suId = dest1.suId";
   private final String MSGS_FOR_SOURCES_SEARCH = "select src1.messageId from sus su1, sources src1 where su1.suName like ? and su1.suId = src1.suId";
   private final String MSGS_FOR_DESTINATIONS_SEARCH = "select dest1.messageId from sus su1, destinations dest1 where su1.suName like ? and su1.suId = dest1.suId";

   private PreparedStatement messageInsert;
   private PreparedStatement messageIdSelect;
   private PreparedStatement messageDelete;
   private PreparedStatement elementInsert;
   private PreparedStatement elementDelete;
   private PreparedStatement sourceInsert;
   private PreparedStatement sourceDelete;
   private PreparedStatement destinationInsert;
   private PreparedStatement destinationDelete;
   private PreparedStatement messageTypeInsert;
   private PreparedStatement suInsert;

   private PreparedStatement typeSearch;
   private PreparedStatement suSearch;
   private PreparedStatement messageSearch;
   private PreparedStatement messageClassSearch;
   private PreparedStatement messageIdSearch;
   private PreparedStatement elementSearch;
   private PreparedStatement messageElementJoin;

   private PreparedStatement allMessageNoElements;

   private HashMap<Integer, PreparedStatement> elementSearchTypeMap;
   private HashMap<Integer, PreparedStatement> messageSearchTypeMap;
   private HashMap<Integer, PreparedStatement> messageElementTypeMap;

   private boolean readyToAddEntries = false;
   private Object addLock = new Object();
   private List<MessageLookupProvider> messageLookupProviders = new ArrayList<MessageLookupProvider>();

   private ExecutorService startupAndLookupThread;
   private Future<?> initialization;
   private HashMap<Integer, PreparedStatement> allMessageSearchTypeMap;

   private PreparedStatement sourcesSearch;

   private PreparedStatement destinationsSearch;

   private PreparedStatement msgsForDestinationSearch;
   private PreparedStatement msgsForSourceSearch;

   public MessageLookupImpl() {
      messageTypeMap = new HashMap<String, Integer>();
      suMap = new HashMap<String, Integer>();
      descriptiveProviderNames = new CopyOnWriteArrayList<String>();
      createStartupAndLookupThread();
   }

   public void start() {
      createStartupAndLookupThread();
      initialization = startupAndLookupThread.submit(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     initialize();
                  } catch (IOException e) {
                     OseeLog.log(getClass(), Level.SEVERE, "Failure during start of MessageLookupComponent", e);
                  } catch (ClassNotFoundException e) {
                     OseeLog.log(getClass(), Level.SEVERE, "Failure during start of MessageLookupComponent", e);
                  } catch (SQLException e) {
                     OseeLog.log(getClass(), Level.SEVERE, "Failure during start of MessageLookupComponent", e);
                  }
               }
            });
   }

   public synchronized void stop() {
      if(startupAndLookupThread != null) {
         startupAndLookupThread.shutdownNow();
         try {
            if(!startupAndLookupThread.awaitTermination(10, TimeUnit.SECONDS)) {
               OseeLog.log(getClass(), Level.WARNING, "Message startupAndLookupThread still running");
            }
         } catch (InterruptedException e) {
            OseeLog.log(getClass(), Level.SEVERE, "Failure during stop of MessageLookupComponent", e);
         }
      }
      
      if (connection != null) {
         try {
            Statement stmt = connection.createStatement();
            stmt.execute("SHUTDOWN IMMEDIATELY");
         } catch (SQLException e) {
            OseeLog.log(getClass(), Level.SEVERE, "Failure during stop of MessageLookupComponent", e);
         }
         connection = null;
      }
      
      if (dbTempDir != null) {
         Lib.deleteDir(dbTempDir);
      }
   }

   public void addMessageLookupProvider(final MessageLookupProvider messageLookupProvider) {
      startupAndLookupThread.submit(new Runnable() {

         public void run() {
            synchronized (addLock) {
               if (readyToAddEntries) {
                  String name = messageLookupProvider.getDescriptiveProviderName();
                  if (name != null && name.length() > 0 && !descriptiveProviderNames.contains(name)) {
                     descriptiveProviderNames.add(messageLookupProvider.getDescriptiveProviderName());
                     Collections.sort(descriptiveProviderNames);
                  }
                  messageLookupProvider.addToDb(MessageLookupImpl.this);
               } else {
                  messageLookupProviders.add(messageLookupProvider);
               }
            }
         }
      });
   }

   public void removeMessageLookupProvider(final MessageLookupProvider messageLookupProvider) {
      startupAndLookupThread.submit(new Runnable() {

         public void run() {
            synchronized (addLock) {
               if (readyToAddEntries) {
                  messageLookupProvider.removeFromDb(MessageLookupImpl.this);
               }
            }
         }
      });
   }

   void initialize() throws IOException, ClassNotFoundException, SQLException {
      synchronized (createLock) {
         createDb();
         messageInsert = connection.prepareStatement(MESSAGES_INSERT);
         messageIdSelect = connection.prepareStatement(MESSAGES_ID_SELECT);
         messageDelete = connection.prepareStatement(MESSAGES_DELETE);
         elementInsert = connection.prepareStatement(ELEMENTS_INSERT);
         elementDelete = connection.prepareStatement(ELEMENTS_DELETE);
         sourceInsert = connection.prepareStatement(SOURCES_INSERT);
         sourceDelete = connection.prepareStatement(SOURCES_DELETE);
         destinationInsert = connection.prepareStatement(DESTINATIONS_INSERT);
         destinationDelete = connection.prepareStatement(DESTINATIONS_DELETE);
         messageTypeInsert = connection.prepareStatement(INSERT_MESSAGE_TYPE);
         suInsert = connection.prepareStatement(INSERT_SU_TYPE);
         messageSearch = connection.prepareStatement(MESSAGE_SEARCH);
         messageClassSearch = connection.prepareStatement(MESSAGE_SEARCH_CLASS);
         messageIdSearch = connection.prepareStatement(MESSAGE_SEARCH_ID);
         elementSearch = connection.prepareStatement(ELEMENT_SEARCH);
         elementSearchTypeMap = new HashMap<Integer, PreparedStatement>();
         messageSearchTypeMap = new HashMap<Integer, PreparedStatement>();
         messageElementTypeMap = new HashMap<Integer, PreparedStatement>();
         allMessageSearchTypeMap = new HashMap<Integer, PreparedStatement>();
         messageElementJoin = connection.prepareStatement(MESSAGE_SEARCH_ELEMENT_JOIN);

         typeSearch = connection.prepareStatement(TYPE_SEARCH);
         suSearch = connection.prepareStatement(SU_SEARCH);
         allMessageNoElements = connection.prepareStatement(ALL_MESSAGE_SEARCH);
         sourcesSearch = connection.prepareStatement(SOURCES_FOR_MSG_SEARCH);
         destinationsSearch = connection.prepareStatement(DESTINATIONS_FOR_MSG_SEARCH);
         msgsForDestinationSearch = connection.prepareStatement(MSGS_FOR_DESTINATIONS_SEARCH);
         msgsForSourceSearch = connection.prepareStatement(MSGS_FOR_SOURCES_SEARCH);
      }
      synchronized (addLock) {
         readyToAddEntries = true;
         for (MessageLookupProvider provider : messageLookupProviders) {
            provider.addToDb(this);
         }
         messageLookupProviders.clear();
      }
   }

   @Override
   public MessageLookupResult lookupClass(String className) {
      className = className.trim();
      waitForInit();
      MessageLookupResult result = null;
      try {
         messageClassSearch.setString(1, className);
         ResultSet classResults = messageClassSearch.executeQuery();
         if (classResults.next()) {
            result = new MessageLookupResult(classResults.getString(1), classResults.getString(2), classResults.getString(3), classResults.getInt(4), classResults.getInt(5), classResults.getString(6), classResults.getString(7), classResults.getString(8));
            addPubsSubs(result);
         }
         if (classResults.next()) {
            OseeLog.log(getClass(), Level.INFO, new Exception("Found more than one message for classname: " + className));
         }
         classResults.close();
      } catch (SQLException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
      return result;
   }

   public void addPubsSubs(MessageLookupResult result) throws SQLException {
      sourcesSearch.setInt(1, result.getMessageId());
      ResultSet pubResults = sourcesSearch.executeQuery();
      while (pubResults.next()) {
         result.addSource(pubResults.getString(1));
      }
      pubResults.close();
      destinationsSearch.setInt(1, result.getMessageId());
      ResultSet subResults = destinationsSearch.executeQuery();
      while (subResults.next()) {
         result.addDestination(subResults.getString(1));
      }
      subResults.close();
   }

   @Override
   public List<MessageLookupResult> lookup(String searchString, String... types) {
      waitForInit();
      @SuppressWarnings("unused")
      long time = System.currentTimeMillis();
      Map<String, MessageLookupResult> uniqueResults = new HashMap<String, MessageLookupResult>();
      try {
         if (!doSpecializedSearches(uniqueResults, searchString, types)) {
            String wildcardSearch = wildcardSearchString(searchString);
            // first check elements
            PreparedStatement elementSearchType = elementSearchTypeMap.get(types.length);
            if (elementSearchType == null) {
               elementSearchType = connection.prepareStatement(String.format(ELEMENT_SEARCH_TYPE, getSqlArgs(types.length)));
               elementSearchTypeMap.put(types.length, elementSearchType);
            }
            elementSearchType.setString(1, wildcardSearch);
            int index = 2;
            for (String type : types) {
               elementSearchType.setString(index, type);
               index++;
            }

            ResultSet results = elementSearchType.executeQuery();
            processMessageElementResultSet(results, uniqueResults);
            results.close();

            PreparedStatement messageSearchType = messageSearchTypeMap.get(types.length);
            if (messageSearchType == null) {
               messageSearchType = connection.prepareStatement(String.format(MESSAGE_SEARCH_TYPE, getSqlArgs(types.length)));
               messageSearchTypeMap.put(types.length, messageSearchType);
            }
            messageSearchType.setString(1, wildcardSearch);
            index = 2;
            for (String type : types) {
               messageSearchType.setString(index, type);
               index++;
            }

            results = messageSearchType.executeQuery();
            while (results.next()) {
               String clazz = results.getString(1);
               MessageLookupResult messageLookupResult = uniqueResults.get(clazz);
               if (messageLookupResult == null) {
                  messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
                  uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
               }
            }
            results.close();

            PreparedStatement messageElementJoinUpdated = messageElementTypeMap.get(types.length);
            if (messageElementJoinUpdated == null) {
               String newQuery = String.format(MESSAGE_SEARCH_ELEMENT_JOIN_TYPE, getSqlArgs(types.length));
               messageElementJoinUpdated = connection.prepareStatement(newQuery);
               messageElementTypeMap.put(types.length, messageElementJoinUpdated);
            }
            messageElementJoinUpdated.setString(1, wildcardSearch);
            index = 2;
            for (String type : types) {
               messageElementJoinUpdated.setString(index, type);
               index++;
            }
            
            results = messageElementJoinUpdated.executeQuery();
            while (results.next()) {
               String clazz = results.getString(1);
               MessageLookupResult messageLookupResult = uniqueResults.get(clazz);
               if (messageLookupResult == null) {
                  messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
                  uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
               }
               messageLookupResult.addElement(results.getString(5));
            }
            results.close();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      List<MessageLookupResult> toReturn = new ArrayList<MessageLookupResult>();
      toReturn.addAll(uniqueResults.values());
      Collections.sort(toReturn, new Comparator<MessageLookupResult>() {
         @Override
         public int compare(MessageLookupResult arg0, MessageLookupResult arg1) {
            return arg0.getMessageName().compareTo(arg1.getMessageName());
         }
      });
      return toReturn;
   }

   private boolean doSpecializedSearches(Map<String, MessageLookupResult> uniqueResults, String searchString, String... types) throws SQLException {
      try {
         int id = Integer.parseInt(searchString);
         lookupId(uniqueResults, id);
         return true;
      } catch (NumberFormatException ex) { // if it's not an int do the normal
                                           // thing
      }
      if (searchString.equals("*") || searchString.equals(".*")) {
         ResultSet results = null;
         try {
            PreparedStatement allMessageSearchType = allMessageSearchTypeMap.get(types.length);
            if (allMessageSearchType == null) {
               allMessageSearchType = connection.prepareStatement(String.format(ALL_MESSAGE_SEARCH_TYPES, getSqlArgs(types.length)));
               allMessageSearchTypeMap.put(types.length, allMessageSearchType);
            }
            int index = 1;
            for (String type : types) {
               allMessageSearchType.setString(index, type);
               index++;
            }
            results = allMessageSearchType.executeQuery();
            while (results.next()) {
               MessageLookupResult messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
               uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
            }
         } catch (SQLException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         } finally {
            try {
               if (results != null) {
                  results.close();
               }
            } catch (SQLException e) {
               OseeLog.log(getClass(), Level.SEVERE, e);
            }
         }
         return true;
      }
      return false;
   }

   private Object getSqlArgs(int length) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < length; i++) {
         sb.append("?,");
      }
      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
   }

   private void lookupId(Map<String, MessageLookupResult> uniqueResults, int id) throws SQLException {
      messageIdSearch.setInt(1, id);
      ResultSet results = messageIdSearch.executeQuery();
      while (results.next()) {
         MessageLookupResult messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
         uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
      }
      results.close();
   }

   @Override
   public List<MessageLookupResult> lookup(String searchString) {
      waitForInit();
      searchString = searchString.trim().toUpperCase();
      @SuppressWarnings("unused")
      long time = System.currentTimeMillis();
      Map<String, MessageLookupResult> uniqueResults = new HashMap<String, MessageLookupResult>();
      try {
         if (!doSpecializedSearches(uniqueResults, searchString)) {
            String wildcardSearch = wildcardSearchString(searchString);
            // first check elements
            elementSearch.setString(1, wildcardSearch);
            ResultSet results = elementSearch.executeQuery();
            processMessageElementResultSet(results, uniqueResults);
            results.close();

            // check messages, if there is a new match (not an element match)
            // then get all elements too
            messageSearch.setString(1, wildcardSearch);
            results = messageSearch.executeQuery();
            while (results.next()) {
               String clazz = results.getString(1);
               MessageLookupResult messageLookupResult = uniqueResults.get(clazz);
               if (messageLookupResult == null) {
                  messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
                  uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
               }
            }
            results.close();

            messageElementJoin.setString(1, wildcardSearch);
            results = messageElementJoin.executeQuery();
            while (results.next()) {
               String clazz = results.getString(1);
               MessageLookupResult messageLookupResult = uniqueResults.get(clazz);
               if (messageLookupResult == null) {
                  messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
                  uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
               }
               messageLookupResult.addElement(results.getString(5));
            }
            results.close();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }

      List<MessageLookupResult> toReturn = new ArrayList<MessageLookupResult>();
      toReturn.addAll(uniqueResults.values());
      Collections.sort(toReturn, new Comparator<MessageLookupResult>() {
         @Override
         public int compare(MessageLookupResult arg0, MessageLookupResult arg1) {
            return arg0.getMessageName().compareTo(arg1.getMessageName());
         }
      });
      return toReturn;
   }

   private boolean doSpecializedSearches(Map<String, MessageLookupResult> uniqueResults, String searchString) throws SQLException {
      try {
         int id = Integer.parseInt(searchString);
         lookupId(uniqueResults, id);
         return true;
      } catch (NumberFormatException ex) { // if it's not an int do the normal
                                           // thing
      }
      if (searchString.equals("*") || searchString.equals(".*")) {
         getAllMessages(uniqueResults);
         return true;
      }
      if (searchString.startsWith("SUB:")) {
         lookupMessagesSingleInputQuery(uniqueResults, msgsForDestinationSearch, searchString.substring(4));
         return true;
      }
      if (searchString.startsWith("PUB:")) {
         lookupMessagesSingleInputQuery(uniqueResults, msgsForSourceSearch, searchString.substring(4));
         return true;
      }
      return false;
   }

   private void lookupMessagesSingleInputQuery(Map<String, MessageLookupResult> uniqueResults, PreparedStatement statement, String expression) {
      waitForInit();
      expression = expression.trim();
      try {
         expression = wildcardSearchString(expression);
         statement.setString(1, expression);
         ResultSet queryResultSet = statement.executeQuery();
         while (queryResultSet.next()) {
            lookupId(uniqueResults, queryResultSet.getInt(1));
         }
         queryResultSet.close();
      } catch (SQLException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   private void waitForInit() {
      if (initialization != null && !initialization.isDone()) {
         try {
            initialization.get(20, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
            OseeLog.log(getClass(), Level.SEVERE, "Failed waiting for init", e);
         } catch (ExecutionException e) {
            OseeLog.log(getClass(), Level.SEVERE, "Failed waiting for init", e);
         } catch (TimeoutException e) {
            OseeLog.log(getClass(), Level.SEVERE, "Failed waiting for init", e);
         }
      }
   }

   private void processMessageElementResultSet(ResultSet results, Map<String, MessageLookupResult> uniqueResults) throws SQLException {
      while (results.next()) {
         String clazz = results.getString(1);
         MessageLookupResult messageLookupResult = uniqueResults.get(clazz);
         if (messageLookupResult == null) {
            messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
            uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
         }
         messageLookupResult.addElement(results.getString(5));
      }
   }

   private String wildcardSearchString(final String searchString) {
      String returnMe = searchString.replaceAll("\\.\\*", "%");
      returnMe = returnMe.replaceAll("\\*", "%");
      return returnMe;
   }

   @Override
   public synchronized void removeFromLookup(int uniqueProviderId) {
      try {
         if (connection == null || connection.isClosed()) {
            return;
         }
         messageDelete.setInt(1, uniqueProviderId);
         messageDelete.executeUpdate();

         elementDelete.setInt(1, uniqueProviderId);
         elementDelete.executeUpdate();

         sourceDelete.setInt(1, uniqueProviderId);
         sourceDelete.executeUpdate();

         destinationDelete.setInt(1, uniqueProviderId);
         destinationDelete.executeUpdate();
      } catch (SQLException ex) {
         OseeLog.log(getClass(), Level.SEVERE, "Failed to remove rows from provider " + uniqueProviderId, ex);
      }
   }

   @Override
   public void addToLookup(int uniqueProviderId, String messageClass, String messageName, String messageType, int messageId, int byteSize, String phase, String rate, String scheduled, List<String> elements) {
      addToLookup(uniqueProviderId, messageClass, messageName, messageType, messageId, byteSize, phase, rate, scheduled, elements, null, null);
   }

   @Override
   public synchronized void addToLookup(int uniqueProviderId, String messageClass, String messageName, String messageType, int messageId, int byteSize, String phase, String rate, String scheduled, List<String> elements, List<String> sources, List<String> destinations) {
      try {
         if (connection == null || connection.isClosed()) {
            return;
         }
         int id = getTypeId(messageType);

         messageInsert.setString(1, messageClass);
         messageInsert.setString(2, messageName);
         messageInsert.setInt(3, id);
         messageInsert.setInt(4, messageId);
         messageInsert.setInt(5, byteSize);
         messageInsert.setString(6, phase);
         messageInsert.setString(7, rate);
         messageInsert.setString(8, scheduled);
         messageInsert.setInt(9, uniqueProviderId);
         messageInsert.executeUpdate();

         messageIdSelect.setString(1, messageClass);
         ResultSet results = messageIdSelect.executeQuery();
         int autoGenId = 0;
         if (results.next()) {
            autoGenId = results.getInt(1);
         } else {
            OseeLog.log(getClass(), Level.SEVERE, String.format("Failed to insert %", messageClass));
            return;
         }

         if (elements != null) {
            for (String element : elements) {
               if (element.length() > biggestElement) {
                  biggestElement = element.length();
               }
               elementInsert.setInt(1, autoGenId);
               elementInsert.setString(2, element);
               elementInsert.setInt(3, uniqueProviderId);
               int count = elementInsert.executeUpdate();
               if (count < 1) {
                  System.out.println("failed to insert");
               }
            }
         }

         if (sources != null) {
            for (String su : sources) {
               if (su.length() > 0) {
                  int suId = getSuId(su);
                  sourceInsert.setInt(1, messageId);
                  sourceInsert.setInt(2, suId);
                  sourceInsert.setInt(3, uniqueProviderId);
                  sourceInsert.executeUpdate();
               }
            }
         }

         if (destinations != null) {
            for (String su : destinations) {
               if (su.length() > 0) {
                  int suId = getSuId(su);
                  destinationInsert.setInt(1, messageId);
                  destinationInsert.setInt(2, suId);
                  destinationInsert.setInt(3, uniqueProviderId);
                  destinationInsert.executeUpdate();
               }
            }
         }
      } catch (SQLException ex) {
         OseeLog.log(getClass(), Level.SEVERE, "Unable to add entry to Message DB", ex);
      }
   }

   private int getSuId(String su) throws SQLException {
      Integer id = suMap.get(su);
      if (id == null) {
         suInsert.setString(1, su);
         suInsert.executeUpdate();

         suSearch.setString(1, su);
         ResultSet results = suSearch.executeQuery();
         while (results.next()) {
            int generatedId = results.getInt(1);
            id = generatedId;
            suMap.put(su, generatedId);
         }
         results.close();
      }
      return id;
   }

   private int getTypeId(String messageType) throws SQLException {
      Integer id = messageTypeMap.get(messageType);
      if (id == null) {
         messageTypeInsert.setString(1, messageType);
         messageTypeInsert.executeUpdate();

         typeSearch.setString(1, messageType);
         ResultSet results = typeSearch.executeQuery();
         while (results.next()) {
            int generatedId = results.getInt(1);
            id = generatedId;
            synchronized (this) {
               messageTypeMap.put(messageType, generatedId);
            }
         }
         results.close();
      }
      return id;
   }

   private void createDb() throws IOException, ClassNotFoundException, SQLException {
      BufferedReader createFile = null;
      String createSql = new String();
      Statement statement = null;
      try {
         URL url = FrameworkUtil.getBundle(MessageLookupImpl.class).getEntry("database.sql/createDB.sql");
         createFile = new BufferedReader(new InputStreamReader(url.openStream()));

         String line = null;

         StringBuilder stringBuilder = new StringBuilder();

         while ((line = createFile.readLine()) != null) {
            stringBuilder.append(line);
         }

         createSql = stringBuilder.toString();

         Class.forName(DB_DRIVER);
         Path tempDirectoryPath = Files.createTempDirectory(DB_NAME + "_");
         dbTempDir = tempDirectoryPath.toFile();
         Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
               MessageLookupImpl.this.stop();
            }
         });

         String connUrl = DB + tempDirectoryPath.toString() + "/" + DB_NAME;
         connection = DriverManager.getConnection(connUrl, USER_NAME, USER_PW);

         statement = connection.createStatement();
         statement.executeUpdate(DISABLE_LOGGING);
         statement.close();

         statement = connection.createStatement();
         statement.executeUpdate(DROP_TABLES);
         statement.close();

         statement = connection.createStatement();
         statement.executeUpdate(createSql);

         createFile.close();
         statement.close();

      } finally {
         if (statement != null) {
            try {
               statement.close();
            } catch (SQLException e) {
            }
         }
         if (createFile != null) {
            try {
               createFile.close();
            } catch (IOException e) {
            }
         }
      }
   }

   @Override
   public List<String> getAvailableMessageTypes() {
      List<String> availableMessageTypes = new ArrayList<String>();
      synchronized (this) {
         availableMessageTypes.addAll(messageTypeMap.keySet());
      }
      return availableMessageTypes;
   }

   @Override
   public List<String> getMessageSources() {
      return descriptiveProviderNames;
   }

   private void getAllMessages(Map<String, MessageLookupResult> uniqueResults) {
      ResultSet results = null;
      try {
         results = allMessageNoElements.executeQuery();
         while (results.next()) {
            MessageLookupResult messageLookupResult = new MessageLookupResult(results.getString(1), results.getString(2), results.getString(3), results.getInt(4));
            uniqueResults.put(messageLookupResult.getClassName(), messageLookupResult);
         }
      } catch (SQLException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } finally {
         try {
            results.close();
         } catch (SQLException e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
      }
   }

   private void createStartupAndLookupThread() {
      if (startupAndLookupThread == null || startupAndLookupThread.isShutdown()) {
         startupAndLookupThread = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable arg0) {
               Thread th = new Thread(arg0);
               th.setName("MessageDBLookup");
               return th;
            }
         });
      }
   }

}
