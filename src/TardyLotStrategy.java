import java.time.Duration;
import java.util.Comparator;

public class TardyLotStrategy implements IStrategy {
    Comparator<Operation> operationComparator;
    int maxIterations;
    Duration discount;

    public TardyLotStrategy(Comparator<Operation> operationComparator, Duration discount, int maxIterations) {
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

        while (maxIterations > 0) {
            for (var lot : frontalAlgorithm.allLots) {
                for (var operation : lot.operations) {
                    operation.reset();
                    for (var resource : operation.group.resources)
                        resource.reset();
                }
            }

            var biggestPenalty = basicSolution.penalties.lastEntry();
            System.out.println("> BIGGEST PENALTY = " + biggestPenalty.getKey());

            var priorityLot = biggestPenalty.getValue();
            for (var operation : priorityLot.operations) {
                operation.priority += 1;
            }

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

            basicSolution = newSolution;
            maxIterations--;
            count++;
        }

        System.out.println("> Закончились попытки - СТОП!");
        //System.out.println("> Итерация " + count);
        return basicSolution;
    }
}
