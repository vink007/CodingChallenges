import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

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
