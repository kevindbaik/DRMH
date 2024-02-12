
package learn.mastery.domain;

import learn.mastery.data.GuestRepositoryDouble;
import learn.mastery.models.Guest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuestServiceTest {
    private GuestService service;
    private GuestRepositoryDouble repositoryDouble = new GuestRepositoryDouble();

    @BeforeEach
    void setUp() {
        service = new GuestService(repositoryDouble);
    }

    @Test
    void findGuestByEmailSuccess() {
        String email = "tester@test.com";
        Result<Guest> result = service.findGuestByEmail(email);

        assertTrue(result.isSuccess());
        assertEquals(email, result.getPayload().getEmail());
    }

    @Test
    void findGuestByEmailNotFound() {
        String email = "nonexistent@example.com";
        Result<Guest> result = service.findGuestByEmail(email);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessages().contains("Guest with email " + email + " does not exist."));
    }
}
