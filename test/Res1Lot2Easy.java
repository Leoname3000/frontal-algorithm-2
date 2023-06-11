import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

public class Res1Lot2Easy {

    LocalDate startingDate;
    Resource res;
    Operation op11, op12, op13;
    Operation op21, op22;
    FrontalAlgorithm frontalAlgorithm;

    public Res1Lot2Easy() {

        startingDate = LocalDate.of(2023, 2, 20);

        // SETTING RESOURCES
        Schedule schedule = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(10, 0), LocalTime.of(18, 0))
                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule);

        res = new Resource("res", schedule);
        Group group = new Group();
        group.add(res);

        // SETTING LOTS
        Duration duration = Duration.ofMinutes(0);

        LocalDateTime arrival1 = startingDate.atTime(10, 0);
        LocalDateTime deadline1 = startingDate.plusDays(2).atTime(18,0);
        Lot lot1 = new Lot(arrival1, deadline1, 0);
        op11 = new Operation("op11", group, lot1, duration.plusHours(3), false);
        op12 = new Operation("op12", group, lot1, duration.plusHours(6), true);
        op13 = new Operation("op13", group, lot1, duration.plusHours(6), false);
        op11.addFollower(op12);
        op12.addFollower(op13);
        op13.addPrecedent(op12);
        op12.addPrecedent(op11);
        lot1.add(op11);
        lot1.add(op12);
        lot1.add(op13);

        LocalDateTime arrival2 = startingDate.atTime(10, 0);
        LocalDateTime deadline2 = startingDate.plusDays(2).atTime(12,0);
        Lot lot2 = new Lot(arrival2, deadline2, 0);
        op21 = new Operation("op21", group, lot2, duration.plusHours(2), false);
        op22 = new Operation("op22", group, lot2, duration.plusHours(7), true);
        op21.addFollower(op22);
        op22.addPrecedent(op21);
        lot2.add(op21);
        lot2.add(op22);

        HashSet<Lot> allLots = new HashSet<>();
        allLots.add(lot1);
        allLots.add(lot2);

        frontalAlgorithm = new FrontalAlgorithm(allLots);
    }

    @Test
    public void durationTest() {

        Solution expected = new Solution();
        expected.assign(op21, startingDate.atTime(10, 0), res);
        expected.assign(op11, startingDate.atTime(12, 0), res);
        expected.assign(op12, startingDate.atTime(15, 0), res);
        expected.assign(op22, startingDate.plusDays(1).atTime(13, 0), res);
        expected.assign(op13, startingDate.plusDays(2).atTime(12, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new DurationComparator()));
    }

    @Test
    public void deadlineTest() {

        Solution expected = new Solution();
        expected.assign(op21, startingDate.atTime(10, 0), res);
        expected.assign(op22, startingDate.atTime(12, 0), res);
        expected.assign(op11, startingDate.plusDays(1).atTime(11, 0), res);
        expected.assign(op12, startingDate.plusDays(1).atTime(14, 0), res);
        expected.assign(op13, startingDate.plusDays(2).atTime(12, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new DeadlineComparator()));
    }

    @Test
    public void lateStartTest() {

        Solution expected = new Solution();
        expected.assign(op21, startingDate.atTime(10, 0), res);
        expected.assign(op11, startingDate.atTime(12, 0), res);
        expected.assign(op22, startingDate.atTime(15, 0), res);
        expected.assign(op12, startingDate.plusDays(1).atTime(14, 0), res);
        expected.assign(op13, startingDate.plusDays(2).atTime(12, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new LateStartComparator()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            var task = new Res1Lot2Easy();
            Solution solution = task.frontalAlgorithm.run(new DeadlineComparator());
            SolutionPanel solutionPanel = new SolutionPanel(solution);

            JScrollPane scrollPane = new JScrollPane();

            scrollPane.setViewportView(solutionPanel);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            frame.getContentPane().add(mainPanel);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
