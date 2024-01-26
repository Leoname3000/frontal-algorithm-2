import javax.swing.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

public class Task04 {
    LocalDate startingDate;
    Resource res1;
    Operation a1, a2, a3, a4;
    Operation b1;
    HashSet<Lot> allLots;
    FrontalAlgorithm frontalAlgorithm;

    public Task04() {

        startingDate = LocalDate.of(2024, 1, 1);

        // SETTING RESOURCES
        Schedule schedule1 = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(18, 0))
                .build();

//        Schedule schedule2 = new Schedule.ScheduleBuilder()
//                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(14, 0))
//                .addWeek(startingDate, LocalTime.of(15, 0), LocalTime.of(19, 0))
//                .addWeek(startingDate.plusDays(7), LocalTime.of(10, 0), LocalTime.of(14, 0))
//                .addWeek(startingDate.plusDays(7), LocalTime.of(15, 0), LocalTime.of(19, 0))
//                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule1);
        //System.out.println(schedule2);

        res1 = new Resource("res1", schedule1);
        //res2 = new Resource("res2", schedule2);
        Group group = new Group();
        group.add(res1);
        //group.add(res2);

        // SETTING LOTS
        Duration duration = Duration.ofMinutes(0);

        LocalDateTime arrival1 = startingDate.atTime(10, 0);
        LocalDateTime deadline1 = startingDate.plusDays(1).atTime(18, 0);
        Lot lot_a = new Lot("lot_a", arrival1, deadline1, 0);
        a1 = new Operation("a1", group, lot_a, duration.plusHours(2), false);
        a2 = new Operation("a2", group, lot_a, duration.plusHours(2), false);
        a3 = new Operation("a3", group, lot_a, duration.plusHours(2), false);
        a4 = new Operation("a4", group, lot_a, duration.plusHours(2), false);

        lot_a.add(a1);
        lot_a.add(a2);
        lot_a.add(a3);
        lot_a.add(a4);
        Lot.addRelation(a1, a2);
        Lot.addRelation(a2, a3);
        Lot.addRelation(a3, a4);


        LocalDateTime arrival2 = startingDate.atTime(10, 0);
        LocalDateTime deadline2 = startingDate.plusDays(0).atTime(18, 0);
        Lot lot_b = new Lot("lot_b", arrival2, deadline2, 0);
        b1 = new Operation("b1", group, lot_b, duration.plusHours(8), false);

        lot_b.add(b1);


        allLots = new HashSet<>();
        allLots.add(lot_a);
        allLots.add(lot_b);

        frontalAlgorithm = new FrontalAlgorithm(allLots);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var task = new Task04();
            Solution solution = task.frontalAlgorithm.run(new FollowersComparator());
            //Solution solution = task.frontalAlgorithm.iterRun(new SpecialCapacityStrategy(new WeightedComparator(), Duration.ofMinutes(61), 10));
            var outputFrame = new OutputFrame(solution);
            outputFrame.setVisible(true);
        });
    }
}
