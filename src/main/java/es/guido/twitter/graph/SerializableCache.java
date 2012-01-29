/*******************************************************************************
 * Twitter Grapher
 * Copyright (C) 2012 guido
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

/**
 * Simple in-memory cache, serializable to disk on JVM shutdown
 * @author guido
 */
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
