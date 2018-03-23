package br.com.fill.samples.job;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.fill.samples.entity.Transaction;
import br.com.fill.samples.entity.mapper.TransactionRowMapper;

@Configuration
public class JobTaxCalculator {
	
	@Bean
	public ItemReader<Transaction> reader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Transaction>()
				.dataSource(dataSource)
				.sql("")
				.rowMapper(new TransactionRowMapper())
				.build();
	}

}
