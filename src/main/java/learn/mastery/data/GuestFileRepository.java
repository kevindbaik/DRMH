package learn.mastery.data;

import learn.mastery.models.Guest;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GuestFileRepository implements GuestRepository {
    private static final String HEADER = "guest_id,first_name,last_name,email,phone,state";
    private static final String DELIMITER = ",";
    private final String filePath;

    public GuestFileRepository(String filePath) {
        this.filePath = filePath;
    }

    // FINDING ALL GUESTS
    public List<Guest> findAll() throws IOException {
        List<Guest> guests = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER);
                if (fields.length == 6) {
                    Guest guest = deserialize(fields);
                    guests.add(guest);
                }
            }
        }
        return guests;
    }

    // FIND ONE GUEST BY ID
    public Guest findById(int guestId) throws IOException {
        List<Guest> guests = findAll();
        for (Guest guest : guests) {
            if (guest.getGuestId() == guestId) {
                return guest;
            }
        }
        return null;
    }

    public Guest findByEmail(String email) throws IOException {
        List<Guest> guests = findAll();
        for (Guest guest : guests) {
            if (guest.getEmail().equalsIgnoreCase(email)) {
                return guest;
            }
        }
        return null;
    }

    // DESERIALIZE HELPER
    private Guest deserialize(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String firstName = fields[1];
        String lastName = fields[2];
        String email = fields[3];
        String phone = fields[4];
        String state = fields[5];

        return new Guest(id, firstName, lastName, email, phone, state);
    }

    @Override
    public String toString() {
        return "GuestFileRepository{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
