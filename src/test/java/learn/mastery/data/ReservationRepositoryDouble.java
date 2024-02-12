package learn.mastery.data;

import learn.mastery.models.Guest;
import learn.mastery.models.Host;
import learn.mastery.models.Reservation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationRepositoryDouble implements ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();
    private final GuestRepositoryDouble guestRepositoryDouble;
    private final HostRepositoryDouble hostRepositoryDouble;

    public ReservationRepositoryDouble(GuestRepositoryDouble guestRepositoryDouble, HostRepositoryDouble hostRepositoryDouble) throws IOException {
        this.guestRepositoryDouble = guestRepositoryDouble;
        this.hostRepositoryDouble = hostRepositoryDouble;

        Guest guest = guestRepositoryDouble.findByEmail("tester@test.com");
        Host host = hostRepositoryDouble.findByEmail("john@example.com");

        Reservation testReservation = new Reservation();
        testReservation.setId(1);
        testReservation.setHost(host);
        testReservation.setGuest(guest);
        testReservation.setGuestId(1);
        testReservation.setStartDate(LocalDate.now().plusDays(200));
        testReservation.setEndDate(LocalDate.now().plusDays(204));

        reservations.add(testReservation);
    }

    @Override
    public List<Reservation> findByHostId(String hostId) {
        return reservations.stream()
                .filter(reservation -> reservation.getHost().getId().equals(hostId))
                .collect(Collectors.toList());
    }


    @Override
    public Reservation add(Reservation reservation) {
        reservation.setId(reservations.size() + 1);
        reservations.add(reservation);
        return reservation;
    }

    @Override
    public boolean update(Reservation reservation) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId() == reservation.getId()) {
                reservations.set(i, reservation);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Reservation reservation) {
        return reservations.removeIf(r -> r.getId() == reservation.getId());
    }
}
