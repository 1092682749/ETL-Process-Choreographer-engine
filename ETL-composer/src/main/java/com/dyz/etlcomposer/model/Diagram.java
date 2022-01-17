package com.dyz.etlcomposer.model;

import lombok.Data;

import java.util.List;

@Data
public class Diagram {
    Long id;
    String name;
    //    @JSONField(serialize = false)
    public List<DiagramNode> nodes;
    //    @JSONField(serialize = false)
    public List<DiagramEdge> edges;
}
