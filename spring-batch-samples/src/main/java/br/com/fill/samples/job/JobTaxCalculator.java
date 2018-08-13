package br.com.fill.samples.job;

import java.util.Arrays;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import br.com.fill.samples.config.EnvironmentConfiguration;
import br.com.fill.samples.entity.Transaction;
import br.com.fill.samples.entity.mapper.TransactionRowMapper;
import br.com.fill.samples.entity.provider.TransactionProvider;
import br.com.fill.samples.job.processor.TaxCalculatorProcessor;

@Configuration
@EnableBatchProcessing
@Import(EnvironmentConfiguration.class)
public class JobTaxCalculator {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Job taxCalculatorJob() {
		return jobBuilderFactory.get("jobTaxCalculator")
    			.start(step())
    			.build();
    }

    @Bean
	public Step step() {
		return stepBuilderFactory.get("stepTaxCalculator")
				.<Transaction, Transaction>chunk(10)
				.reader(reader(null, null))
				.processor(processor())
				.writer(writer())
				.build();
	}

	@Bean
	@StepScope
	public JdbcCursorItemReader<Transaction> reader(@Value("#{jobParameters['DT_TRANSACTION']}") Date dtTransaction,
			@Value("#{jobParameters['BANK']}") Integer bank) {
		return new JdbcCursorItemReaderBuilder<Transaction>().dataSource(dataSource)
				.sql("SELECT TRANS_ID, TRANS_DATE, BANK, TRANS_VALUE, TRANS_TYPE FROM TB_TRANSACTION "
						+ "WHERE TRANS_DATE < ? AND BANK = ?")
				.queryArguments(new Object[] { dtTransaction, bank }).rowMapper(new TransactionRowMapper())
				.saveState(false).build();
	}
	
	@Bean
	public ItemProcessor<Transaction, Transaction> processor() {
		return new TaxCalculatorProcessor();
	}
	
	@Bean
	public ItemWriter<Transaction> writer() {
		return new CompositeItemWriterBuilder<Transaction>()
				.delegates(Arrays.asList(fooInsertWriter(), transactionUpdateWriter()))
				.build();
	}
	
	@Bean
	public ItemWriter<Transaction> fooInsertWriter() {
		return new JdbcBatchItemWriterBuilder<Transaction>()
				.dataSource(dataSource)
				.sql("INSERT INTO TB_FOO (FOO_DATE, TRANS_ID) VALUES (:FOO_DATE, :TRANS_ID)")
				.itemSqlParameterSourceProvider(new TransactionProvider())
				.build();
	}
	
	@Bean
	public ItemWriter<Transaction> transactionUpdateWriter() {
		return new JdbcBatchItemWriterBuilder<Transaction>()
				.dataSource(dataSource)
				.sql("UPDATE TB_TRANSACTION SET TAX = :TAX WHERE TRANS_ID = :TRANS_ID")
				.itemSqlParameterSourceProvider(new TransactionProvider())
				.build();
	}

}
