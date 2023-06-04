import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeSet;

public class CoolFrame extends JPanel {
    public CoolFrame(Solution solution) {
        this.solution = solution;
        this.offsetY = 40;
        this.height = 30;
        this.entryTime = solution.assignations.first().time.toLocalDate().atStartOfDay();

        this.resources = new ArrayList<>();
        for (Solution.Assignation assignation : solution.assignations) {
            resources.add(assignation.resource);
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        this.secondsInDay = (int) Duration.ofDays(1).toSeconds();
        this.secondsPerPixel = secondsInDay / width;
        setPreferredSize(new Dimension(width * 3, (int) (height * 0.5)));
    }
    Solution solution;
    int offsetY;
    int height;
    int secondsInDay;
    int secondsPerPixel;
    LocalDateTime entryTime;
    ArrayList<Resource> resources;

    private int pixelX(LocalDateTime open) {
        Duration duration = Duration.between(entryTime, open);
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel);
    }
    private int pixelLength(LocalDateTime open, LocalDateTime close) {
        Duration duration = Duration.between(open, close);
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

    private void drawAssignation(Graphics g, LocalDateTime open, LocalDateTime close, Solution.Assignation assignation) {
        int x = pixelX(open) + 2;
        int y = resources.indexOf(assignation.resource) * (height + offsetY) + offsetY + 2;
        int length = pixelLength(open, close) - 2;
        g.setColor(Color.pink);
        g.fillRect(x, y, length, height - 4);
        g.setColor(Color.red);
        g.drawRect(x, y, length, height - 4);
        g.drawString(assignation.operation.toString(), x + length / 2 - 16, y + height / 2 + 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
            if (!assignation.operation.interruptable)
                drawAssignation(g, assignation.time, assignation.operation.endOfService, assignation);
            else
                for (Schedule.Interval interval : assignation.resource.schedule.intervals) {
                    if (assignation.operation.endOfService.isBefore(interval.open))
                        break;
                    if (!assignation.time.isBefore(interval.open)) {
                        LocalDateTime open = interval.open;
                        LocalDateTime close = interval.close;
                        if (assignation.time.toLocalDate().isEqual(interval.open.toLocalDate()))
                            open = assignation.time;
                        if (assignation.operation.endOfService.isBefore(interval.close))
                            close = assignation.operation.endOfService;
                        drawAssignation(g, open, close, assignation);
                    }
                }
        }
    }

    public static void main(String[] args) {

    }
}
