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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import com.itextpdf.text.PageSize;

/**
 * Manages the node graph model (in memory)
 * @author guido
 */
public class GraphBuilder {
    private GraphModel graphModel;
    private static final float SIZE_CORRECTION_FACTOR = 0.5f;
    
    public GraphBuilder() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        
        // Get a graph model - it exists because we have a workspace
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
    }
    
    public void setSize(String idNode, float size) {
         size = SIZE_CORRECTION_FACTOR * (float) Math.log10(1 + size);
        
         if (size < 1f) {
             size += 1f;
         }
        
        Node node = findOrCreateNode(idNode);
        node.getNodeData().setSize(size);
    }
    
    public void setAlpha(String idNode, float alpha) {
        Node node = findOrCreateNode(idNode);
        node.getNodeData().setAlpha(alpha);
    }
    
    public void setColor(String idNode, float r, float g, float b) {
        Node node = findOrCreateNode(idNode);
        node.getNodeData().setColor(r, g, b);
    }
    
    public void addDirectedRelation(String idSource, String idTarget) {
        addDirectedRelation(idSource, idTarget, 1f);
    }
    
    public void addDirectedRelation(String idSource, String idTarget, float weight) {
        System.out.println(idSource + " -- (" + weight + ") --> " + idTarget);
        Node source = findOrCreateNode(idSource);
        Node target = findOrCreateNode(idTarget);
        buildEdge(source, target, weight, true);
    }
    
    public void export(String format, OutputStream os) throws IOException {
        configurePreview();
        
        configureLayout();
        
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        Workspace workspace = pc.getCurrentWorkspace();
        
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        PDFExporter pdfExporter = (PDFExporter) ec.getExporter(format);
        pdfExporter.setPageSize(PageSize.A0);
        pdfExporter.setWorkspace(workspace);
        ec.exportStream(os, pdfExporter);
        os.flush();
    }

    private void configureLayout() {
        AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
        autoLayout.setGraphModel(graphModel);
        
        ForceAtlasLayout layout = new ForceAtlasLayout(null);
        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f); // True after 10% of layout time
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f); // 500 for the complete period
        autoLayout.addLayout(layout, 1f, new AutoLayout.DynamicProperty[]{ adjustBySizeProperty, repulsionProperty });
        
        autoLayout.execute();
    }

    private void configurePreview() {
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_COLOR, new DependantColor(Color.LIGHT_GRAY));
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_SHOW_BOX, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_BOX_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_BOX_COLOR, new DependantColor(Color.LIGHT_GRAY));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 2f);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        previewController.refreshPreview();
    }
    
    private Node findOrCreateNode(String id) {
        Graph graph = graphModel.getGraph();
        Node result = graph.getNode(id);
        if (result == null) {
            result = buildNode(id);
            graph.addNode(result);
        }
        return result;
    }
    
    private Node buildNode(String id) {
        Node result = graphModel.factory().newNode(id);
        result.getNodeData().setLabel(id);
        return result;
    }
    
    private Edge buildEdge(Node n1, Node n2, float weight, boolean flag) {
        Edge result = graphModel.factory().newEdge(n1, n2, weight, flag);
        graphModel.getDirectedGraph().addEdge(result);
        return result;
    }
}
