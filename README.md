# Multi-threaded Data Processing System (Java)

This project contains an implementation of a multi-threaded data processing system in **Java**. The system simulates a producer-consumer model where multiple worker threads retrieve tasks from a shared queue, process them, and store the results.

### Overview

The Java implementation uses a traditional thread-based concurrency model. It leverages classes from the `java.util.concurrent` package to manage threads and shared resources, ensuring thread safety and efficient execution.

### Key Concepts

* **Concurrency:**
    * **`ExecutorService`**: A fixed thread pool is used to manage a set of worker threads, efficiently handling their lifecycle.
    * **`LinkedBlockingQueue`**: This serves as the shared, thread-safe task queue. It automatically handles synchronization, blocking a worker thread if the queue is empty (`poll` with timeout).
    * **`ConcurrentLinkedQueue`**: A thread-safe collection is used for storing the processed results, preventing race conditions.
    * **`CountDownLatch`**: The main thread uses a `CountDownLatch` to wait for all worker threads to signal that they have completed their tasks, ensuring a graceful shutdown.

* **Error Handling:**
    * The system uses **`try-catch` blocks** to handle potential exceptions like `InterruptedException` and other runtime errors, logging the details using a simple `Logger` implementation.

### How to Run on macOS

1.  Ensure you have the Java Development Kit (JDK) installed. You can check by running `java -version` in your Terminal.
2.  Save the Java code as `DataProcessor.java`.
3.  Open Terminal and navigate to the directory where the file is saved.
4.  Compile the code: `javac DataProcessor.java`
5.  Run the program: `java DataProcessor`
