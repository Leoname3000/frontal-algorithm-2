import com.sun.source.tree.Tree;

import java.time.*;
import java.util.Comparator;
import java.util.TreeSet;
import static java.time.temporal.TemporalAdjusters.firstInMonth;


public class Schedule {
    public static class Interval {
        public Interval(LocalDateTime open, LocalDateTime close) {
            if (open.isAfter(close))
                throw new RuntimeException("Opening after closing!");
            this.open = open;
            this.close = close;
        }
        LocalDateTime open;
        LocalDateTime close;
        Duration length() {
            return Duration.between(open, close);
        }

        @Override
        protected Interval clone() {
            return new Interval(open, close);
        }
    }
    public static class IntervalComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval o1, Interval o2) {
            return o1.open.compareTo(o2.open);
        }
    }
    TreeSet<Interval> intervals;

//    public Schedule() {
//        this.intervals = new TreeSet<>(new IntervalComparator());
//    }
//
//    public void addManual(LocalDateTime open, LocalDateTime close) {
//        if (intervals.isEmpty()) {
//            intervals.add(new Interval(open, close));
//            return;
//        }
//
//        Interval interval = intervals.first();
//        for (int i = 0; i < intervals.size(); i++) {
//            if (open.isBefore(interval.open) && close.isBefore(interval.open)
//                    || open.isAfter(interval.close) && close.isAfter(interval.close))
//                this.intervals.add(new Interval(open, close));
//            else
//                throw new RuntimeException("Collision of intervals!");
//            interval = intervals.iterator().next();
//        }
//    }
//
//    public void addWeekdays(int year, DayOfWeek weekday, LocalTime openHour, LocalTime closeHour) {
//        LocalDate date = LocalDate.now().withYear(year).withMonth(1).with(firstInMonth(weekday));
//        TreeSet<Interval> newIntervals = new TreeSet<>(new IntervalComparator());
//        while (date.getYear() <= year) {
//            LocalDateTime open = date.atTime(openHour);
//            LocalDateTime close = date.atTime(closeHour);
//            // Collision is checked only for "intervals" because elements of "newIntervals" won't collide
//            for (Interval interval : intervals)
//                if (open.isBefore(interval.open) && close.isBefore(interval.open)
//                        || open.isAfter(interval.close) && close.isAfter(interval.close))
//                    newIntervals.add(new Interval(open, close));
//                else
//                    throw new RuntimeException("Collision of intervals!");
//            date = date.plusDays(7);
//        }
//        intervals.addAll(newIntervals);
//    }
//        public void removeDay(LocalDate day) {
//            LocalDateTime startOfDay = day.atStartOfDay();
//            LocalDateTime endOfDay = day.atTime(23, 59, 59);
//            intervals.removeIf(interval ->
//                    interval.open.isAfter(startOfDay) && interval.open.isBefore(endOfDay)
//                 || interval.close.isAfter(startOfDay) && interval.close.isBefore(endOfDay)
//                 || interval.open.isBefore(startOfDay) && interval.close.isAfter(endOfDay)
//            );
//        }

    public Schedule(ScheduleBuilder builder) {
        this.intervals = builder.intervals;
    }

    public static class ScheduleBuilder {
        TreeSet<Interval> intervals = new TreeSet<>(new IntervalComparator());

        public ScheduleBuilder() {}

        public ScheduleBuilder addManual(LocalDateTime open, LocalDateTime close) {
            if (intervals.isEmpty()) {
                intervals.add(new Interval(open, close));
                return this;
            }
            Interval interval = intervals.first();
            for (int i = 0; i < intervals.size(); i++) {
                if (open.isBefore(interval.open) && close.isBefore(interval.open)
                        || open.isAfter(interval.close) && close.isAfter(interval.close))
                    this.intervals.add(new Interval(open, close));
                else
                    throw new RuntimeException("Collision of intervals!");
                interval = intervals.iterator().next();
            }
            return this;
        }

        public ScheduleBuilder addDayOfWeek(int year, DayOfWeek weekday, LocalTime openHour, LocalTime closeHour) {
            LocalDate date = LocalDate.of(year, 1, 1).with(firstInMonth(weekday));
            TreeSet<Interval> newIntervals = new TreeSet<>(new IntervalComparator());
            if (intervals.isEmpty()) {
                while (date.getYear() <= year) {
                    LocalDateTime open = date.atTime(openHour);
                    LocalDateTime close = date.atTime(closeHour);
                    intervals.add(new Interval(open, close));
                    date = date.plusDays(7);
                }
                return this;
            }
            while (date.getYear() <= year) {
                LocalDateTime open = date.atTime(openHour);
                LocalDateTime close = date.atTime(closeHour);
                // Collision is checked only for "intervals" because elements of "newIntervals" won't collide
                Interval interval = intervals.first();
                for (int i = 0; i < intervals.size(); i++) {
                    if (open.isBefore(interval.open) && close.isBefore(interval.open)
                            || open.isAfter(interval.close) && close.isAfter(interval.close))
                        newIntervals.add(new Interval(open, close));
                    else
                        throw new RuntimeException("Collision of intervals!");
                    interval = intervals.iterator().next();
                }
//                for (Interval interval : intervals)
//                    if (open.isBefore(interval.open) && close.isBefore(interval.open)
//                            || open.isAfter(interval.close) && close.isAfter(interval.close))
//                        newIntervals.add(new Interval(open, close));
//                    else
//                        throw new RuntimeException("Collision of intervals!");
                date = date.plusDays(7);
            }
            intervals.addAll(newIntervals);
            return this;
        }

        public ScheduleBuilder addWeek(LocalDate firstDayOfWeek, LocalTime openHour, LocalTime closeHour) {
            LocalDate date = firstDayOfWeek;
            TreeSet<Interval> newIntervals = new TreeSet<>(new IntervalComparator());
            if (intervals.isEmpty()) {
                for (int i = 0; i < 7; i++) {
                    LocalDateTime open = date.atTime(openHour);
                    LocalDateTime close = date.atTime(closeHour);
                    intervals.add(new Interval(open, close));
                    date = date.plusDays(1);
                }
                return this;
            }
            for (int j = 0; j < 7; j++) {
                LocalDateTime open = date.atTime(openHour);
                LocalDateTime close = date.atTime(closeHour);
                // Collision is checked only for "intervals" because elements of "newIntervals" won't collide
                Interval interval = intervals.first();
                for (int i = 0; i < intervals.size(); i++) {
                    if (open.isBefore(interval.open) && close.isBefore(interval.open)
                            || open.isAfter(interval.close) && close.isAfter(interval.close))
                        newIntervals.add(new Interval(open, close));
                    else
                        throw new RuntimeException("Collision of intervals!");
                    interval = intervals.iterator().next();
                }
                date = date.plusDays(1);
            }
            intervals.addAll(newIntervals);
            return this;
        }

        public ScheduleBuilder removeDay(LocalDate day) {
            LocalDateTime startOfDay = day.atStartOfDay();
            LocalDateTime endOfDay = day.atTime(23, 59, 59);
            intervals.removeIf(interval ->
                    interval.open.isAfter(startOfDay) && interval.open.isBefore(endOfDay)
                 || interval.close.isAfter(startOfDay) && interval.close.isBefore(endOfDay)
                 || interval.open.isBefore(startOfDay) && interval.close.isAfter(endOfDay)
            );
            return this;
        }

        public Schedule build() {
            return new Schedule(this);
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Interval interval : intervals) {
            result += "From " + interval.open + " to " + interval.close + "\n";
        }
        return result;
    }
}