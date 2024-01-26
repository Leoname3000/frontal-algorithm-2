import java.util.Comparator;

public class LotPriorityComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        Integer priority1 = o1.lot.priority;
        Integer priority2 = o2.lot.priority;
        int result = priority1.compareTo(priority2) * -1;
        //if (result != 0) return result;
        return result;

//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}
