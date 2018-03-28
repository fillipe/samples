package br.com.fill.samples.job;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.fill.samples.entity.Transaction;
import br.com.fill.samples.entity.mapper.TransactionRowMapper;
import br.com.fill.samples.entity.provider.FooProvider;
import br.com.fill.samples.job.processor.TaxCalculatorProcessor;

@Configuration
public class JobTaxCalculator {
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public ItemReader<Transaction> reader() {
		return new JdbcCursorItemReaderBuilder<Transaction>()
				.dataSource(dataSource)
				.sql("SELECT TRANS_ID, TRANS_DATE, BANK, VALUE FROM TB_TRANSACTION")
				.rowMapper(new TransactionRowMapper())
				.saveState(false)
				.build();
	}
	
	@Bean
	public ItemProcessor<Transaction, Transaction> processor() {
		return new TaxCalculatorProcessor();
	}
	
	@Bean
	public ItemWriter<Transaction> writer() {
		return new JdbcBatchItemWriterBuilder<Transaction>()
				.dataSource(dataSource)
				.sql("INSERT INTO TB_FOO (FOO_DATE, TRANS_ID) VALUES (:FOO_DATE, :TRANS_ID)")
				.itemSqlParameterSourceProvider(new FooProvider())
				.build();
	}

}
