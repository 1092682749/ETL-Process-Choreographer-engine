package com.dyz.etlcomposer.model;


import lombok.Data;

 @Data
public class DiagramEdge {
    String startNodeId;
    String endNodeId;


    DiagramNode startNode;

    DiagramNode endNode;
}
