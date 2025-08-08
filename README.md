Multi-threaded Data Processing System
This repository contains an implementation of a multi-threaded data processing system in Java. This system simulates a producer-consumer model where multiple worker threads/goroutines retrieve tasks from a shared queue, process them, and store the results.

The primary goal of this project is to demonstrate and compare different concurrency models and error-handling techniques in Java and Go.

Java Implementation
Overview
The Java implementation uses a traditional thread-based concurrency model. It leverages classes from the java.util.concurrent package to manage threads and shared resources, ensuring thread safety and efficient execution.

Key Concepts
Concurrency:

ExecutorService: A fixed thread pool is used to manage a set of worker threads, efficiently handling their lifecycle.

LinkedBlockingQueue: This serves as the shared, thread-safe task queue. It automatically handles synchronization, blocking a worker thread if the queue is empty (poll with timeout).

ConcurrentLinkedQueue: A thread-safe collection is used for storing the processed results, preventing race conditions.

CountDownLatch: The main thread uses a CountDownLatch to wait for all worker threads to signal that they have completed their tasks, ensuring a graceful shutdown.

Error Handling:

The system uses try-catch blocks to handle potential exceptions like InterruptedException and other runtime errors, logging the details using a simple Logger implementation.

How to Run on macOS
Ensure you have the Java Development Kit (JDK) installed. You can check by running java -version in your Terminal.

Save the Java code as DataProcessor.java.

Open Terminal and navigate to the directory where the file is saved.

Compile the code: javac DataProcessor.java

Run the program: java DataProcessor

