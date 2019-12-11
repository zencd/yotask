package svc;

import com.fasterxml.jackson.databind.ObjectMapper;
import svc.entity.SimCard;

import java.util.Calendar;
import java.util.Date;

/**
 * Used to create test data. For junit tests and for demo server too.
 */
public class TestUtils {

    public static final String MSISDN_1 = "+79210000001";
    public static final String MSISDN_2 = "+79210000002";

    public static final long INEXISTENT_SIM_ID = 999999;

    public static final SimCard SIM_1 = makeSimCard(1, MSISDN_1);
    public static final SimCard SIM_2 = makeSimCard(2, MSISDN_2);

    public static SimCard makeSimCard(long id) {
        return makeSimCard(id, null);
    }

    public static SimCard makeSimCard(long id, String msisdn) {
        if (msisdn == null) {
            msisdn = MSISDN_1;
        }

        SimCard sim = new SimCard();
        sim.setId(id);
        sim.setMsisdn(msisdn);
        return sim;
    }

    public static Date makeDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
