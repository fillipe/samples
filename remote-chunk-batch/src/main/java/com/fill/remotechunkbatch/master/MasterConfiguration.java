package com.fill.remotechunkbatch.master;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@EnableBatchIntegration
@ComponentScan(basePackages = "com.fill.remotechunkbatch.master")
@Import(value = {DataSourceConfiguration.class, BrokerConfiguration.class})
public class MasterConfiguration {

	private final JobBuilderFactory jobBuilderFactory;

	private final RemotePartitioningManagerStepBuilderFactory masterStepBuilderFactory;


	public MasterConfiguration(JobBuilderFactory jobBuilderFactory,
								RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory) {

		this.jobBuilderFactory = jobBuilderFactory;
		this.masterStepBuilderFactory = managerStepBuilderFactory;
	}
	
	@Bean
	public DirectChannel requests() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows
				.from(requests())
				.handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
				.get();
	}

	@Bean
	public Job remotePartitioningJob(Step masterStep) {
		return this.jobBuilderFactory.get("remotePartitioningJob")
				.start(masterStep)
				.build();
	}

	@Bean
	public Step masterStep(SamplePartitioner partitioner, DirectChannel requests) {
		return this.masterStepBuilderFactory.get("masterStep")
				.partitioner("slaveStep", partitioner)
				.outputChannel(requests)
				.build();
	}
	
}
