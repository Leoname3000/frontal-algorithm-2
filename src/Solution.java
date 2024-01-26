import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.TreeSet;

public class Solution {
    public static class Assignation implements Comparable<Assignation> {
        public Assignation(Operation operation, LocalDateTime time, Resource resource) {
            this.operation = operation;
            this.time = time;
            this.resource = resource;
        }
        Operation operation;
        LocalDateTime time;
        Resource resource;

        @Override
        public int compareTo(Assignation a) {
            int result = time.compareTo(a.time);
            if (result == 0)
                result = resource.compareTo(a.resource);
            if (result == 0)
                result = operation.compareTo(a.operation);
            return result;
        }


//        @Override
//        public boolean equals(Object obj) {
//            if (obj.getClass() != getClass())
//                return false;
//            Assignation evaluated = (Assignation) obj;
//            return operation.equals(evaluated.operation)
//                    && resource.equals(evaluated.resource)
//                    && time.equals(evaluated.time);
//        }

        @Override
        public String toString() {
            return operation + " on " + resource + " at " + time;
        }
    }

    public Solution() {
        this.assignations = new TreeSet<>();
        this.totalPenalty = null;

        this.history = new TreeMap<>();
        this.capacityDrops = new TreeMap<>();
        this.specialCapacityDrops = new TreeMap<>();
    }
    TreeSet<Assignation> assignations;
    public TreeMap<Duration, Lot> penalties;
    private Duration totalPenalty;
    public Duration totalPenalty() {
        if (totalPenalty != null) return totalPenalty;
        var result = Duration.ZERO;
        for (var penalty : penalties.keySet()) {
            if (penalty.isPositive()) {
                result = result.plus(penalty);
            }
        }
        totalPenalty = result;
        return result;
    }

    public void assign(Operation operation, LocalDateTime time, Resource resource) {
        assignations.add(new Assignation(operation, time, resource));

        // Count capacity drop:
        Assignation prevAssign = null;
        for (var a : assignations) {
            if (a.time.isBefore(time) && a.resource.compareTo(resource) == 0 && (prevAssign == null || a.time.isAfter(prevAssign.time))) {
                prevAssign = a;
            }
        }
        if (prevAssign == null) {
            capacityDrops.put(Duration.ZERO, operation);
            specialCapacityDrops.put(Duration.ZERO, operation);
            return;
        }
        var capacityDrop = prevAssign.operation.timeCapacity().minus(operation.timeCapacity());
        if (capacityDrop.isPositive()) {
            capacityDrops.put(capacityDrop, operation);
        } else {
            capacityDrops.put(Duration.ZERO, operation);
        }

        var specialCapacityDrop = prevAssign.operation.specialTimeCapacity().minus(operation.specialTimeCapacity());
        if (specialCapacityDrop.isPositive()) {
            specialCapacityDrops.put(specialCapacityDrop, operation);
        } else {
            specialCapacityDrops.put(Duration.ZERO, operation);
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Assignation assignation : assignations)
            result += assignation + "\n";
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (getClass() != object.getClass())
            return false;
        Solution evaluated = (Solution) object;
        return assignations.equals(evaluated.assignations);
    }

    TreeMap<Resource, TreeMap<Operation, LocalDateTime>> history;
    TreeMap<Duration, Operation> capacityDrops;
    TreeMap<Duration, Operation> specialCapacityDrops;
}
