package com.dyz.etlcomposer.model;



import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class RunTreeNode {
    List<RunTreeNode> preNodes = new LinkedList<>();
    List<RunTreeNode> nextNodes = new LinkedList<>();
    DiagramNode node;
    public static ThreadLocal<Map<String, RunTreeNode>> mapThreadLocal = new ThreadLocal<>();

    public static RunTreeNode constructRunTree(Diagram diagram) {
        mapThreadLocal.set(new HashMap<>());
        RunTreeNode runTreeStartNode = new RunTreeNode();
        List<DiagramEdge> edges = diagram.edges;
        List<DiagramNode> nodes = diagram.nodes;
        for (DiagramNode node : nodes) {
            if (node.getNodeType().equals(NodeType.NODE_START)) {
                runTreeStartNode.setNode(node);
                mapThreadLocal.get().put(node.getName(), runTreeStartNode);
                findNext(runTreeStartNode, nodes, edges);
            }
        }
        mapThreadLocal.remove();
        return runTreeStartNode;
    }

    public static void findNext(RunTreeNode currentNode, List<DiagramNode> nodes, List<DiagramEdge> edges) {

        String currentNodeID = currentNode.getNode().getNodeId();
        List<String> nextNodeIds = edges.stream().filter(edge -> edge.getStartNodeId().equals(currentNodeID)).map(DiagramEdge::getEndNodeId).collect(Collectors.toList());
        if (nextNodeIds.size() == 0) {
            return;
        }
        currentNode.setNextNodes(nodes.stream().filter(ni -> nextNodeIds.contains(ni.getNodeId())).map(i -> {
            RunTreeNode treeNode = new RunTreeNode();
            RunTreeNode existNode = mapThreadLocal.get().get(i.getName());
            if (existNode != null) {
             treeNode = existNode;
            } else {
                mapThreadLocal.get().put(i.getName(), treeNode);
            }
            treeNode.setNode(i);
            treeNode.getPreNodes().add(currentNode);
            findNext(treeNode, nodes, edges);
            return treeNode;
        }).collect(Collectors.toList()));
    }
}
