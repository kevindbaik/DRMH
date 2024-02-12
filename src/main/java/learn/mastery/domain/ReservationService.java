package learn.mastery.domain;

import learn.mastery.data.DataException;
import learn.mastery.data.GuestRepository;
import learn.mastery.data.ReservationRepository;
import learn.mastery.data.HostRepository;
import learn.mastery.models.Host;
import learn.mastery.models.Reservation;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HostRepository hostRepository;
    private  final GuestRepository guestRepository;

    public ReservationService(ReservationRepository reservationRepository, HostRepository hostRepository, GuestRepository guestRepository) {
        this.reservationRepository = reservationRepository;
        this.hostRepository = hostRepository;
        this.guestRepository = guestRepository;
    }

    public List<Reservation> findByHostId(String hostId) throws IOException {
        return reservationRepository.findByHostId(hostId);
    }

    public Result<Reservation> makeReservation(Reservation reservation) throws IOException {
        Result<Reservation> result = new Result<>();

        if (!validateReservation(reservation, result, false)) {
            return result;
        }

        BigDecimal total = calculateTotal(reservation.getStartDate(), reservation.getEndDate(), reservation.getHost());
        reservation.setTotal(total);

        try {
            reservationRepository.add(reservation);
            result.setPayload(reservation);
        } catch (Exception | DataException e) {
            result.addErrorMessage("Failed to save the reservation: " + e.getMessage());
        }

        return result;
    }

    public Result<Reservation> updateReservation(Reservation updatedReservation) throws DataException, IOException {
        Result<Reservation> result = new Result<>();

        if (!validateReservation(updatedReservation, result, true)) {
            return result;
        }

        List<Reservation> existingReservations = reservationRepository.findByHostId(updatedReservation.getHost().getId());
        boolean exists = existingReservations.stream()
                .anyMatch(r -> r.getId() == updatedReservation.getId());

        if (!exists) {
            result.addErrorMessage("Reservation does not exist.");
            return result;
        }

        boolean conflict = existingReservations.stream()
                .filter(r -> r.getId() != updatedReservation.getId())
                .anyMatch(existing -> !isDateRangeValid(updatedReservation, existing));

        if (conflict) {
            result.addErrorMessage("Updated reservation conflicts with an existing reservation.");
            return result;
        }

        boolean success = reservationRepository.update(updatedReservation);
        if (!success) {
            result.addErrorMessage("Failed to update the reservation.");
            return result;
        }

        result.setPayload(updatedReservation);
        return result;
    }

    public Result<Boolean> deleteReservation(int reservationId, String hostId) {
        Result<Boolean> result = new Result<>();

        try {
            List<Reservation> existingReservations = reservationRepository.findByHostId(hostId);

            Reservation reservationToDelete = existingReservations.stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);

            if (reservationToDelete == null) {
                result.addErrorMessage("Reservation with ID " + reservationId + " does not exist.");
                result.setPayload(false);
                return result;
            }

            if (!reservationToDelete.getStartDate().isAfter(LocalDate.now())) {
                result.addErrorMessage("Cannot cancel a reservation that's in the past.");
                result.setPayload(false);
                return result;
            }

            boolean deleted = reservationRepository.delete(reservationToDelete);
            if (!deleted) {
                result.addErrorMessage("Failed to delete the reservation.");
                result.setPayload(false);
                return result;
            }

            result.setPayload(true);
        } catch (Exception | DataException e) {
            result.addErrorMessage("Failed to delete the reservation: " + e.getMessage());
            result.setPayload(false);
        }

        return result;
    }

    // VALIDATIONS VALIDATIONS VALIDATIONS VALIDATIONS VALIDATIONS VALIDATIONS VALIDATIONS VALIDATIONS

    private boolean validateReservation(Reservation reservation, Result<Reservation> result, boolean isUpdate) throws IOException {
        boolean isValid = true;

        if (reservation.getGuest() == null) {
            result.addErrorMessage("Guest is required.");
            isValid = false;
            return isValid;
        }

        if (reservation.getHost() == null) {
            result.addErrorMessage("Host is required.");
            isValid = false;
            return isValid;
        }

        if (reservation.getStartDate() == null || reservation.getEndDate() == null) {
            result.addErrorMessage("Start and end dates are required.");
            isValid = false;
            return isValid;
        }

        if (reservation.getGuest() != null && guestRepository.findByEmail(reservation.getGuest().getEmail()) == null) {
            result.addErrorMessage("Guest does not exist.");
            isValid = false;
            return isValid;
        }

        if (reservation.getHost() != null && hostRepository.findByEmail(reservation.getHost().getEmail()) == null) {
            result.addErrorMessage("Host does not exist.");
            isValid = false;
            return isValid;
        }

        if (!reservation.getStartDate().isBefore(reservation.getEndDate())) {
            result.addErrorMessage("Start date must come before end date.");
            isValid = false;
            return isValid;
        }

        if (!isUpdate) {
            if (!isReservationDateValid(reservation)) {
                result.addErrorMessage("Reservation dates overlap with an existing reservation.");
                isValid = false;
                return isValid;
            }
        }

        if (!reservation.getStartDate().isAfter(LocalDate.now())) {
            result.addErrorMessage("Start date must be in the future.");
            isValid = false;
            return isValid;
        }

        return isValid;
    }


    // HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS

    public BigDecimal calculateTotal(LocalDate startDate, LocalDate endDate, Host host) {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            if (isWeekend(date)) {
                total = total.add(host.getWeekendRate());
            } else {
                total = total.add(host.getStandardRate());
            }
            date = date.plusDays(1);
        }

        return total;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isReservationDateValid(Reservation reservation) {
        List<Reservation> existingReservations = reservationRepository.findByHostId(reservation.getHost().getId());
        return existingReservations.stream().noneMatch(existing ->
                (reservation.getStartDate().isBefore(existing.getEndDate()) || reservation.getStartDate().isEqual(existing.getEndDate())) &&
                        (reservation.getEndDate().isAfter(existing.getStartDate()) || reservation.getEndDate().isEqual(existing.getStartDate())));
    }

    private boolean isReservationDateValidForUpdate(Reservation reservation, Integer updatingReservationId) {
        List<Reservation> existingReservations = reservationRepository.findByHostId(reservation.getHost().getId());
        return existingReservations.stream()
                .filter(existing -> existing.getId() != updatingReservationId)
                .noneMatch(existing ->
                        (reservation.getStartDate().isBefore(existing.getEndDate()) || reservation.getStartDate().isEqual(existing.getEndDate())) &&
                                (reservation.getEndDate().isAfter(existing.getStartDate()) || reservation.getEndDate().isEqual(existing.getStartDate())));
    }

    private boolean isDateRangeValid(Reservation updatedReservation, Reservation existingReservation) {
        LocalDate updatedStart = updatedReservation.getStartDate();
        LocalDate updatedEnd = updatedReservation.getEndDate();
        LocalDate existingStart = existingReservation.getStartDate();
        LocalDate existingEnd = existingReservation.getEndDate();

        return updatedStart.isAfter(existingEnd) || updatedEnd.isBefore(existingStart);
    }

}
