import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;

public class Res2Lot2 {

    LocalDate startingDate;
    Resource res1, res2;
    Operation op11, op12, op13, op14, op15;
    Operation op21, op22, op23, op24, op25;
    FrontalAlgorithm frontalAlgorithm;

    public Res2Lot2() {

        startingDate = LocalDate.of(2023, 2, 20);

        // SETTING RESOURCES
        Schedule schedule1 = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(8, 0), LocalTime.of(12, 0))
                .addWeek(startingDate, LocalTime.of(14, 0), LocalTime.of(18, 0))
                .build();

        Schedule schedule2 = new Schedule.ScheduleBuilder()
                .addWeek(startingDate, LocalTime.of(11, 0), LocalTime.of(15, 0))
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
        LocalDateTime deadline2 = startingDate.plusDays(2).atTime(17, 0);
        Lot lot2 = new Lot(arrival2, deadline2, 0);
        op21 = new Operation("op21", group, lot2, duration.plusHours(3), false);
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

        Solution durationExpected = new Solution();
        durationExpected.assign(op11, startingDate.atTime(8, 0), res1);
        durationExpected.assign(op12, startingDate.atTime(11, 0), res1);
        durationExpected.assign(op13, startingDate.plusDays(1).atTime(8, 0), res1);
        durationExpected.assign(op14, startingDate.plusDays(1).atTime(10, 0), res1);
        durationExpected.assign(op22, startingDate.plusDays(1).atTime(16, 0), res1);
        durationExpected.assign(op21, startingDate.plusDays(1).atTime(11, 0), res2);
        durationExpected.assign(op23, startingDate.plusDays(1).atTime(14, 0), res2);
        durationExpected.assign(op15, startingDate.plusDays(2).atTime(8, 0), res1);
        durationExpected.assign(op25, startingDate.plusDays(2).atTime(15, 0), res1);
        durationExpected.assign(op24, startingDate.plusDays(2).atTime(11, 0), res2);

        Assert.assertEquals(durationExpected, frontalAlgorithm.run(new DurationComparator()));
    }

    @Test
    public void deadlineTest() {

        Solution durationExpected = new Solution();
        durationExpected.assign(op11, startingDate.atTime(8, 0), res1);
        durationExpected.assign(op12, startingDate.atTime(11, 0), res1);
        durationExpected.assign(op13, startingDate.plusDays(1).atTime(8, 0), res1);
        durationExpected.assign(op14, startingDate.plusDays(1).atTime(10, 0), res1);
        durationExpected.assign(op15, startingDate.plusDays(1).atTime(11, 0), res2);
        durationExpected.assign(op21, startingDate.plusDays(2).atTime(8, 0), res1);
        durationExpected.assign(op23, startingDate.plusDays(2).atTime(11, 0), res1);
        durationExpected.assign(op22, startingDate.plusDays(2).atTime(11, 0), res2);
        durationExpected.assign(op24, startingDate.plusDays(2).atTime(13, 0), res2);
        durationExpected.assign(op25, startingDate.plusDays(3).atTime(13, 0), res2);

        Assert.assertEquals(durationExpected, frontalAlgorithm.run(new DeadlineComparator()));
    }

    @Test
    public void lateStartTest() {

        Solution durationExpected = new Solution();
        durationExpected.assign(op12, startingDate.atTime(8, 0), res1);
        durationExpected.assign(op13, startingDate.atTime(15, 0), res1);
        durationExpected.assign(op14, startingDate.atTime(17, 0), res1);
        durationExpected.assign(op11, startingDate.atTime(11, 0), res2);
        durationExpected.assign(op21, startingDate.plusDays(1).atTime(14, 0), res1);
        durationExpected.assign(op24, startingDate.plusDays(1).atTime(17, 0), res1);
        durationExpected.assign(op15, startingDate.plusDays(1).atTime(11, 0), res2);
        durationExpected.assign(op23, startingDate.plusDays(2).atTime(11, 0), res1);
        durationExpected.assign(op22, startingDate.plusDays(2).atTime(11, 0), res2);
        durationExpected.assign(op25, startingDate.plusDays(2).atTime(13, 0), res2);

        Assert.assertEquals(durationExpected, frontalAlgorithm.run(new LateStartComparator()));
    }
}
