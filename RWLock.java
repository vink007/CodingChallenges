public class RWLock {
    private boolean isWriting = false;
    private int writerWaiting  = 0;
    private int reader = 0;

    public synchronized void acquireReadLock() throws InterruptedException {
        while (isWriting == true || writerWaiting > 0) {
            wait();
        }
        reader++;
    }
    public synchronized void releaseReadLock() {
        reader--;
        notifyAll();
    }
    public synchronized void acquireWriteLock() throws InterruptedException {
        writerWaiting++;
        while (isWriting == true || reader > 0) {
            wait();
        }
        isWriting = true;
        writerWaiting--;
    }

    public synchronized void releaseWriteLock() {
        isWriting = false;
        notifyAll();
    }

    public static void main(String[] args) throws InterruptedException {
        RWLock rwLock = new RWLock();
        Thread rThread = new Thread(new ReaderThread(rwLock));
        Thread wThread = new Thread(new WriterThread(rwLock));
        Thread rThread2 = new Thread(new ReaderThread(rwLock));
        Thread wThread2 = new Thread(new WriterThread(rwLock));

        wThread.start();
        rThread.start();
        rThread2.start();
        wThread2.start();

        rThread.join();
        wThread.join();
        rThread2.join();
        wThread2.join();
    }
}

class ReaderThread implements Runnable {
    private RWLock rwLock;

    public ReaderThread(RWLock rwLock) {
        this.rwLock = rwLock;
    }

    @Override
    public void run() {
        try {
            this.rwLock.acquireReadLock();
            System.out.println("[" + Thread.currentThread().getId() + "] [" + System.currentTimeMillis()/1000 + "] Reading ...");
            Thread.sleep(1000);
            System.out.println("[" + Thread.currentThread().getId() + "] [" + System.currentTimeMillis()/1000 + "] Done Reading ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.rwLock.releaseReadLock();
        }
    }
}

class WriterThread implements Runnable {
    private RWLock rwLock;

    public WriterThread(RWLock rwLock) {
        this.rwLock = rwLock;
    }

    @Override
    public void run() {
        try {
            this.rwLock.acquireWriteLock();
            System.out.println("[" + Thread.currentThread().getId() + "] [" + System.currentTimeMillis()/1000 + "] Writing ...");
            Thread.sleep(2000);
            System.out.println("[" + Thread.currentThread().getId() + "] [" + System.currentTimeMillis()/1000 + "] Done Writing ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
             this.rwLock.releaseWriteLock();
        }
    }
}

