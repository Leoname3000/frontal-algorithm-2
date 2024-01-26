import java.util.Comparator;

public class DurationComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        int result = o1.duration.compareTo(o2.duration);
        if (result != 0) return result * 10;

        var nc = new NameComparator();
        return nc.compare(o1, o2);

//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}
