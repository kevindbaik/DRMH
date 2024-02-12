package learn.mastery.data;

import learn.mastery.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HostFileRepositoryTest {
    static final String TEST_FILE_PATH = "./data/hosts_test.csv";
    HostFileRepository repository;
    @BeforeEach
    void setUp() {
        repository = new HostFileRepository(TEST_FILE_PATH);
    }

    @Test
    void findAllShouldReturnCorrectNumberOfHosts() throws IOException {
        List<Host> result = repository.findAll();
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void findByIdShouldReturnCorrectHost() throws IOException {
        final String expectedID = "3zzzz6bc-ab95-49a8-8962-d50b53f84333";
        Host result = repository.findById(expectedID);
        assertNotNull(result);
        assertEquals(expectedID, result.getId());
        assertEquals("Tester", result.getLastName());
    }

    @Test
    void findByEmailShouldReturnCorrectHost() throws IOException {
        final String expectedEmail = "test2@example.com";
        Host result = repository.findByEmail(expectedEmail);
        assertNotNull(result);
        assertEquals(expectedEmail, result.getEmail());
        assertEquals("TesterTwo", result.getLastName());
    }
}
