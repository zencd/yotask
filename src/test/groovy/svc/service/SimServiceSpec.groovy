package svc.service

import spock.lang.Specification
import svc.entity.SimCardEntity
import svc.entity.SimCardStatus
import svc.repository.SimCardRepository

/**
 * Юнит-тест
 */
class SimServiceSpec extends Specification {

    def simCardRepository = Mock(SimCardRepository)
    def simService = new SimServiceImpl(simCardRepository)

    void 'activateSim, main success #id'() {
        given:
        def sim1 = new SimCardEntity(id: 100, status: statusBefore)

        when:
        simService.activateSim(100, enabled)

        then:
        1 * simCardRepository.findById(100) >> Optional.of(sim1)
        1 * simCardRepository.save(_) >> { SimCardEntity sim2 ->
            assert sim2.id == 100
            assert sim2.status == statusAfter
        }
        0 * _

        where:
        id | statusBefore           | enabled | statusAfter
        1  | SimCardStatus.DISABLED | true    | SimCardStatus.ENABLED
        2  | SimCardStatus.DISABLED | false   | SimCardStatus.DISABLED
        3  | SimCardStatus.ENABLED  | true    | SimCardStatus.ENABLED
        4  | SimCardStatus.ENABLED  | false   | SimCardStatus.DISABLED
    }

}