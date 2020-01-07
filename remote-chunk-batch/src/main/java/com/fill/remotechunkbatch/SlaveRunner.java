package com.fill.remotechunkbatch;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SlaveRunner {
	
	private static final String JOB_PATH = "com.fill.remotechunkbatch.slave.SlaveConfiguration";
	
	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(Class.forName(JOB_PATH));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
