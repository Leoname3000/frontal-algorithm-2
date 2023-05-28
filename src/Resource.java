import java.time.Duration;
import java.time.LocalDateTime;

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
        throw new RuntimeException("Impossible to assign " + operation +" on given schedule!");
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

    @Override
    public int compareTo(Resource o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
