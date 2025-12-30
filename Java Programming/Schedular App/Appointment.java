package obj;

import java.time.LocalDate;

public abstract class Appointment implements Comparable<Appointment>
{
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public Appointment(String description, LocalDate startDate, LocalDate endDate)
    {
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDescription()
    {
        return description;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    protected boolean inBetween(LocalDate date)
    {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public abstract boolean occursOn(LocalDate date);

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + ": " + description +
                " from " + startDate + " to " + endDate;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Appointment)) return false;
        Appointment other = (Appointment) obj;
        return description.equals(other.description) &&
                startDate.equals(other.startDate) &&
                endDate.equals(other.endDate);
    }

    @Override
    public int compareTo(Appointment other)
    {
        if (!startDate.equals(other.startDate))
        {
            return startDate.compareTo(other.startDate);
        }
        if (!endDate.equals(other.endDate))
        {
            return endDate.compareTo(other.endDate);
        }
        return description.compareTo(other.description);
    }
}