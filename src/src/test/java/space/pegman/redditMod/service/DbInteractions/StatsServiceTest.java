package space.pegman.redditMod.service.DbInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatsServiceTest {

    @Autowired
    StatsService statsService;

    @Test
    public void testMedianDouble(){
        Double[] medianArray = new Double[]{0.0, 1.0, 0.0, 1.0};
        assertEquals(0.5, statsService.median_d(Arrays.asList(medianArray)), 0.001);

        Double[] oddMedianArray = new Double[]{0.0, 1.0, 0.0, 0.0, 0.0};
        assertEquals(0.0, statsService.median_d(Arrays.asList(oddMedianArray)), 0.001);
    }

    @Test
    public void testMedianLong(){
        Long[] medianArray = new Long[]{0L, 1L, 0L, 0b1L};
        assertEquals(0.5, statsService.median_l(Arrays.asList(medianArray)), 0.001);

        Long[] oddMedianArray = new Long[]{0L, 1L, 0L, 1L, 0L};
        assertEquals(0.0, statsService.median_l(Arrays.asList(oddMedianArray)), 0.001);
    }

    @Test
    public void testMean(){
        Long[] meanArray = new Long[]{0L, 1L, 0L, 1L};
        assertEquals(0.5, statsService.medianDistFrom(Arrays.asList(meanArray), 0.5), 0.001);
    }


}
