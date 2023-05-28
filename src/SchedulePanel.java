import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.TreeSet;

public class SchedulePanel extends JPanel {
    public SchedulePanel(TreeSet<Schedule.Interval> intervals) {
        this.intervals = intervals;
    }
    int offsetX = 0;
    int offsetY = 30;
    int height = 15;
    final private int minutesPerPixel = 4;
    TreeSet<Schedule.Interval> intervals;

    int pixels(long minutes) {
        return (int) Math.round((double) minutes / minutesPerPixel);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        LocalDate startingDay = intervals.first().open.toLocalDate();
        for (Schedule.Interval interval : intervals) {
            long daysFromStart = Duration.between(startingDay.atStartOfDay(), interval.open).toDays();
            int dayPixels = pixels(daysFromStart * 24 * 60);
            int x = dayPixels + pixels(interval.open.getHour() * 60 + interval.open.getMinute());
            int length = pixels(interval.length().toMinutes());
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(offsetX + x, offsetY, length, height);
            g.setColor(Color.BLACK);
            g.drawRect(offsetX + x, offsetY, length, height);
        }
    }
}