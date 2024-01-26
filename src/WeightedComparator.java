import java.util.Comparator;

public class WeightedComparator implements Comparator<Operation> {

    double lotPriorityWeight = 3;
    double operationPriorityWeight = 0.6;
    double lateStartWeight = 0.6;
    double deadlineWeight = 0.4;
    double durationWeight = 0.2;
    double followingDurationsWeight = 0.3;
    double followersWeight = 0.2;

    @Override
    public int compare(Operation o1, Operation o2) {
        var lpc = new LotPriorityComparator();
        double lotPriorityResult = lpc.compare(o1, o2) * lotPriorityWeight;

        var opc = new OperationPriorityComparator();
        double operationPriorityResult = opc.compare(o1, o2) * operationPriorityWeight;

        var lsc = new LateStartComparator();
        double lateStartResult = lsc.compare(o1, o2) * lateStartWeight;

        var dc1 = new DeadlineComparator();
        double deadlineResult = dc1.compare(o1, o2) * deadlineWeight;

        var dc2 = new DurationComparator();
        double durationResult = dc2.compare(o1, o2) * durationWeight;

        var fdc = new FollowingDurationsComparator();
        double followingDurationsResult = fdc.compare(o1, o2) * followingDurationsWeight;

        var fc = new FollowersComparator();
        double followersResult = fc.compare(o1, o2) * followersWeight;

        double sumResult = lotPriorityResult + operationPriorityResult + lateStartResult +
                deadlineResult + durationResult + followingDurationsResult + followersResult;

        if (sumResult == 0) {
            var nc = new NameComparator();
            return nc.compare(o1, o2);
        }

        return sumResult > 0 ? 1 : -1;
    }
}
