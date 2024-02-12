package learn.mastery.data;

import learn.mastery.models.Guest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuestRepositoryDouble implements GuestRepository {
    private final List<Guest> guests = new ArrayList<>();

    public GuestRepositoryDouble() {
        Guest guest1 = new Guest(1,"TestFirst", "TestLast",
                "tester@test.com","(909) 2222222", "CA");
        guests.add(guest1);
    }
    @Override
    public List<Guest> findAll() throws IOException {
        return null;
    }

    @Override
    public Guest findById(int guestId) throws IOException {
        return null;
    }

    @Override
    public Guest findByEmail(String email) throws IOException {
        for (Guest guest : guests) {
            if (guest.getEmail().equalsIgnoreCase(email)) {
                return guest;
            }
        }
        return null;
    }
}
