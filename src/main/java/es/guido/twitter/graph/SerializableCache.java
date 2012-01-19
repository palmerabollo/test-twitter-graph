/*

(c) Copyright 2011 Telefonica, I+D. Printed in Spain (Europe). All Rights
Reserved.

The copyright to the software program(s) is property of Telefonica I+D.
The program(s) may be used and or copied only with the express written
consent of Telefonica I+D or in accordance with the terms and conditions
stipulated in the agreement/contract under which the program(s) have
been supplied.

*/
package es.guido.twitter.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SerializableCache<T> {
    private Map<String, T> CACHE = Collections.synchronizedMap(new HashMap<String, T>());
    private static final String LOCAL_CACHE_FILE = "df9bd757-49a8-481f-948a-e926814b39f0.twitter"; // just an unique file name
    
    public SerializableCache(boolean persist) {
        if (persist) {
            System.out.println("cache = " + getCacheFile().getAbsolutePath());
            
            try {
                unserializeCache();
            } catch (IOException ioe) {
                throw new RuntimeException("Unable to unserialize cache", ioe);
            }
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("cache hook");
                    try {
                        serializeCache();
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to hook cache serialization", e);
                    }
                }
            });
        }
    }
    
    private void serializeCache() throws IOException {
        FileOutputStream fos = new FileOutputStream(getCacheFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(CACHE);
        oos.close();
    }

    private void unserializeCache() throws IOException {
        File cacheFile = getCacheFile();
        if (cacheFile.exists() == false) {
            cacheFile.createNewFile();
        }
        FileInputStream fis = new FileInputStream(cacheFile);
        
        ObjectInputStream ois = new ObjectInputStream(fis);
        try {
            CACHE = (Map<String, T>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to read cache file", e);
        }
        ois.close();
    }
    
    public boolean containsKey(String key) {
        return CACHE.containsKey(key); 
    }
    
    public T get(String key) {
        return (T) CACHE.get(key); 
    }
    
    public void put(String key, T value) {
        CACHE.put(key, value); 
    }
    
    private static File getCacheFile() {
        String systemTmp = System.getProperty("java.io.tmpdir");
        return new File(systemTmp, LOCAL_CACHE_FILE);
    }
}
