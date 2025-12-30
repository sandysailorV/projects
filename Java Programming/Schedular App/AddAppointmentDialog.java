package obj;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class AddAppointmentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField desField;
    private JRadioButton optionOneTime;
    private JRadioButton optionDaily;
    private JRadioButton optionMonthly;
    private JLabel startFieldLabel;
    private JLabel desFieldLabel;
    private JLabel endFieldLabel;
    private Appointment appointment;
    private DatePicker startPicker;
    private DatePicker endPicker;

    ButtonGroup groupTimeType = new ButtonGroup(); // https://docs.oracle.com/javase/tutorial/uiswing/components/button.html#radiobutton


    public AddAppointmentDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setModal(true);


        //Register as buttonGroup for one button to be selected
        groupTimeType.add(optionOneTime);
        groupTimeType.add(optionDaily);
        groupTimeType.add(optionMonthly);


        buttonOK.addActionListener(e -> {
            onOK();
        });

        buttonCancel.addActionListener(e -> {
            onCancel();
        });



        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> {onCancel();}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if (appointment != null) {
            desField.setText(appointment.getDescription());
            startPicker.setDate(appointment.getStartDate());
            endPicker.setDate(appointment.getEndDate());

            if (appointment instanceof OneTimeAppointment) {
                optionOneTime.setSelected(true);
            } else if (appointment instanceof DailyAppointment) {
                optionDaily.setSelected(true);
            } else if (appointment instanceof MonthlyAppointment) {
                optionMonthly.setSelected(true);
            }
        }
    }


    private void addValidationListeners() {

        //Register a listener for the radio buttons.
        optionOneTime.addActionListener((ActionListener) this);
        optionDaily.addActionListener((ActionListener) this);
        optionMonthly.addActionListener((ActionListener) this);

    }

    private void onOK() {
        String description = desField.getText();
        LocalDate start = startPicker.getDate();
        LocalDate end = endPicker.getDate();

        if (start.isAfter(end) ||  end.isBefore(start)) {
            JOptionPane.showMessageDialog(contentPane, "Start Date cannot be after End Date!");
            return;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(contentPane, "Description cannot be empty!");
            return;
        }

        try {
            if (optionOneTime.isSelected()) {
                appointment = new OneTimeAppointment(description, start);
            } else if (optionDaily.isSelected()) {
                appointment = new DailyAppointment(description, start, end);
            } else if (optionMonthly.isSelected()) {
                appointment = new MonthlyAppointment(description, start, end);
            }
            dispose();
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Appointment getAppointment() {
        return appointment;
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {

    }
}