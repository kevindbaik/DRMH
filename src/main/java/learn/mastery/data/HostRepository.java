package learn.mastery.data;

import learn.mastery.models.Host;
import java.io.IOException;
import java.util.List;

public interface HostRepository {
    List<Host> findAll() throws IOException;
    Host findById(String hostId) throws IOException;
    Host findByEmail(String email) throws IOException;
}