package learn.mastery.data;

import learn.mastery.models.Host;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HostRepositoryDouble implements HostRepository{
    private final List<Host> hosts = new ArrayList<>();

    public HostRepositoryDouble() {
        hosts.add(new Host("3effa6vf-ab45-52a8-6462-d50x55h84b15", "Doe",
                "john@example.com", "2483215", "2 Kingston Rd", "Chino Hills",
                "CA", "91709", new BigDecimal("100.00"), new BigDecimal("150.00")));
        hosts.add(new Host("xxxx-223-dfdada", "NoReservations", "noreservations@example.com", "23333",
                "0 reservations", "test", "TA", "99999", new BigDecimal("2.00"), new BigDecimal("20.00")));
    }
    @Override
    public List<Host> findAll() throws IOException {
        return new ArrayList<>(hosts);
    }

    @Override
    public Host findById(String hostId) throws IOException {
        return hosts.stream()
                .filter(host -> host.getId().equals(hostId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Host findByEmail(String email) throws IOException {
        return hosts.stream()
                .filter(host -> host.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}
