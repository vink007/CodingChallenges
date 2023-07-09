import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter();
        UUID clientId1 = UUID.randomUUID();
        UUID clientId2 = UUID.randomUUID();
        for (int i = 0; i <300; i++) {
            UUID client = (i % 2) == 0 ? clientId1 : clientId2;
            System.out.println(String.format("[%d][%s][%d] allowed = %s", i, clientId2, System.currentTimeMillis(), rateLimiter.isAllowed(client)));
        }
        Thread.sleep(1000L);
        for (int i = 0; i <200; i++) {
            UUID client = (i % 2) == 0 ? clientId1 : clientId2;
            System.out.println(String.format("[%d][%s][%d] allowed = %s", i, clientId2, System.currentTimeMillis(), rateLimiter.isAllowed(client)));
        }
    }

    // Caller/ ClienId to hits map
    private Map<UUID, Hits> hits;

    public RateLimiter() {
        hits = new ConcurrentHashMap();
    }

    public boolean isAllowed(UUID clientId) {
        if (hits.containsKey(clientId) ) {
            return hits.get(clientId).isAllowed();
        }
        hits.put(clientId, new Hits());
        hits.get(clientId).isAllowed();
        return true;
    }
}

class Hits {
    private long rps;
    private long milliSeconds;
    private Queue<Long> history;

    public Hits() {
        this.rps = 100L;
        this.milliSeconds = 1000L;
        this.history = new LinkedList<>();
    }

    public boolean isAllowed() {
        long currentTime = System.currentTimeMillis();
        while(!history.isEmpty() && history.peek() <= currentTime - milliSeconds) {
            history.poll();
        }

        if (history.size() < this.rps) {
            history.add(currentTime);
            return true;
        }
        return false;
    }
}
