import java.util.Comparator;

public class NameComparator implements Comparator<Operation> {
    @Override
    public int compare(Operation o1, Operation o2) {
        int result = o1.name.compareTo(o2.name);
        //if (result != 0) return result;
        return result;

//        Integer hash1 = o1.hashCode();
//        Integer hash2 = o2.hashCode();
//        return hash1.compareTo(hash2);
    }
}
