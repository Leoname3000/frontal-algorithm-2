import java.util.Comparator;

public class DeadlineComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        int result = o1.lot.deadline.compareTo(o2.lot.deadline);
        DurationComparator durationComparator = new DurationComparator();
        return (result != 0 ? result : durationComparator.compare(o1, o2));
    }
}
