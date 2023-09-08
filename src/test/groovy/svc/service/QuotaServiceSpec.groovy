package svc.service

import org.mapstruct.factory.Mappers
import spock.lang.Specification
import svc.dto.ConsumeQuotaRequest
import svc.dto.CreateQuotaRequest
import svc.dto.SimQuotaStatus
import svc.dto.SimQuotaType
import svc.entity.SimCardEntity
import svc.entity.SimQuotaEntity
import svc.exception.NotFoundException
import svc.mapper.SimQuotaMapper
import svc.repository.SimCardRepository
import svc.repository.SimQuotaRepository

/**
 * Юнит-тест
 */
class QuotaServiceSpec extends Specification {

    def simCardRepository = Mock(SimCardRepository)
    def simQuotaRepository = Mock(SimQuotaRepository)
    def simQuotaMapper = Mappers.getMapper(SimQuotaMapper)
    def simCardService = new QuotaServiceImpl(
            simCardRepository,
            simQuotaRepository,
            simQuotaMapper)

    void 'getQuotaAvailable, main success'() {
        given:
        def sim = new SimCardEntity()

        when:
        def info = simCardService.getQuotaAvailable(555)

        then:
        assert info.traffic == 100
        assert info.voice == 500
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        1 * simQuotaRepository.sumQuota(_, svc.entity.SimQuotaType.TRAFFIC, _) >> Optional.of(100.toBigDecimal())
        1 * simQuotaRepository.sumQuota(_, svc.entity.SimQuotaType.VOICE, _) >> Optional.of(500.toBigDecimal())
        0 * _
    }

    void 'createQuota, main success'() {
        given:
        def sim = new SimCardEntity()

        when:
        def request = new CreateQuotaRequest(simId: 555, amount: 123, type: SimQuotaType.TRAFFIC)
        def result = simCardService.createQuota(request)

        then:
        result.balance == 123
        result.dateCreated == null
        result.endDate == null
        result.id == 222
        result.lastUpdated == null
        result.status == SimQuotaStatus.ENABLED
        result.type == SimQuotaType.TRAFFIC
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        1 * simQuotaRepository.save(_) >> { SimQuotaEntity quota ->
            assert quota.simCard == sim
            assert quota.type == svc.entity.SimQuotaType.TRAFFIC
            assert quota.balance == 123
            assert quota.endDate == null
            quota.id = 222
        }
        0 * _
    }

    void 'consumeQuota, balance charged and something remains yet'() {
        given:
        def sim = new SimCardEntity()
        def quota1 = new SimQuotaEntity(balance: 10)
        def quota2 = new SimQuotaEntity(balance: 10)
        def quota3 = new SimQuotaEntity(balance: 10)

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 20)
        simCardService.consumeQuota(request)

        then:
        2 * simQuotaRepository.save(_) >> { SimQuotaEntity entity ->
            assert entity.balance == 0
        }
        1 * simQuotaRepository.findAllActiveQuota(*_) >> [quota1, quota2, quota3]
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        0 * _
    }

    void 'consumeQuota, the charge is small and the first quota is charged a little'() {
        given:
        def quota1 = new SimQuotaEntity(balance: 10)
        def quota2 = new SimQuotaEntity(balance: 10)
        def sim = new SimCardEntity()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 3)
        simCardService.consumeQuota(request)

        then:
        1 * simQuotaRepository.save(_) >> { SimQuotaEntity entity ->
            assert entity.balance == 7
        }
        1 * simQuotaRepository.findAllActiveQuota(*_) >> [quota1, quota2]
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        0 * _
    }

    void 'consumeQuota, the charge is too big'() {
        given:
        def quota1 = new SimQuotaEntity(balance: 10)
        def quota2 = new SimQuotaEntity(balance: 10)
        def sim = new SimCardEntity()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 100_000)
        simCardService.consumeQuota(request)

        then:
        2 * simQuotaRepository.save(_) >> { SimQuotaEntity entity ->
            assert entity.balance == 0
        }
        1 * simQuotaRepository.findAllActiveQuota(*_) >> [quota1, quota2]
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        0 * _
    }

    void 'consumeQuota, no quotas exists'() {
        given:
        def sim = new SimCardEntity()

        when:
        def request = new ConsumeQuotaRequest(simId: 555, amount: 100_000)
        simCardService.consumeQuota(request)

        then:
        1 * simCardRepository.findById(555) >> Optional.of(sim)
        1 * simQuotaRepository.findAllActiveQuota(*_) >> []
        0 * simQuotaRepository.save(_)
        0 * _
    }

    void 'consumeQuota, no sim found'() {
        when:
        def request = new ConsumeQuotaRequest(simId: 555)
        simCardService.consumeQuota(request)

        then:
        thrown(NotFoundException)
        1 * simCardRepository.findById(555) >> Optional.empty()
        0 * _
    }
}
