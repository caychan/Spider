package f_pipeline;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsolePipeline implements Pipeline {
	
    private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(File file) {
		
        logger.info("downloading page {}", file);
		
	}
}
