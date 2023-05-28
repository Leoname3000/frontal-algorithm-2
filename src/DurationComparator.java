import java.util.Comparator;

public class DurationComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        int result = o1.duration.compareTo(o2.duration);
        return (result != 0 ? result : o1.compareTo(o2));
    }
}
