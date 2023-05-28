import java.util.Comparator;

public class LateStartComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        int result = o1.lateStart().compareTo(o2.lateStart());
        DeadlineComparator deadlineComparator = new DeadlineComparator();
        return (result != 0 ? result : deadlineComparator.compare(o1, o2));
    }
}

