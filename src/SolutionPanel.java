import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SolutionPanel extends JPanel {

    class DayPanel {
        int height = resources.size();
    }
    public SolutionPanel(Solution solution) {
        this.solution = solution;
        this.resources = new ArrayList<>();
        for (Solution.Assignation assignation : solution.assignations) {
            resources.add(assignation.resource);
        }
        this.start = solution.assignations.first().time.toLocalDate().atStartOfDay();
        this.daysFromStart = 0;
        this.height = 30;
    }
    Solution solution;
    ArrayList<Resource> resources;
    final LocalDateTime start;
    int daysFromStart;
    private int secondsPerPixel;
    final int height;

    int pixelLength(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.secondsPerPixel = (int) Duration.ofDays(1).toSeconds() / getWidth();
        int row = 0;
        for (Resource resource : resources) {
            //LocalDateTime dayStart = start;
            int daysPassed = 0;
            for (Schedule.Interval interval : resource.schedule.intervals) {
                if (interval.open.isBefore(start) && start.isBefore(interval.close)) {
                    int daysBetween = (int) Duration.between(start, interval.close).toDays();
                    for (int i = 0; i < daysBetween; i++) {
                        g.drawRect(pixelLength(start, start), height * (row + resources.size() * daysPassed), pixelLength(start.plusDays(daysPassed), start.plusDays(daysPassed + 1)), height);
                        daysPassed += 1;
                    }
                    g.drawRect(pixelLength(start, start), height * (row + resources.size() * daysPassed), pixelLength(start.plusDays(daysPassed), interval.close), height);
                }
                else if (interval.close.isAfter(start.plusDays(daysPassed).plusDays(1))) {
                    daysPassed += (int) Duration.between(start.plusDays(daysPassed), interval.open).toDays();
                    g.drawRect(pixelLength(start.plusDays(daysPassed), interval.open), height * (row + resources.size() * daysPassed), pixelLength(interval.open, start.plusDays(1)), height);
                    daysPassed += 1;
                    int daysBetween = (int) Duration.between(start.plusDays(daysPassed), interval.close).toDays();
                    for (int i = 0; i < daysBetween; i++) {
                        g.drawRect(pixelLength(start, start), height * (row + resources.size() * daysPassed), pixelLength(start.plusDays(daysPassed), start.plusDays(daysPassed + 1)), height);
                        daysPassed += 1;
                    }
                    g.drawRect(pixelLength(start, start), height * (row + resources.size() * daysPassed), pixelLength(start.plusDays(daysPassed), interval.close), height);
                }
                else if (interval.close.isAfter(start)) {
                    daysPassed += (int) Duration.between(start.plusDays(daysPassed), interval.open).toDays();
                    g.drawRect(pixelLength(start, start), height * (row + resources.size() * daysPassed), pixelLength(interval.open, interval.close), height);
                }
            }
            row += 1;
        }
//        int rowOffset = offsetY + resources.size() * (height + resourceOffset);
//        for (Resource resource : resources) {
//            LocalDate startingDay = resource.schedule.intervals.first().open.toLocalDate();
//            for (Schedule.Interval interval : resource.schedule.intervals) {
//                long daysFromStart = Duration.between(startingDay.atStartOfDay(), interval.open).toDays();
//                int dayPixels = pixels(daysFromStart * 24 * 60);
//                int x = dayPixels + pixels(interval.open.getHour() * 60 + interval.open.getMinute());
//                int length = pixels(interval.length().toMinutes());
//                int row = (int) daysFromStart / 3;
//                g.setColor(Color.LIGHT_GRAY);
//                g.fillRect(offsetX + x, offsetY + resources.indexOf(resource) * (height + resourceOffset) + row * rowOffset, length, height);
//                g.setColor(Color.BLACK);
//                g.drawRect(offsetX + x, offsetY + resources.indexOf(resource) * (height + resourceOffset) + row * rowOffset, length, height);
//            }
//        }
//        LocalDate startingDay = solution.assignations.first().time.toLocalDate();
//        for (Solution.Assignation assignation : solution.assignations) {
//            long daysFromStart = Duration.between(startingDay.atStartOfDay(), assignation.time).toDays();
//            int dayPixels = pixels(daysFromStart * 24 * 60);
//            int x = dayPixels + pixels(assignation.time.getHour() * 60 + assignation.time.getMinute());
//            int length = pixels(assignation.operation.duration.toMinutes());
//            g.setColor(Color.RED);
//            g.fillRect(offsetX + x, offsetY + resources.indexOf(assignation.resource) * (height + resourceOffset), length, height);
//            g.setColor(Color.BLACK);
//            g.drawRect(offsetX + x, offsetY + resources.indexOf(assignation.resource) * (height + resourceOffset), length, height);
//        }
    }
}
