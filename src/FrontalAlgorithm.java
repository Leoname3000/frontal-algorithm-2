import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class FrontalAlgorithm {

    public FrontalAlgorithm(HashSet<Lot> allLots) {
        this.allLots = allLots;
        //this.allResources = allResources; // <-- Needed only to determine 'endOfService'!
    }
    HashSet<Lot> allLots;

    public static Resource pickResource(LocalDateTime frontTime, Operation operation) {
        TreeSet<Resource> resourceGroup = operation.group.resources;
        Resource minResource = null;
        for (Resource res : resourceGroup) {
            var lockTime = res.requestLockTime(frontTime, operation);
            if ((minResource == null && lockTime != null) || (lockTime != null && minResource.requestLockTime(frontTime, operation) != null && lockTime.isBefore(minResource.requestLockTime(frontTime, operation)))) {
                minResource = res;
            }
        }
        if (minResource == null) throw new RuntimeException("Ни один из ресурсов не смог назначить " + operation + " ни на один из своих интервалов");
        return minResource;
    }

    public Solution iterRun(IStrategy strategy) {
        return strategy.iterativeRun(this);
    }

    public Solution run(Comparator<Operation> operationComparator) {
        Solution solution = new Solution();

        // Frontal Algorithm
        // FA step 1 (Setting initial Fronts)
        TreeMap<LocalDateTime, TreeSet<Operation>> fronts = new TreeMap<>();
        for (Lot lot : allLots) {
            for (Operation op : lot.operations) {
                if (op.precedents.isEmpty()) {
                    TreeSet<Operation> operations = fronts.get(lot.arrival);
                    if (operations == null || operations.isEmpty())
                        operations = new TreeSet<>(operationComparator);
                    operations.add(op);
                    fronts.put(lot.arrival, operations); // Redundant?
                }
            }
            System.out.println("Fronts: " + fronts);
        }
        System.out.println("------------");

        // FA step 2
        while (!fronts.isEmpty()) {
            // FA step 3
            System.out.println("\nFronts: " + fronts);
            TreeSet<Operation> front = fronts.firstEntry().getValue();
            LocalDateTime frontTime = fronts.firstEntry().getKey();
            System.out.println("front: " + front + ", frontTime: " + frontTime);
            Operation operation = front.first();
            Resource resource = pickResource(frontTime, operation);
            LocalDateTime minResTime = resource.requestLockTime(frontTime, operation);
            //System.out.println(operation + " -> " + resource + ", minResTime: " + minResTime);
            // What if all resources in group are busy? (held in else)

//            if (!minResTime.equals(frontTime) && !operation.interruptable) {
//                TreeSet<Operation> interruptables = new TreeSet<>(operationComparator);
//                for (Operation op : front)
//                    if (op.interruptable)
//                        interruptables.add(op);
//                if (!interruptables.isEmpty()) {
//                    operation = interruptables.first();
//                    resource = pickResource(frontTime, operation);
//                    minResTime = resource.requestLockTime(frontTime, operation);
//                    System.out.println("REPICKED " + resource + " for " + operation + ", minResTime: " + minResTime);
//                }
//            }

            // FA step 4 TODO: Assure "IsAfter" is needed
            if (minResTime != null && minResTime.equals(frontTime) /* || frontTime.isBefore(minResTime)*/ ) {
                // FA step 5
                LocalDateTime lockTime = resource.lock(frontTime, operation);
                solution.assign(operation, minResTime, resource);
                operation.endOfService = resource.availableTime;
                front.remove(operation);
                System.out.println("Assigned " + operation + " on " + resource + " at " + minResTime);

                for (Operation follower : operation.followers) {
                    boolean allPrecedentsServed = true;
                    LocalDateTime maxEndOfService = operation.endOfService;
                    for (Operation precedent : follower.precedents) {
                        if (precedent.endOfService == null) {
                            allPrecedentsServed = false;
                            break;
                        }
                        else if (precedent.endOfService.isAfter(maxEndOfService)) {
                            maxEndOfService = precedent.endOfService;
                        }
                    }
                    if (allPrecedentsServed) {
                        System.out.println("> Follower " + follower + " can be added!");
                        TreeSet<Operation> frontForFollower = fronts.get(maxEndOfService);
                        if (frontForFollower == null || frontForFollower.isEmpty()) {
                            frontForFollower = new TreeSet<>(operationComparator);
                        }
                        frontForFollower.add(follower);
                        fronts.put(maxEndOfService, frontForFollower); // Can be simplified?
                    } else {
                        System.out.println("> Follower " + follower + " CAN'T be added yet!");
                    }
                }
//
//                // Add operation.duration to frontTime
//                if (!nextOperations.isEmpty()) {
//                    //LocalDateTime createdFrontTime = resource.availableTime; //frontTime.plus(operation.duration);
//                    if (fronts.get(createdFrontTime) == null || fronts.get(createdFrontTime).isEmpty())
//                        fronts.put(createdFrontTime, new TreeSet<>(operationComparator));
//
//                    TreeSet<Operation> ops = fronts.get(createdFrontTime);
//                    ops.addAll(nextOperations);
//                    fronts.put(createdFrontTime, ops);
//                }

                if (front.isEmpty())
                    fronts.remove(frontTime);

                // Recount values due to assignment of current 'operation'
//                operation = front.first();
//                resource = pickResource(operation);
//                minResTime = resource.requestLockTime(operation);
            }
            //TODO: this 'else' should execute any way? (NO, should not if 'if' above worked, because 'else' will execute on the next iteration anyways)
            else if (minResTime != null && minResTime.isAfter(frontTime)) {
                // Continuation of FA step 4

                TreeSet<Operation> nextFront = fronts.get(minResTime);
                if (nextFront == null || nextFront.isEmpty())
                    nextFront = new TreeSet<>(operationComparator);

                nextFront.add(operation);

//                if (fronts.containsKey(minResTime)) {
//                    var f = fronts.get(minResTime);
//                    f.add(operation);
//                    fronts.put(minResTime, f);
//                }
                fronts.put(minResTime, nextFront); // Can be simplified?
                front.remove(operation);

                System.out.println("Moved " + operation + " (" + resource + ", minResTime: " + minResTime + ")");

                if (front.isEmpty())
                    fronts.remove(frontTime);
            }
            else {
                //System.out.println(resource + " не смог назначить " + operation + " в ходе работы алгоритма");
                throw new RuntimeException("FrontTime is after minResTime!");
            }
        }
        // FA exit
        System.out.println("\nSOLUTION\n" + solution);

        // endOfWork output
        LocalDateTime endOfWork = null;
        for (Lot lot : allLots)
            for (Operation operation : lot.operations)
                for (Resource resource : operation.group.resources)
                    if (endOfWork == null || resource.availableTime.isAfter(endOfWork))
                        endOfWork = resource.availableTime;
        System.out.println("END OF WORK\n" + endOfWork + "\n");

        // NEW! Penalty calculation:
        var penalties = new TreeMap<Duration, Lot>();
        for (Lot lot : allLots) {
            Operation last = null;
            for (Operation operation : lot.operations) {
                if (last == null || last.endOfService.isBefore(operation.endOfService)) {
                    last = operation;
                }
            }
            assert last != null;
            if (last.endOfService.isAfter(lot.deadline)) {
                System.out.println(lot.name + " missed deadline! Late by " + Duration.between(lot.deadline, last.endOfService));
                penalties.put(Duration.between(lot.deadline, last.endOfService), lot);
            }
            else {
                System.out.println(lot.name + " processed on time! Early by " + Duration.between(last.endOfService, lot.deadline));
                penalties.put(Duration.ZERO, lot);
            }
        }
        solution.penalties = penalties;
        Duration totalPenalty = solution.totalPenalty();
        if (totalPenalty.isPositive()) {
            System.out.println("Total penalty is " + totalPenalty);
        }
        else {
            System.out.println("No penalties!");
        }

        System.out.println("\n***************\n");

        return solution;
    }
}
