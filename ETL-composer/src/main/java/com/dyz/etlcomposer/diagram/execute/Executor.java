package com.dyz.etlcomposer.diagram.execute;

import com.dyz.etlcomposer.model.RunTreeNode;
import com.dyz.etlcomposer.utils.redis.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
public class Executor {
    RedisTemplate<String, Object> redisTemplate;
    public Executor(RedisTemplate redisTemplate) {
        this.redisTemplate =redisTemplate;
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
        RedisLock redisLock = new RedisLock(redisTemplate, executeId + node.getNode().getName());
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
                log.info(node.getNode().getCode());

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
}
