package com.dyz.etlcomposer.diagram.context;

public class TaskContext {
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
