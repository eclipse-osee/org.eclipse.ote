package org.eclipse.ote.services.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class BundleUtility {

   /**
    * Finds files in a bundle and return the URL to that file.  It also handles the case of files existing in fragments
    * but the symbolic name used is that of the host bundle. 
    * 
    * @param bundleSymbolicName
    * @param path
    * @return URL or null if path was not found
    */
   public static URL findEntry(String bundleSymbolicName, String path){
      URL url = null;
      Bundle bundle = findBundle(bundleSymbolicName);
      url = bundle.getEntry(path);
      if(url == null && bundle instanceof BundleHost){
         BundleFragment[] fragments = ((BundleHost)bundle).getFragments();
         if(fragments != null){
            for(BundleFragment fragment: fragments){
               url = fragment.getEntry(path);
               if(url != null){
                  break;
               }
            }
         }
      }
      return url;
   }
   
   /**
    * Finds all paths in a bundle under and including a given folder path.
    * 
    * @param bundleSymbolicName
    * @param path
    * @return URL or null if path was not found
    */
   public static Set<String> findSubEntries(String bundleSymbolicName, String folderPath){
      Set<String> paths = new HashSet<String>();
      paths.add(folderPath);
      Bundle bundle = findBundle(bundleSymbolicName);
      findSubEntries(bundle, paths, folderPath);
      return paths;
   }
   
   private static void findSubEntries(Bundle bundle, Set<String> paths, String folderPath){
      bundle.getEntryPaths(folderPath);
      entryPaths(bundle.getEntryPaths(folderPath), bundle, paths);
      if(bundle instanceof BundleHost){
         BundleFragment[] fragments = ((BundleHost)bundle).getFragments();
         if(fragments != null){
            for(BundleFragment fragment: fragments){
               entryPaths(fragment.getEntryPaths(folderPath), fragment, paths);
            }
         }
      }
   }
   
   private static void entryPaths(Enumeration<String> enumEntries, Bundle bundle, Set<String> paths){
      if(enumEntries == null){
         return;
      }
      while(enumEntries.hasMoreElements()){
         String foundPath = enumEntries.nextElement();
         paths.add(foundPath);
         if(foundPath.endsWith("/")){
            findSubEntries(bundle, paths, foundPath);
         } 
      }
   }
   
   /**
    * Force a bundle to start.  
    * 
    * @param symbolicName
    * @return true if the bundle was started false otherwise.
    */
   public static boolean startBundle(String symbolicName){
      Bundle bundle = findBundle(symbolicName);
      if(bundle != null){
         return startBundle(bundle);
      } else {
         return false;
      }
   }
   
   private static boolean startBundle(Bundle bundle){
      if(bundle != null){
         try {
            bundle.start();
            return true;
         } catch (BundleException e) {
            OseeLog.log(BundleUtility.class, Level.SEVERE, String.format("Unable to start bundle [%s].", bundle.getSymbolicName()), e);
         }
      }
      return false;
   }
   
   private static Bundle findBundle(String symbolicName){
      Bundle[] bundles = FrameworkUtil.getBundle(BundleUtility.class).getBundleContext().getBundles();
      for(Bundle bundle:bundles){
         if(bundle.getSymbolicName().equals(symbolicName)){
            return bundle;
         }
      }
      return null;
   }

   /**
    * Gets all found entries for a specified bundle given a list of entries.  This does not search directories, the path must match exactly.
    * Most likely this is used in conjunction with the {@link #findSubEntries(String, String) findSubEntries} method.
    * 
    * @param bundleSymbolicName
    * @param entries
    * @return all found entries as URLs
    */
   public static List<URL> entriesToURLs(String bundleSymbolicName, List<String> entries) {
      List<URL> paths = new ArrayList<URL>();
      Bundle bundle = findBundle(bundleSymbolicName);
      for(String path:entries){
          URL url = findEntry(bundleSymbolicName, path);
          if(url != null){
             paths.add(url);
          }
      }
      return paths;
   }
}