package obj;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Comparator;

public class MainGui {
    private JPanel MainPanel;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JList <Appointment> displayList;
    private JRadioButton displayAll;
    private JComboBox<String> sortByComboBox;
    private JTextField textField1;
    private JButton displayBtn;
    private JLabel titleLabel;

    private AppointmentManager manager;
    private DatePicker datePicker;
    private Comparator comparator;

    public MainGui() {
        manager = new AppointmentManager();

        sortByComboBox.addItem("Sort by Date");
        sortByComboBox.addItem("Sort by Description ");

        addBtn.addActionListener(this::addAppointment);
        editBtn.addActionListener(this::addAppointment);
        deleteBtn.addActionListener(this::deleteAppointment);

        // Main layout
        JFrame frame = new JFrame("Appointment Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Main Panel
        MainPanel = new JPanel(new BorderLayout(10, 10));
        MainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title Panel
        JLabel titleLabel = new JLabel("Appointment Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 112, 147));
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        MainPanel.add(titleLabel, BorderLayout.NORTH);

        // Control Panel (Top Buttons and Filters)
        JPanel controlPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        controlPanel.add(addBtn);
        controlPanel.add(editBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(displayBtn);
        controlPanel.add(displayAll);
        controlPanel.add(sortByComboBox);

        // Center Panel - Appointment List
        displayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(displayList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        listScrollPane.setPreferredSize(new Dimension(500, 300));

        // Footer - Status or Instructions
        JLabel footerLabel = new JLabel("Use the controls above to manage your appointments.", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Add Components to Main Panel
        MainPanel.add(controlPanel, BorderLayout.NORTH);
        MainPanel.add(listScrollPane, BorderLayout.CENTER);
        MainPanel.add(footerLabel, BorderLayout.SOUTH);

        // Add Action Listeners
        addBtn.addActionListener(this::addAppointment);
        editBtn.addActionListener(this::editAppointment);
        deleteBtn.addActionListener(this::deleteAppointment);
        sortByComboBox.addActionListener(this::sortAppointment);

        // Frame Settings
        frame.add(MainPanel);
        frame.setVisible(true);
    }


    private void addAppointment(ActionEvent e) {
        try {
            AddAppointmentDialog dialog = new AddAppointmentDialog();
            dialog.pack();
            dialog.setVisible(true);
            Appointment appointment = dialog.getAppointment();
            if (e.getSource().equals(addBtn)) {
                manager.add(appointment);
                Appointment[] appointments = manager.getAppointmentsOn(null, null);
                displayList.setListData(appointments); //refresh
                // Get selected date from DatePicker
                LocalDate selectedDate = datePicker.getDate();
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }




    private void deleteAppointment(ActionEvent e) {
        try {
            if (e.getSource().equals(deleteBtn)) {
                manager.delete(displayList.getSelectedValue());
                Appointment[] appointments = manager.getAppointmentsOn(null, null);
                displayList.setListData(appointments); // Get selected date from DatePicker
                LocalDate selectedDate = datePicker.getDate();
                System.out.println("Delete btn clicked");
            } else if (e.getSource().equals(editBtn))
                System.out.println("Edit btn clicked");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void editAppointment(ActionEvent e) {
        try {
            Appointment selected = displayList.getSelectedValue();
            AddAppointmentDialog dialog = new AddAppointmentDialog();
            dialog.setVisible(true);
            Appointment updatedAppointment = dialog.getAppointment();
            manager.update(selected, updatedAppointment);

            //refresh
            Appointment[] appointments = manager.getAppointmentsOn(null, null);
            displayList.setListData(appointments);

            JOptionPane.showMessageDialog(MainPanel, "Appointment updated successfully!");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void sortAppointment(ActionEvent e) {
        String selectedSortOrder = (String) sortByComboBox.getSelectedItem();

        if (selectedSortOrder.equals("Sort by Date")) {
            Appointment[] appointments = manager.getAppointmentsOn(null, null);

            //
            Appointment selected = displayList.getSelectedValue();
            LocalDate selectedDate = datePicker.getDate();


            // Sort the List by date
            comparator = Comparator.comparing(Appointment::getStartDate);

            // Get sorted appointments using the selected comparator
            Appointment[] sortedAppointments = manager.getAppointmentsOn(null, comparator);
        } else if (selectedSortOrder.equals("Sort by Description")) {
            Appointment[] appointments = manager.getAppointmentsOn(null, null);
            comparator = Comparator.comparing(Appointment::getDescription);
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame( "Application App");
        frame.setContentPane(new MainGui().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //click close btn, whole app closes
        frame.pack();
        frame.setVisible(true);
    }
}