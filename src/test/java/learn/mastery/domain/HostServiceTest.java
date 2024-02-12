package learn.mastery.domain;

import learn.mastery.data.GuestRepositoryDouble;
import learn.mastery.data.HostRepositoryDouble;
import learn.mastery.data.ReservationRepositoryDouble;
import learn.mastery.models.Host;
import learn.mastery.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HostServiceTest {

    private HostService service;
    private HostRepositoryDouble hostRepositoryDouble;
    private ReservationRepositoryDouble reservationRepositoryDouble;
    private GuestRepositoryDouble guestRepositoryDouble;

    @BeforeEach
    void setUp() throws IOException {
        hostRepositoryDouble = new HostRepositoryDouble();
        guestRepositoryDouble = new GuestRepositoryDouble();
        reservationRepositoryDouble = new ReservationRepositoryDouble(guestRepositoryDouble, hostRepositoryDouble);
        service = new HostService(hostRepositoryDouble, reservationRepositoryDouble);
    }

    @Test
    void findHostByEmailFound() throws IOException {
        Result<Host> result = service.findHostByEmail("john@example.com");
        assertTrue(result.isSuccess());
    }

    @Test
    void findHostByEmailNotFound() throws IOException {
        Result<Host> result = service.findHostByEmail("nonexistent@example.com");
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessages().contains("Host with email nonexistent@example.com does not exist."));
    }

    @Test
    void checkForReservations() throws IOException {
        Result<Host> result = service.checkForReservations("john@example.com");
        assertTrue(result.isSuccess());
    }

    @Test
    void checkForReservationsNoReservations() throws IOException {
        Result<Host> result = service.checkForReservations("noreservations@example.com");
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessages().contains("Host does not have any reservations."));
    }
}
