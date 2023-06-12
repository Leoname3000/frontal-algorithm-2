import javax.swing.*;
import java.awt.*;

public class OutputFrame extends JFrame {
    public OutputFrame(Solution solution) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        SolutionPanel solutionPanel = new SolutionPanel(solution);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(solutionPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
        pack();
    }
}
