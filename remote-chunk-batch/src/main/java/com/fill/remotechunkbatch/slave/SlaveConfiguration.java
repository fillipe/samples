package com.fill.remotechunkbatch.slave;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.fill.remotechunkbatch.configuration.BrokerConfiguration;
import com.fill.remotechunkbatch.configuration.DataSourceConfiguration;
import com.fill.remotechunkbatch.model.Client;
import com.fill.remotechunkbatch.model.ClientRowMapper;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@ComponentScan(basePackages = "com.fill.remotechunkbatch.slave")
@Import(value = {DataSourceConfiguration.class, BrokerConfiguration.class})
public class SlaveConfiguration {

	private final RemotePartitioningWorkerStepBuilderFactory slaveStepBuilderFactory;


	public SlaveConfiguration(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory) {
		this.slaveStepBuilderFactory = workerStepBuilderFactory;
	}

	@Bean
	public DirectChannel requests() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows
				.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("requests"))
				.channel(requests())
				.get();
	}

	@Bean
	public Step slaveStep(ItemReader<Client> reader, ItemProcessor<Client, Client> processor, ItemWriter<Client> writer) {
		return this.slaveStepBuilderFactory.get("slaveStep")
				.inputChannel(requests())
				.<Client, Client>chunk(10)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	@Bean
	@StepScope
	public JdbcCursorItemReader<Client> reader(DataSource dataSource,
			@Value("#{stepExecutionContext['initialIdPartition']}") Long initialIdPartition,
			@Value("#{stepExecutionContext['finalIdPartition']}") Long finalIdPartition) {
		return new JdbcCursorItemReaderBuilder<Client>()
				.dataSource(dataSource)
				.sql("SELECT ID, NAME, STATUS FROM TB_CLIENT WHERE ID BETWEEN ? AND ?")
				.queryArguments(new Object[] { initialIdPartition, finalIdPartition })
				.rowMapper(new ClientRowMapper())
				.saveState(false)
				.build();
	}
	
	@Bean
	public ItemProcessor<Client, Client> processor() {
		return new ClientProcessor();
	}
	
	@Bean
	public ItemWriter<Client> writer(DataSource dataSource) {
		return new CompositeItemWriterBuilder<Client>()
				.delegates(Arrays.asList(dbWriter(dataSource), printerWriter()))
				.build();
	}
	
	private ItemWriter<Client> dbWriter(DataSource dataSource) {
		JdbcBatchItemWriter<Client> writer = new JdbcBatchItemWriterBuilder<Client>()
				.dataSource(dataSource)
				.sql("UPDATE TB_CLIENT SET STATUS = :status WHERE ID = :id")
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Client>())
				.build();
		writer.afterPropertiesSet();
		return writer;
	}
	
	private ItemWriter<Client> printerWriter() {
		return new ItemWriter<Client>() {

			@Override
			public void write(List<? extends Client> items) throws Exception {
				for (Client client : items) {
					System.out.println("Writed: " + client);
				}
			}
		};
	}

}
