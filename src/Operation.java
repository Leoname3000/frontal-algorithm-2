import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;

public class Operation implements Comparable<Operation> {
    public Operation(String name, Group group, Lot lot, Duration duration, boolean interruptable) {
        this.name = name;
        this.group = group;
        this.lot = lot;
        followers = new HashSet<>();
        precedents = new HashSet<>();
        this.duration = duration;
        this.interruptable = interruptable;
        this.endOfService = null;
    }
    final String name;
    final Group group;
    final Lot lot;
    final HashSet<Operation> followers;
    final HashSet<Operation> precedents;
    final Duration duration;
    final boolean interruptable;
    LocalDateTime endOfService;

    void addFollower(Operation operation) {
        followers.add(operation);
    }
    void addPrecedent(Operation operation) {
        precedents.add(operation);
    }

    LocalDateTime lateStart() {
        if (followers.isEmpty())
            return lot.deadline.minus(duration);
        else {
            LocalDateTime minLateStart = null;
            for (Operation follower : followers) {
                LocalDateTime lateStart = follower.lateStart();
                if (minLateStart == null || lateStart.isBefore(minLateStart))
                    minLateStart = lateStart;
            }
            return minLateStart.minus(duration);
        }
    }

    @Override
    public int compareTo(Operation o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
