package com.skp.kafkaalert.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.config.Config;
import com.skp.kafkaalert.entity.LogEvent;
import com.skp.kafkaalert.generator.OutputQueueGenerator;
import com.skp.kafkaalert.output.OutputFile;
import com.skp.kafkaalert.output.OutputProcessor;
import com.skp.kafkaalert.output.OutputQueue;
import com.skp.util.FileHelper;
import com.skp.util.FileHelper.LineReadCallback;

public class OutputProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(OutputProcessorTest.class);

	@Before
	public void setUp() {
	}

	@Test
	public void testOutputFile() throws IOException, ParseException, InterruptedException {
		// Get config
		Config config = Config.createFromResource("process.conf", "regex.conf");

	    // Create OutputProcessor
	    OutputProcessor oprocess = new OutputProcessor(config);
	    oprocess.init();

	    // Get OutputFile
	    OutputFile outputFile = (OutputFile) oprocess.getOutputPluginList().get(0);

	    // Generate sample data
	    OutputQueueGenerator.generateSampleCnxlogJson("access.log", outputFile.getOutputQueue());
//	    generateSampleJson(outputFile.getOutputQueue());

	    // Process
	    outputFile.process();
	}

}
