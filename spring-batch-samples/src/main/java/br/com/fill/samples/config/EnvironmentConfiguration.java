package br.com.fill.samples.config;

import javax.sql.DataSource;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class EnvironmentConfiguration {

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		return jobLauncher;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
		factoryBean.setDataSource(dataSource());;
		factoryBean.setTransactionManager(transactionManager());
		factoryBean.setDatabaseType("HSQL");
		return factoryBean.getObject();
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
		dataSource.setUrl("jdbc:hsqldb:hsql://localhost:9001/treinamento-batch");
		dataSource.setUsername("SA");
		dataSource.setPassword("");
		return dataSource;
	}
	
	@Bean
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}
	
//	@Value("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
//	private Resource dropBatchSchema;
//
//	@Value("classpath:org/springframework/batch/core/schema-hsqldb.sql")
//	private Resource createBatchSchema;
//
//	@Bean
//	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
//	    final DataSourceInitializer initializer = new DataSourceInitializer();
//	    initializer.setDataSource(dataSource);
//	    initializer.setDatabasePopulator(databasePopulator());
//	    return initializer;
//	}
//
//	private DatabasePopulator databasePopulator() {
//	    final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//	    populator.addScript(dropBatchSchema);
//	    populator.addScript(createBatchSchema);
//	    return populator;
//	}
	
}
