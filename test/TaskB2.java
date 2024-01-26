import javax.swing.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

public class TaskB2 {
    LocalDate startingDate;
    Resource res1, res2;
    Operation a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;
//    Operation b1, b2, b3, b4, b5, b6, b7;
//    Operation c1, c2, c3, c4;
//    Operation d1, d2, d3;
    HashSet<Lot> allLots;
    FrontalAlgorithm frontalAlgorithm;

    public TaskB2() {

        startingDate = LocalDate.of(2024, 1, 1);

        // SETTING RESOURCES
        Schedule schedule1 = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(15, 0))
                .addManual(startingDate.plusDays(7).atTime(10, 0), startingDate.plusDays(7).atTime(15, 0))
                .build();

        Schedule schedule2 = new Schedule.ScheduleBuilder()
                .addManual(startingDate.plusDays(1).atTime(11, 0), startingDate.plusDays(1).atTime(14, 0))
                .addManual(startingDate.plusDays(2).atTime(11, 0), startingDate.plusDays(2).atTime(14, 0))
                .addWeek(startingDate.plusDays(3), LocalTime.of(11, 0), LocalTime.of(15, 0))
//                .addWeek(startingDate.plusDays(7), LocalTime.of(10, 0), LocalTime.of(14, 0))
//                .addWeek(startingDate.plusDays(7), LocalTime.of(15, 0), LocalTime.of(19, 0))
                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule1);
        System.out.println(schedule2);

        res1 = new Resource("res1", schedule1);
        res2 = new Resource("res2", schedule2);
        Group group = new Group();
        group.add(res1);
        group.add(res2);

        // SETTING LOTS
        Duration duration = Duration.ofMinutes(0);

        LocalDateTime arrival1 = startingDate.atTime(10, 0);
        LocalDateTime deadline1 = startingDate.plusDays(5).atTime(12, 0);
        Lot lot_a = new Lot("lot_a", arrival1, deadline1, 0);
        a1 = new Operation("a1", group, lot_a, duration.plusHours(4), false);
        a2 = new Operation("a2", group, lot_a, duration.plusHours(4), false);
        a3 = new Operation("a3", group, lot_a, duration.plusHours(4), false);
        a4 = new Operation("a4", group, lot_a, duration.plusHours(4), false);
        a5 = new Operation("a5", group, lot_a, duration.plusHours(4), false);
        a6 = new Operation("a6", group, lot_a, duration.plusHours(4), false);
        a7 = new Operation("a7", group, lot_a, duration.plusHours(3), false);
        a8 = new Operation("a8", group, lot_a, duration.plusHours(3), false);
        a9 = new Operation("a9", group, lot_a, duration.plusHours(2), false);
        a10 = new Operation("a10", group, lot_a, duration.plusHours(2), false);
        a11 = new Operation("a11", group, lot_a, duration.plusHours(4), true);

        lot_a.add(a1);
        lot_a.add(a2);
        lot_a.add(a3);
        lot_a.add(a4);
        lot_a.add(a5);
        lot_a.add(a6);
        lot_a.add(a7);
        lot_a.add(a8);
        lot_a.add(a9);
        lot_a.add(a10);
        lot_a.add(a11);
        Lot.addRelation(a1, a5);
        Lot.addRelation(a2, a6);
        Lot.addRelation(a3, a7);
        Lot.addRelation(a4, a8);
        Lot.addRelation(a5, a9);
        Lot.addRelation(a6, a10);
        Lot.addRelation(a7, a11);
        Lot.addRelation(a8, a11);
        Lot.addRelation(a9, a11);
        Lot.addRelation(a10, a11);


//        LocalDateTime arrival2 = startingDate.plusDays(0).atTime(10, 0);
//        LocalDateTime deadline2 = startingDate.plusDays(2).atTime(18, 0);
//        Lot lot_b = new Lot("lot_b", arrival2, deadline2, 0);
//        b1 = new Operation("b1", group, lot_b, duration.plusHours(6), false);
//        b2 = new Operation("b2", group, lot_b, duration.plusHours(6), false);
//        b3 = new Operation("b3", group, lot_b, duration.plusHours(6), false);
//        b4 = new Operation("b4", group, lot_b, duration.plusHours(1), false);
//        b5 = new Operation("b5", group, lot_b, duration.plusHours(2), false);
//        b6 = new Operation("b6", group, lot_b, duration.plusHours(2), false);
//        b7 = new Operation("b7", group, lot_b, duration.plusHours(2), false);

//        lot_b.add(b1);
//        lot_b.add(b2);
//        lot_b.add(b3);
//        lot_b.add(b4);
//        lot_b.add(b5);
//        lot_b.add(b6);
//        lot_b.add(b7);
//        Lot.addRelation(b1, b3);
//        Lot.addRelation(b2, b3);
//        Lot.addRelation(b1, b4);
//        Lot.addRelation(b2, b5);
//        Lot.addRelation(b3, b6);
//        Lot.addRelation(b3, b7);


//        LocalDateTime arrival3 = startingDate.plusDays(2).atTime(10, 0);
//        LocalDateTime deadline3 = startingDate.plusDays(3).atTime(19, 0);
//        Lot lot_c = new Lot("lot_c", arrival3, deadline3, 0);
//        c1 = new Operation("c1", group, lot_c, duration.plusHours(1), false);
//        c2 = new Operation("c2", group, lot_c, duration.plusHours(1), false);
//        c3 = new Operation("c3", group, lot_c, duration.plusHours(1), false);
//        c4 = new Operation("c4", group, lot_c, duration.plusHours(1), false);
//        c5 = new Operation("c5", group, lot_c, duration.plusHours(5), false);
//        c6 = new Operation("c6", group, lot_c, duration.plusHours(3), false);
//        c7 = new Operation("c7", group, lot_c, duration.plusHours(5), false);

//        lot_c.add(c1);
//        lot_c.add(c2);
//        lot_c.add(c3);
//        lot_c.add(c4);
//        lot_c.add(c5);
//        lot_c.add(c6);
//        lot_c.add(c7);
//        Lot.addRelation(c1, c2);
//        Lot.addRelation(c1, c3);
//        Lot.addRelation(c1, c4);
//        Lot.addRelation(c2, c5);
//        Lot.addRelation(c3, c6);
//        Lot.addRelation(c3, c7);


//        LocalDateTime arrival4 = startingDate.atTime(10, 0);
//        LocalDateTime deadline4 = startingDate.plusDays(2).atTime(19, 0);
//        Lot lot_d = new Lot("lot_d", arrival4, deadline4, 0);
//        d1 = new Operation("d1", group, lot_d, duration.plusHours(9), false);
//        d2 = new Operation("d2", group, lot_d, duration.plusHours(9), false);
//        d3 = new Operation("d3", group, lot_d, duration.plusHours(9), false);
//        d4 = new Operation("d4", group, lot_d, duration.plusHours(5), false);
//        d5 = new Operation("d5", group, lot_d, duration.plusHours(5), false);
//        d6 = new Operation("d6", group, lot_d, duration.plusHours(4), false);
//        d7 = new Operation("d7", group, lot_d, duration.plusHours(6), false);

//        lot_d.add(d1);
//        lot_d.add(d2);
//        lot_d.add(d3);
//        lot_d.add(d4);
//        lot_d.add(d5);
//        lot_d.add(d6);
//        lot_d.add(d7);
//        Lot.addRelation(d1, d2);
//        Lot.addRelation(d2, d3);
//        Lot.addRelation(d2, d4);
//        Lot.addRelation(d2, d5);
//        Lot.addRelation(d3, d6);
//        Lot.addRelation(d3, d7);


        allLots = new HashSet<>();
        allLots.add(lot_a);
//        allLots.add(lot_b);
//        allLots.add(lot_c);
//        allLots.add(lot_d);

        frontalAlgorithm = new FrontalAlgorithm(allLots);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var task = new TaskB2();
            //Solution solution = task.frontalAlgorithm.run(new TrueCombinedComparator());
            Solution solution = task.frontalAlgorithm.iterRun(new TimeCapacityStrategy(new TrueCombinedComparator(), Duration.ofMinutes(61), 10));
            var outputFrame = new OutputFrame(solution);
            outputFrame.setVisible(true);
        });
    }
}
