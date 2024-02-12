package learn.mastery.ui;

import learn.mastery.models.Host;
import learn.mastery.models.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

public class View {
    private final ConsoleIO io;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    public View(ConsoleIO io) {
        this.io = io;
    }

    public void displayMainMenu() {
        io.println("\nMain Menu");
        io.println("=========");
        for (MainMenuOption option : MainMenuOption.values()) {
            io.println(option.getValue() + ". " + option.getMessage());
        }
    }

    public int getMainMenuSelection() {
        return io.readInt("Select [0-4]: ", 0, 4);
    }

    public void displayReservations(List<Reservation> reservations) {
        io.println(" ");

        if (reservations.isEmpty()) {
            io.println("No reservations found for this host.");
            return;
        }

        Host host = reservations.get(0).getHost();
        String hostInfo = host.getLastName() + ": " + host.getCity() + ", " + host.getState();
        io.println(hostInfo);
        io.println("=================");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        reservations.sort(Comparator.comparing(Reservation::getStartDate));

        reservations.forEach(reservation ->
                io.printf("Reservation ID: %d, Dates: %s - %s, Guest: %s, Email: %s%n",
                        reservation.getId(),
                        reservation.getStartDate().format(dateFormatter),
                        reservation.getEndDate().format(dateFormatter),
                        reservation.getGuest().getLastName() + ", " + reservation.getGuest().getFirstName(),
                        reservation.getGuest().getEmail()));
    }



    public String readHostEmail() {
        return io.readRequiredString("Enter host email: ");
    }

    public String readGuestEmail() {
        return io.readRequiredString("Enter guest email: ");
    }

    public LocalDate readDate(String prompt) {
        return io.readLocalDate(prompt);
    }
    public void displayHeader(String message) {
        io.println("");
        io.println(message);
        io.println("=".repeat(message.length()));
    }

    public void displayMessage(String message) {
        io.println(message);
    }

    public void displayResult(String message) {
        io.println(" ");
        io.println(message);
    }

    public void displayStatus(boolean success, String message) {
        if (success) {
            io.println("\n[SUCCESS] " + message);
        } else {
            io.println("\n[ERROR] " + message);
        }
    }

    public void displayStatus(boolean success, List<String> messages) {
        if (success) {
            io.println("\n[SUCCESS]");
        } else {
            io.println("\n[ERROR]");
        }
        for (String message : messages) {
            io.println(message);
        }
    }

    public void displayException(Exception ex) {
        displayHeader("A critical error occurred:");
        io.println(ex.getMessage());
    }

    public boolean confirmReservation(Reservation reservation, BigDecimal estimatedTotal) {
        displayHeader("Reservation Summary");
        String summary = String.format("Start Date: %s\nEnd Date: %s",
                reservation.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                reservation.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        displayMessage(summary);
        return displayTotalAndConfirm(estimatedTotal);
    }

    public boolean displayTotalAndConfirm(BigDecimal total) {
        io.printf("Total: $%.2f%n", total);

        String input = io.readRequiredString("Confirm reservation? (y/n): ").trim().toLowerCase();

        if ("y".equals(input) || "yes".equals(input)) {
            return true;
        } else {
            return false;
        }
    }

    public LocalDate readDateWithDefault(String prompt, LocalDate defaultValue) {
        String input = io.readRequiredString(prompt);

        if (input.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return LocalDate.parse(input, dateFormatter);
        } catch (DateTimeParseException e) {
            io.println("[INVALID] Enter a date in MM/dd/yyyy format or press Enter to use the default value.");
            return readDateWithDefault(prompt, defaultValue);
        }
    }

    public int readReservationId() {
        return io.readInt("Enter the ID of the reservation: ");
    }

    public boolean confirmCancellation(Reservation reservation) {
        displayMessage("Confirm cancellation of the following reservation:");
        displayReservationDetails(reservation);
        return io.readBoolean("Confirm cancellation? (y/n): ");
    }

    public void displayReservationDetails(Reservation reservation) {
        io.printf("Reservation ID: %d\n", reservation.getId());
        io.printf("Start Date: %s\n", reservation.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        io.printf("End Date: %s\n", reservation.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

}
