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

    private int pixelX(Schedule.Interval interval) {
        Duration duration = Duration.between(entryTime, interval.open);
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel);
    }
    private int pixelLength(Schedule.Interval interval) {
        Duration duration = Duration.between(interval.open, interval.close);
        return (int) Math.round((double) duration.toSeconds() / secondsPerPixel);
    }

    private void drawInterval(Graphics g, Schedule.Interval interval, int resIndex) {
        int x = pixelX(interval);
        int y = resIndex * (height + offsetY) + offsetY;
        int length = pixelLength(interval);
        g.setColor(Color.lightGray);
        g.fillRect(x, y, length, height);
        g.setColor(Color.darkGray);
        g.drawRect(x, y, length, height);
        g.drawString(interval.open.toLocalTime().toString(), x, y + height + 15);
        g.drawString(interval.close.toLocalTime().toString(), x + length - 38, y + height + 15);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //TreeSet<Solution.Assignation> assignations = solution.assignations;
        ArrayList<Resource> resources = new ArrayList<>();
        for (Solution.Assignation assignation : solution.assignations) {
            resources.add(assignation.resource);
        }

        setBackground(Color.white);
        for (Resource resource : resources) {
            for (Schedule.Interval interval : resource.schedule.intervals) {
                drawInterval(g, interval, resources.indexOf(resource));
            }
            int y = resources.indexOf(resource) * (height + offsetY) + offsetY;
            g.drawString(resource.toString(), 18, y + 20);
        }
    }

    public static void main(String[] args) {

    }
}
