import java.util.Comparator;

public class FollowersComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        Integer followers1 = o1.countAllFollowers();
        Integer followers2 = o2.countAllFollowers();
        int result = followers1.compareTo(followers2) * -1;
        if (result != 0) return result * 10;

        var nc = new NameComparator();
        return nc.compare(o1, o2);

//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}

