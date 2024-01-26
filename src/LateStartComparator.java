import java.util.Comparator;

public class LateStartComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        // TODO: contains discount policy!
//        var lateStart1 = o1.lateStart().minus(o1.lot.discount);
//        var lateStart2 = o2.lateStart().minus(o2.lot.discount);
//        int result = lateStart1.compareTo(lateStart2);
//        if (result != 0) return result;

        var lateStart1 = o1.lateStart();
        var lateStart2 = o2.lateStart();
        int result = lateStart1.compareTo(lateStart2);
        if (result != 0) return result * 10;

        var nc = new NameComparator();
        return nc.compare(o1, o2);

//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}

