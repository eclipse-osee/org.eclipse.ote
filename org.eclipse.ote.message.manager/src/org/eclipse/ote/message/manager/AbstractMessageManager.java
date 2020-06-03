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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.message.io.IOWriter;
import org.eclipse.osee.ote.message.listener.DDSDomainParticipantListener;
import org.eclipse.osee.ote.message.listener.IMessageCreationListener;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipant;
import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipantFactory;
import org.eclipse.osee.ote.messaging.dds.entity.Publisher;
import org.eclipse.osee.ote.messaging.dds.entity.Subscriber;
import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.listener.DataReaderListener;
import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.service.DomainId;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;
import org.eclipse.osee.ote.properties.OtePropertiesCore;


/**
 * Provides the majority of the implementation needed to create OTE messages as readers or writers and injecting DDS components
 * for those objects so that the messaging system can read and write accordingly. 
 * 
 * Subclasses need only define the concrete MessageData and Message classes used by the generic methods, bind the environment
 * and NamespaceMapper and call the init method.
 * 
 * @author Michael P. Masterson
 */
public abstract class AbstractMessageManager<D extends MessageData, M extends Message<? extends ITestEnvironmentMessageSystemAccessor, D, M>> implements IMessageManager<D, M>, OTETopicLookup {

   private DomainId domainId;
   private DomainParticipant participant;
   private WeakReference<TestEnvironmentInterface> env;
   private final DDSDomainParticipantListener ddsListener;
   protected MessageCollection<M> messageCollection;
   private NamespaceMapper nsMapper;
   private TopicListener topicListener;
   private Publisher uutPublisher;
   private Subscriber subscriber;
   
   private final  List<IMessageCreationListener> preCreation = new ArrayList<IMessageCreationListener>();
   private final  List<IMessageCreationListener> postCreation = new ArrayList<IMessageCreationListener>();
   private final  List<IMessageCreationListener> instanceRequestListeners = new ArrayList<IMessageCreationListener>();
   private final  HashMap<M, HashSet<IMessageRequestor<D,M>>> requestorReferenceMap =
         new HashMap<M, HashSet<IMessageRequestor<D,M>>>(200);
   private volatile boolean initialized = false;
   protected volatile boolean destroyed = false;

   private final ConcurrentHashMap<Namespace, MessageDataLookup> messageDataLookupHash =
         new ConcurrentHashMap<Namespace, MessageDataLookup>();
   private MessageSignalMapping messageSignalMapping;

   private final CopyOnWriteArraySet<DataType> dataTypes = new CopyOnWriteArraySet<DataType>();

   
   public AbstractMessageManager() {
      ddsListener = new DDSDomainParticipantListener();
      messageCollection = new MessageCollection<>();

   }
   
   public void bindEnv(TestEnvironmentInterface env) {
      this.env = new WeakReference<TestEnvironmentInterface>(env);
   }
   
   @Override
   public void init() {
      checkState();
      GCHelper.getGCHelper().addRefWatch(this);
      
      if (env == null) {
         throw new IllegalArgumentException("Test Environment cannot be null");
      }
      if(nsMapper == null) {
         throw new IllegalArgumentException("Namespace Mapper cannot be null");
      }
      messageCollection.init(getEnv(), nsMapper);
      
      topicListener = new DDSTopicListener();
      
      domainId = new DomainId(0);
      participant = DomainParticipantFactory.getInstance().createParticipant(domainId, ddsListener, false);
      uutPublisher = participant.getMiddlewarePublisherInstance(null);
      subscriber = participant.createSubscriber(null);
      initialized = true;
   }

   @Override
   public Class<M> getMessageClass(String msgClass) throws ClassCastException, ClassNotFoundException {
      return (Class<M>) env.get().getRuntimeManager().loadFromRuntimeLibraryLoader(msgClass).asSubclass(Message.class);
   }

   public MessageDataLookup getMessageDataLookup(Namespace namespace) {
      MessageDataLookup lookup = messageDataLookupHash.get(namespace);
      if (lookup == null) {
         lookup = new MessageDataLookupImpl();
         putMessageDataLookup(namespace, lookup);
      }
      return lookup;
   }

   public void putMessageDataLookup(Namespace namespace, MessageDataLookup lookup) {
      MessageDataLookup setLookup = messageDataLookupHash.get(namespace);
      if (setLookup == null) {
         messageDataLookupHash.put(namespace, lookup);
      } else {
         if (lookup == null) {
            lookup = new MessageDataLookupImpl();
         }
         copyMessageDataBetweenLookups(setLookup, lookup);
         messageDataLookupHash.put(namespace, lookup);
      }
   }

   private void copyMessageDataBetweenLookups(MessageDataLookup src, MessageDataLookup dest) {
      for (MessageData data : src.allValues()) {
         dest.put(data);
      }
   }

   @Override
   public DDSDomainParticipantListener getDDSListener() {
      return ddsListener;
   }

   @Override
   public void addPostCreateMessageListener(IMessageCreationListener listener) {
      checkState();
      postCreation.add(listener);
   }

   @Override
   public void addPreCreateMessageListener(IMessageCreationListener listener) {
      checkState();
      preCreation.add(listener);
   }

   @Override
   public void addInstanceRequestListener(IMessageCreationListener listener) {
      checkState();
      instanceRequestListeners.add(listener);
   }

   /**
    * This method just creates and sets up a message in the environment.... it does not set the backing buffer that must
    * be done from a listener
    */
   @Override
   public <CLASSTYPE extends M> CLASSTYPE createAndSetUpMessage(Class<CLASSTYPE> messageClass, IMessageRequestor<D,M> requestor, boolean writer) throws TestException {
      checkState();
      notifyPreCreateMessage(messageClass, requestor, writer);

      // instantiate the message
      CLASSTYPE message = createMessage(messageClass);

      Namespace namespace = nsMapper.getNamespace(message.getMemType());

      // create readers or writers for each data source
      MessageData data = message.getDefaultMessageData();
      if (writer) {
         registerType(data.getTypeSupport(), data.getTypeName());
         Topic newTopic = createTopic(namespace, data.getTopicName(), data.getTypeName());
         OTEWriterImpl oteWriter = new OTEWriterImpl(newTopic, uutPublisher, true, data, uutPublisher, this.ddsListener, this, data, namespace.toString());
         data.setWriter(oteWriter);
      } else {
         data.setReader(createDataReader(data.getTypeSupport(), data, namespace, data.getTopicName(),
               data.getTypeName()));

         insertNewMessageDataIntoLookup(namespace, data);
      }

      messageCollection.onMessageCreated(messageClass, requestor, writer, message, namespace);

      try {
          //do new thing if it exists
          if(isMessageMappingEnabled()) {
              messageSignalMapping.map(requestor, message);
          } else {
              message.postCreateMessageSetup(this, message.getDefaultMessageData());
          }
      } catch (Exception ex) {
          throw new TestException(String.format("Unable to instantiate [%s].", messageClass.getName()), Level.SEVERE, ex);
      }
      if(writer){
         messageCollection.setupMessageWriter(message);
      }
      notifyPostCreateMessage(messageClass, requestor, writer, message, namespace);
      return message;
   }

   /**
    * Can be overwritten by application specific 
    */
   protected boolean isMessageMappingEnabled() {
      return OtePropertiesCore.signalMappingEnabled.getBooleanValue();
   }

   private void insertNewMessageDataIntoLookup(Namespace namespace, MessageData data) {
      MessageDataLookup lookup = getMessageDataLookup(namespace);
      lookup.put(data);
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.ote.simple.io.manager.SimpleMessageManager#createMessage(java.lang.Class)
    */
   @Override
   public <CLASSTYPE extends M> CLASSTYPE createMessage(Class<CLASSTYPE> messageClass) throws TestException {
      checkState();
      try {
         CLASSTYPE message = messageClass.newInstance();
         return message;
      } catch (InstantiationException ex) {
         throw new TestException(String.format("Unable to instantiate [%s].", messageClass.getName()), Level.SEVERE, ex);
      } catch (IllegalAccessException ex) {
         throw new TestException(String.format("Unable to instantiate [%s].", messageClass.getName()), Level.SEVERE, ex);
      }
   }

   private DataReader createDataReader(TypeSupport type, DataReaderListener dataReaderListener, Namespace namespace, String topicName, String typeName) {

      registerType(type, typeName);
      return subscriber.createDataReader(createTopic(namespace, topicName, typeName), dataReaderListener);
   }

   private Topic createTopic(Namespace namespace, String topicName, String typeName) {

      Topic topic = participant.createTopic(topicName, namespace.toString(), typeName, topicListener);
      if (topic == null) {
         throw new MessageSystemException(
               "Unable to create topic for [" + namespace + ", " + topicName + ", " + typeName + "]", Level.SEVERE);
      }
      return topic;
   }

   @Override
   public synchronized void destroy() {
      if (destroyed) {
         return;
      }
      OseeLog.log(getClass(), Level.INFO, "destroying message manager");

      this.requestorReferenceMap.clear();
      messageCollection.destroy();
      messageCollection = null;

      onDestroy();
      destroyed = true;
      preCreation.clear();
      postCreation.clear();
      instanceRequestListeners.clear();
      OseeLog.log(
            getClass(),
            Level.INFO,
            String.format("instance report: Message %d/%d, Scripts %d/%d", Message.getFinalized(),
                  Message.getConstructed(), TestScript.getFinalized(), TestScript.getConstructed()));

   }

   protected void checkState() {
      if (destroyed) {
         throw new IllegalStateException("message manager is destroyed");
      }
   }

   @Override
   public Collection<M> getAllMessages() {
      return messageCollection.getAllMessages();
   }

   @Override
   public Collection<M> getAllReaders() {
      return messageCollection.getAllReaders();
   }

   @Override
   public Collection<M> getAllWriters() {
      return messageCollection.getAllWriters();
   }

   @Override
   public Collection<M> getAllReaders(DataType type) {
      return messageCollection.getAllReaders(type);
   }

   @Override
   public Collection<M> getAllWriters(DataType type) {
      return messageCollection.getAllWriters(type);
   }

   public synchronized <CLASSTYPE extends M> CLASSTYPE getMessageReader(IMessageRequestor<D,M> requestor, Class<CLASSTYPE> type) {
      checkState();
      CLASSTYPE classtype = type.cast(messageCollection.get(type, false));
      if (classtype == null) {
         try {
            classtype = createAndSetUpMessage(type, requestor, false);
         } catch (Exception ex) {
            throw new TestException(String.format("unable to create message of type [%s]", type.getName()),
                  Level.SEVERE, ex);
         }

         if (classtype == null) {
            throw new TestException(String.format("unable to create message of type [%s]", type.getName()),
                  Level.SEVERE);
         }

      }
      addRequestorReference(requestor, classtype);
      for (IMessageCreationListener listener : instanceRequestListeners) {
         listener.onInstanceRequest(type, classtype, requestor, false);
      }
      return classtype;
   }

   private boolean addRequestorReference(IMessageRequestor<D,M> requestor, M msg) {
      HashSet<IMessageRequestor<D,M>> list = requestorReferenceMap.get(msg);
      if (list == null) {
         list = new HashSet<IMessageRequestor<D,M>>(24);
         requestorReferenceMap.put(msg, list);
      }
      return list.add(requestor);
   }

   @Override
   public synchronized boolean removeRequestorReference(IMessageRequestor<D,M> requestor, M msg) {
      checkState();
      HashSet<IMessageRequestor<D,M>> list = requestorReferenceMap.get(msg);
      if (list != null) {
         boolean result = list.remove(requestor);
         if (list.isEmpty()) {
            requestorReferenceMap.remove(msg);
            if (!msg.isDestroyed()) {
               messageCollection.remove(msg.getClass(), msg.getDefaultMessageData().getNamespace(), msg.isWriter());
            } else {
               OseeLog.log(AbstractMessageManager.class, Level.WARNING,
                     String.format("%s is getting removed twice.", msg.getMessageName()), new Exception());
            }
         }
         return result;
      }
      return false;
   }

   @Override
   public <CLASSTYPE extends M> int getReferenceCount(CLASSTYPE classtype) {
      checkState();
      if( requestorReferenceMap.containsKey(classtype)) {
         return requestorReferenceMap.get(classtype).size();
      } else {
         return 0;
      }
   }

   public synchronized <CLASSTYPE extends M> CLASSTYPE getMessageWriter(IMessageRequestor<D,M> requestor, Class<CLASSTYPE> type) throws TestException {
      checkState();
      CLASSTYPE classtype = type.cast(messageCollection.get(type, true));
      if (classtype == null) {
         try {
            classtype = createAndSetUpMessage(type, requestor, true);
         } catch (Exception ex) {
            throw new TestException(String.format("unable to create message of type [%s]", type.getName()),
                  Level.SEVERE, ex);
         }

         if (classtype == null) {
            throw new TestException(String.format("unable to create message of type [%s]", type.getName()),
                  Level.SEVERE);
         }
      }
      addRequestorReference(requestor, classtype);
      for (IMessageCreationListener listener : instanceRequestListeners) {
         listener.onInstanceRequest(type, classtype, requestor, true);
      }
      return classtype;
   }

   @Override
   public <T extends M> T findInstance(Class<T> clazz, boolean writer) {
      return clazz.cast(messageCollection.hasInstance(clazz, writer));
   }

   protected TestEnvironmentInterface getTestEnvironment() {
      return env.get();
   }

   protected boolean isInitialized() {
      return initialized;
   }

   public TestEnvironmentInterface getEnv() {
      return env.get();
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType physicalType) {
      if(getEnv() != null){
         return getEnv().getDataTypes().contains(physicalType) || dataTypes.contains(physicalType);
      } else {
         return dataTypes.contains(physicalType);
      }
   }

   private <CLASSTYPE extends M> void notifyPostCreateMessage(Class<CLASSTYPE> messageClass, IMessageRequestor<D,M> requestor, boolean writer, CLASSTYPE message, Namespace namespace) {

      for (IMessageCreationListener listener : postCreation) {
         listener.onPostCreate(messageClass, requestor, writer, message, namespace);
      }
   }

   private <CLASSTYPE extends M> void notifyPreCreateMessage(Class<CLASSTYPE> messageClass, IMessageRequestor<D,M> requestor, boolean writer) {

      for (IMessageCreationListener listener : preCreation) {
         listener.onPreCreate(messageClass, requestor, writer);
      }
   }

   protected void onDestroy(){
   }

   private void registerType(TypeSupport type, String typeName) throws MessageSystemException {

      ReturnCode val = type.registerType(participant, typeName, type.getClass().getClassLoader());
      if (val != ReturnCode.OK) {
         throw new MessageSystemException(typeName + " - " + val.getDescription(), Level.SEVERE);
      }
   }

   public Set<Pair<Double, Integer>> getTasks() {
      return messageCollection.getPeriodicPublicationTasks().getRatePhaseMap().keySet();
   }

   public PeriodicPublishTask getPeriodicTask(Double rate) {
      return messageCollection.getPeriodicPublicationTasks().getRatePhaseMap().get(rate, 0);
   }

   @Override
   public IMessageRequestor<D,M> createMessageRequestor(String name) {
      checkState();
      return new MessageRequestor<>(name, this);
   }

   synchronized Collection<IMessageRequestor<D,M>> getMessageRequestors(M msg) {
      checkState();
      return requestorReferenceMap.get(msg);
   }

   public void addIOWriter(IOWriter writer){
      ddsListener.registerWriter(writer);
   }
   
   public void removeIOWriter(IOWriter writer){
      ddsListener.unregisterWriter(writer);
   }
   
   public void addDataTypeProvider(DataTypeProvider provider) {
       dataTypes.addAll(provider.getProvidedDataTypes());
   }
   
   public void removeDataTypeProvider(DataTypeProvider provider) {
       dataTypes.removeAll(provider.getProvidedDataTypes());
   }
   
   @Override
   public Set<DataType> getAvailableDataTypes() {
       return dataTypes;
   }
   
   @Override
   public MessageData getReader(TopicDescription topic){
       return messageCollection.getMessageDataReader(topic);
   }
   
   public void bindNamespaceMapper(NamespaceMapper nsMapper) {
      this.nsMapper = nsMapper;
   }

}
