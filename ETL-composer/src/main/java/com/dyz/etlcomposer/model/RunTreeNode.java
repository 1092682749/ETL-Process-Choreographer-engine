package com.dyz.etlcomposer.model;



import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RunTreeNode {
    List<RunTreeNode> preNodes = new LinkedList<>();
    List<RunTreeNode> nextNodes = new LinkedList<>();
    DiagramNode node;


    public static RunTreeNode constructRunTree(Diagram diagram) {
        RunTreeNode runTreeStartNode = new RunTreeNode();
        List<DiagramEdge> edges = diagram.edges;
        List<DiagramNode> nodes = diagram.nodes;
        for (DiagramNode node : nodes) {
            if (node.getNodeType().equals(NodeType.NODE_START)) {
                runTreeStartNode.setNode(node);
                findNext(runTreeStartNode, nodes, edges);
            }
        }
        return runTreeStartNode;
    }

    public static void findNext(RunTreeNode currentNode, List<DiagramNode> nodes, List<DiagramEdge> edges) {
        String currentNodeID = currentNode.getNode().getNodeId();
        List<String> nextNodeIds = edges.stream().filter(edge -> edge.getStartNode().equals(currentNodeID)).map(DiagramEdge::getEndNodeId).collect(Collectors.toList());
        if (nextNodeIds.size() == 0) {
            return;
        }
        currentNode.setNextNodes(nodes.stream().filter(ni -> nextNodeIds.contains(ni.getNodeId())).map(i -> {
            RunTreeNode treeNode = new RunTreeNode();
            treeNode.setNode(i);
            treeNode.getPreNodes().add(currentNode);
            findNext(treeNode, nodes, edges);
            return treeNode;
        }).collect(Collectors.toList()));
    }
}
