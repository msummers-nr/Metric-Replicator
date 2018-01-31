package com.nrh.api.module.task.metrics;

import static org.junit.Assert.*;

import com.nrh.api.APIApplication;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestCopierConfig {
  
  @Before
	public void setUp() throws Exception {
		
		// Read in the config files
    APIApplication.readConfig();
  }
  
  @Test
  public void testCopierConfig() {
    
    // This test will make sure the config is created and doesn't return a null
    CopierConfig copierConfig = new CopierConfig();
    List<CSVMetric> csvMetricList = copierConfig.getCsvMetricList();
    assertNotNull(csvMetricList);
  }
}