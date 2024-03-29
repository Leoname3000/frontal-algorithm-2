import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeSet;

public class Resource implements Comparable<Resource> {
    public Resource(String name, Schedule schedule) {
        this.name = name;
        this.schedule = schedule;
        availableTime = schedule.intervals.first().open;
        //availableTime = LocalDateTime.of(2000, 1, 1, 0, 0);
    }

    String name;
    Schedule schedule;
    public LocalDateTime availableTime;
    public void reset() {
        availableTime = schedule.intervals.first().open;
    }
    public LocalDateTime requestLockTime(LocalDateTime frontTime, Operation operation) {
        LocalDateTime result;
        if (frontTime.isAfter(availableTime))
            result = frontTime;
        else
            result = availableTime;

        for (Schedule.Interval interval : schedule.intervals) {
            if (interval.close.isAfter(result)) {
                if (result.isBefore(interval.open))
                    result = interval.open;
                if (!result.plus(operation.duration).isAfter(interval.close) || operation.interruptable) { // <--- instead of 'isBefore', too
                    return result;
                }
            }
        }
        return null;
        //throw new RuntimeException("Ресурс " + name + " не смог назначить " + operation + " ни на один из своих интервалов.");
    }

    public LocalDateTime lock(LocalDateTime frontTime, Operation operation) {
        LocalDateTime result = requestLockTime(frontTime, operation);
        if (!operation.interruptable) {
            availableTime = result.plus(operation.duration);
            return result;
        }
        else {
            Duration durationLeft = operation.duration;
            LocalDateTime start = result;
            for (Schedule.Interval interval : schedule.intervals) {
                if (interval.close.isAfter(result)) {
                    if (start.isBefore(interval.open))
                        start = interval.open;
                    if (!start.plus(durationLeft).isAfter(interval.close)) { // <--- was 'isBefore' instead of '!isAfter'
                        availableTime = start.plus(durationLeft);
                        return result;
                    }
                    else {
                        durationLeft = durationLeft.minus(Duration.between(start, interval.close));
                    }
                }
            }
        }
        throw new RuntimeException("Impossible to assign operation on given schedule!");
    }

    public LocalDateTime whenEarlyStart(LocalDateTime arrival, Operation operation) {
        var intervalsClone = new TreeSet<Schedule.Interval>(new Schedule.IntervalComparator());
        for (var interval : schedule.intervals) {
            intervalsClone.add(interval.clone());
        }
        var earlyInterval = intervalsClone.first();
        while (!earlyInterval.close.isAfter(arrival)) {
            earlyInterval = intervalsClone.higher(earlyInterval);
            if (earlyInterval == null) return null;
        }
        if (earlyInterval.open.isBefore(arrival)) {
            earlyInterval.open = arrival;
        }

        if (!operation.interruptable) {
            while (operation.duration.compareTo(earlyInterval.length()) > 0) {
                earlyInterval = intervalsClone.higher(earlyInterval);
                if (earlyInterval == null) return null;
            }
            //return earlyInterval.open.plus(operation.duration);
            return earlyInterval.open;
        }
        else {
            return earlyInterval.open;
//            Duration durationLeft = operation.duration;
//            while (durationLeft.isPositive()) {
//                if (durationLeft.compareTo(earlyInterval.length()) > 0) {
//                    durationLeft = durationLeft.minus(earlyInterval.length());
//                }
//                else {
//                    return earlyInterval.open.plus(durationLeft);
//                }
//                earlyInterval = intervalsClone.higher(earlyInterval);
//                if (earlyInterval == null) return null;
//            }
//            return null;
        }
    }

    public LocalDateTime whenLateStart(LocalDateTime deadline, Operation operation) {
        var intervalsClone = new TreeSet<Schedule.Interval>(new Schedule.IntervalComparator());
        for (var interval : schedule.intervals) {
            intervalsClone.add(interval.clone());
        }
        var lateInterval = intervalsClone.last();
        while (!lateInterval.open.isBefore(deadline)) {
            lateInterval = intervalsClone.lower(lateInterval);
            if (lateInterval == null) return null;
        }
        if (lateInterval.close.isAfter(deadline)) {
            lateInterval.close = deadline;
        }

        if (!operation.interruptable) {
            while (operation.duration.compareTo(lateInterval.length()) > 0) {
                lateInterval = intervalsClone.lower(lateInterval);
                if (lateInterval == null) return null;
            }
            return lateInterval.close.minus(operation.duration);
        }
        else {
            Duration durationLeft = operation.duration;
            while (durationLeft.isPositive()) {
                if (durationLeft.compareTo(lateInterval.length()) > 0) {
                    durationLeft = durationLeft.minus(lateInterval.length());
                }
                else {
                    return lateInterval.close.minus(durationLeft);
                }
                lateInterval = intervalsClone.lower(lateInterval);
                if (lateInterval == null) return null;
            }
            return null;
        }
    }

    public LocalDateTime oldWhenLateStart(LocalDateTime deadline, Operation operation) {
        Schedule.Interval lateInterval = schedule.intervals.first().clone();
//        while (schedule.intervals.higher(lateInterval) != null && schedule.intervals.higher(lateInterval).open.isBefore(deadline)) {
//            lateInterval = schedule.intervals.higher(lateInterval).clone();
//        }
        for (var interval : schedule.intervals) {
            if (interval.close.isAfter(deadline)) {
                if (!interval.open.isBefore(deadline)) {
                    lateInterval = schedule.intervals.lower(interval).clone();
                }
                else {
                    lateInterval = interval.clone();
                    lateInterval.close = deadline;
                }
                break;
            }
        }

        if (operation.interruptable) {
            Duration durationLeft = operation.duration;
            while (durationLeft.isPositive()) {
                if (durationLeft.compareTo(lateInterval.length()) <= 0) {
                    return lateInterval.close.minus(durationLeft);
                }
                else {
                    durationLeft = durationLeft.minus(lateInterval.length());
                    lateInterval = schedule.intervals.lower(lateInterval).clone();
                }
            }
        }
        else {
            while (lateInterval != null) {
                if (operation.duration.compareTo(lateInterval.length()) <= 0) {
                    return lateInterval.close.minus(operation.duration);
                }
                else {
                    lateInterval = schedule.intervals.lower(lateInterval).clone();
                }
            }
        }
        throw new RuntimeException("У ресурса " + name + " не найден интервал позднего начала для " + operation.name);
    }

    @Override
    public int compareTo(Resource o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }

    public Schedule.Interval lastIntervalOpeningBefore(LocalDateTime lateThreshold) {
        for (var interval : schedule.intervals) {
            if (lateThreshold.isBefore(interval.close)) {
                if (interval.open.isBefore(lateThreshold)) {
                    return interval;
                }
                else {
                    var prevInterval = schedule.intervals.lower(interval);
                    if (prevInterval != null)
                        return prevInterval;
                }
            }
        }
        throw new RuntimeException("No corresponding late intervals in " + name + " for late threshold " + lateThreshold);
    }
}
