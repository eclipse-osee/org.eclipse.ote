package org.eclipse.ote.services.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.OteBundleLocator;
import org.osgi.framework.Bundle;

public class OTEClientToServerBundleLocator implements OteBundleLocator {

   private static final String PROPERTY_OTE_DEBUG_PRECOMPILED_PRODUCT_PATH = "ote.debug.precompiled.product.path";

   private static final String HEADER = "OTE-server-dl";
   
   private CopyOnWriteArrayList<LoadBundleProvider> providers = new CopyOnWriteArrayList<LoadBundleProvider>();
   
   
   public void add(LoadBundleProvider provider){
      providers.add(provider);
   }
   
   public void remove(LoadBundleProvider provider){
      providers.remove(provider);
   }
   
   @Override
   public Collection<BundleInfo> getRuntimeLibs() {
      int total = 0;
      ArrayList<BundleInfo> bundlesFound = new ArrayList<BundleInfo>();
      HashSet<String> alreadyLoaded = new HashSet<String>();
      try{
         
         Map<String, File> buildSymbolicNames = buildSymbolicNames();
         Bundle[] bundles = ServiceUtility.getContext().getBundles();
         for(int i = 0; i < bundles.length; i++){
            Object value = bundles[i].getHeaders().get(HEADER);
            if(value != null){
               Bundle bundle = bundles[i];
               alreadyLoaded.add(bundle.getSymbolicName());
               resolveBundle(bundle, bundlesFound, buildSymbolicNames);
            }
         }
         for(LoadBundleProvider provider:providers){
            List<String> bundleSymbolicNames = provider.getBundleSymbolicNames();
            total += bundleSymbolicNames.size();
            for(String name:bundleSymbolicNames){
               if(!alreadyLoaded.contains(name)){
                  Bundle b = BundleUtility.findBundle(name);
                  if(b != null){
                     resolveBundle(b, bundlesFound, buildSymbolicNames);
                  } else {
                     OseeLog.log(getClass(), Level.SEVERE, String.format("Couldn't find[%s]", name));
                  }
               }
            }
         }
      } catch (IOException ex){
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
      if(total > bundlesFound.size()){
         OseeLog.log(getClass(), Level.SEVERE, String.format("Did not resolve [%d] bundles", total - bundlesFound.size()));
      }
      return bundlesFound;
   }
   
   private Map<String, File> buildSymbolicNames() throws IOException{
      Map<String, File> nameToFile = new HashMap<String,File>();
      String location = System.getProperty(PROPERTY_OTE_DEBUG_PRECOMPILED_PRODUCT_PATH);
      if(location != null){
         List<File> files = Lib.recursivelyListFiles(new File(location), Pattern.compile(".*.jar"));
         for(File file: files){
            JarFile jarFile = new JarFile(file);
            Manifest manifest = jarFile.getManifest();
            String symbolicName = generateBundleName(manifest);
            if(symbolicName != null){
               if(!nameToFile.containsKey(symbolicName)){
                  nameToFile.put(symbolicName, file);
               } else {
                  File otherFile = nameToFile.get(symbolicName);
                  JarFile otherJarFile = new JarFile(otherFile);
                  Manifest otherManifest = otherJarFile.getManifest();
                  String otherVersion = otherManifest.getMainAttributes().getValue("Bundle-Version");
                  String version = manifest.getMainAttributes().getValue("Bundle-Version");
                  if(version.compareTo(otherVersion) > 0){
                     nameToFile.put(symbolicName, file);
                  }
               }
            }
         }
      }
      
      return nameToFile;
   }
   
   public static String generateBundleName(Manifest jarManifest) {
      String nameEntry = jarManifest.getMainAttributes().getValue("Bundle-SymbolicName");
     if(nameEntry == null){
        return null;
     }
      // Sometimes there's a semicolon then extra info - ignore this
      int index = nameEntry.indexOf(';');
      if (index != -1) {
         nameEntry = nameEntry.substring(0, index);
      }

      return nameEntry;
   }
   
   private void resolveBundle(Bundle bundle, List<BundleInfo> bundlesFound, Map<String, File> buildSymbolicNames) throws IOException{
      URL entry = bundle.getEntry("");
      try {
         URL urlResolved = FileLocator.resolve(entry);
         if(urlResolved != null){
            String path = sanatizePath(urlResolved.getPath());
            File file = new File(path);
            if(file.exists() && file.isDirectory()){
               if(buildSymbolicNames != null){
                  File localFile = buildSymbolicNames.get(bundle.getSymbolicName());
                  if(localFile != null){
                     bundlesFound.add(new BundleInfo(localFile.toURI().toURL()));
                  } else {
                     OseeLog.log(ServiceUtility.class, Level.SEVERE, "Did not resolve from other files: " + bundle.getSymbolicName());
                  }
               }
            } else if(file.exists()){
               bundlesFound.add(new BundleInfo(file.toURI().toURL()));
            } else {
               OseeLog.log(getClass(), Level.SEVERE, String.format("Strange Path[%s]", path));
            }
         } else {
            OseeLog.log(ServiceUtility.class, Level.SEVERE, "Did not resolve: " + bundle.getSymbolicName());
         }
      } catch (Exception e) {
         OseeLog.log(ServiceUtility.class, Level.SEVERE, e);
      }
   }

   private String sanatizePath(String path) {
      if(path.endsWith("!/")){
         path = path.substring(0, path.length()-2);
      }
      if(path.startsWith("file:/")){
         path = path.substring(6);
      }
      return path;
   }

   @Override
   public Collection<BundleInfo> consumeModifiedLibs() throws IOException, CoreException {
      return new ArrayList<BundleInfo>();
   }
   
   /**
    * Pass in the git root folder to find all tHe precompile.xml files and list all of the bundles you'll need to load.  
    * This can be used with the OteServerFileLoad interface to ensure all necessary bundles are available to the server. 
    * @param args
    * @throws IOException
    */
   public static void main(String[] args) throws IOException{
      File file = new File(args[0]);
      file.listFiles();
      Matcher[] matchers = new Matcher[3];
      matchers[0] = Pattern.compile(".*<move.*commonBundles(.*?)</move>.*", Pattern.MULTILINE | Pattern.DOTALL).matcher("");
      matchers[1] = Pattern.compile(".*<move.*externalServerDependencies(.*?)</move>.*", Pattern.MULTILINE | Pattern.DOTALL).matcher("");
      matchers[2] = Pattern.compile(".*<move.*runtimeLibs(.*?)</move>.*", Pattern.MULTILINE | Pattern.DOTALL).matcher("");
      Matcher internal = Pattern.compile(".*?<file name=\"(.*?).jar\".*?/>", Pattern.MULTILINE | Pattern.DOTALL).matcher("");
      List<File> recursivelyListFiles = Lib.recursivelyListFiles(file, Pattern.compile(".*precompile.xml"));
      
      ArrayList<String> bundles = new ArrayList<String>();
      
      for(File precompiledFile:recursivelyListFiles){
         String fileToString = Lib.fileToString(precompiledFile);
         for(int i = 0; i < matchers.length; i++){
            matchers[i].reset(fileToString);
            if(matchers[i].matches()){
               String innerString = matchers[i].group(1);
               internal.reset(innerString);
//               System.out.println(innerString);
               while(internal.find()){
                  bundles.add(internal.group(1));
               }
            }
         }
      }
      
      Collections.sort(bundles);
      
      for(String name:bundles){
         System.out.println(name);
      }
   }

}
