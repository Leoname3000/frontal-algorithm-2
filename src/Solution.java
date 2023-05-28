import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
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
    }
    TreeSet<Assignation> assignations;
    public void assign(Operation operation, LocalDateTime time, Resource resource) {
        assignations.add(new Assignation(operation, time, resource));
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
}
