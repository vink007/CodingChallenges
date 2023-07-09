import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerConsumer {
    BlockingQueue<Employee> queue;
    Producer producer;
    Consumer consumer;

    public ProducerConsumer() {
        this.queue = new ArrayBlockingQueue<Employee>(10);
        this.producer = new Producer(queue);
        this.consumer = new Consumer(queue);
    }

    public static void main(String[] args) {
        ProducerConsumer producerConsumer = new ProducerConsumer();
        producerConsumer.dance();
    }
    

    public void dance() {
        new Thread(producer).start();
        new Thread(consumer).start();
    }
}


class Producer implements Runnable {
    BlockingQueue<Employee>  queue;

    public Producer(BlockingQueue<Employee> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            Employee e = new Employee(i, i);
            queue.offer(e);
            System.out.println("Added: " + e);
            try {
                Thread.sleep(( long) Math.random() * 100);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}


class Consumer implements Runnable {
    BlockingQueue<Employee>  queue;

    public Consumer(BlockingQueue<Employee> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Employee e = queue.poll(100, TimeUnit.MILLISECONDS);
                if (e == null) {
                    System.out.println("Err. Nothing that I consume has arrived in last 100 millisecond.");
                    break;
                }
                System.out.println("Polled: " + e);
            } catch (InterruptedException e) {

            }
        }
    }
}