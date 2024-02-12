package learn.mastery.domain;

import learn.mastery.data.HostRepository;
import learn.mastery.data.ReservationRepository;
import learn.mastery.models.Host;

import java.io.IOException;

public class HostService {
    private final HostRepository hostRepository;
    private final ReservationRepository reservationRepository;

    public HostService(HostRepository hostRepository, ReservationRepository reservationRepository) {
        this.hostRepository = hostRepository;
        this.reservationRepository = reservationRepository;
    }

    public Result<Host> findHostByEmail(String email) throws IOException {
        Result<Host> result = new Result<>();
        Host host = hostRepository.findByEmail(email);

        if (host == null) {
            result.addErrorMessage("Host with email " + email + " does not exist.");
            return result;
        }

        result.setPayload(host);
        return result;
    }


    public Result<Host> checkForReservations(String hostEmail) throws IOException {
        Result<Host> result = findHostByEmail(hostEmail);

        if (!result.isSuccess()) {
            return result;
        }

        Host host = result.getPayload();
        var reservations = reservationRepository.findByHostId(host.getId());

        if (reservations.isEmpty()) {
            result.addErrorMessage("Host does not have any reservations.");
        }

        return result;
    }
}
