package learn.mastery.ui;

public enum MainMenuOption {
    EXIT(0, "Exit"),
    VIEW_RESERVATIONS_FOR_HOST(1, "View Reservations for Host"),
    MAKE_RESERVATION(2, "Make a Reservation"),
    EDIT_RESERVATION(3, "Edit a Reservation"),
    CANCEL_RESERVATION(4, "Cancel a Reservation");

    private final int value;
    private final String message;

    MainMenuOption(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MainMenuOption fromValue(int value) {
        for (MainMenuOption option : MainMenuOption.values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        return EXIT;
    }
}
