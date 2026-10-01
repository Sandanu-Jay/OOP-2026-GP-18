import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ruhuna Faculty of Technology - ICT2132 Practical 12
 * Simple BMI Calculator using OOP Concepts
 */
public class BMICalculator extends JFrame {

    // Global variables for components
    private JRadioButton metricBtn, englishBtn;
    private JRadioButton maleBtn, femaleBtn;
    private JTextField heightField, weightField;
    private JLabel resultLabel;

    // Constructor - UI එක නිර්මාණය කිරීම මෙහි සිදු වේ
    public BMICalculator() {
        // Window එකෙහි මූලික සැකසුම්
        setTitle("BMI Calculator");
        setSize(350, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Screen එක මැදට ගැනීමට
        setLayout(new GridLayout(7, 1, 10, 10)); // සරලව Grid එකක් ලෙස Layout එක සකසා ඇත

        // 1. Header Section
        JLabel titleLabel = new JLabel("BMI CALCULATOR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);

        // 2. Unit Selection Section (Metric vs English)
        JPanel unitPanel = new JPanel();
        metricBtn = new JRadioButton("Metric (kg/m)", true); // Default true
        englishBtn = new JRadioButton("English (lb/in)");
        ButtonGroup unitGroup = new ButtonGroup(); // එකක් තෝරන විට අනෙක අක්රිය වීමට
        unitGroup.add(metricBtn);
        unitGroup.add(englishBtn);
        unitPanel.add(metricBtn);
        unitPanel.add(englishBtn);
        add(unitPanel);

        // 3. Gender Selection Section
        JPanel genderPanel = new JPanel();
        maleBtn = new JRadioButton("Male", true);
        femaleBtn = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleBtn);
        genderGroup.add(femaleBtn);
        genderPanel.add(maleBtn);
        genderPanel.add(femaleBtn);
        add(genderPanel);

        // 4. Height Input Section
        JPanel heightPanel = new JPanel();
        heightPanel.add(new JLabel("Height: "));
        heightField = new JTextField(10);
        heightPanel.add(heightField);
        add(heightPanel);

        // 5. Weight Input Section
        JPanel weightPanel = new JPanel();
        weightPanel.add(new JLabel("Weight: "));
        weightField = new JTextField(10);
        weightPanel.add(weightField);
        add(weightPanel);

        // 6. Calculate Button Section
        JButton calcBtn = new JButton("CALCULATE");
        add(calcBtn);

        // 7. Result Display Section
        resultLabel = new JLabel("Your BMI will appear here", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(resultLabel);

        // Event Listener - Calculate බොත්තම එබූ විට සිදුවන දේ
        calcBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateBMI(); // BMI ගණනය කරන Method එක Call කිරීම
            }
        });
    }

    // BMI Logic එක සහ ඛණ්ඩනය කිරීම් (Calculation Logic)
    private void calculateBMI() {
        try {
            // Text fields වලින් අගයන් ලබාගෙන Double වලට හරවා ගැනීම
            double height = Double.parseDouble(heightField.getText());
            double weight = Double.parseDouble(weightField.getText());
            double bmi = 0;

            // 1. පැවරුමේ ඇති Formulas වලට අනුව ගණනය කිරීම
            if (metricBtn.isSelected()) {
                // Metric formula: weight (kg) / [height (m) * height (m)]
                // පරිශීලකයා සෙන්ටිමීටර වලින් දැමුවහොත් එය මීටර් කිරීමට 100න් බෙදයි
                if (height > 3) height = height / 100;
                bmi = weight / (height * height);
            } else {
                // English formula: (weight (lb) * 703) / [height (in) * height (in)]
                bmi = (weight * 703) / (height * height);
            }

            // දශමස්ථාන 1කට වටයන්න
            bmi = Math.round(bmi * 10.0) / 10.0;

            // 2. DHHS / NIH BMI සීමාවන්ට අනුව Category එක සෙවීම
            String category;
            if (bmi < 18.5) {
                category = "Underweight";
            } else if (bmi >= 18.5 && bmi <= 24.9) {
                category = "Normal";
            } else if (bmi >= 25 && bmi <= 29.9) {
                category = "Overweight";
            } else {
                category = "Obese";
            }

            // ප්‍රතිඵලය Screen එකේ පෙන්වීම
            resultLabel.setText("BMI: " + bmi + " (" + category + ")");

        } catch (NumberFormatException ex) {
            // අකුරු හෝ හිස් තැන් ඇතුළත් කළහොත් පෙන්වන Error එක
            resultLabel.setText("Please enter valid numbers!");
        }
    }

    // Main Method එක - Program එක Run වන ස්ථානය
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BMICalculator().setVisible(true);
            }
        });
    }
}
