import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OutputFrame extends JFrame {
    public OutputFrame(Solution solution) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        SolutionPanel solutionPanel = new SolutionPanel(solution);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(solutionPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        JButton zoomOutButton = new JButton("Отдалить");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solutionPanel.scaleFactor /= 1.1;
                solutionPanel.repaint();
            }
        });
        controlPanel.add(zoomOutButton);
        JButton zoomInButton = new JButton("Приблизить");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solutionPanel.scaleFactor *= 1.1;
                solutionPanel.repaint();
            }
        });
        controlPanel.add(zoomInButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
        pack();
    }
}
