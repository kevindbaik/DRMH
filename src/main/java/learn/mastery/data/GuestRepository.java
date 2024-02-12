package learn.mastery.data;

import learn.mastery.models.Guest;
import java.io.IOException;
import java.util.List;

public interface GuestRepository {
    List<Guest> findAll() throws IOException;

    Guest findById(int guestId) throws IOException;

    Guest findByEmail(String email) throws IOException;
}
