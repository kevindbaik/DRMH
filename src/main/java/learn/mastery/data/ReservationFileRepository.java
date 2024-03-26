package learn.mastery.data;

import learn.mastery.models.Guest;
import learn.mastery.models.Host;
import learn.mastery.models.Reservation;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ReservationFileRepository implements ReservationRepository {
    private final String directory;
    private final GuestRepository guestRepository;
    private final HostRepository hostRepository;

    private static final String DELIMITER = ",";
    private static final String HEADER = "id,start_date,end_date,guest_id,total";

    public ReservationFileRepository(String directory, GuestRepository guestRepository, HostRepository hostRepository) {
        this.directory = directory;
        this.guestRepository = guestRepository;
        this.hostRepository = hostRepository;
    }

    @Override
    public List<Reservation> findByHostId(String hostId) {
        List<Reservation> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(hostId)))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER, -1);
                if (fields.length == 5) {
                        Reservation reservation = deserialize(fields, hostId);
                        reservations.add(reservation);
                }
            }
        } catch (IOException ignored) {
        }
        return reservations;
    }

    @Override
    public Reservation add(Reservation reservation) throws DataException {
        List<Reservation> reservations = findByHostId(reservation.getHost().getId());
        reservation.setId(generateNewId(reservations));
        reservations.add(reservation);

        writeReservations(reservation.getHost().getId(), reservations);

        return reservation;
    }


    @Override
    public boolean update(Reservation reservation) throws DataException {
        List<Reservation> reservations = findByHostId(reservation.getHost().getId());
        boolean found = false;

        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId() == reservation.getId()) {
                reservations.set(i, reservation);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        writeReservations(reservation.getHost().getId(), reservations);
        return true;
    }

    @Override
    public boolean delete(Reservation reservation) throws DataException {
        List<Reservation> reservations = findByHostId(reservation.getHost().getId());
        boolean found = false;

        for (Iterator<Reservation> iterator = reservations.iterator(); iterator.hasNext(); ) {
            Reservation currentReservation = iterator.next();
            if (currentReservation.getId() == reservation.getId()) {
                iterator.remove();
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        writeReservations(reservation.getHost().getId(), reservations);
        return true;
    }

    // HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS HELPERS
    private String getFilePath(String hostId) {
        return Paths.get(directory, hostId + ".csv").toString();
    }

    private int generateNewId(List<Reservation> reservations) {
        return reservations.stream()
                .mapToInt(Reservation::getId)
                .max()
                .orElse(0) + 1;
    }

    private void writeReservations(String hostId, List<Reservation> reservations) throws DataException {
        Path path = Paths.get(getFilePath(hostId));
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("id,start_date,end_date,guest_id,total\n");
            for (Reservation reservation : reservations) {
                String line = serialize(reservation);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException ignored) {

        }
    }

    // SERIALIZE
    private String serialize(Reservation reservation) {
        // format = "1,2020-07-01,2020-07-02,18,870"
        return String.format("%d,%s,%s,%d,%s",
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getGuestId(),
                reservation.getTotal());
    }

    // DESERIALIZE
    private Reservation deserialize(String[] fields, String hostId) throws IOException {
        int id = Integer.parseInt(fields[0]);
        LocalDate startDate = LocalDate.parse(fields[1]);
        LocalDate endDate = LocalDate.parse(fields[2]);
        int guestId = Integer.parseInt(fields[3]);
        BigDecimal total = new BigDecimal(fields[4]);

        Guest guest = guestRepository.findById(guestId);
        Host host = hostRepository.findById(hostId);

        if (guest == null || host == null) {
            throw new IOException("Failed to find guest or host for reservation id " + id);
        }
        return new Reservation(id, startDate, endDate, guest, host, guestId, total);
    }

    @Override
    public String toString() {
        return "ReservationFileRepository{" +
                "directory='" + directory + '\'' +
                ", guestRepository=" + guestRepository +
                ", hostRepository=" + hostRepository +
                '}';
    }
}