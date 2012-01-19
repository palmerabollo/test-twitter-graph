package es.guido.twitter.graph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.social.twitter.api.TwitterProfile;

public class TwitterTraverser {
    private Set<String> ALREADY_TRAVERSED = new HashSet<String>();
    private final GraphBuilder builder;
    
    private final TwitterDecorator twitter;
    private final int maxDepth;
    private final String startingNode;
    
    public TwitterTraverser(String startingNode, int maxDepth) {
        this(startingNode, maxDepth, true);
    }
    
    public TwitterTraverser(String startingNode, int maxDepth, boolean localCache) {
        this.builder = new GraphBuilder();
        this.twitter = new TwitterDecorator(localCache);
        this.maxDepth = maxDepth;
        this.startingNode = startingNode;
    }
    
    public void traverse() {
        traverse(startingNode, 0); // recursive algorithm
    }
    
    private void traverse(String node, int depth) {
        System.out.println("Traverse " + node + " / depth = " + depth);
        
        List<TwitterProfile> followers = twitter.getFollowers(node);
        System.out.println("\tFollowers " + node + " / size = " + followers.size() + " / depth = " + depth + " / req = " + twitter.getRequestCount());
        builder.setSize(node, followers.size());
        builder.setColor(node, 0, 0, 0.9f);
        
        if (depth == maxDepth) {
            System.out.println("Max depth reached " + node + " / " + depth);
            return;
        }
        
        if (ALREADY_TRAVERSED.contains(node)) {
            System.out.println("Node already traversed " + node + " / " + depth);
            return;
        }
        
        ALREADY_TRAVERSED.add(node);
        
        List<TwitterProfile> friends = twitter.getFriends(node);
        System.out.println("\tFriends " + node + " / depth = " + depth + " / req = " + twitter.getRequestCount());
        
        for (TwitterProfile friend : friends) {
            builder.addDirectedRelation(node, friend.getScreenName());
            traverse(friend.getScreenName(), depth + 1);
        }
    
        /**
        System.out.println("\tFollowers " + node + " / depth = " + depth + " / req = " + twitter.getRequests());
        List<TwitterProfile> followers = twitter.getFollowers(node);
        for (TwitterProfile follower : followers) {
            builder.addDirectedRelation(follower.getScreenName(), node);
            traverse(follower.getScreenName(), depth + 1);
        }
        */
        
        System.out.println("End " + node + " / depth = " + depth);
    }
    
    public void export(String format, String fileName) throws IOException {
        System.out.println("Exporting as " + format + " to " + fileName);
        FileOutputStream fos = new FileOutputStream(fileName);
        builder.export(format, fos);
    }
}
