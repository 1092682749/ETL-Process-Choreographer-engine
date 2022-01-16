package com.dyz.etlcomposer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.dyz.etlcomposer.utils.Result;
import com.dyz.etlcomposer.utils.ResultGenerator;

//import io.swagger.annotations.Api;
import com.dyz.etlcomposer.utils.redis.RedisLock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;


import javax.annotation.Resource;
import javax.xml.soap.Node;
import java.util.*;
import java.util.stream.Collectors;

@Data
class Diagram {
    Long id;
    String name;
    //    @JSONField(serialize = false)
    List<DiagramNode> nodes;
    //    @JSONField(serialize = false)
    List<DiagramEdge> edges;

}

@Data
class DiagramNode {
    String nodeId;
    String code;
    Double x;
    Double y;
    String name;
    String nodeType;
}

@Data
class DiagramEdge {
    String startNodeId;
    String endNodeId;


    DiagramNode startNode;

    DiagramNode endNode;
}

@Data
class RunTreeNode {
    List<RunTreeNode> preNodes = new LinkedList<>();
    List<RunTreeNode> nextNodes = new LinkedList<>();
    DiagramNode node;
}

class NodeType {
    public static String NODE_START = "start";
    public static String NODE_GR = "gr";
    public static String NODE_END = "end";
}

class TaskContext {
    public static ThreadLocal<String> executeID = new ThreadLocal<>();

    public static String getExecuteID() {
        return executeID.get();
    }

    public static void setExecuteID(String executeID) {
        TaskContext.executeID.set(executeID);
    }

    public static void destroyContext() {
        executeID.remove();
    }
}

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

        String s = (String) redisTemplate.opsForValue().get(String.format(nodesFormat, diagram.getId(), diagram.name));
        String s1 = (String) redisTemplate.opsForValue().get(String.format(edgesFormat, diagram.getId(), diagram.name));
        diagram.nodes = JSON.parseArray(s, DiagramNode.class);
        diagram.edges = JSON.parseArray(s1, DiagramEdge.class);
        return ResultGenerator.successResult(diagram);
    }


    @RequestMapping("/runTask")
    public Result runTask(@RequestBody Diagram diagram) {

        String s = (String) redisTemplate.opsForValue().get(String.format(nodesFormat, diagram.getId(), diagram.name));
        String s1 = (String) redisTemplate.opsForValue().get(String.format(edgesFormat, diagram.getId(), diagram.name));
        diagram.nodes = JSON.parseArray(s, DiagramNode.class);
        diagram.edges = JSON.parseArray(s1, DiagramEdge.class);

        // 生成执行树
        RunTreeNode runTreeNode = constructRunTree(diagram);
        // 为本次执行生成一个id
        String executeId = String.format(executeIdFormat, diagram.getName(), new Date().getTime());

        TaskContext.setExecuteID(executeId);


        run(runTreeNode, executeId);

        TaskContext.destroyContext();
        return ResultGenerator.successResult(executeId.toString());
    }

    @RequestMapping("/getStatus")
    public Result getStatus(String executeID) {
        return ResultGenerator.successResult(redisTemplate.opsForList().range(executeID, 0, -1));
    }

    public void run(RunTreeNode node, String executeId) {
        // 广度遍历队列
        Queue<RunTreeNode> executeQueue = new LinkedList<>();


        // 根节点入队
        if (node != null) {
            executeQueue.offer(node);
        }

        while (executeQueue.peek() != null) {
            RunTreeNode pollNode = executeQueue.poll();
            // 这里交给其他线程执行 提升并行度
            new Thread(() -> {
                TaskContext.setExecuteID(executeId);
                executeCode(pollNode, executeId);
            }).start();

            List<RunTreeNode> nextNodes = pollNode.getNextNodes();
            nextNodes.forEach(executeQueue::offer);
        }
    }

    public void executeCode(RunTreeNode node, String executeId) {

        // 使用redis共享锁 锁定executeId+name
        RedisLock redisLock = new RedisLock(redisTemplate, executeId + node.getNode().name);
        boolean islock = redisLock.lock();
        if (!islock) {
            throw new RuntimeException("获取锁一小时尝试失败，请检查任务状态!");
        }
        try {
            boolean completed = false;
            // 检查前置
            while (!completed) {
                completed = checkPre(node, executeId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.info("wait");
                }
            }
            // 单向图 要保证没换个节点 执行一次
            List<Object> complatedList = redisTemplate.opsForList().range(executeId, 0, -1);
            if (!complatedList.contains(node.getNode().getName())) {

                // 模拟真实任务
                log.info(node.getNode().code);

                redisTemplate.opsForList().leftPush(executeId, node.getNode().getName());
            }
        }finally {
            redisLock.unlock();
        }
    }

    public boolean checkPre(RunTreeNode node, String executeId) {
        List<RunTreeNode> preNodes = node.getPreNodes();
        if (preNodes.size() == 0) return true;
        List<Object> complatedList = redisTemplate.opsForList().range(executeId, 0, -1);
        Boolean completed = preNodes.stream().map(i -> complatedList.contains(i.getNode().getName())).reduce((p, n) -> p && n).get();
        return completed;
    }

    public RunTreeNode constructRunTree(Diagram diagram) {
        RunTreeNode runTreeStartNode = new RunTreeNode();
        List<DiagramEdge> edges = diagram.edges;
        List<DiagramNode> nodes = diagram.nodes;
        for (DiagramNode node : nodes) {
            if (node.getNodeType().equals(NodeType.NODE_START)) {
                runTreeStartNode.node = node;
                findNext(runTreeStartNode, nodes, edges);
            }
        }

        return runTreeStartNode;
    }

    public void findNext(RunTreeNode currentNode, List<DiagramNode> nodes, List<DiagramEdge> edges) {
        String currentNodeID = currentNode.getNode().getNodeId();
        List<String> nextNodeIds = edges.stream().filter(edge -> edge.startNodeId.equals(currentNodeID)).map(DiagramEdge::getEndNodeId).collect(Collectors.toList());
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

    public void saveOrUpdate(Diagram diagram) {
        Diagram small = new Diagram();
        small.setId(diagram.getId());
        small.setName(diagram.getName());
        redisTemplate.opsForSet().add(allDiagram, JSON.toJSONString(small));
        redisTemplate.opsForValue().set(String.format(nodesFormat, diagram.getId(), diagram.name), JSON.toJSONString(diagram.nodes));
        redisTemplate.opsForValue().set(String.format(edgesFormat, diagram.getId(), diagram.name), JSON.toJSONString(diagram.edges));
    }
}
