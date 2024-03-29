import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

public class Res1Lot3 {

    LocalDate startingDate;
    Resource res;
    Operation op11, op12, op13, op14, op15;
    Operation op21, op22, op23, op24, op25;
    Operation op31, op32, op33, op34, op35, op36;
    FrontalAlgorithm frontalAlgorithm;

    public Res1Lot3() {

        startingDate = LocalDate.of(2023, 2, 20);

        // SETTING RESOURCE
        Schedule schedule = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(17, 0))
                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule);

        res = new Resource("res", schedule);
        Group group = new Group();
        group.add(res);

        // SETTING LOTS
        Duration duration = Duration.ofMinutes(0);

        LocalDateTime arrival1 = startingDate.plusDays(2).atTime(11, 0);
        LocalDateTime deadline1 = startingDate.plusDays(6).atTime(14, 0);
        Lot lot1 = new Lot(arrival1, deadline1, 0);
        op11 = new Operation("op11", group, lot1, duration.plusHours(2), false);
        op12 = new Operation("op12", group, lot1, duration.plusHours(1), false);
        op13 = new Operation("op13", group, lot1, duration.plusHours(2), false);
        op14 = new Operation("op14", group, lot1, duration.plusHours(3), true);
        op15 = new Operation("op15", group, lot1, duration.plusHours(4), false);
        Lot.addRelation(op11, op12);
        Lot.addRelation(op11, op13);
        Lot.addRelation(op11, op14);
        Lot.addRelation(op12, op15);
        Lot.addRelation(op13, op15);
        Lot.addRelation(op14, op15);
        lot1.add(op11);
        lot1.add(op12);
        lot1.add(op13);
        lot1.add(op14);
        lot1.add(op15);

        LocalDateTime arrival2 = startingDate.atTime(10, 0);
        LocalDateTime deadline2 = startingDate.plusDays(2).atTime(13, 0);
        Lot lot2 = new Lot(arrival2, deadline2, 0);
        op21 = new Operation("op21", group, lot2, duration.plusHours(3), false);
        op22 = new Operation("op22", group, lot2, duration.plusHours(2), false);
        op23 = new Operation("op23", group, lot2, duration.plusHours(1), false);
        op24 = new Operation("op24", group, lot2, duration.plusHours(5), true);
        op25 = new Operation("op25", group, lot2, duration.plusHours(2), false);
        Lot.addRelation(op21, op23);
        Lot.addRelation(op22, op23);
        Lot.addRelation(op23, op24);
        Lot.addRelation(op23, op25);
        lot2.add(op21);
        lot2.add(op22);
        lot2.add(op23);
        lot2.add(op24);
        lot2.add(op25);

        LocalDateTime arrival3 = startingDate.atTime(15, 0);
        LocalDateTime deadline3 = startingDate.plusDays(5).atTime(14, 0);
        Lot lot3 = new Lot(arrival3, deadline3, 0);
        op31 = new Operation("op31", group, lot3, duration.plusHours(6), true);
        op32 = new Operation("op32", group, lot3, duration.plusHours(4), true);
        op33 = new Operation("op33", group, lot3, duration.plusHours(2), false);
        op34 = new Operation("op34", group, lot3, duration.plusHours(3), true);
        op35 = new Operation("op35", group, lot3, duration.plusHours(2), false);
        op36 = new Operation("op36", group, lot3, duration.plusHours(1), false);
        Lot.addRelation(op31, op33);
        Lot.addRelation(op32, op33);
        Lot.addRelation(op33, op34);
        Lot.addRelation(op33, op35);
        Lot.addRelation(op34, op36);
        Lot.addRelation(op35, op36);
        lot3.add(op31);
        lot3.add(op32);
        lot3.add(op33);
        lot3.add(op34);
        lot3.add(op35);
        lot3.add(op36);

        HashSet<Lot> allLots = new HashSet<>();
        allLots.add(lot1);
        allLots.add(lot2);
        allLots.add(lot3);

        frontalAlgorithm = new FrontalAlgorithm(allLots);
    }

    @Test
    public void durationTest() {

        Solution expected = new Solution();
        expected.assign(op22, startingDate.atTime(10, 0), res);
        expected.assign(op21, startingDate.atTime(12, 0), res);
        expected.assign(op23, startingDate.atTime(15, 0), res);
        expected.assign(op32, startingDate.atTime(16, 0), res);
        expected.assign(op25, startingDate.plusDays(1).atTime(13, 0), res);
        expected.assign(op24, startingDate.plusDays(1).atTime(15, 0), res);
        expected.assign(op11, startingDate.plusDays(2).atTime(13, 0), res);
        expected.assign(op12, startingDate.plusDays(2).atTime(15, 0), res);
        expected.assign(op14, startingDate.plusDays(2).atTime(16, 0), res);
        expected.assign(op13, startingDate.plusDays(3).atTime(12, 0), res);
        expected.assign(op31, startingDate.plusDays(3).atTime(14, 0), res);
        expected.assign(op33, startingDate.plusDays(4).atTime(13, 0), res);
        expected.assign(op35, startingDate.plusDays(4).atTime(15, 0), res);
        expected.assign(op34, startingDate.plusDays(5).atTime(10, 0), res);
        expected.assign(op36, startingDate.plusDays(5).atTime(13, 0), res);
        expected.assign(op15, startingDate.plusDays(6).atTime(10, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new DurationComparator()));
    }

    @Test
    public void deadlineTest() {

        Solution expected = new Solution();
        expected.assign(op22, startingDate.atTime(10, 0), res);
        expected.assign(op21, startingDate.atTime(12, 0), res);
        expected.assign(op23, startingDate.atTime(15, 0), res);
        expected.assign(op24, startingDate.atTime(16, 0), res);
        expected.assign(op25, startingDate.plusDays(1).atTime(14, 0), res);
        expected.assign(op32, startingDate.plusDays(1).atTime(16, 0), res);
        expected.assign(op31, startingDate.plusDays(2).atTime(13, 0), res);
        expected.assign(op33, startingDate.plusDays(3).atTime(12, 0), res);
        expected.assign(op35, startingDate.plusDays(3).atTime(14, 0), res);
        expected.assign(op34, startingDate.plusDays(3).atTime(16, 0), res);
        expected.assign(op36, startingDate.plusDays(4).atTime(12, 0), res);
        expected.assign(op11, startingDate.plusDays(4).atTime(13, 0), res);
        expected.assign(op12, startingDate.plusDays(4).atTime(15, 0), res);
        expected.assign(op14, startingDate.plusDays(4).atTime(16, 0), res);
        expected.assign(op13, startingDate.plusDays(5).atTime(12, 0), res);
        expected.assign(op15, startingDate.plusDays(6).atTime(10, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new DeadlineComparator()));
    }

    @Test
    public void lateStartTest() {

        Solution expected = new Solution();
        expected.assign(op21, startingDate.atTime(10, 0), res);
        expected.assign(op22, startingDate.atTime(13, 0), res);
        expected.assign(op23, startingDate.atTime(15, 0), res);
        expected.assign(op24, startingDate.atTime(16, 0), res);
        expected.assign(op25, startingDate.plusDays(1).atTime(14, 0), res);
        expected.assign(op31, startingDate.plusDays(1).atTime(16, 0), res);
        expected.assign(op32, startingDate.plusDays(2).atTime(15, 0), res);
        expected.assign(op33, startingDate.plusDays(3).atTime(12, 0), res);
        expected.assign(op34, startingDate.plusDays(3).atTime(14, 0), res);
        expected.assign(op35, startingDate.plusDays(4).atTime(10, 0), res);
        expected.assign(op11, startingDate.plusDays(4).atTime(12, 0), res);
        expected.assign(op36, startingDate.plusDays(4).atTime(14, 0), res);
        expected.assign(op14, startingDate.plusDays(4).atTime(15, 0), res);
        expected.assign(op13, startingDate.plusDays(5).atTime(11, 0), res);
        expected.assign(op12, startingDate.plusDays(5).atTime(13, 0), res);
        expected.assign(op15, startingDate.plusDays(6).atTime(10, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new LateStartComparator()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var task = new Res1Lot3();
            IStrategy strategy = new DefaultStrategy(new LateStartComparator(), Duration.ofMinutes(30), 10);
            Solution solution = task.frontalAlgorithm.iterRun(strategy);
            var outputFrame = new OutputFrame(solution);
            outputFrame.setVisible(true);
        });
    }
}

