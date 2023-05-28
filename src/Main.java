import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Main {
    static Comparator<Operation> operationComparator = Comparator.comparing(op -> op.duration);
    //static Comparator<Resource> resourceComparator = (res1, res2, Operation op) -> res1.canRun(op).compareTo(res2.canRun(op));
    public static void main(String[] args) {

        // Setting Schedule
        LocalTime open = LocalTime.of(10, 0, 0);
        LocalTime close = LocalTime.of(18, 0, 0);

        LocalDateTime timeSample = LocalDateTime.of(2023, 02, 10, 10, 00);

        LocalDateTime timeSampleOpen = LocalDateTime.of(2023, 2, 20, 10, 0);
        LocalDateTime timeSampleClose = LocalDateTime.of(2023, 2, 20, 18, 0);
        Schedule schedule = new Schedule.ScheduleBuilder()
                .addManual(timeSampleOpen, timeSampleClose)
                .addManual(timeSampleOpen.plusDays(1), timeSampleClose.plusDays(1))
                .addManual(timeSampleOpen.plusDays(2), timeSampleClose.plusDays(2))
                .build();
//        Schedule schedule = new Schedule.ScheduleBuilder()
//                .addWeekdays(2023, DayOfWeek.MONDAY, open, close)
//                .build();

        System.out.println("SCHEDULE");
        System.out.println(schedule);

        // Setting Resource and Group
        Resource res = new Resource("res", schedule);
        Group group = new Group();
        group.add(res);

        Resource dummy = new Resource("res1", schedule);
        group.add(dummy);

        // Setting Duration and Deadline
        Duration duration = Duration.ofMinutes(0);
        LocalDateTime deadline = LocalDateTime.of(2023, 05, 20, 23, 59);

        // Setting Lots
        LocalDateTime arrival1 = LocalDateTime.of(2023, 02, 10, 10, 00);
        Lot lot1 = new Lot(arrival1, deadline, 0);
        Operation op11 = new Operation("op11", group, lot1, duration.plusMinutes(30), false);
        Operation op12 = new Operation("op12", group, lot1, duration.plusMinutes(10), false);
        op11.addFollower(op12);
        op12.addPrecedent(op11);
        lot1.add(op11);
        lot1.add(op12);

        LocalDateTime arrival2 = LocalDateTime.of(2023, 02, 10, 10, 10);
        Lot lot2 = new Lot(arrival2, deadline, 0);
        Operation op21 = new Operation("op21", group, lot2, duration.plusMinutes(15), false);
        Operation op22 = new Operation("op22", group, lot2, duration.plusMinutes(20), false);
        op21.addFollower(op22);
        op22.addPrecedent(op21);
        lot2.add(op21);
        lot2.add(op22);

        // Setting AllLots and creating Solution
//        HashSet<Resource> allResources = new HashSet<>(); // Redundant?
//        allResources.add(res);

        HashSet<Lot> allLots = new HashSet<>();
        allLots.add(lot1);
        allLots.add(lot2);

        Solution solution = new Solution();



        // Frontal Algorithm
        // FA step 1 (Setting initial Fronts)
        TreeMap<LocalDateTime, TreeSet<Operation>> fronts = new TreeMap<>();
        for (Lot lot : allLots) {
            for (Operation op : lot.operations) {
                if (op.precedents.isEmpty()) {
                    TreeSet<Operation> operations = fronts.get(lot.arrival);
                    if (operations == null || operations.isEmpty())
                        operations = new TreeSet<>(operationComparator);
                    operations.add(op);
                    fronts.put(lot.arrival, operations); // Redundant?
                }
            }
            System.out.println("Fronts: " + fronts);
        }
        System.out.println("------------");

        // FA step 2
        while (!fronts.isEmpty()) {
            // FA step 3
            System.out.println("\nFronts: " + fronts);
            TreeSet<Operation> front = fronts.firstEntry().getValue();
            LocalDateTime frontTime = fronts.firstEntry().getKey();
            System.out.println("front: " + front + " time " + frontTime);
            Operation operation = front.first();
            Resource resource = pickResource(frontTime, operation);
            LocalDateTime minResTime = resource.requestLockTime(frontTime, operation);
            // What if all resources in group are busy? (held in else)

            // FA step 4 TODO: Assure Year/Month do not affect comparison and "IsAfter" is needed
            if (frontTime.equals(minResTime) || frontTime.isAfter(minResTime)) {
                // FA step 5
                // TODO: Assign operation to resource
                LocalDateTime lockTime = resource.lock(frontTime, operation);
                solution.assign(operation, lockTime, resource);
                front.remove(operation);
                System.out.println("Assigned " + operation.name + " on " + resource.name + " at " + lockTime);

                TreeSet<Operation> nextOperations = new TreeSet<>(operationComparator);
                for (Operation follower : operation.followers) {
                    follower.precedents.remove(operation);
                    if (follower.precedents.isEmpty()) {
                        nextOperations.add(follower);
                        // front.remove(follower); <- There are none!
                    }
                }

                // Add operation.duration to frontTime
                if (!nextOperations.isEmpty()) {
                    LocalDateTime createdFrontTime = frontTime.plus(operation.duration);
                    if (fronts.get(createdFrontTime) == null || fronts.get(createdFrontTime).isEmpty())
                        fronts.put(createdFrontTime, new TreeSet<>(operationComparator));

                    TreeSet<Operation> ops = fronts.get(createdFrontTime);
                    ops.addAll(nextOperations);
                    fronts.put(createdFrontTime, ops); // Can be simplified?
                }

                if (front.isEmpty())
                    fronts.remove(frontTime);
            }
            else {
                // Continuation of FA step 4

                if (fronts.get(minResTime) == null || fronts.get(minResTime).isEmpty())
                    fronts.put(minResTime, new TreeSet<>(operationComparator));
                TreeSet<Operation> operations = fronts.get(minResTime);
                operations.addAll(front);
                fronts.put(minResTime, operations); // Can be simplified?

                fronts.remove(frontTime);
            }
        }
        // FA exit
        // TODO: Implement Solution class and fixate assignations with it
        System.out.println("\nSOLUTION\n" + solution);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            CoolFrame coolFrame = new CoolFrame(solution);
            //coolFrame.setPreferredSize(new Dimension(3500, 2000));
            //coolFrame.add(new JLabel("res1"));

            JScrollPane scrollPane = new JScrollPane();
            //scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            //scrollPane.setPreferredSize(new Dimension(500,500));

            scrollPane.setViewportView(coolFrame);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            frame.getContentPane().add(mainPanel);
            frame.pack();
            frame.setVisible(true);

//            var panel = new SolutionPanel(solution);
//            panel.setBackground(Color.WHITE);
//            var frame = new JFrame("A simple graphics program");
//            frame.setSize(1440, 820);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.getContentPane().add(panel, BorderLayout.CENTER);
//            frame.setVisible(true);
        });

    }

//    public static LocalDateTime increase(LocalDateTime time, Duration duration) {
//        return time.plusHours(duration.toHours());
//    }
//    public static Operation pickOperation(TreeSet<Operation> front) {
//        Operation minOperation = front.first();
//        //front.remove(minOperation);
//        return minOperation;
//    }
    public static Resource pickResource(LocalDateTime frontTime, Operation operation) {
        TreeSet<Resource> resourceGroup = operation.group.resources;
        Resource minResource = null;
        for (Resource res : resourceGroup) {
            if (minResource == null || res.requestLockTime(frontTime, operation).isBefore(minResource.requestLockTime(frontTime, operation))) {
                minResource = res;
            }
        }
        return minResource;
    }
}