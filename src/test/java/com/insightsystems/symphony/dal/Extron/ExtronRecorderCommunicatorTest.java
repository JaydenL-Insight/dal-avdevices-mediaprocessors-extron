package com.insightsystems.symphony.dal.Extron;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.GenericStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Tag;


import java.util.List;
import java.util.Map;

public class ExtronRecorderCommunicatorTest {

    private static final ExtronRecorderCommunicator device = new ExtronRecorderCommunicator();

    @Before
    public void Setup() throws Exception {
        device.setPassword(System.getenv("dalPassword"));
        device.setHost(System.getenv("dalIp"));
        device.init();
    }

    @After
    public void Cleanup() {
        device.destroy();
    }

    @Tag("RealDevice")
    @Test
    public void CheckExtendedStatisticsAreValid() throws Exception {
        List<Statistics> statistics = device.getMultipleStatistics();
        ExtendedStatistics extStatistics = (ExtendedStatistics) statistics.get(1);
        Map<String,String> stats = extStatistics.getStatistics();

        Assert.assertNotNull(stats.get("Device#dateTime"));
        Assert.assertNotNull(stats.get("Device#firmwareVersion"));

        if (stats.containsKey("HdcpState")){
            Assert.assertNotNull(stats.get("HdcpState"));
        } else {
            Assert.assertNotNull(stats.get("HdcpStates#Input 2"));
        }

        Assert.assertNotNull(stats.get("Device#model"));
        Assert.assertNotNull(stats.get("Record#destination"));
        Assert.assertNotNull(stats.get("Record#transferConfig"));
        Assert.assertNotNull(stats.get("Device#macAddress"));
        Assert.assertNotNull(stats.get("Device#connectedUsers"));
        Assert.assertNotNull(stats.get("Device#serialNumber"));
        Assert.assertNotNull(stats.get("Device#name"));
        Assert.assertNotNull(stats.get("Device#partNumber"));
        Assert.assertNotNull(stats.get("Record#status"));

        Assert.assertEquals(stats.get("Controls#reboot"),"0");
        Assert.assertEquals(stats.get("Controls#ejectUsb"),"0");
        Assert.assertEquals(stats.get("Controls#clearAlarms"), "0");
        Assert.assertEquals(stats.get("Controls#restartNetwork"), "0");
        Assert.assertEquals(stats.get("Controls#playbackControl"), "0");

        Assert.assertNotNull(stats.get("Alarms"));
    }

    @Tag("RealDevice")
    @Test
    public void CheckAllGenericStatisticsArePresentAndValid() throws Exception {
        List<Statistics> statistics = device.getMultipleStatistics();
        GenericStatistics genStatistics = (GenericStatistics) statistics.get(0);

        Assert.assertTrue(genStatistics.getCpuPercentage()>0f);
        Assert.assertTrue(genStatistics.getMemoryInUse()>0f);
        Assert.assertTrue(genStatistics.getMemoryTotal()>0f);
    }
}
