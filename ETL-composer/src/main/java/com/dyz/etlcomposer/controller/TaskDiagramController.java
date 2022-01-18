package com.dyz.etlcomposer.controller;


import com.alibaba.fastjson.JSON;
import com.dyz.etlcomposer.diagram.context.TaskContext;
import com.dyz.etlcomposer.model.*;
import com.dyz.etlcomposer.utils.Result;
import com.dyz.etlcomposer.utils.ResultGenerator;
import com.dyz.etlcomposer.diagram.execute.Executor;
//import io.swagger.annotations.Api;
import com.dyz.etlcomposer.utils.redis.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
//import javax.xml.soap.Node;
import java.util.*;
import java.util.stream.Collectors;

import static com.dyz.etlcomposer.model.RunTreeNode.constructRunTree;


/**
 * @author dingyinzhao
 * @version 1.0
 * @data 2022/1/10 15:24
 **/
@Slf4j
//@Api(tags = "任务可视化")
@RestController
@RequestMapping("/v2/task/diagram")
@SuppressWarnings("all")
public class TaskDiagramController {
    @Resource
    RedisTemplate<String, Object> redisTemplate;


    String nodesFormat = "%d_%s_nodes";
    String edgesFormat = "%d_%s_edges";
    String executeIdFormat = "%s_%s";
    String allDiagram = "diagram_set";
    String idSeq = "diagram_id";

    @RequestMapping("/saveDiagram")
    public Result saveDiagram(@RequestBody Diagram diagram) {
        // 数据检查1 有且只有一个nodetype 为 start/end
        // 检查要所有的gr节点必有父节点
        List<DiagramNode> startNodes = diagram.nodes.stream().filter(i -> NodeType.NODE_START.equals(i.getNodeType())).collect(Collectors.toList());
        List<DiagramNode> endNodes = diagram.nodes.stream().filter(i -> NodeType.NODE_END.equals(i.getNodeType())).collect(Collectors.toList());
        if (startNodes.size() != 1) {
            return ResultGenerator.successResult("需要能保证只有一个start节点!");
        }
        if (endNodes.size() != 1) {
            return ResultGenerator.successResult("需要能保证只有一个end节点!");
        }

        Long diagramId = redisTemplate.opsForValue().increment(idSeq, 1);
        diagram.setId(diagramId);
        log.info(diagram.getId() + "");
        saveOrUpdate(diagram);
        return ResultGenerator.successResult(diagram);
    }


    @RequestMapping("/updateDiagram")
    public Result updateDiagram(@RequestBody Diagram diagram) {
        // 数据检查1 有且只有一个nodetype 为 start/end
        // 检查要所有的gr节点必有父节点
        List<DiagramNode> startNodes = diagram.nodes.stream().filter(i -> NodeType.NODE_START.equals(i.getNodeType())).collect(Collectors.toList());
        List<DiagramNode> endNodes = diagram.nodes.stream().filter(i -> NodeType.NODE_END.equals(i.getNodeType())).collect(Collectors.toList());
        if (startNodes.size() != 1) {
            return ResultGenerator.successResult("需要能保证只有一个start节点!");
        }
        if (endNodes.size() != 1) {
            return ResultGenerator.successResult("需要能保证只有一个end节点!");
        }

        saveOrUpdate(diagram);
        return ResultGenerator.successResult(diagram);
    }

    @RequestMapping("/queryList")
    public Result queryList() {

        LinkedHashSet<Object> o = (LinkedHashSet<Object>) redisTemplate.opsForSet().members(allDiagram);

        return ResultGenerator.successResult(o);
    }

    @RequestMapping("/readDiagram")

    public Result readDiagram(@RequestBody Diagram diagram) {

        String s = (String) redisTemplate.opsForValue().get(String.format(nodesFormat, diagram.getId(), diagram.getName()));
        String s1 = (String) redisTemplate.opsForValue().get(String.format(edgesFormat, diagram.getId(), diagram.getName()));
        diagram.nodes = JSON.parseArray(s, DiagramNode.class);
        diagram.edges = JSON.parseArray(s1, DiagramEdge.class);
        return ResultGenerator.successResult(diagram);
    }


    @RequestMapping("/runTask")
    public Result runTask(@RequestBody Diagram diagram) {

        String s = (String) redisTemplate.opsForValue().get(String.format(nodesFormat, diagram.getId(), diagram.getName()));
        String s1 = (String) redisTemplate.opsForValue().get(String.format(edgesFormat, diagram.getId(), diagram.getName()));
        diagram.nodes = JSON.parseArray(s, DiagramNode.class);
        diagram.edges = JSON.parseArray(s1, DiagramEdge.class);

        // 生成执行树
        RunTreeNode runTreeNode = constructRunTree(diagram);
        // 为本次执行生成一个id
        String executeId = String.format(executeIdFormat, diagram.getName(), new Date().getTime());
        Executor executor = new Executor(redisTemplate);

        TaskContext.setExecuteID(executeId);


        executor.run(runTreeNode, executeId);

        TaskContext.destroyContext();
        return ResultGenerator.successResult(executeId.toString());
    }

    @RequestMapping("/getStatus")
    public Result getStatus(String executeID) {
        return ResultGenerator.successResult(redisTemplate.opsForList().range(executeID, 0, -1));
    }





    public void saveOrUpdate(Diagram diagram) {
        Diagram small = new Diagram();
        small.setId(diagram.getId());
        small.setName(diagram.getName());
        redisTemplate.opsForSet().add(allDiagram, JSON.toJSONString(small));
        redisTemplate.opsForValue().set(String.format(nodesFormat, diagram.getId(), diagram.getName()), JSON.toJSONString(diagram.nodes));
        redisTemplate.opsForValue().set(String.format(edgesFormat, diagram.getId(), diagram.getName()), JSON.toJSONString(diagram.edges));
    }
}
