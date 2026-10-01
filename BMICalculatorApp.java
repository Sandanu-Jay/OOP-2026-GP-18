import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BMICalculatorApp extends JFrame {

    private JComboBox<String> unitComboBox;
    private JLabel weightLabel;
    private JLabel heightLabel;
    private JTextField weightTextField;
    private JTextField heightTextField;
    private JButton calculateButton;
    private JLabel resultLabel;
    private JTextArea referenceTextArea;

    public BMICalculatorApp() {
        setTitle("Body Mass Index Calculator App");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Unit Selector
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Select Unit System:"), gbc);

        String[] units = {"English Units (lbs / inches)", "Metric Units (kg / cm)"};
        unitComboBox = new JComboBox<>(units);
        gbc.gridx = 1; gbc.gridy = 0;
        add(unitComboBox, gbc);

        // 2. Weight Input
        weightLabel = new JLabel("Weight (pounds):");
        gbc.gridx = 0; gbc.gridy = 1;
        add(weightLabel, gbc);

        weightTextField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 1;
        add(weightTextField, gbc);

        // 3. Height Input (Defaults to inches)
        heightLabel = new JLabel("Height (inches):");
        gbc.gridx = 0; gbc.gridy = 2;
        add(heightLabel, gbc);

        heightTextField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 2;
        add(heightTextField, gbc);

        // 4. Calculate Button
        calculateButton = new JButton("Calculate BMI");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(calculateButton, gbc);

        // 5. Result Display Panel
        resultLabel = new JLabel("Your BMI: -- | Category: --", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setForeground(Color.BLUE);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(resultLabel, gbc);

        // 6. NIH Reference Guidelines Display
        referenceTextArea = new JTextArea();
        referenceTextArea.setText(
                "--- BMI VALUES REFERENCE ---\n" +
                        "Underweight:\tless than 18.5\n" +
                        "Normal:\t\tbetween 18.5 and 24.9\n" +
                        "Overweight:\tbetween 25 and 29.9\n" +
                        "Obese:\t\t30 or greater\n\n" +
                        "Source: Dept. of Health & Human Services / NIH"
        );
        referenceTextArea.setEditable(false);
        referenceTextArea.setBackground(new Color(245, 245, 245));
        referenceTextArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        referenceTextArea.setMargin(new Insets(5, 5, 5, 5));

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(referenceTextArea, gbc);

        // Change text labels dynamically when dropdown switches
        unitComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (unitComboBox.getSelectedIndex() == 0) {
                    weightLabel.setText("Weight (pounds):");
                    heightLabel.setText("Height (inches):");
                } else {
                    weightLabel.setText("Weight (kilograms):");
                    heightLabel.setText("Height (centimeters - cm):");
                }
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performBMICalculation();
            }
        });
    }

    private void performBMICalculation() {
        try {
            double weight = Double.parseDouble(weightTextField.getText().trim());
            double height = Double.parseDouble(heightTextField.getText().trim());
            double bmi = 0;

            if (height <= 0 || weight <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter numbers greater than 0.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (unitComboBox.getSelectedIndex() == 0) {
                // English Formula (lbs / inches)
                bmi = (weight * 703) / (height * height);
            } else {
                // Metric Formula (kg / cm)
                // If they typed meters by mistake (e.g., 1.7), we leave it.
                // If they typed cm (e.g., 170), we convert it to meters by dividing by 100.
                if (height > 3.0) {
                    height = height / 100.0;
                }
                bmi = weight / (height * height);
            }

            String category;
            if (bmi < 18.5) {
                category = "Underweight";
            } else if (bmi <= 24.9) {
                category = "Normal";
            } else if (bmi <= 29.9) {
                category = "Overweight";
            } else {
                category = "Obese";
            }

            resultLabel.setText(String.format("Your BMI: %.2f | Category: %s", bmi, category));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BMICalculatorApp().setVisible(true);
            }
        });
    }
}
