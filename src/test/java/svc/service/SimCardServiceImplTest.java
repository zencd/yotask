package svc.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuotaInfo;
import svc.entity.SimCard;
import svc.entity.SimCardStatus;
import svc.entity.SimQuota;
import svc.dto.SimQuotaType;
import svc.exception.NotFoundException;
import svc.mapper.SimQuotaMapperImpl;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimCardServiceImplTest {

    @InjectMocks
    private SimCardServiceImpl simCardService;

    @Mock
    private SimCardRepository simCardRepository;

    @Mock
    private SimQuotaRepository simQuotaRepository;

    @InjectMocks
    SimQuotaMapperImpl simQuotaMapper;

    @Before
    public void beforeEach() {
        Assert.isTrue(simQuotaMapper != null, "self check");
        ReflectionTestUtils.setField(simCardService, "simQuotaMapper", simQuotaMapper);
    }

    @Test
    public void activateSim_statusChanged() {
        SimCard sim = new SimCard();
        sim.setId(100L);
        sim.setStatus(SimCardStatus.DISABLED);
        when(simCardRepository.findById(100L)).thenReturn(Optional.of(sim));

        simCardService.activateSim(100L, true);

        SimCard sim2 = new SimCard();
        sim2.setId(100L);
        sim2.setStatus(SimCardStatus.ENABLED);
        verify(simCardRepository).save(sim2);
    }

    @Test
    public void getQuotaAvailable() {
        SimCard sim = new SimCard();
        when(simCardRepository.findById(555L)).thenReturn(Optional.of(sim));
        when(simQuotaRepository.sumQuota(any(), eq(SimQuotaType.TRAFFIC), any())).thenReturn(Optional.of(new BigDecimal(100)));
        when(simQuotaRepository.sumQuota(any(), eq(SimQuotaType.VOICE), any())).thenReturn(Optional.of(new BigDecimal(500)));

        SimQuotaInfo info = simCardService.getQuotaAvailable(555L);

        assertEquals(new BigDecimal(100), info.getMegabytes());
        assertEquals(new BigDecimal(500), info.getMinutes());
    }

    @Test
    public void createQuota() {
        SimCard sim = new SimCard();
        when(simCardRepository.findById(555L)).thenReturn(Optional.of(sim));

        CreateQuotaRequest request = new CreateQuotaRequest();
        request.simId = 555L;
        request.amount = BigDecimal.valueOf(123);
        request.type = SimQuotaType.TRAFFIC;
        simCardService.createQuota(request);

        ArgumentCaptor<SimQuota> ac = ArgumentCaptor.forClass(SimQuota.class);
        verify(simQuotaRepository).save(ac.capture());
        assertSame(sim, ac.getValue().getSimCard());
        assertEquals(SimQuotaType.TRAFFIC, ac.getValue().getType());
        assertEquals(BigDecimal.valueOf(123), ac.getValue().getBalance());
        assertNull(ac.getValue().getEndDate());
    }

    @Test
    public void consumeQuota_balanceChargedAndSomethingRemainsYet() {
        {
            // prepare: find sim
            SimCard sim = new SimCard();
            when(simCardRepository.findById(555L)).thenReturn(Optional.of(sim));
        }

        {
            // prepare: find quotas
            SimQuota q1 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            SimQuota q2 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            SimQuota q3 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            when(simQuotaRepository.findAllActiveQuota(any(), any(), any())).thenReturn(Arrays.asList(q1, q2, q3));
        }

        {
            // actual invocation
            ConsumeQuotaRequest request = new ConsumeQuotaRequest();
            request.simId = 555L;
            request.amount = BigDecimal.valueOf(20);
            simCardService.consumeQuota(request);
        }

        {
            // verify
            InOrder inOrder = inOrder(simQuotaRepository, simQuotaRepository);
            ArgumentCaptor<SimQuota> ac = ArgumentCaptor.forClass(SimQuota.class);
            inOrder.verify(simQuotaRepository, times(2)).save(ac.capture());
            List<SimQuota> qq = ac.getAllValues();
            assertEquals(BigDecimal.ZERO, qq.get(0).getBalance());
            assertEquals(BigDecimal.ZERO, qq.get(1).getBalance());
        }
    }

    @Test
    public void consumeQuota_theChargeIsSmallAndTheFirstQuotaIsChargedALittle() {
        {
            // prepare: find sim
            SimCard sim = new SimCard();
            when(simCardRepository.findById(555L)).thenReturn(Optional.of(sim));
        }

        {
            // prepare: find quotas
            SimQuota q1 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            SimQuota q2 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            when(simQuotaRepository.findAllActiveQuota(any(), any(), any())).thenReturn(Arrays.asList(q1, q2));
        }

        {
            // actual invocation
            ConsumeQuotaRequest request = new ConsumeQuotaRequest();
            request.simId = 555L;
            request.amount = BigDecimal.valueOf(3);
            simCardService.consumeQuota(request);
        }

        {
            // verify
            InOrder inOrder = inOrder(simQuotaRepository, simQuotaRepository);
            ArgumentCaptor<SimQuota> ac = ArgumentCaptor.forClass(SimQuota.class);
            inOrder.verify(simQuotaRepository, times(1)).save(ac.capture());
            List<SimQuota> qq = ac.getAllValues();
            assertEquals(BigDecimal.valueOf(7), qq.get(0).getBalance());
        }
    }

    @Test
    public void consumeQuota_theChargeIsTooBig() {
        {
            // prepare: find sim
            SimCard sim = new SimCard();
            when(simCardRepository.findById(555L)).thenReturn(Optional.of(sim));
        }

        {
            // prepare: find quotas
            SimQuota q1 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            SimQuota q2 = new SimQuota() {{
                setBalance(BigDecimal.valueOf(10));
            }};
            when(simQuotaRepository.findAllActiveQuota(any(), any(), any())).thenReturn(Arrays.asList(q1, q2));
        }

        {
            // actual invocation
            ConsumeQuotaRequest request = new ConsumeQuotaRequest();
            request.simId = 555L;
            request.amount = BigDecimal.valueOf(100_000);
            simCardService.consumeQuota(request);
        }

        {
            // verify
            InOrder inOrder = inOrder(simQuotaRepository, simQuotaRepository);
            ArgumentCaptor<SimQuota> ac = ArgumentCaptor.forClass(SimQuota.class);
            inOrder.verify(simQuotaRepository, times(2)).save(ac.capture());
            List<SimQuota> qq = ac.getAllValues();
            assertEquals(BigDecimal.valueOf(0), qq.get(0).getBalance());
            assertEquals(BigDecimal.valueOf(0), qq.get(1).getBalance());
        }
    }
    
    @Test
    public void consumeQuota_noQuotasExists() {
        {
            // prepare: find sim
            SimCard sim = new SimCard();
            when(simCardRepository.findById(555L)).thenReturn(Optional.of(sim));
        }

        {
            // prepare: find quotas
            when(simQuotaRepository.findAllActiveQuota(any(), any(), any())).thenReturn(Collections.emptyList());
        }

        {
            // actual invocation
            ConsumeQuotaRequest request = new ConsumeQuotaRequest();
            request.simId = 555L;
            request.amount = BigDecimal.valueOf(100_000);
            simCardService.consumeQuota(request);
        }

        {
            // verify
            verify(simQuotaRepository, never()).save(any());
        }
    }

    @Test(expected = NotFoundException.class)
    public void consumeQuota_noSimFound() {
        SimCard sim = new SimCard();
        when(simCardRepository.findById(555L)).thenReturn(Optional.ofNullable(null));
        //given(simCardService.consumeQuota(any())).willThrow(NotFoundException.class);

        ConsumeQuotaRequest request = new ConsumeQuotaRequest();
        request.simId = 555L;
        simCardService.consumeQuota(request);
    }

}