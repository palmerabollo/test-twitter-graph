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

import java.io.IOException;

public class TwitterGrapherBot {
    private static final String DEFAULT_STARTING_NODE = "palmerabollo";
    private static final int MAX_DEPTH = 1;
    
    private static final String EXPORT_FORMAT = "pdf";
    private static final String EXPORT_PATH = System.getProperty("java.io.tmpdir") + "twitter-graph.pdf";
    
    public static void main(String[] args) throws IOException {
        String startingNode = DEFAULT_STARTING_NODE;
        if (args.length > 0) {
            startingNode = args[0];
        }
        TwitterTraverser traverser = new TwitterTraverser(startingNode, MAX_DEPTH);
        traverser.traverse();
        traverser.export(EXPORT_FORMAT, EXPORT_PATH);
    }
}
