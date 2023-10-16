import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

public class Lot {
    public Lot(LocalDateTime arrival, LocalDateTime deadline, int priority) {
        operations = new HashSet<>();
        this.arrival = arrival;
        this.deadline = deadline;
        this.priority = priority;
        this.discount = Duration.ZERO;
    }
    HashSet<Operation> operations;
    LocalDateTime arrival;
    LocalDateTime deadline;
    int priority;
    Duration discount;
    void add(Operation operation) {
        operations.add(operation);
    }
    static void addRelation(Operation precedent, Operation follower) {
        if (precedent.lot != follower.lot)
            throw new RuntimeException("Relating operations (" + precedent + "and" + follower + ") from different lots!");
        precedent.followers.add(follower);
        follower.precedents.add(precedent);
    }
}
