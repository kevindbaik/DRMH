package learn.mastery.data;

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

public class ReservationFileRepositoryTest {
    private ReservationFileRepository repository;
    private GuestFileRepository guestRepository;
    private HostFileRepository hostRepository;

    @BeforeEach
    void setUp() {
        String guestsFilePath = "./data/guests_test.csv";
        String hostsFilePath = "./data/hosts_test.csv";
        String reservationsDirPath = "./data/reservations_test";

        guestRepository = new GuestFileRepository(guestsFilePath);
        hostRepository = new HostFileRepository(hostsFilePath);
        repository = new ReservationFileRepository(reservationsDirPath, guestRepository, hostRepository);
    }


    @Test
    void findByHostIdShouldReturnReservationsForHost() throws Exception {
        String hostId = "9d469342-ad0b-4f5a-8d28-e81e690ba29a";

        List<Reservation> reservations = repository.findByHostId(hostId);

        assertFalse(reservations.isEmpty(), "Reservations list should not be empty.");
        assertTrue(reservations.stream()
                        .allMatch(reservation -> reservation.getGuestId() == 18),
                "All reservations should have a guestId of 18");
    }

    @Test
    void findByHostIdShouldReturnEmptyForNonExistingHost() throws Exception {
        String hostId = "testing-fake-uuid-123";

        List<Reservation> reservations = repository.findByHostId(hostId);
        assertTrue(reservations.isEmpty(), "Reservations list should not be empty.");

    }

    @Test
    void shouldAddReservation() throws DataException {
        Guest guest = new Guest(12,"Bob", "Smith", "bobsmith@hi.com", "(000) 000123", "CA");
        Host host = new Host("testing-123-add", "AddTest", "Addtest@hoster.com", "(444) 2930495", "1 TestHost Rd",
                "Chino Hills", "CA", "91709", new BigDecimal("200.00"), new BigDecimal("250.00"));
        Reservation testReservation = new Reservation(2, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 4),
                guest, host, 12, new BigDecimal("600.00"));

        Reservation addedReservation = repository.add(testReservation);

        List<Reservation> reservations = repository.findByHostId("testing-123-add");
        assertEquals(guest.getGuestId(), addedReservation.getGuestId(), "The guest ID of the reservation should match the expected guest ID.");
    }

    @Test
    void shouldUpdateReservation() throws DataException, IOException {
        Guest guest = guestRepository.findById(1);
        Host host = hostRepository.findById("bzzzz829-c663-48fc-8bf3-7fca47a7a333");

        Reservation newReservation = new Reservation(3, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 4),
                guest, host, 1, new BigDecimal("300.00"));
        Reservation addedReservation = repository.add(newReservation);

        BigDecimal totalBeforeUpdate = addedReservation.getTotal();

        addedReservation.setTotal(new BigDecimal("350.00"));
        boolean updateResult = repository.update(addedReservation);
        assertTrue(updateResult, "The update should be successful.");

        List<Reservation> reservations = repository.findByHostId(addedReservation.getHost().getId());
        Reservation updatedReservation = reservations.stream()
                .filter(r -> r.getId() == addedReservation.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Updated reservation not found"));

        assertEquals(new BigDecimal("350.00"), updatedReservation.getTotal(), "The total after update should be updated.");
        assertNotEquals(totalBeforeUpdate, updatedReservation.getTotal(), "The total before and after the update should not be equal.");
    }

    @Test
    void shouldDeleteReservation() throws DataException, IOException {
        Guest guest = guestRepository.findById(1);
        Host host = hostRepository.findById("bzzzz829-c663-48fc-8bf3-7fca47a7a333");

        Reservation newReservation = new Reservation(3, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 4),
                guest, host, 1, new BigDecimal("100.00"));
        Reservation addedReservation = repository.add(newReservation);

        boolean deleteResult = repository.delete(addedReservation);
        assertTrue(deleteResult, "Reservation should be successfully deleted.");
        List<Reservation> reservationsAfterDeletion = repository.findByHostId("test-host-id");
        assertFalse(reservationsAfterDeletion.contains(addedReservation), "Deleted reservation should not be in the list.");
    }
}

