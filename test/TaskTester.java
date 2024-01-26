import java.util.LinkedList;

public class TaskTester {

    public static void main(String[] args) {

        var task = new Task01();
        var results = new LinkedList<Solution>();

        results.add(task.frontalAlgorithm.run(new FollowersComparator()));
        for (var lot : task.frontalAlgorithm.allLots) {
            for (var operation : lot.operations) {
                operation.reset();
                for (var resource : operation.group.resources)
                    resource.reset();
            }
        }

        results.add(task.frontalAlgorithm.run(new FollowingDurationsComparator()));
        for (var lot : task.frontalAlgorithm.allLots) {
            for (var operation : lot.operations) {
                operation.reset();
                for (var resource : operation.group.resources)
                    resource.reset();
            }
        }

        results.add(task.frontalAlgorithm.run(new DurationComparator()));
        for (var lot : task.frontalAlgorithm.allLots) {
            for (var operation : lot.operations) {
                operation.reset();
                for (var resource : operation.group.resources)
                    resource.reset();
            }
        }

        results.add(task.frontalAlgorithm.run(new DeadlineComparator()));
        for (var lot : task.frontalAlgorithm.allLots) {
            for (var operation : lot.operations) {
                operation.reset();
                for (var resource : operation.group.resources)
                    resource.reset();
            }
        }

        results.add(task.frontalAlgorithm.run(new LateStartComparator()));
        for (var lot : task.frontalAlgorithm.allLots) {
            for (var operation : lot.operations) {
                operation.reset();
                for (var resource : operation.group.resources)
                    resource.reset();
            }
        }

        System.out.println("Follower: " + results.get(0).totalPenalty());
        System.out.println("FollDurs: " + results.get(1).totalPenalty());
        System.out.println("Duration: " + results.get(2).totalPenalty());
        System.out.println("Deadline: " + results.get(3).totalPenalty());
        System.out.println("LateStart: " + results.get(4).totalPenalty());
    }
}
