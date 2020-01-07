package com.fill.remotechunkbatch;

import java.util.Collections;
import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MasterRunner {
	
	private static final String JOB_NAME = "remotePartitioningJob";
	private static final String JOB_PATH = "com.fill.remotechunkbatch.master.MasterConfiguration";

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(Class.forName(JOB_PATH));
			JobLauncher jobLauncher = (JobLauncher) applicationContext.getBean("jobLauncher");
			JobParameter jobParam = new JobParameter(UUID.randomUUID().toString());
			Job job = (Job) applicationContext.getBean(JOB_NAME);
			JobParameters jobParameters = new JobParameters(Collections.singletonMap("param1", jobParam));
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);
			System.out.println("Execution completed. Status = " + jobExecution.getStatus());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
