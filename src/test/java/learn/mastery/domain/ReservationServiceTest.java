package learn.mastery.domain;

import learn.mastery.data.DataException;
import learn.mastery.data.GuestRepositoryDouble;
import learn.mastery.data.HostRepositoryDouble;
import learn.mastery.data.ReservationRepositoryDouble;
import learn.mastery.models.Guest;
import learn.mastery.models.Host;
import learn.mastery.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {
    private ReservationService service;
    private ReservationRepositoryDouble reservationRepositoryDouble;
    private GuestRepositoryDouble guestRepositoryDouble;
    private HostRepositoryDouble hostRepositoryDouble;

    private Reservation createValidReservation() throws IOException {
        Reservation reservation = new Reservation();
        Guest guest = guestRepositoryDouble.findByEmail("tester@test.com");
        Host host = hostRepositoryDouble.findByEmail("john@example.com");
        reservation.setId(1);
        reservation.setStartDate(LocalDate.now().plusDays(10));
        reservation.setEndDate(LocalDate.now().plusDays(14));
        reservation.setGuest(guest);
        reservation.setHost(host);
        reservation.setGuestId(guest.getGuestId());
        reservation.setTotal(new BigDecimal("500.00"));
        return reservation;
    }

    private Reservation createValidReservationToUpdate() throws IOException {
        Reservation reservation = new Reservation();
        Guest guest = guestRepositoryDouble.findByEmail("tester@test.com");
        Host host = hostRepositoryDouble.findByEmail("john@example.com");
        reservation.setId(2);
        reservation.setStartDate(LocalDate.now().plusDays(100));
        reservation.setEndDate(LocalDate.now().plusDays(104));
        reservation.setGuest(guest);
        reservation.setHost(host);
        reservation.setGuestId(guest.getGuestId());
        reservation.setTotal(new BigDecimal("500.00"));
        return reservation;
    }

    @BeforeEach
    void setUp() throws IOException {
        guestRepositoryDouble = new GuestRepositoryDouble();
        hostRepositoryDouble = new HostRepositoryDouble();
        reservationRepositoryDouble = new ReservationRepositoryDouble(guestRepositoryDouble, hostRepositoryDouble);
        service = new ReservationService(reservationRepositoryDouble, hostRepositoryDouble,  guestRepositoryDouble);
    }

    @Test
    void shouldMakeReservation() throws IOException {
        Reservation validReservation = createValidReservation();

        Result<Reservation> result = service.makeReservation(validReservation);

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
        assertEquals(validReservation.getStartDate(), result.getPayload().getStartDate());
    }

    @Test
    void shouldNotMakeReservationWithMissingGuest() throws IOException {
        Reservation reservationMissingGuest = createValidReservation();
        reservationMissingGuest.setGuest(null);

        Result<Reservation> result = service.makeReservation(reservationMissingGuest);

        assertFalse(result.isSuccess());
        assertEquals("Guest is required.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotMakeReservationWithMissingHost() throws IOException {
        Reservation reservationMissingHost = createValidReservation();
        reservationMissingHost.setHost(null);

        Result<Reservation> result = service.makeReservation(reservationMissingHost);

        assertFalse(result.isSuccess());
        assertEquals("Host is required.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotMakeReservationWhenStartDateInFuture() throws IOException {
        Reservation reservation = createValidReservation();
        reservation.setStartDate(reservation.getStartDate().minusDays(100));

        Result<Reservation> result = service.makeReservation(reservation);

        assertFalse(result.isSuccess());
        assertEquals("Start date must be in the future.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotMakeReservationWhenStartDateAfterEndDate() throws IOException {
        Reservation reservation = createValidReservation();
        reservation.setEndDate(reservation.getStartDate().minusDays(5));

        Result<Reservation> result = service.makeReservation(reservation);

        assertFalse(result.isSuccess());
        assertEquals("Start date must come before end date.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotMakeReservationWithOverlappingDates() throws IOException {
        Reservation reservation = createValidReservation();
        Result<Reservation> validResult = service.makeReservation(reservation);

        Guest guest = guestRepositoryDouble.findByEmail("tester@test.com");
        Host host = hostRepositoryDouble.findByEmail("john@example.com");
        Reservation overlappingReservation = new Reservation(2, LocalDate.now().plusDays(11),
                LocalDate.now().plusDays(15),guest ,host ,guest.getGuestId(), new BigDecimal("300.00"));

        Result<Reservation> inValidResult = service.makeReservation(overlappingReservation);

        assertFalse(inValidResult.isSuccess());
        assertEquals("Reservation dates overlap with an existing reservation.", inValidResult.getErrorMessages().get(0));
    }

    @Test
    void shouldUpdateReservation() throws IOException, DataException {
        Reservation reservation = createValidReservationToUpdate();
        reservationRepositoryDouble.add(reservation);

        reservation.setEndDate(reservation.getEndDate().plusDays(1));
        Result<Reservation> result = service.updateReservation(reservation);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateReservationWithConflict() throws IOException, DataException {
        Reservation reservation = createValidReservationToUpdate();
        reservationRepositoryDouble.add(reservation);

        reservation.setStartDate(LocalDate.now().plusDays(200));
        reservation.setEndDate(LocalDate.now().plusDays(204));

        Result<Reservation> result = service.updateReservation(reservation);

        assertFalse(result.isSuccess());
        assertEquals("Updated reservation conflicts with an existing reservation.", result.getErrorMessages().get(0));
    }

    @Test
    void shouldDeleteReservation() throws DataException, IOException {
        Reservation preExistingReservation = createValidReservation();
        reservationRepositoryDouble.add(preExistingReservation);

        Result<Boolean> result = service.deleteReservation(preExistingReservation.getId(), preExistingReservation.getHost().getId());

        assertTrue(result.getPayload());
        assertTrue(result.getErrorMessages().isEmpty());


        // I AM DOUBLE-CHECKING TO MAKE SURE RESERVATION DELETED
        List<Reservation> remainingReservations = reservationRepositoryDouble.findByHostId(preExistingReservation.getHost().getId());
        boolean reservationExists = remainingReservations.stream()
                .anyMatch(r -> r.getId() == preExistingReservation.getId());
        assertFalse(reservationExists);
    }


    @Test
    void deleteReservation_PastStartDate() throws DataException, IOException {
        Reservation pastReservation = createValidReservation();
        pastReservation.setStartDate(LocalDate.now().minusDays(10));
        pastReservation.setEndDate(LocalDate.now().minusDays(8));
        reservationRepositoryDouble.add(pastReservation);

        Result<Boolean> result = service.deleteReservation(pastReservation.getId(), pastReservation.getHost().getId());

        assertFalse(result.getPayload());
        assertTrue(result.getErrorMessages().contains("Cannot cancel a reservation that's in the past."));
    }
}
