import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreams extends JFrame {
    private JTextArea originalTA, filteredTA;
    private JTextField searchField;
    private JButton loadBtn, searchBtn, quitBtn;
    private Path filePath;

    public DataStreams() {
        setTitle("Data Streaming");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        originalTA = new JTextArea(10, 20);
        filteredTA = new JTextArea(10, 20);
        searchField = new JTextField(20);
        loadBtn = new JButton("Load File");
        searchBtn = new JButton("Search");
        quitBtn = new JButton("Quit");

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("Search: "));
        northPanel.add(searchField);
        northPanel.add(loadBtn);
        northPanel.add(searchBtn);
        northPanel.add(quitBtn);

        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.add(new JLabel("Original Data"), BorderLayout.NORTH);
        originalPanel.add(new JScrollPane(originalTA), BorderLayout.CENTER);

        JPanel filteredPanel = new JPanel(new BorderLayout());
        filteredPanel.add(new JLabel("Filtered Data"), BorderLayout.NORTH);
        filteredPanel.add(new JScrollPane(filteredTA), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, originalPanel, filteredPanel);
        splitPane.setDividerLocation(0.5);

        add(northPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        loadBtn.addActionListener(new LoadBtnListener());
        searchBtn.addActionListener(new SearchBtnListener());
        quitBtn.addActionListener(e -> System.exit(0));
    }

    private class LoadBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().toPath();
                try (Stream<String> lines = Files.lines(filePath)) {
                    originalTA.setText(lines.collect(Collectors.joining("\n")));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error loading file: " + ex.getMessage());
                }
            }
        }
    }
    private class SearchBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (filePath == null) {
                JOptionPane.showMessageDialog(null, "Please load a file first.");
                return;
            }
            String searchStr = searchField.getText();
            if (searchStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a search string.");
                return;
            }
            try (Stream<String> lines = Files.lines(filePath)) {
                List<String> filteredLines = lines.filter(line -> line.contains(searchStr)).collect(Collectors.toList());
                filteredTA.setText(String.join("\n", filteredLines));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error searching file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreams frame = new DataStreams();
            frame.setVisible(true);
        });
    }
}