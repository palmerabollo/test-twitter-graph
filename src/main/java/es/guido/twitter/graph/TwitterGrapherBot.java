package es.guido.twitter.graph;

import java.io.IOException;

public class TwitterGrapherBot {
    private static final String STARTING_NODE = "palmerabollo";
    private static final int MAX_DEPTH = 1;
    
    private static final String EXPORT_FORMAT = "pdf";
    private static final String EXPORT_PATH = System.getProperty("java.io.tmpdir") + "twitter-graph.pdf";
    
    public static void main(String[] args) throws IOException {
        TwitterTraverser traverser = new TwitterTraverser(STARTING_NODE, MAX_DEPTH);
        traverser.traverse();
        traverser.export(EXPORT_FORMAT, EXPORT_PATH);
    }
}
