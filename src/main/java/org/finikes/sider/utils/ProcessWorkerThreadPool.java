package org.finikes.sider.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessWorkerThreadPool {
	private static final int DEFAULT_THREAD_NUMBER = Runtime.getRuntime().availableProcessors() * 2;
	private static ExecutorService EXEC = Executors.newFixedThreadPool(DEFAULT_THREAD_NUMBER);

	public static ExecutorService getExec() {
		return EXEC;
	}

	public static void work(Runnable runnable) {
		EXEC.execute(runnable);
	}

	public static void shutdown(){
		ProcessWorkerThreadPool.EXEC.shutdown();
	}
}
