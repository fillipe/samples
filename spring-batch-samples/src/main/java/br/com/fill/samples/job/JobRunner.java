package br.com.fill.samples.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class JobRunner {

	public static void main(String[] args) {
		// Parametros de execucao do job
		Date dtTransaction = parseDate("01/01/2018");
		long bank = 237;

		// Carrega o contexto do Spring
//		GenericApplicationContext context = new ClassPathXmlApplicationContext("job-tax-calculator.xml");
		GenericApplicationContext context = new AnnotationConfigApplicationContext(JobTaxCalculator.class);

		// Obtem atraves do contexto alguns dos beans necessarios para a execucao do job
		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("taxCalculatorJob");

		// Cria os parametros para o job
		JobParameters jobParameters = new JobParametersBuilder()
				.addDate("DT_TRANSACTION", dtTransaction)
				.addLong("BANK", bank)
				.toJobParameters();

		try {
			
			jobLauncher.run(job, new RunIdIncrementer().getNext(jobParameters));
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			System.out.println("Error while running " + job.getName());
			e.printStackTrace();
		} finally {
			context.close();
		}
	}

	private static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (ParseException e) {
			System.out.println("Error! Invalide date parameter: " + date);
			System.exit(1);
			return null;
		}
	}
}
