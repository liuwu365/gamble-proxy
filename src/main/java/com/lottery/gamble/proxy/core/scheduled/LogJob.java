package com.lottery.gamble.proxy.core.scheduled;

import com.lottery.gamble.proxy.core.util.LogWriter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by henry on 3/28/14.
 */

@Component
public class LogJob {

	@Resource
    LogWriter logWriter;

	@Scheduled(cron = "0 0 * * * *")
	public void dayChange() {
		logWriter.changeFile();
	}

	@Scheduled(fixedDelay = 1000)
	public void writeLog() throws IOException {
		logWriter.writeToFile();
	}

}
