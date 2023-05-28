import com.sun.source.util.Trees;

import java.util.HashSet;
import java.util.TreeSet;

public class Group {
    public Group() {
        resources = new TreeSet<>();
    }
    final TreeSet<Resource> resources;
    void add(Resource resource) {
        resources.add(resource);
    }
}
