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

package org.eclipse.ote.message.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;

/**
 * Keeps both the collection of readers and writers but also handles the periodic publish tasks for each periodic
 * message writer. This class will create a task for each requested rate and add all messages to that task that matches
 * the rate. It will also switch tasks for a message when the rate for that message is changed.
 *
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 * @param <U> The specific Message type to be created when getting message instances
 */
@SuppressWarnings("rawtypes")
public class MessageCollection<U extends Message> implements IMessageScheduleChangeListener {

   private final DoubleKeyHashMap<Namespace, Class, U> messageReaders;
   private final ConcurrentHashMap<TopicDescription, MessageData> messageDataReaders;
   private final DoubleKeyHashMap<Namespace, Class, U> messageWriters;
   private volatile boolean isDestroyed = false;
   private WeakReference<TestEnvironmentInterface> testEnv;
   private final PeriodicPublishMap periodicPublicationTasks;

   private final ConsoleCommandTracker cmdTracker;
   private final List<MessageWriterSetupHandler> messageSetupHandlers;
   private final List<MessageRemoveHandler> messageRemoveHandlers;
   private NamespaceMapper namespaceMapper;
   private IMessageManager manager;

   public MessageCollection() {
      super();
      GCHelper.getGCHelper().addRefWatch(this);

      periodicPublicationTasks = new PeriodicPublishMap();
      messageSetupHandlers = new CopyOnWriteArrayList<MessageWriterSetupHandler>();
      messageRemoveHandlers = new CopyOnWriteArrayList<MessageRemoveHandler>();
      messageReaders = new DoubleKeyHashMap<Namespace, Class, U>();
      messageDataReaders = new ConcurrentHashMap<TopicDescription, MessageData>();
      messageWriters = new DoubleKeyHashMap<Namespace, Class, U>();

      cmdTracker = new ConsoleCommandTracker(new MessageCollectionConsole());
      cmdTracker.open(true);
   }

   public void init(TestEnvironmentInterface testEnv, NamespaceMapper nameSpaceMapper) {
      this.testEnv = new WeakReference<TestEnvironmentInterface>(testEnv);
      this.namespaceMapper = nameSpaceMapper;
   }

   public void initMessageWriters() {
      checkState();
      for (Message msg : messageWriters.allValues()) {
         setupMessageWriter(msg);
      }
   }

   /**
    * This method returns an instance of the specified class. It will return null if an instance does not already exist.
    * If a message exists it also adds the message to the list of messages to be zeroized after a script completes.
    */
   public U get(Class<? extends U> clazz, Namespace namespace, boolean writer) {
      checkState();
      final U o;
      if (writer) {
         synchronized (messageWriters) {
            o = messageWriters.get(namespace, clazz);
         }
      } else {
         synchronized (messageReaders) {
            o = messageReaders.get(namespace, clazz);
         }
      }
      return o;
   }

   public U hasInstance(Class<? extends U> clazz, boolean writer) {
      checkState();
      if (writer) {
         synchronized (messageWriters) {
            for (Map<Class, U> map : messageWriters.getInnerMaps()) {
               U msg = map.get(clazz);
               if (msg != null) {
                  return msg;
               }
            }
            return null;
         }
      } else {
         synchronized (messageReaders) {
            for (Map<Class, U> map : messageReaders.getInnerMaps()) {
               U msg = map.get(clazz);
               if (msg != null) {
                  return msg;
               }
            }
            return null;
         }
      }
   }

   public void add(Class<? extends U> clazz, Namespace namespace, boolean writer, U message) {
      checkState();
      if (namespace == null) {
         throw new IllegalArgumentException("the argument 'namespace' can not be null.");
      }
      if (clazz == null) {
         throw new IllegalArgumentException("the argument 'clazz' can not be null.");
      }
      if (message == null) {
         throw new IllegalArgumentException("the argument 'message' can not be null.");
      }

      if (writer) {
         synchronized (messageWriters) {
            if (messageWriters.get(namespace, clazz) == null) {
               messageWriters.put(namespace, clazz, message);
            } else {
               throw new TestException(String.format("Message [%s] exists more than once in namespace[%s]",
                  clazz.getName(), namespace.toString()), Level.SEVERE);
            }
         }
      } else {
         synchronized (messageReaders) {
            if (messageReaders.get(namespace, clazz) == null) {
               messageReaders.put(namespace, clazz, message);
            } else {
               throw new TestException(String.format("Message [%s] exists more than once in namespace[%s]",
                  clazz.getName(), namespace.toString()), Level.SEVERE);
            }
            addMessageDataReader(message);
         }
      }
   }

   public List<Message> getList(Class<? extends Message> clazz, boolean writer) {
      checkState();
      List<Message> o = new ArrayList<Message>();
      if (writer) {
         synchronized (messageWriters) {
            Set<Namespace> namespaces = messageWriters.getKeySetOne();
            for (Namespace namespace : namespaces) {
               Message msg = messageWriters.get(namespace, clazz);
               if (msg != null) {
                  o.add(msg);
               }
            }
         }
      } else {
         synchronized (messageReaders) {
            Set<Namespace> namespaces = messageReaders.getKeySetOne();
            for (Namespace namespace : namespaces) {
               Message msg = messageReaders.get(namespace, clazz);
               if (msg != null) {
                  o.add(msg);
               }
            }
         }

      }
      return o;
   }

   public <CLASSTYPE extends Message> Message get(Class<CLASSTYPE> clazz, boolean writer) {
      checkState();
      List<? extends Message> o = getList(clazz, writer);
      if (o.size() == 1) {
         return o.get(0);
      } else if (o.size() > 1) {
         throw new TestException(String.format("Message [%s] exists in more than one namespace.", clazz.getName()),
            Level.SEVERE);
      }
      return null;
   }

   /**
    * Removes a message from a rate task.
    */
   private void removeMessageFromRateTask(Message message, double currentHzRate) {
      PeriodicPublishTask task = null;
      task = periodicPublicationTasks.get(currentHzRate, 0);
      if (task != null) {
         task.remove(message);
      }
   }

   /**
    * Adds a mesasge to a rate task. Adds the rate task if it doesn't exist already.
    */
   private void addMessageToRateTask(Message message, double newHzRate) {
      PeriodicPublishTask task = null;
      if (newHzRate == 0) {
         log(Level.SEVERE,
            "Trying to schedule a message at 0Hz. [" + message.getMessageName() + ", default Hz='" + message.getRate() + "']");
      } else {
         if (periodicPublicationTasks.containsKey(newHzRate, 0)) {
            task = periodicPublicationTasks.get(newHzRate, 0);
         } else {
            task = new PeriodicPublishTask(newHzRate, 0);
            testEnv.get().addTask(task);
            periodicPublicationTasks.put(newHzRate, message.getPhase(), task);
         }
         task.put(message);
      }
   }

   private void removeListeners() {
      for (Message message : messageReaders.allValues()) {
         message.clearRemovableListeners();
      }
      for (Message message : messageWriters.allValues()) {
         message.clearRemovableListeners();
      }
   }

   /**
    * This method iterates through the list of periodic publish tasks and adds them to the environment task scheduler.
    * This method must be called before a script begins to run so that all registered messages will be sent by the
    * environment at the appropriate time.
    */
   public void startPeriodicMessages() {
      checkState();
      TestEnvironmentInterface env = testEnv.get();
      for (PeriodicPublishTask task : periodicPublicationTasks.getTasks()) {
         log(Level.INFO, "adding task " + task.toString());
         env.addTask(task);
      }
   }

   /**
    * This method calls onInitListener() on all registered messages MessageSystemReadListener and
    * MessageSystemWriteListener. This method is called at the begining of each script run.
    */
   public void initMessageListeners() throws MessageSystemException {
      checkState();
      for (Message message : messageReaders.allValues()) {
         try {
            message.getListener().onInitListener();
         } catch (Exception e) {
            log(Level.SEVERE, "problems calling onInitListener() for " + message.getName(), e);
         }
      }
      for (Message message : messageWriters.allValues()) {
         try {
            message.getListener().onInitListener();
         } catch (Exception e) {
            log(Level.SEVERE, "problems calling onInitListener() for " + message.getName(), e);
         }
      }
   }

   public Collection<U> getAllMessages() {
      checkState();
      Collection<U> coll;
      synchronized (messageReaders) {
         coll = messageReaders.allValues();
      }
      synchronized (messageWriters) {
         coll.addAll(messageWriters.allValues());
      }

      return coll;
   }

   public Collection<U> getAllReaders() {
      checkState();
      synchronized (messageReaders) {
         return new ArrayList<U>(messageReaders.allValues());
      }
   }

   public Collection<U> getAllWriters() {
      checkState();
      synchronized (messageWriters) {
         return new ArrayList<U>(messageWriters.allValues());
      }
   }

   public Collection<U> getAllWriters(DataType type) {
      checkState();
      Namespace namespace = namespaceMapper.getNamespace(type);
      synchronized (messageWriters) {
         if (namespace == null) {
            OseeLog.log(MessageCollection.class, Level.FINEST, String.format("namespace for %s is null", type.name()));
         }
         Collection<U> currentList = messageWriters.get(namespace);
         if (currentList != null) {
            return new ArrayList<U>(currentList);
         } else {
            return new ArrayList<U>();
         }
      }
   }

   public Collection<U> getAllReaders(DataType type) {
      checkState();
      Namespace namespace = namespaceMapper.getNamespace(type);
      synchronized (messageReaders) {
         if (namespace == null) {
            OseeLog.log(MessageCollection.class, Level.FINEST, String.format("namespace for %s is null", type.name()));
         }
         Collection<U> currentList = messageReaders.get(namespace);
         if (currentList != null) {
            return new ArrayList<U>(currentList);
         } else {
            return new ArrayList<U>();
         }
      }
   }

   public void destroy() {
      cmdTracker.close();
      isDestroyed = true;
      log(Level.INFO, "destroy message collection");
      for (Message message : messageReaders.allValues()) {
         try {
            message.destroy();
         } catch (Exception e) {
            log(Level.SEVERE, "failed to destroy message " + message.getName(), e);
         }
      }
      for (Message message : messageWriters.allValues()) {
         try {
            message.destroy();
         } catch (Exception e) {
            log(Level.SEVERE, "failed to destroy message " + message.getName(), e);
         }
      }
      messageReaders.clear();
      messageWriters.clear();
      messageDataReaders.clear();

      periodicPublicationTasks.clear();
   }

   public <CLASSTYPE extends U> void onMessageCreated(Class<CLASSTYPE> messageClass, IMessageRequestor requestor, boolean writer, CLASSTYPE message, Namespace namespace) {
      if (requestor == null) {
         return;
      }
      checkState();
      message.addSchedulingChangeListener(this);
      if (writer) {
         synchronized (messageWriters) {
            if (messageWriters.get(namespace, messageClass) == null) {
               messageWriters.put(namespace, messageClass, message);
            } else {
               log(Level.WARNING, String.format(
                  "[%s] has already been added to the message collection, you have multiple instances in the environment.",
                  message.getName()));
            }
         }
      } else {
         messageReaders.put(namespace, messageClass, message);
         addMessageDataReader(message);
      }
   }

   private void addMessageDataReader(Message message) {
      MessageData messageData = message.getDefaultMessageData();
      TopicDescription topic = createTopicDescription(messageData);
      if (messageDataReaders.get(topic) == null) {
         if (topicShouldWrap(topic)) {
            messageDataReaders.put(topic, messageData);
         }
      } else {
         throw new TestException(String.format("MessageData [%s] exists more than once in namespace[%s]",
            messageData.getTopicName(), messageData.getNamespace().toString()), Level.SEVERE);
      }
   }

   /**
    * @param topic
    * @return true if this topic should copy the databuffer from the writer into the reader when sent
    */
   private boolean topicShouldWrap(TopicDescription topic) {
      boolean shouldWrap = true;
      for (MessageWriterSetupHandler handler : messageSetupHandlers) {
         shouldWrap = shouldWrap && handler.shouldWrap(topic);
      }

      return shouldWrap;
   }

   private TopicDescription createTopicDescription(MessageData messageData) {
      return new TopicDescriptionImpl(messageData.getTopicName(), messageData.getNamespace().toString());
   }

   @SuppressWarnings("deprecation")
   void setupMessageWriter(Message message) {
      for (MessageWriterSetupHandler handler : messageSetupHandlers) {
         handler.setup(message);
      }
      if (message.getRate() != 0.0) {
         addMessageToRateTask(message, message.getRate());
      } else if (message.isScheduled()) {
         log(Level.INFO, message.getMessageName() + " has attempted to be scheduled at 0 Hz!!!");
      }
   }

   @Override
   public void isScheduledChanged(boolean isScheduled) {
   }

   @Override
   public void onRateChanged(Message message, double oldRate, double newRate) {
      checkState();
      removeMessageFromRateTask(message, oldRate);
      addMessageToRateTask(message, newRate);
   }

   public String getMessageInformation() {
      checkState();
      StringBuilder sb = new StringBuilder();

      Iterator<Namespace> it = messageReaders.getKeySetOne().iterator();
      while (it.hasNext()) {
         Namespace namespace = it.next();
         Map<Class, U> subHash = messageReaders.getSubHash(namespace);
         Iterator<Class> innerit = subHash.keySet().iterator();
         while (innerit.hasNext()) {
            Class clazz = innerit.next();
            Message msg = subHash.get(clazz);
            if (msg != null) {
               sb.append(
                  String.format("Reader.%s.%s [%d]\n", namespace, msg.getName(), manager.getReferenceCount(msg)));
            }
         }
      }
      Iterator<Namespace> rit = messageWriters.getKeySetOne().iterator();
      while (rit.hasNext()) {
         Namespace namespace = rit.next();
         Map<Class, U> subHash = messageWriters.getSubHash(namespace);
         Iterator<Class> innerit = subHash.keySet().iterator();
         while (innerit.hasNext()) {
            Class clazz = innerit.next();
            Message msg = subHash.get(clazz);
            if (msg != null) {
               sb.append(
                  String.format("Writer.%s.%s [%d]\n", namespace, msg.getName(), manager.getReferenceCount(msg)));
            }
         }
      }

      return sb.toString();
   }

   private class MessageCollectionConsole extends ConsoleCommand {

      private static final String DESCRIPTION =
         "Prints the messages that exist in the environment and their reference count.";
      private static final String NAME = "mc";

      protected MessageCollectionConsole() {
         super(NAME, DESCRIPTION);
      }

      @Override
      protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
         try {
            println(getMessageInformation());
         } catch (Exception e) {
            printStackTrace(e);
         }
      }
   }

   private void checkState() {
      if (isDestroyed) {
         throw new IllegalStateException("Message Collection is destroyed");
      }
   }

   protected void log(Level level, String msg) {
      log(level, msg, null);
   }

   protected void log(Level level, String msg, Throwable t) {
      OseeLog.log(MessageCollection.class, level, msg, t);
   }

   /**
    * @return the periodicPublicationTasks
    */
   public PeriodicPublishMap getPeriodicPublicationTasks() {
      return periodicPublicationTasks;
   }

   public void remove(Class<? extends Message> class1, Namespace namespace, boolean writer) {
      checkState();
      for (MessageRemoveHandler removeHandler : messageRemoveHandlers) {
         if (removeHandler.shouldNotRemove(class1, namespace, writer)) {
            return;
         }
      }
      Message msg = null;
      if (writer) {
         msg = messageWriters.remove(namespace, class1);
         for (MessageRemoveHandler removeHandler : messageRemoveHandlers) {
            removeHandler.writerRemoveHandler(msg);
         }
         log(Level.FINEST,
            String.format("disposing the message [%s][writer] because it's reference count is 0.", msg.getName()));
      } else {
         msg = messageReaders.remove(namespace, class1);
         for (MessageRemoveHandler removeHandler : messageRemoveHandlers) {
            removeHandler.readerRemoveHandler(msg);
         }
         log(Level.FINEST,
            String.format("disposing the message [%s][reader] because it's reference count is 0.", msg.getName()));
         log(Level.FINEST, String.format("%d messages related", msg.getDefaultMessageData().getMessages().size()));

         MessageData removed = messageDataReaders.remove(createTopicDescription(msg.getDefaultMessageData()));
         if (removed == null) {
            log(Level.WARNING, String.format("Failed to remove reader %s.%s -- %s", namespace.toString(),
               msg.getDefaultMessageData().getTopicName(), namespace.toString()));
         }
      }
      msg.destroy();
   }

   public void add(MessageWriterSetupHandler messageWriterSetupHandler) {
      messageSetupHandlers.add(messageWriterSetupHandler);
   }

   public void remove(MessageWriterSetupHandler messageWriterSetupHandler) {
      messageSetupHandlers.remove(messageWriterSetupHandler);
   }

   public void add(MessageRemoveHandler messageRemoveHandler) {
      messageRemoveHandlers.add(messageRemoveHandler);
   }

   public void remove(MessageRemoveHandler messageRemoveHandler) {
      messageRemoveHandlers.remove(messageRemoveHandler);
   }

   public MessageData getMessageDataReader(TopicDescription topic) {
      return messageDataReaders.get(topic);
   }

}
