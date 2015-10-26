package com.himoo.ydsc.manager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 线程池对象
 * @author gwm
 *		个人推荐使用该类来替代你的创建线程Thread的操作，
 */
public class ThreadManager {
	private static final int corePoolSize = 3;  //池中所保存的线程数，包括空闲线程。
	private static final int maximumPoolSize = 10;  //池中允许的最大线程数
	private static final int keepAliveTime = 1; //当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，单位(s)
	private static final int workQueue = 5;  //执行前用于保持任务的队列。此队列仅由保持 execute 方法提交的 Runnable 任务。
	
	private static ThreadPoolExecutor exector;
	private static ThreadManager manager = new ThreadManager();
	private ThreadManager() {
		//创建线程池执行器，默认3条，最多10条，等待1秒
		/**
		 * 该段代码的意义：
		 * 	创建一个专门放线程的资源池，该池子中默认有三条线程，最多放10条，如果在1秒子类发现有空闲线程，则销毁空闲线程，最多5个等待线程，如果超出5个线程则超出的部分一直丢，直到该线程可以执行为止
		 * 	详细说明如下：
		 * public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          RejectedExecutionHandler handler)用给定的初始参数和默认的线程工厂创建新的 ThreadPoolExecutor。 
			参数：
				corePoolSize - 池中所保存的线程数，包括空闲线程。
				maximumPoolSize - 池中允许的最大线程数。
				keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
				unit - keepAliveTime 参数的时间单位。
				workQueue - 执行前用于保持任务的队列。此队列仅由保持 execute 方法提交的 Runnable 任务。
				handler - 由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序。 
			抛出： 
				IllegalArgumentException - 如果 corePoolSize 或 keepAliveTime 小于 0，或者 maximumPoolSize 小于等于 0，或者 corePoolSize 大于 maximumPoolSize。 
				NullPointerException - 如果 workQueue 或 handler 为 null。
		 */
		exector = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(workQueue),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	/**
	 * 获取线程池实例
	 * @return
	 */
	public static ThreadManager getInstance() {
		if(exector == null || exector.isShutdown()){
			exector = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
					TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(workQueue),
					new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return manager;
	}
	/**
	 * 执行线程
	 * @param run
	 */
	public void run(Runnable run){
		exector.execute(run);
	}
	
	public void close(){
		exector.shutdown();
	}
}
