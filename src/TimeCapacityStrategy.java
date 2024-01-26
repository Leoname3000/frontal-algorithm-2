import java.awt.*;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

public class TimeCapacityStrategy implements IStrategy {

    Comparator<Operation> operationComparator;
    int maxIterations;
    Duration discount;

    public TimeCapacityStrategy(Comparator<Operation> operationComparator, Duration discount, int maxIterations) {
        this.operationComparator = operationComparator;
        this.maxIterations = maxIterations;
        this.discount = discount;
    }
    @Override
    public Solution iterativeRun(FrontalAlgorithm frontalAlgorithm) {
        Solution basicSolution = frontalAlgorithm.run(operationComparator);
        if (!basicSolution.totalPenalty().isPositive()) {
            System.out.println("> Basic solution is acceptable - STOP!");
            return basicSolution;
        }

        int count = 1;
        LinkedList<Duration> penaltyHistory = new LinkedList<>();

        while (maxIterations > 0) {
            for (var lot : frontalAlgorithm.allLots) {
                for (var operation : lot.operations) {
                    operation.reset();
                    for (var resource : operation.group.resources)
                        resource.reset();
                }
            }

            var tardylotEntry = basicSolution.penalties.lastEntry();
            //System.out.println("> HIGHEST DROP = " + highestDrop.getKey());

//            var priorOperation = highestDrop.getValue();
//            priorOperation.priority += 1;
//            priorOperation.prioritizeFollowers();

            var operationDrops = new TreeMap<Operation, Duration>();

            for (var ass : basicSolution.assignations) {
                if (ass.operation.lot.equals(tardylotEntry.getValue())) {
                    var drop = Duration.between(ass.operation.earlyStart(), ass.time);
                    operationDrops.put(ass.operation, drop);
                    System.out.println("Drop for " + ass.operation + " is " + drop);
                }
            }
            System.out.println();

            var deltaDrops = new TreeMap<Operation, Duration>();

            for (var operation : operationDrops.keySet()) {
                var maxPrecedentDrop = Duration.ZERO;
                for (var precedent : operation.precedents) {
                    var precedentDrop = operationDrops.get(precedent);
                    if (precedentDrop.compareTo(maxPrecedentDrop) > 0) {
                        maxPrecedentDrop = precedentDrop;
                    }
                }
                var deltaDrop = operationDrops.get(operation).minus(maxPrecedentDrop);
                System.out.println("DeltaDrop for " + operation + " is " + deltaDrop + " = " + operationDrops.get(operation) + " - " + maxPrecedentDrop);
                deltaDrops.put(operation, deltaDrop);
            }
            System.out.println();

            var maxDelta = Duration.ZERO;
            for (var operation : deltaDrops.keySet()) {
                var operationDelta = deltaDrops.get(operation);
                if (operationDelta.compareTo(maxDelta) > 0) {
                    maxDelta = operationDelta;
                }
            }

            Operation causeOperation = null;
            for (var ass : basicSolution.assignations) {
                if (ass.operation.lot.equals(tardylotEntry.getValue())) {
                    var operatoinDelta = deltaDrops.get(ass.operation);
                    if (operatoinDelta.equals(maxDelta)) {
                        causeOperation = ass.operation;
                        break;
                    }
                }
            }
            if (causeOperation == null)
                throw new RuntimeException("Tardiness cause operation is null!");

            causeOperation.priority += 1;
            causeOperation.prioritizePrecedents();

            var newSolution = frontalAlgorithm.run(operationComparator);

            // DEBUG OUTPUT
            for (var lot : frontalAlgorithm.allLots) {
                for (var operation : lot.operations) {
                    System.out.println("Operation " + operation + " priority = " + operation.priority);
                }
            }

            if (newSolution.totalPenalty().compareTo(basicSolution.totalPenalty()) > 0) {
                System.out.println("> Previous solution was better - STOP!");
                System.out.println("> Итерация " + count);
                return basicSolution;
            }
            if (!newSolution.totalPenalty().isPositive()) {
                System.out.println("> Got acceptable solution - STOP!");
                System.out.println("> Итерация " + count);
                return newSolution;
            }

            //System.out.println("> Получено решение лучше - ПРОДОЛЖАЕМ!");
            System.out.println("> Итерация " + count);

            penaltyHistory.add(newSolution.totalPenalty());
            basicSolution = newSolution;
            maxIterations--;
            count++;
        }

        System.out.println("> Закончились попытки - СТОП!");
        //System.out.println("> Итерация " + count);

        System.out.println("\nHISTORY REPORT:");
        for (int i = 0; i < penaltyHistory.size(); i++) {
            System.out.println("Итер " + (i+1) + " -> " + penaltyHistory.get(i));
        }

        return basicSolution;
    }
}
