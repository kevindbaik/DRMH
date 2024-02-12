package learn.mastery.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reservation {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Guest guest;
    private Host host;
    private int guestId;
    private BigDecimal total;

    public Reservation() {
    }

    public Reservation(int id, LocalDate startDate, LocalDate endDate, Guest guest, Host host, int guestId, BigDecimal total) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.guest = guest;
        this.host = host;
        this.guestId = guestId;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", guest=" + guest +
                ", host=" + host +
                ", guestId=" + guestId +
                ", total=" + total +
                '}';
    }
}
