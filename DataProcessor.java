// DataProcessor.java
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A simple multi-threaded data processing system in Java.
 * It uses a producer-consumer model with a shared queue and worker threads.
 */
public class DataProcessor {

    private static final Logger logger = Logger.getLogger(DataProcessor.class.getName());
    private static final int NUM_WORKERS = 4;
    private static final int NUM_TASKS = 20;

    static {
        // Configure a simple logger format for better output readability
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String stackTrace = "";
                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    stackTrace = "\n" + sw.toString();
                }
                return String.format("[%s] %s: %s%s\n",
                    record.getLevel(),
                    record.getSourceClassName() + "." + record.getSourceMethodName(),
                    record.getMessage(),
                    stackTrace
                );
            }
        });
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    // A simple task class. In a real-world scenario, this could be more complex.
    private static class Task {
        private final int id;

        public Task(int id) {
            this.id = id;
        }


        @Override
        public String toString() {
            return "Task-" + id;
        }
    }

    /**
     * The worker class that processes tasks.
     * Implements Runnable to be executed by the ExecutorService.
     */
    private static class Worker implements Runnable {
        private final BlockingQueue<Task> taskQueue;
        private final ConcurrentLinkedQueue<String> results;
        private final CountDownLatch latch;
        private final int workerId;
        private final AtomicInteger processedCount;

        public Worker(BlockingQueue<Task> taskQueue, ConcurrentLinkedQueue<String> results, CountDownLatch latch, int workerId, AtomicInteger processedCount) {
            this.taskQueue = taskQueue;
            this.results = results;
            this.latch = latch;
            this.workerId = workerId;
            this.processedCount = processedCount;
        }

        @Override
        public void run() {
            logger.info("Worker " + workerId + " starting...");

            try {
                // Loop to process tasks from the queue until the queue is empty
                while (true) {
                    // Poll for a task with a timeout. If the queue is empty, the thread will wait.
                    Task task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task == null) {
                        // If the queue is empty and the main thread has finished producing tasks, break the loop
                        if (processedCount.get() >= NUM_TASKS) {
                            break;
                        }
                        continue;
                    }

                    try {
                        // Simulate computational work with a random delay
                        long delay = (long) (Math.random() * 500);
                        Thread.sleep(delay);

                        // Process the task and add a result to the shared queue
                        String result = "Worker " + workerId + " processed " + task + " in " + delay + "ms.";
                        results.add(result);
                        logger.info(result);
                        processedCount.incrementAndGet();

                    } catch (InterruptedException e) {
                        // Handle thread interruption gracefully
                        Thread.currentThread().interrupt();
                        throw new InterruptedException("Worker " + workerId + " was interrupted while processing task " + task);
                    } catch (Exception e) {
                        // Catch and log any other general exceptions
                        logger.log(Level.SEVERE, "Worker " + workerId + " encountered an error", e);
                    }
                }
            } catch (InterruptedException e) {
                // Handle the InterruptedException for the main queue loop
                logger.log(Level.WARNING, "Worker " + workerId + " was interrupted while waiting for a task.", e);
            } finally {
                logger.info("Worker " + workerId + " finished.");
                latch.countDown(); // Decrement the latch, signaling this worker is done
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Shared queue for tasks. LinkedBlockingQueue is thread-safe and manages synchronization.
        BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
        
        // Shared thread-safe queue for results
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        
        // Latch to wait for all workers to complete
        CountDownLatch latch = new CountDownLatch(NUM_WORKERS);
        
        // Atomic integer to track the number of tasks processed, ensuring proper termination
        AtomicInteger processedCount = new AtomicInteger(0);

        // ExecutorService manages a pool of worker threads
        ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKERS);

        logger.info("Main thread: Populating the task queue with " + NUM_TASKS + " tasks.");
        for (int i = 0; i < NUM_TASKS; i++) {
            taskQueue.add(new Task(i));
        }

        logger.info("Main thread: Starting " + NUM_WORKERS + " worker threads.");
        for (int i = 0; i < NUM_WORKERS; i++) {
            executor.submit(new Worker(taskQueue, results, latch, i + 1, processedCount));
        }

        // Wait for all worker threads to complete their tasks
        logger.info("Main thread: Waiting for all workers to finish...");
        latch.await();

        // Shutdown the executor service after all tasks are done
        executor.shutdown();
        
        logger.info("Main thread: All workers have finished. Final results:");
        results.forEach(logger::info);

        logger.info("Main thread: Program terminated successfully.");
    }
}
