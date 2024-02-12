package learn.mastery.data;

import learn.mastery.models.Host;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HostFileRepository implements HostRepository {
    private static final String DELIMITER = ",";
    private static final String HEADER = "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate";

    private final String filePath;

    public HostFileRepository(String filePath) {
        this.filePath = filePath;
    }


    public List<Host> findAll() throws IOException {
        List<Host> hosts = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER, -1);
                if (fields.length == 10) {
                    Host host = deserialize(fields);
                    hosts.add(host);
                }
            }
        }
        return hosts;
    }

    public Host findById(String hostId) throws IOException {
        List<Host> hosts = findAll();
        for (Host host : hosts) {
            if (host.getId().equalsIgnoreCase(hostId)) {
                return host;
            }
        }
        return null;
    }

    public Host findByEmail(String email) throws IOException {
        List<Host> hosts = findAll();
        for (Host host : hosts) {
            if (host.getEmail().equalsIgnoreCase(email)) {
                return host;
            }
        }
        return null;
    }

    private Host deserialize(String[] fields) {
        String id = fields[0];
        String lastName = fields[1];
        String email = fields[2];
        String phone = fields[3];
        String address = fields[4];
        String city = fields[5];
        String state = fields[6];
        String postalCode = fields[7];
        BigDecimal standardRate = new BigDecimal(fields[8]);
        BigDecimal weekendRate = new BigDecimal(fields[9]);

        return new Host(id, lastName, email, phone, address, city, state, postalCode, standardRate, weekendRate);
    }

    @Override
    public String toString() {
        return "HostFileRepository{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
