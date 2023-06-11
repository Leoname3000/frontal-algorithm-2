import com.sun.source.tree.Tree;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class SolutionPanel extends JPanel {
    public SolutionPanel(Solution solution) {
        this.solution = solution;
        this.offsetY = 40;
        this.height = 30;
        this.entryTime = solution.assignations.first().time.toLocalDate().atStartOfDay();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        this.secondsInDay = (int) Duration.ofDays(1).toSeconds();
        this.secondsPerPixel = secondsInDay / width;

        this.resources = new ArrayList<>();
        LocalDateTime firstDay = null;
        LocalDateTime lastDay = null;
        for (Solution.Assignation assignation : solution.assignations) {
            resources.add(assignation.resource);
            if (firstDay == null || assignation.time.isBefore(firstDay))
                firstDay = assignation.time.toLocalDate().atStartOfDay();
            if (lastDay == null || assignation.operation.endOfService.isAfter(lastDay))
                lastDay = assignation.operation.endOfService.toLocalDate().atStartOfDay();
        }
        int daysBetween = (int) Duration.between(firstDay, lastDay).toDays() + 1;

        TreeMap<LocalDate, Duration> extraBefore = new TreeMap<>();
        TreeMap<LocalDate, Duration> extraAfter = new TreeMap<>();
        for (Resource resource : resources) {
            for (Schedule.Interval interval : resource.schedule.intervals) {
                var currentBefore = Duration.between(interval.open.toLocalDate().atStartOfDay(), interval.open);
                var minBefore = extraBefore.get(interval.open.toLocalDate());
                if (minBefore == null || currentBefore.compareTo(minBefore) < 0)
                    extraBefore.put(interval.open.toLocalDate(), currentBefore);
                var currentAfter = Duration.between(interval.close, interval.close.toLocalDate().plusDays(1).atStartOfDay());
                var minAfter = extraAfter.get(interval.close.toLocalDate());
                if (minAfter == null || currentAfter.compareTo(minAfter) < 0)
                    extraAfter.put(interval.close.toLocalDate(), currentAfter);
            }
        }
//        for (var b : extraBefore.entrySet())
//            System.out.println("Extra before for " + b.getKey() + " is " + b.getValue());
//        for (var a : extraAfter.entrySet())
//            System.out.println("Extra after for " + a.getKey() + " is " + a.getValue());

        extraOffset = new TreeMap<>();
        for (var entry : extraBefore.entrySet()) {
            Duration minBetween = Duration.ofMinutes(60);
            LocalDate day = entry.getKey();
            Duration extra = entry.getValue();
            if (extra.compareTo(minBetween) > 0)
                extra = extra.minus(minBetween);
            int offset = pixelLength(extra);
            //System.out.println("--> There is " + offset + " pixels (" + day + "), dur=" + entry.getValue() + ", durInSec=" + (Duration.ofHours(10).toSeconds() / secondsPerPixel));
            for (var beforeEntry : extraBefore.entrySet())
                if (beforeEntry.getKey().isBefore(day)) {
                    extra = beforeEntry.getValue();
                    if (extra.compareTo(minBetween) > 0)
                        extra = extra.minus(minBetween);
                    offset += pixelLength(extra);
                    //System.out.println("--> There is " + offset + " pixels (" + beforeEntry.getKey() + "), dur=" + beforeEntry.getValue());
                }
            for (var afterEntry : extraAfter.entrySet())
                if (afterEntry.getKey().isBefore(day)) {
                    extra = afterEntry.getValue();
                    if (extra.compareTo(minBetween) > 0)
                        extra = extra.minus(minBetween);
                    offset += pixelLength(extra);
                    //System.out.println("--> There is " + offset + " pixels (" + afterEntry.getKey() + "), dur=" + afterEntry.getValue());
                }
            extraOffset.put(day, offset);
            //System.out.println("Offset for day " + day + " is " + offset + " pixels");
        }

        setPreferredSize(new Dimension(width * daysBetween, (int) (height * 0.5)));
    }
    Solution solution;
    int offsetY;
    int height;
    int secondsInDay;
    int secondsPerPixel;
    LocalDateTime entryTime;
    ArrayList<Resource> resources;
    TreeMap<LocalDate, Integer> extraOffset;

    private int pixelX(LocalDateTime open) {
        Duration duration = Duration.between(entryTime, open);
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel) - extraOffset.get(open.toLocalDate());
    }
    private int pixelLength(LocalDateTime open, LocalDateTime close) {
        Duration duration = Duration.between(open, close);
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel);
    }

    private int pixelLength(Duration duration) {
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel);
    }

    private void drawInterval(Graphics g, Schedule.Interval interval, Resource resource) {
        int x = pixelX(interval.open);
        int y = resources.indexOf(resource) * (height + offsetY) + offsetY;
        int length = pixelLength(interval.open, interval.close);
        g.setColor(Color.lightGray);
        g.fillRect(x, y, length, height);
        g.setColor(Color.darkGray);
        g.drawRect(x, y, length, height);
        g.drawString(interval.open.toLocalTime().toString(), x, y + height + 15);
        g.drawString(interval.close.toLocalTime().toString(), x + length - 38, y + height + 15);
    }

    private void drawAssignation(Graphics g, LocalDateTime open, LocalDateTime close, Solution.Assignation assignation, boolean leftPadding, boolean rightPadding) {
        int lp = leftPadding ? 2 : 0;
        int rp = rightPadding ? 2 : 0;
        int tp = 2, bp = 2;

        int x = pixelX(open) + lp;
        int y = resources.indexOf(assignation.resource) * (height + offsetY) + offsetY + tp;
        int length = pixelLength(open, close) - lp - rp;
        Color fillColor = assignation.operation.interruptable ? Color.orange : Color.pink;
        g.setColor(fillColor);
        g.fillRect(x, y, length, height - tp - bp);
        Color borderColor = Color.red;
        g.setColor(borderColor);
        g.drawRect(x, y, length, height - tp - bp);

        String name = assignation.operation.toString();
        g.drawString(name, x + length / 2 - name.length() * 4, y + height / 2 + 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double scaleFactor = 1;
        var at = new AffineTransform();
        at.scale(scaleFactor, scaleFactor);
        Graphics2D g2 = (Graphics2D) g;
        g2.transform(at);

        //TreeSet<Solution.Assignation> assignations = solution.assignations;
//        ArrayList<Resource> resources = new ArrayList<>();
//        for (Solution.Assignation assignation : solution.assignations) {
//            resources.add(assignation.resource);
//        }

        setBackground(Color.white);
        for (Resource resource : resources) {
            for (Schedule.Interval interval : resource.schedule.intervals) {
                drawInterval(g, interval, resource);
            }
            int y = resources.indexOf(resource) * (height + offsetY) + offsetY;
            g.drawString(resource.toString(), 18, y + 20);
        }
        for (Solution.Assignation assignation : solution.assignations) {
            //if (!assignation.operation.interruptable)
            //    drawAssignation(g, assignation.time, assignation.operation.endOfService, assignation);
            //else {
                Duration durationLeft = assignation.operation.duration;
                LocalDateTime start = assignation.time;
                for (Schedule.Interval interval : assignation.resource.schedule.intervals) {
                    if (interval.close.isAfter(assignation.time)) {
                        if (start.isBefore(interval.open))
                            start = interval.open;
                        if (!start.plus(durationLeft).isAfter(interval.close)) {
                            drawAssignation(g, start, start.plus(durationLeft), assignation, start.isEqual(assignation.time), start.plus(durationLeft).isEqual(interval.close));
                            break;
                        }
                        else {
                            durationLeft = durationLeft.minus(Duration.between(start, interval.close));
                        }
                        drawAssignation(g, start, interval.close, assignation, start.isEqual(assignation.time), false);
                    }
                }
            //}
        }
    }

    public static void main(String[] args) {

    }
}
