import java.util.Comparator;

public class LateStartComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        var lateStart1 = o1.lateStart().minus(o1.lot.discount);
        var lateStart2 = o2.lateStart().minus(o2.lot.discount);
        int result = lateStart1.compareTo(lateStart2);
        DeadlineComparator deadlineComparator = new DeadlineComparator();
        return (result != 0 ? result : deadlineComparator.compare(o1, o2));
    }
}

