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

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.ApiException;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

/**
 * Decorates the Twitter interface provided by spring-social
 * @author guido
 */
public class TwitterDecorator {
    private static final int MILLIS_BETWEEN_REQUESTS = 100;
    private long requestCount = 0;

    private Twitter twitter;
    private SerializableCache<List<TwitterProfile>> cache;
    
    public TwitterDecorator() {
        this(true);
    }

    public TwitterDecorator(boolean persistCache) {
        this.twitter = new TwitterTemplate();
        this.cache = new SerializableCache<List<TwitterProfile>>(persistCache);
    }

    private void prepareRequest() {
        requestCount++;
        try {
            Thread.sleep(MILLIS_BETWEEN_REQUESTS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to include a pause. Stop.", e);
        }
    }

    public List<TwitterProfile> getFollowers(String username) {
        final String cacheKey = username + ".followers";
        try {
            if (cache.containsKey(cacheKey)) {
                System.out.println(username + " followers found in cache");
                return cache.get(cacheKey);
            }

            prepareRequest();

            List<TwitterProfile> result = twitter.friendOperations().getFollowers(username);
            cache.put(cacheKey, result);
            return result;
        } catch (UncategorizedApiException e) {
            System.out.println("Uncategorized API exception with " + username + " (" + e.getMessage() + ")" + " Continue.");
            return new ArrayList<TwitterProfile>();
        } catch (ApiException e) {
            System.out.println("API exception with " + username + ". Continue.");
            return new ArrayList<TwitterProfile>();
        }
    }

    public List<TwitterProfile> getFriends(String username) {
        final String cacheKey = username + ".friends";
        try {
            if (cache.containsKey(cacheKey)) {
                System.out.println(username + " friends found in cache");
                return cache.get(cacheKey);
            }

            prepareRequest();
            List<TwitterProfile> result = twitter.friendOperations().getFriends(username);
            cache.put(cacheKey, result);
            return result;
        } catch (UncategorizedApiException e) {
            System.out.println("Uncategorized API exception with " + username + " (" + e.getMessage() + ")" + " Continue.");
            return new ArrayList<TwitterProfile>();
        } catch (ApiException e) {
            System.out.println("API exception with " + username + ". Continue.");
            return new ArrayList<TwitterProfile>();
        }
    }

    public long getRequestCount() {
        return requestCount;
    }
}
