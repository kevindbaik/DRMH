package learn.mastery.ui;

import learn.mastery.data.DataException;
import learn.mastery.domain.GuestService;
import learn.mastery.domain.HostService;
import learn.mastery.domain.ReservationService;
import learn.mastery.domain.Result;
import learn.mastery.models.Guest;
import learn.mastery.models.Host;
import learn.mastery.models.Reservation;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private final GuestService guestService;
    private final HostService hostService;
    private final ReservationService reservationService;
    private final View view;

    public Controller(GuestService guestService, HostService hostService, ReservationService reservationService, View view) {
        this.guestService = guestService;
        this.hostService = hostService;
        this.reservationService = reservationService;
        this.view = view;
    }

    public void run() {
        view.displayHeader("Welcome to Don't Wreck My House");
        try {
            runMainMenu();
        } catch (Exception | DataException ex) {
            view.displayException((Exception) ex);
        }
        view.displayHeader("Goodbye.");
    }

    public void runMainMenu() throws IOException, DataException {
        boolean running = true;
        while (running) {
            view.displayMainMenu();
            int selection = view.getMainMenuSelection();
            MainMenuOption selectedOption = MainMenuOption.fromValue(selection);

            switch (selectedOption) {
                case EXIT:
                    running = false;
                    break;
                case VIEW_RESERVATIONS_FOR_HOST:
                    viewReservationsForHost();
                    break;
                case MAKE_RESERVATION:
                    makeReservation();
                    break;
                case EDIT_RESERVATION:
                    editReservation();
                    break;
                case CANCEL_RESERVATION:
                    cancelReservation();
                    break;
                default:
                    view.displayMessage("Invalid selection. Please try again.");
            }
        }
    }

    private void viewReservationsForHost() throws IOException {
        String email = view.readHostEmail();
        Result<Host> hostResult = hostService.findHostByEmail(email);

        if (!hostResult.isSuccess()) {
            view.displayStatus(false, hostResult.getErrorMessages());
            return;
        }

        Host host = hostResult.getPayload();
        List<Reservation> reservations = reservationService.findByHostId(host.getId());
        view.displayReservations(reservations);
    }

    private void makeReservation() throws IOException {
        // verifying host exists
        String hostEmail = view.readHostEmail();
        Result<Host> hostResult = hostService.findHostByEmail(hostEmail);
        if (!hostResult.isSuccess()) {
            view.displayStatus(false, hostResult.getErrorMessages());
            return;
        }
        Host host = hostResult.getPayload();

        // verifying guest exists
        String guestEmail = view.readGuestEmail();
        Result<Guest> guestResult = guestService.findGuestByEmail(guestEmail);
        if (!guestResult.isSuccess()) {
            view.displayStatus(false, guestResult.getErrorMessages());
            return;
        }
        Guest guest = guestResult.getPayload();

        // now i display reservations for that host
        List<Reservation> reservations = reservationService.findByHostId(host.getId());
        if (reservations.isEmpty()) {
            view.displayMessage("Host has no reservations currently scheduled.");
        } else {
            view.displayReservations(reservations);
        }

        // now i ask user for dates
        LocalDate startDate = view.readDate("Enter start date (MM/dd/yyyy): ");
        LocalDate endDate = view.readDate("Enter end date (MM/dd/yyyy): ");

        // now i create a reservation instance for
        Reservation reservation = new Reservation();
        reservation.setHost(host);
        reservation.setGuest(guest);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setGuestId(guest.getGuestId());

        // calculate total to show user
        BigDecimal estimatedTotal = reservationService.calculateTotal(reservation.getStartDate(), reservation.getEndDate(), host);
        if (estimatedTotal.compareTo(BigDecimal.ZERO) <= 0) {
            view.displayMessage("Start date must be before end date.");
            return;
        }

        // confirm with user to make or cancel
        if (view.confirmReservation(reservation, estimatedTotal)) {
            Result<Reservation> saveResult = reservationService.makeReservation(reservation);
            if (saveResult.isSuccess()) {
                view.displayResult("Reservation successfully made with ID: " + saveResult.getPayload().getId());
            } else {
                view.displayStatus(false, saveResult.getErrorMessages());
            }
        } else {
            view.displayResult("Reservation cancelled.");
        }
    }

    private void editReservation() throws IOException, DataException {
        String hostEmail = view.readHostEmail();
        Result<Host> hostResult = hostService.findHostByEmail(hostEmail);
        if (!hostResult.isSuccess()) {
            view.displayStatus(false, hostResult.getErrorMessages());
            return;
        }
        Host host = hostResult.getPayload();

        List<Reservation> reservations = reservationService.findByHostId(host.getId());
        if (reservations.isEmpty()) {
            view.displayMessage("No reservations found for this host.");
            return;
        }
        view.displayReservations(reservations);

        int reservationId = view.readReservationId();
        Reservation reservationToEdit = findReservationById(reservations, reservationId);
        if (reservationToEdit == null) {
            view.displayResult("Reservation not found.");
            return;
        }

        LocalDate newStartDate = view.readDateWithDefault("Enter new start date (MM/dd/yyyy) [" +
                        reservationToEdit.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "]: ",
                reservationToEdit.getStartDate());
        LocalDate newEndDate = view.readDateWithDefault("Enter new end date (MM/dd/yyyy) [" +
                        reservationToEdit.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "]: ",
                reservationToEdit.getEndDate());

        reservationToEdit.setStartDate(newStartDate);
        reservationToEdit.setEndDate(newEndDate);

        BigDecimal estimatedTotal = reservationService.calculateTotal(reservationToEdit.getStartDate(), reservationToEdit.getEndDate(), host);
        if (view.confirmReservation(reservationToEdit, estimatedTotal)) {
            Result<Reservation> updateResult = reservationService.updateReservation(reservationToEdit);
            if (updateResult.isSuccess()) {
                view.displayResult("Reservation " + reservationToEdit.getId() + " successfully updated.");
            } else {
                view.displayStatus(false, updateResult.getErrorMessages());
            }
        } else {
            view.displayResult("Update cancelled.");
        }
    }

    private void cancelReservation() throws IOException {
        String hostEmail = view.readHostEmail();
        Result<Host> hostResult = hostService.findHostByEmail(hostEmail);
        if (!hostResult.isSuccess()) {
            view.displayStatus(false, hostResult.getErrorMessages());
            return;
        }
        Host host = hostResult.getPayload();


        List<Reservation> reservations = reservationService.findByHostId(host.getId())
                .stream()
                .filter(reservation -> reservation.getStartDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());


        if (reservations.isEmpty()) {
            view.displayResult("No future reservations found for this host.");
            return;
        }

        view.displayReservations(reservations);

        int reservationId = view.readReservationId();
        Reservation reservationToCancel = findReservationById(reservations, reservationId);
        if (reservationToCancel == null) {
            view.displayResult("Reservation ID not found.");
            return;
        }

        boolean confirm = view.confirmCancellation(reservationToCancel);
        if (!confirm) {
            view.displayResult("Cancellation aborted.");
            return;
        }

        Result<Boolean> cancellationResult = reservationService.deleteReservation(reservationToCancel.getId(), host.getId());
        if (cancellationResult.isSuccess()) {
            view.displayResult("Reservation successfully cancelled.");
        } else {
            view.displayStatus(false, cancellationResult.getErrorMessages());
        }
    }

    private Reservation findReservationById(List<Reservation> reservations, int reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getId() == reservationId) {
                return reservation;
            }
        }
        return null;
    }
}
