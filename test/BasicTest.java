import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.TreeSet;

public class BasicTest {
    @Test
    public void basicStuff() {
        LocalDate startingDate = LocalDate.of(2023, 2, 20);
        Schedule schedule = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(18, 0))
                .build();

        Resource res = new Resource("r", schedule);
        Group group = new Group();
        group.add(res);

//        JPanel panel = new SchedulePanel(res.schedule.intervals);
//        panel.setBackground(Color.white);
//        JFrame frame = new JFrame("Schedule scheme");
//        frame.setSize(800, 1000);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(panel, BorderLayout.CENTER);
//        frame.setVisible(true);

        Duration duration = Duration.ofMinutes(0);
        Lot lot = new Lot(startingDate.atTime(10,0), startingDate.plusDays(10).atTime(10, 0), 0);
        Operation op1 = new Operation("op1", group, lot, duration.plusHours(3), false);
        Operation op2 = new Operation("op2", group, lot, duration.plusHours(4), false);
        Operation op3 = new Operation("op3", group, lot, duration.plusHours(4), false);

        DurationComparator durationComparator = new DurationComparator();
        TreeSet<Operation> treeSet = new TreeSet<>(durationComparator);
        treeSet.add(op1);
        System.out.println("TreeSet: " + treeSet);
        treeSet.add(op2);
        System.out.println("TreeSet: " + treeSet);
        treeSet.add(op3);
        System.out.println("TreeSet: " + treeSet);
    }
    @Test
    public void simpleTest() {
        DurationComparator operationComparator = new DurationComparator();

        LocalDate startingDate = LocalDate.of(2023, 2, 20);
        Schedule schedule = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(18, 0))
                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule);

        // Setting Resource and Group
        Resource res = new Resource("res", schedule);
        Group group = new Group();
        group.add(res);

        // Setting Duration and Deadline
        Duration duration = Duration.ofMinutes(0);
        LocalDateTime deadline = LocalDateTime.of(2023, 12, 31, 23, 59);

        // Setting Lots
        LocalDateTime arrival1 = startingDate.atTime(10, 0);
        Lot lot1 = new Lot(arrival1, deadline, 0);
        Operation op11 = new Operation("op11", group, lot1, duration.plusHours(1), false);
        Operation op12 = new Operation("op12", group, lot1, duration.plusHours(2), false);
        Operation op13 = new Operation("op13", group, lot1, duration.plusHours(3), false);
        Operation op14 = new Operation("op14", group, lot1, duration.plusHours(4), false);
        Operation op15 = new Operation("op15", group, lot1, duration.plusHours(4), true);
        Operation op16 = new Operation("op16", group, lot1, duration.plusHours(2), true);
        Operation op17 = new Operation("op17", group, lot1, duration.plusHours(3), false);
        Lot.addRelation(op11, op15);
        Lot.addRelation(op12, op15);
        Lot.addRelation(op13, op16);
        Lot.addRelation(op14, op16);
        Lot.addRelation(op15, op17);
        Lot.addRelation(op16, op17);
        lot1.add(op11);
        lot1.add(op12);
        lot1.add(op13);
        lot1.add(op14);
        lot1.add(op15);
        lot1.add(op16);
        lot1.add(op17);

        // Setting AllLots and creating Solution
        HashSet<Lot> allLots = new HashSet<>();
        allLots.add(lot1);

        Solution solution = new Solution();

        // Setting Expected Solution
        Solution expected = new Solution();
//        expected.assign(op11, startingDate.atTime(10, 0), res);
//        expected.assign(op21, startingDate.atTime(13, 0), res);
//        expected.assign(op12, startingDate.atTime(15, 0), res);
//        expected.assign(op22, startingDate.plusDays(1).atTime(13, 0), res);
//        expected.assign(op13, startingDate.plusDays(2).atTime(12, 0), res);

        FrontalAlgorithm frontalAlgorithm = new FrontalAlgorithm(allLots);
        //Assert.assertEquals(expected, frontalAlgorithm.run(operationComparator));
    }
}
