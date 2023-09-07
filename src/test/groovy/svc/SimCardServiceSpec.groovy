package svc

import org.mapstruct.factory.Mappers
import spock.lang.Specification
import spock.lang.Unroll
import svc.dto.ConsumeQuotaRequest
import svc.dto.CreateQuotaRequest
import svc.dto.SimQuotaAvailable
import svc.dto.SimQuotaType
import svc.entity.SimCardEntity
import svc.entity.SimCardStatus
import svc.entity.SimQuotaEntity
import svc.exception.NotFoundException
import svc.mapper.SimQuotaMapper
import svc.repository.SimCardRepository
import svc.repository.SimQuotaRepository
import svc.service.SimCardServiceImpl

/**
 * Юнит-тест
 */
class SimCardServiceSpec extends Specification {

    def simCardRepository = Mock(SimCardRepository)
    def simQuotaRepository = Mock(SimQuotaRepository)
    def simQuotaMapper = Mappers.getMapper(SimQuotaMapper)
    def simCardService = new SimCardServiceImpl(
            simCardRepository,
            simQuotaRepository,
            simQuotaMapper)

    @Unroll
    void 'activateSim, main success #id'() {
        given:
        SimCardEntity sim1 = new SimCardEntity(id: 100, status: statusWas)

        when:
        simCardService.activateSim(100L, enabled)

        then:
        1 * simCardRepository.findById(100L) >> Optional.of(sim1)
        1 * simCardRepository.save(_) >> { SimCardEntity sim2 ->
            assert sim2.id == 100
            assert sim2.status == statusGot
        }
        0 * _

        where:
        id | statusWas              | enabled | statusGot
        1  | SimCardStatus.DISABLED | true    | SimCardStatus.ENABLED
        2  | SimCardStatus.DISABLED | false   | SimCardStatus.DISABLED
        3  | SimCardStatus.ENABLED  | true    | SimCardStatus.ENABLED
        4  | SimCardStatus.ENABLED  | false   | SimCardStatus.DISABLED
    }

    void 'getQuotaAvailable, main success'() {
        given:
        SimCardEntity sim = new SimCardEntity()

        when:
        SimQuotaAvailable info = simCardService.getQuotaAvailable(555)

        then:
        assert info.megabytes == 100
        assert info.minutes == 500
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        1 * simQuotaRepository.sumQuota(_, SimQuotaType.TRAFFIC, _) >> Optional.of(new BigDecimal(100))
        1 * simQuotaRepository.sumQuota(_, SimQuotaType.VOICE, _) >> Optional.of(new BigDecimal(500))
        0 * _
    }

    void 'createQuota, main success'() {
        given:
        SimCardEntity sim = new SimCardEntity()

        when:
        def request = new CreateQuotaRequest(simId: 555, amount: 123, type: SimQuotaType.TRAFFIC)
        simCardService.createQuota(request)

        then:
        1 * simCardRepository.findById(555L) >> Optional.of(sim)
        1 * simQuotaRepository.save(_) >> { SimQuotaEntity quota ->
            assert quota.simCard == sim
            assert quota.type == SimQuotaType.TRAFFIC
            assert quota.balance == 123
            assert quota.endDate == null
        }
        0 * _
    }

    void 'consumeQuota, balance charged and something remains yet'() {
        given:
        def sim = new SimCardEntity()
        def quota1 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()
        def quota2 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()
        def quota3 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 20)
        simCardService.consumeQuota(request)

        then:
        2 * simQuotaRepository.save(_) >> { SimQuotaEntity entity ->
            assert entity.balance == 0
        }
        1 * simQuotaRepository.findAllActiveQuota(*_) >> [quota1, quota2, quota3]
        1 * simCardRepository.findById(555L) >> Optional.of(sim)
        0 * _
    }

    void 'consumeQuota, the charge is small and the first quota is charged a little'() {
        given:
        def quota1 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()
        def quota2 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()
        def sim = new SimCardEntity()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 3)
        simCardService.consumeQuota(request)

        then:
        1 * simQuotaRepository.save(_) >> { SimQuotaEntity entity ->
            assert entity.balance == 7
        }
        1 * simQuotaRepository.findAllActiveQuota(*_) >> [quota1, quota2]
        1 * simCardRepository.findById(555L) >> Optional.of(sim)
        0 * _
    }

    void 'consumeQuota, the charge is too big'() {
        given:
        def quota1 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()
        def quota2 = SimQuotaEntity.builder().balance(10.toBigDecimal()).build()
        def sim = new SimCardEntity()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 100_000)
        simCardService.consumeQuota(request)

        then:
        2 * simQuotaRepository.save(_) >> { SimQuotaEntity entity ->
            assert entity.balance == 0
        }
        1 * simQuotaRepository.findAllActiveQuota(*_) >> [quota1, quota2]
        1 * simCardRepository.findById(555L) >> Optional.of(sim)
        0 * _
    }

    void 'consumeQuota, no quotas exists'() {
        given:
        def sim = new SimCardEntity()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 100_000)
        simCardService.consumeQuota(request)

        then:
        1 * simCardRepository.findById(555L) >> Optional.of(sim)
        1 * simQuotaRepository.findAllActiveQuota(*_) >> []
        0 * simQuotaRepository.save(_)
        0 * _
    }

    void 'consumeQuota, no sim found'() {
        when:
        ConsumeQuotaRequest request = new ConsumeQuotaRequest(simId: 555)
        simCardService.consumeQuota(request)

        then:
        thrown(NotFoundException)
        1 * simCardRepository.findById(555L) >> Optional.empty()
        0 * _
    }
}
