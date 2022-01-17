package com.dyz.etlcomposer.model;

import lombok.Data;

@Data
public class DiagramNode {
    String nodeId;
    String code;
    Double x;
    Double y;
    String name;
    String nodeType;
}
