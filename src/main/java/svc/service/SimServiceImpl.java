package svc.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import svc.entity.SimCardEntity;
import svc.entity.SimCardStatus;
import svc.exception.NotFoundException;
import svc.repository.SimCardRepository;

/**
 * A service managing sim cards.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimServiceImpl implements SimService {

    SimCardRepository simCardRepository;

    public void activateSim(long simId, boolean enabled) {
        SimCardEntity sim = simCardRepository.findById(simId).orElseThrow(NotFoundException::new);
        sim.setStatus(SimCardStatus.of(enabled));
        simCardRepository.save(sim);
    }
}
