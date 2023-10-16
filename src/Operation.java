import java.io.Console;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Operation implements Comparable<Operation> {
    public Operation(String name, Group group, Lot lot, Duration duration, boolean interruptable) {
        this.name = name;
        this.group = group;
        this.lot = lot;
        followers = new HashSet<>();
        precedents = new HashSet<>();
        this.duration = duration;
        this.interruptable = interruptable;
        this.endOfService = null;
        this.lateStart = null;
    }
    final String name;
    final Group group;
    final Lot lot;
    final HashSet<Operation> followers;
    final HashSet<Operation> precedents;
    final Duration duration;
    final boolean interruptable;
    LocalDateTime endOfService;

    void addFollower(Operation operation) {
        followers.add(operation);
    }
    void addPrecedent(Operation operation) {
        precedents.add(operation);
    }

    private LocalDateTime lateStart;
    LocalDateTime lateStart() {
        if (lateStart != null) return lateStart;

        LocalDateTime threshold = lot.deadline;
        for (var follower : followers) {
            if (follower.lateStart().isBefore(threshold)) {
                threshold = follower.lateStart();
            }
        }
        LocalDateTime bestLateStart = null;
        for (var resource : group.resources) {
            var lateStart = resource.whenLateStart(threshold, this);
            if (bestLateStart == null || lateStart.isAfter(bestLateStart)) {
                bestLateStart = lateStart;
            }
        }
        System.out.println("Late start for " + name + " is " + bestLateStart);
        lateStart = bestLateStart;
        return bestLateStart;
    }

    void reset() {
        endOfService = null;
    }

    LocalDateTime lateSta() {
        LocalDateTime lateThreshold = lot.deadline;

        if (!followers.isEmpty()) {
            for (var follower : followers) {
                var followerLateStart = follower.lateStart();
                if (followerLateStart.isBefore(lateThreshold)) {
                    lateThreshold = followerLateStart;
                }
            }
        }

        if (interruptable) {
            for (var res : group.resources) {
                var durationLeft = duration;
                var interval = res.lastIntervalOpeningBefore(lateThreshold);
                while (durationLeft.isPositive()) {
                    if (interval == null) {
                        throw new RuntimeException("Not enough intervals in " + res + " for interruptable " + name);
                    }
                    if (durationLeft.compareTo(interval.length()) <= 0) {
                        return interval.close.minus(durationLeft);
                    }
                    durationLeft = durationLeft.minus(interval.length());
                    interval = res.schedule.intervals.lower(interval);
                }
            }
        }
        else {
            while (lot.arrival.isBefore(lateThreshold)) {
                var lateIntervals = new TreeSet<Schedule.Interval>(new Comparator<Schedule.Interval>() {
                    @Override
                    public int compare(Schedule.Interval i1, Schedule.Interval i2) {
                        return i1.close.compareTo(i2.close);
                    }
                });
                for (var res : group.resources) {
                    var interval = res.lastIntervalOpeningBefore(lateThreshold);
                    if (interval.close.isAfter(lot.deadline)) {
                        interval = new Schedule.Interval(interval.open, lot.deadline);
                    }
                    lateIntervals.add(interval);
                }
                for (var interval : lateIntervals) {
                    if (duration.compareTo(interval.length()) <= 0) {
                        System.out.println("Late start for " + name + " is " + interval.close.minus(duration));
                        return interval.close.minus(duration);
                    }
                }
                var lateIntervalsByOpen = new TreeSet<Schedule.Interval>(new Comparator<Schedule.Interval>() {
                    @Override
                    public int compare(Schedule.Interval i1, Schedule.Interval i2) {
                        return i1.open.compareTo(i2.open);
                    }
                });
                lateIntervalsByOpen.addAll(lateIntervals);
                lateThreshold = lateIntervalsByOpen.first().open;
            }
        }
        throw new RuntimeException("Couldn't define late start time for operation " + name);
//        if (followers.isEmpty())
//            return lot.deadline.minus(duration);
//        else {
//            LocalDateTime minLateStart = null;
//            for (Operation follower : followers) {
//                LocalDateTime lateStart = follower.lateStart();
//                if (minLateStart == null || lateStart.isBefore(minLateStart))
//                    minLateStart = lateStart;
//            }
//            return minLateStart.minus(duration);
//        }
    }

    @Override
    public int compareTo(Operation o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
