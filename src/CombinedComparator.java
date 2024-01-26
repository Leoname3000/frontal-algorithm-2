import java.util.Comparator;

public class CombinedComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        var lpc = new LotPriorityComparator();
        if (lpc.compare(o1, o2) != 0) return lpc.compare(o1, o2);

        var opc = new OperationPriorityComparator();
        if (opc.compare(o1, o2) != 0) return opc.compare(o1, o2);

        var lsc = new LateStartComparator();
        if (lsc.compare(o1, o2) != 0) return lsc.compare(o1, o2);

        var dc1 = new DeadlineComparator();
        if (dc1.compare(o1, o2) != 0) return dc1.compare(o1, o2);

        var dc2 = new DurationComparator();
        if (dc2.compare(o1, o2) != 0) return dc2.compare(o1, o2);

        var fdc = new FollowingDurationsComparator();
        if (fdc.compare(o1, o2) != 0) return fdc.compare(o1, o2);

        var fc = new FollowersComparator();
        if (fc.compare(o1, o2) != 0) return fc.compare(o1, o2);

        var nc = new NameComparator();
        return nc.compare(o1, o2);

        // TODO: специально хотим ошибку при nc.compare == 0, т.к. не должно быть операций с одинаковым именем
        // TODO: поэтому не используем сравнение хэшей
//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}
