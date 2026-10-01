import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// File name එක Main.j
public class Main extends JFrame {

    private JRadioButton metricRadio;
    private JRadioButton englishRadio;
    private JTextField weightField;
    private JTextField heightField;
    private JLabel weightLabel;
    private JLabel heightLabel;
    private JLabel resultLabel;
    private JLabel categoryLabel;

    public Main() {
        setTitle("BMI Calculator - ICT2132");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(230, 242, 255));
        JLabel headerLabel = new JLabel("Body Mass Index (BMI) Calculator", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Input Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Unit Selection
        JPanel unitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        unitPanel.setBorder(BorderFactory.createTitledBorder("Select Unit System"));
        metricRadio = new JRadioButton("Metric (kg, meters)", true);
        englishRadio = new JRadioButton("English (pounds, inches)");
        ButtonGroup unitGroup = new ButtonGroup();
        unitGroup.add(metricRadio);
        unitGroup.add(englishRadio);
        unitPanel.add(metricRadio);
        unitPanel.add(englishRadio);
        mainPanel.add(unitPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Inputs Panel
        JPanel inputGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        weightLabel = new JLabel("Weight (Kilograms):");
        weightField = new JTextField();
        heightLabel = new JLabel("Height (Meters):");
        heightField = new JTextField();
        inputGrid.add(weightLabel);
        inputGrid.add(weightField);
        inputGrid.add(heightLabel);
        inputGrid.add(heightField);
        mainPanel.add(inputGrid);
        mainPanel.add(Box.createVerticalStrut(15));

        // Calculate Button
        JButton calculateButton = new JButton("Calculate BMI");
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(calculateButton);
        mainPanel.add(Box.createVerticalStrut(15));

        // Results Panel
        JPanel resultPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Your Result"));
        resultLabel = new JLabel("BMI: --", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        categoryLabel = new JLabel("Category: --", JLabel.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultPanel.add(resultLabel);
        resultPanel.add(categoryLabel);
        mainPanel.add(resultPanel);

        add(mainPanel, BorderLayout.CENTER);

        // --- Reference Info Panel (Bottom) ---
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("BMI Reference Values"));
        JTextArea infoText = new JTextArea(
                "Underweight:\tless than 18.5\n" +
                        "Normal:\t\tbetween 18.5 and 24.9\n" +
                        "Overweight:\tbetween 25 and 29.9\n" +
                        "Obese:\t\t30 or greater"
        );
        infoText.setEditable(false);
        infoText.setBackground(infoPanel.getBackground());
        infoText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoPanel.add(infoText);
        add(infoPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---
        ActionListener unitListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (metricRadio.isSelected()) {
                    weightLabel.setText("Weight (Kilograms):");
                    heightLabel.setText("Height (Meters):");
                } else {
                    weightLabel.setText("Weight (Pounds):");
                    heightLabel.setText("Height (Inches):");
                }
                clearResults();
            }
        };

        metricRadio.addActionListener(unitListener);
        englishRadio.addActionListener(unitListener);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateBMI();
            }
        });
    }

    private void clearResults() {
        resultLabel.setText("BMI: --");
        categoryLabel.setText("Category: --");
        categoryLabel.setForeground(Color.BLACK);
    }

    private void calculateBMI() {
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());

            if (weight <= 0 || height <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter positive values greater than zero.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double bmi = 0;

            if (metricRadio.isSelected()) {
                bmi = weight / (height * height);
            } else {
                bmi = (weight * 703) / (height * height);
            }

            displayResult(bmi);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for weight and height.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResult(double bmi) {
        resultLabel.setText(String.format("BMI: %.2f", bmi));

        String category;
        Color color;

        if (bmi < 18.5) {
            category = "Underweight";
            color = new Color(70, 130, 180);
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            category = "Normal";
            color = new Color(46, 139, 87);
        } else if (bmi >= 25 && bmi <= 29.9) {
            category = "Overweight";
            color = new Color(218, 165, 32);
        } else {
            category = "Obese";
            color = new Color(178, 34, 34);
        }

        categoryLabel.setText("Category: " + category);
        categoryLabel.setForeground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
