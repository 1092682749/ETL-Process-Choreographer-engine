package com.dyz.etlcomposer.diagram.execute;

import com.dyz.etlcomposer.diagram.context.TaskContext;
import com.dyz.etlcomposer.model.Diagram;
import com.dyz.etlcomposer.model.RunTreeNode;
import com.dyz.etlcomposer.utils.redis.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.dyz.etlcomposer.utils.Constants.executeIdFormat;

@Slf4j
public abstract class Executor {
    RedisTemplate<String, Object> redisTemplate;
    Diagram diagram;

    public Executor(RedisTemplate redisTemplate, Diagram diagram) {
        this.redisTemplate = redisTemplate;
        this.diagram = diagram;
    }
    public void run() {
        // 构造执行树
        RunTreeNode runTreeNode = RunTreeNode.constructRunTree(diagram);
        run(runTreeNode);
    }
    private void run(RunTreeNode node) {
        // 生成执行id
        String executeId = String.format(executeIdFormat, diagram.getName(), new Date().getTime());
        TaskContext.setExecuteID(executeId);
        // 广度遍历队列
        Queue<RunTreeNode> executeQueue = new LinkedList<>();

        // 根节点入队
        if (node != null) {
            executeQueue.offer(node);
        }

        while (executeQueue.peek() != null) {
            RunTreeNode pollNode = executeQueue.poll();
            // 这里交给其他线程执行 提升并行度
            FutureTask<Boolean> task = new FutureTask<Boolean>(() -> {
                TaskContext.setExecuteID(executeId);
                executeCode(pollNode, executeId);
                return true;
            });
            new Thread(task).start();
            while (true) {
                try {
                    if (task.get()) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            List<RunTreeNode> nextNodes = pollNode.getNextNodes();
            nextNodes.forEach(executeQueue::offer);
        }
        TaskContext.destroyContext();
    }

    // 抽象成接口方法
    public void executeCode(RunTreeNode node, String executeId) {

        // 使用redis共享锁 锁定executeId+name
        RedisLock redisLock = new RedisLock(redisTemplate, executeId + node.getNode().getName());
        boolean islock = redisLock.lock();
//        log.info("redis lock" + redisLock.getLockName());
        if (!islock) {
            throw new RuntimeException("获取锁一小时尝试失败，请检查任务状态!");
        }
        try {

            boolean completed = checkPre(node, executeId);

            // 如果前置没有完成 就不再继续执行
            if (!completed) return;
            // 单向图 要保证没换个节点 执行一次
            List<Object> complatedList = redisTemplate.opsForList().range(executeId, 0, -1);
            if (!complatedList.contains(node.getNode().getName())) {

//                log.info(node.getNode().getCode());
                execute(node.getNode().getName(), node.getNode().getCode());

                redisTemplate.opsForList().leftPush(executeId, node.getNode().getName());
            }
        } finally {
            redisLock.unlock();
//            log.info("redis lock release" + redisLock.getLockName());
        }
    }

    public boolean checkPre(RunTreeNode node, String executeId) {
        List<RunTreeNode> preNodes = node.getPreNodes();

        if (preNodes.size() == 0) return true;
        List<Object> complatedList = redisTemplate.opsForList().range(executeId, 0, -1);
        log.info("check node {}, result {}", node.getNode().getName(), complatedList);
        Boolean completed = preNodes.stream().map(i -> complatedList.contains(i.getNode().getName())).reduce((p, n) -> p && n).get();
        return completed;
    }

    public abstract Object execute(String taskName, String code);
}
