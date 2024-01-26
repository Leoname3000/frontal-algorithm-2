import java.util.Comparator;

public class DeadlineComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        int result = o1.lot.deadline.compareTo(o2.lot.deadline);
        if (result != 0) return result * 10;

        var nc = new NameComparator();
        return nc.compare(o1, o2);

//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}
