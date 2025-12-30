package obj;

import java.time.LocalDate;
import java.util.Objects;

public class OneTimeAppointment extends Appointment
{
    public OneTimeAppointment(String description, LocalDate date)
    {
        super(description, date, date);
    }

    @Override
    public String toString() {return "(onetime) " + super.toString();}

    @Override
    public boolean occursOn(LocalDate date)
    {
        return date.equals(getStartDate());
    }
}