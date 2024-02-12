package learn.mastery.domain;

import learn.mastery.data.GuestRepository;
import learn.mastery.models.Guest;

import java.io.IOException;

public class GuestService {
    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public Result<Guest> findGuestByEmail(String email) {
        Result<Guest> result = new Result<>();
        Guest guest;
        try {
            guest = guestRepository.findByEmail(email);
        } catch (IOException e) {
            result.addErrorMessage("Unable to access guest data: " + e.getMessage());
            return result;
        }

        if (guest == null) {
            result.addErrorMessage("Guest with email " + email + " does not exist.");
            return result;
        }

        result.setPayload(guest);
        return result;
    }
}
