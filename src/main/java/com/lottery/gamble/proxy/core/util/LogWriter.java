package com.lottery.gamble.proxy.core.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by henry on 3/28/14.
 */
public class LogWriter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

	private Boolean writeToFile;

	private String logPath;

	private Integer logNumber;

	private File logFile;

	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	private static final String FILE_NAME_PATTERN = "/app-server-%tY_%tm_%td-%d.log";

	public LogWriter(boolean writeToFile, String logPath, Integer logNumber) {
		this.writeToFile = writeToFile;
		this.logPath = logPath;
		this.logNumber = logNumber;
	}

	public void changeFile() {
		if (!writeToFile) {
			return;
		}
		Date date = new Date();
		logFile = new File(logPath + String.format(FILE_NAME_PATTERN, date, date, date, logNumber));
	}

	public void writeToFile() throws IOException {
		while (!queue.isEmpty()) {
			writeToFile(queue.poll());
		}
	}

	private void writeToFile(String str) throws IOException {
		if (logFile == null) {
			changeFile();
		}
		if (writeToFile) {
			FileUtils.write(logFile, str, "UTF-8", true);
		} else {
			logger.info(str);
		}
	}

	public void write(String str) throws InterruptedException {
		if (str != null && !str.isEmpty()) {
			if (!str.endsWith("\n")) {
				str += "\n";
			}
			queue.put(str);
		}
	}

}
