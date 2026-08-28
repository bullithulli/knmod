package org.bullithulli.feature.sandbox;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.List;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class DayLogicSandboxTest {

    private String getTestResourcePath() {
        return Objects.requireNonNull(getClass().getClassLoader().getResource("sandboxtests/day_logic")).getPath();
    }

    @Test
    @Ignore("Phase 3 and 4 of SANDBOX_KNMOD_PLAN.md are not implemented yet. Flattener does not understand day/time state simulation.")
    public void testDayAndTimeScheduling() throws Exception {
        String outPath = System.getProperty("java.io.tmpdir") + "/sandbox_day_logic_out.rpy";
        KNModSandbox sandbox = new KNModSandbox();
        sandbox.setStartLabel("start");
        sandbox.assemble(getTestResourcePath(), outPath);

        String content = Files.readString(Path.of(outPath));
        
        // Ensure chronological ordering of events across locations
        int evBedroomDay1MorningIdx = content.indexOf("label ev_bedroom_day1_morning:");
        int evParkDay1AfternoonIdx = content.indexOf("label ev_park_day1_afternoon:");
        int evBedroomDay2NightIdx = content.indexOf("label ev_bedroom_day2_night:");
        
        assertTrue("Output should contain ev_bedroom_day1_morning", evBedroomDay1MorningIdx != -1);
        assertTrue("Output should contain ev_park_day1_afternoon", evParkDay1AfternoonIdx != -1);
        assertTrue("Output should contain ev_bedroom_day2_night", evBedroomDay2NightIdx != -1);
        
        // Assert chronological order
        assertTrue("ev_bedroom_day1_morning should come before ev_park_day1_afternoon", 
            evBedroomDay1MorningIdx < evParkDay1AfternoonIdx);
            
        assertTrue("ev_park_day1_afternoon should come before ev_bedroom_day2_night", 
            evParkDay1AfternoonIdx < evBedroomDay2NightIdx);

        new File(outPath).delete();
    }
}
