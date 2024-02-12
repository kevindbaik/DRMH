package learn.mastery.data;

import learn.mastery.models.Guest;
import learn.mastery.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GuestFIleRepositoryTest {
    static final String TEST_FILE_PATH = "./data/guests_test.csv";
    GuestFileRepository repository;
    @BeforeEach
    void setUp() {
        repository = new GuestFileRepository(TEST_FILE_PATH);
    }

    @Test
    void findAllShouldReturnCorrectNumberOfGuests() throws IOException {
        List<Guest> result = repository.findAll();
        assertNotNull(result);
        assertEquals(6, result.size());
    }

    @Test
    void findByIdShouldReturnCorrectGuest() throws IOException {
        final int expectedID = 1;
        Guest result = repository.findById(expectedID);
        assertNotNull(result);
        assertEquals(expectedID, result.getGuestId());
        assertEquals("Kevin", result.getFirstName());
    }

    @Test
    void findByEmailShouldReturnCorrectGuest() throws IOException {
        final String expectedEmail = "kbaik1@mediafire.com";
        Guest result = repository.findByEmail(expectedEmail);
        assertNotNull(result);
        assertEquals(expectedEmail, result.getEmail());
        assertEquals("Baik", result.getLastName());
    }
}
