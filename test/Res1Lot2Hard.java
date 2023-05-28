import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

public class Res1Lot2Hard {

    LocalDate startingDate;
    Resource res;
    Operation op11, op12, op13, op14, op15;
    Operation op21, op22, op23, op24, op25;
    FrontalAlgorithm frontalAlgorithm;

    public Res1Lot2Hard() {

        startingDate = LocalDate.of(2023, 2, 20);

        // SETTING RESOURCE
        Schedule schedule = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(8, 0), LocalTime.of(13, 0))
                .addWeek(startingDate, LocalTime.of(15, 0), LocalTime.of(20, 0))
                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule);

        res = new Resource("res", schedule);
        Group group = new Group();
        group.add(res);

        // SETTING LOTS
        Duration duration = Duration.ofMinutes(0);

        LocalDateTime arrival1 = startingDate.atTime(8, 0);
        LocalDateTime deadline1 = startingDate.plusDays(2).atTime(12, 0);
        Lot lot1 = new Lot(arrival1, deadline1, 0);
        op11 = new Operation("op11", group, lot1, duration.plusHours(3), false);
        op12 = new Operation("op12", group, lot1, duration.plusHours(5), true);
        op13 = new Operation("op13", group, lot1, duration.plusHours(2), false);
        op14 = new Operation("op14", group, lot1, duration.plusHours(4), true);
        op15 = new Operation("op15", group, lot1, duration.plusHours(4), false);
        Lot.addRelation(op11, op13);
        Lot.addRelation(op12, op13);
        Lot.addRelation(op13, op14);
        Lot.addRelation(op13, op15);
        lot1.add(op11);
        lot1.add(op12);
        lot1.add(op13);
        lot1.add(op14);
        lot1.add(op15);

        LocalDateTime arrival2 = startingDate.plusDays(1).atTime(8, 0);
        LocalDateTime deadline2 = startingDate.plusDays(2).atTime(20, 0);
        Lot lot2 = new Lot(arrival2, deadline2, 0);
        op21 = new Operation("op21", group, lot2, duration.plusHours(3), true);
        op22 = new Operation("op22", group, lot2, duration.plusHours(2), false);
        op23 = new Operation("op23", group, lot2, duration.plusHours(1), false);
        op24 = new Operation("op24", group, lot2, duration.plusHours(4), true);
        op25 = new Operation("op25", group, lot2, duration.plusHours(2), false);
        Lot.addRelation(op21, op22);
        Lot.addRelation(op21, op23);
        Lot.addRelation(op21, op24);
        Lot.addRelation(op22, op25);
        Lot.addRelation(op23, op25);
        Lot.addRelation(op24, op25);
        lot2.add(op21);
        lot2.add(op22);
        lot2.add(op23);
        lot2.add(op24);
        lot2.add(op25);

        HashSet<Lot> allLots = new HashSet<>();
        allLots.add(lot1);
        allLots.add(lot2);

        frontalAlgorithm = new FrontalAlgorithm(allLots);
    }

    @Test
    public void durationTest() {

        Solution expected = new Solution();
        expected.assign(op11, startingDate.atTime(8, 0), res);
        expected.assign(op12, startingDate.atTime(11, 0), res);
        expected.assign(op13, startingDate.atTime(18, 0), res);
        expected.assign(op21, startingDate.plusDays(1).atTime(8, 0), res);
        expected.assign(op23, startingDate.plusDays(1).atTime(11, 0), res);
        expected.assign(op14, startingDate.plusDays(1).atTime(12, 0), res);
        expected.assign(op22, startingDate.plusDays(1).atTime(18, 0), res);
        expected.assign(op15, startingDate.plusDays(2).atTime(8, 0), res);
        expected.assign(op24, startingDate.plusDays(2).atTime(12, 0), res);
        expected.assign(op25, startingDate.plusDays(2).atTime(18, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new DurationComparator()));
    }

    @Test
    public void deadlineTest() {

        Solution expected = new Solution();
        expected.assign(op11, startingDate.atTime(8, 0), res);
        expected.assign(op12, startingDate.atTime(11, 0), res);
        expected.assign(op13, startingDate.atTime(18, 0), res);
        expected.assign(op14, startingDate.plusDays(1).atTime(8, 0), res);
        expected.assign(op21, startingDate.plusDays(1).atTime(12, 0), res);
        expected.assign(op23, startingDate.plusDays(1).atTime(17, 0), res);
        expected.assign(op22, startingDate.plusDays(1).atTime(18, 0), res);
        expected.assign(op15, startingDate.plusDays(2).atTime(8, 0), res);
        expected.assign(op24, startingDate.plusDays(2).atTime(12, 0), res);
        expected.assign(op25, startingDate.plusDays(2).atTime(18, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new DeadlineComparator()));
    }

    @Test
    public void lateStartTest() {

        Solution expected = new Solution();
        expected.assign(op12, startingDate.atTime(8, 0), res);
        expected.assign(op11, startingDate.atTime(15, 0), res);
        expected.assign(op13, startingDate.atTime(18, 0), res);
        expected.assign(op14, startingDate.plusDays(1).atTime(8, 0), res);
        expected.assign(op21, startingDate.plusDays(1).atTime(12, 0), res);
        expected.assign(op24, startingDate.plusDays(1).atTime(17, 0), res);
        expected.assign(op15, startingDate.plusDays(2).atTime(9, 0), res);
        expected.assign(op22, startingDate.plusDays(2).atTime(15, 0), res);
        expected.assign(op23, startingDate.plusDays(2).atTime(17, 0), res);
        expected.assign(op25, startingDate.plusDays(2).atTime(18, 0), res);

        Assert.assertEquals(expected, frontalAlgorithm.run(new LateStartComparator()));
    }
}
