import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Operation implements Comparable<Operation> {
    public Operation(String name, Group group, Lot lot, Duration duration, boolean interruptable) {
        this.priority = 0;
        this.name = name;
        this.group = group;
        this.lot = lot;
        followers = new HashSet<>();
        precedents = new HashSet<>();
        this.duration = duration;
        this.interruptable = interruptable;
        this.endOfService = null;

        this.lateStart = null;
        this.earlyStart = null;

        this.specialLateStart = null;
        this.specialEarlyStart = null;

        allFollowers = -1;
        allFollowersDuration = null;
    }
    public int priority;
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
    private LocalDateTime earlyStart;

    private LocalDateTime specialLateStart;
    private LocalDateTime specialEarlyStart;

    int allFollowers;
    Duration allFollowersDuration;
    public int countAllFollowers() {
        if (allFollowers >= 0) return allFollowers;

        int count = 0;
        for (var follower : followers) {
            count += 1;
            count += follower.countAllFollowers();
        }
        allFollowers = count;
        return count;
    }

    public Duration countAllFollowingDurations() {
        if (allFollowersDuration != null) return allFollowersDuration;

        Duration sum = Duration.ZERO;
        for (var follower : followers) {
            sum = sum.plus(follower.duration);
            sum = sum.plus(follower.countAllFollowingDurations());
        }
        allFollowersDuration = sum;
        return sum;
    }

    public void prioritizeFollowers() {
        for (var follower : followers) {
            follower.priority += 1;
            follower.prioritizeFollowers();
        }
    }

    public void prioritizePrecedents() {
        for (var precedent : precedents) {
            precedent.priority += 1;
            precedent.prioritizeFollowers();
        }
    }

    public Duration specialTimeCapacity() {
        return Duration.between(specialEarlyStart(), specialLateStart());
    }

    public LocalDateTime specialEarlyStart() {
        if (specialEarlyStart != null) return specialEarlyStart;
        var threshold = lot.arrival;
        for (var precedent : precedents) {
            var e = precedent.specialEarlyStart().plus(precedent.duration);
            if (e.isAfter(threshold)) {
                threshold = e;
            }
        }
        specialEarlyStart = threshold;
        return threshold;
    }

    public LocalDateTime specialLateStart() {
        if (specialLateStart != null) return specialLateStart;
        var threshold = lot.deadline;
        for (var follower : followers) {
            var l = follower.specialLateStart();
            if (l.isBefore(threshold)) {
                threshold = l;
            }
        }
        threshold = threshold.minus(duration);
        specialLateStart = threshold;
        return threshold;
    }

    public Duration timeCapacity() {
        return Duration.between(earlyStart(), lateStart());
    }
    LocalDateTime earlyStart() {
        if (earlyStart != null) return earlyStart;

        LocalDateTime threshold = lot.arrival;
        for (var precedent : precedents) {
            var precedentEarlyStartEND = precedent.earlyStart().plus(precedent.duration);
            if (precedentEarlyStartEND.isAfter(threshold)) {
                threshold = precedentEarlyStartEND;
            }
        }
        // OLD CODE:
//        for (var precedent : precedents) {
//            var precedentEarlyStart = precedent.earlyStart();
//            if (precedentEarlyStart.isAfter(threshold)) {
//                threshold = precedentEarlyStart;
//            }
//        }

        LocalDateTime bestEarlyStart = null;
        for (var resource : group.resources) {
            var currentEarlyStart = resource.whenEarlyStart(threshold, this);
            if (bestEarlyStart == null || (currentEarlyStart != null && currentEarlyStart.isBefore(bestEarlyStart))) {
                bestEarlyStart = currentEarlyStart;
            }
        }
        if (bestEarlyStart == null) throw new RuntimeException("Не удалось определить время раннего старта для операции " + name + " ни на одном из ресурсов её группы");
        System.out.println("Early start for " + name + " is " + bestEarlyStart);
        earlyStart = bestEarlyStart;
        return bestEarlyStart;
    }

    LocalDateTime lateStart() {
        if (lateStart != null) return lateStart;

        LocalDateTime threshold = lot.deadline;
        for (var follower : followers) {
            var followerLateStart = follower.lateStart();
            if (followerLateStart.isBefore(threshold)) {
                threshold = followerLateStart;
            }
        }
        LocalDateTime bestLateStart = null;
        for (var resource : group.resources) {
            var currentLateStart = resource.whenLateStart(threshold, this);
            if (bestLateStart == null || (currentLateStart != null && currentLateStart.isAfter(bestLateStart))) {
                bestLateStart = currentLateStart;
            }
        }
        if (bestLateStart == null) throw new RuntimeException("Не удалось определить время позднего старта для операции " + name + " ни на одном из ресурсов её группы");

//        //TODO: WTF? Minus one day needed!
//        //TODO: NO minus not needed!!! Day numeration starts at 0 so: plus 0 day is 01-01!!!
//        if (followers.isEmpty()) {
//            bestLateStart = bestLateStart.minusDays(1);
//        }

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
