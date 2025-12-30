package obj;

import java.time.LocalDate;
import java.util.Objects;

public class DailyAppointment extends Appointment {
    public DailyAppointment(String description, LocalDate startDate, LocalDate endDate) {
        super(description, startDate, endDate);
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return inBetween(date);
    }
}