package com.fill.remotechunkbatch.configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DataSourceConfiguration {
	
	@Autowired
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
//		ds.setUrl(env.getProperty("db.datasource.url"));
//		ds.setDriverClassName(env.getProperty("db.datasource.driver-class-name"));
//		ds.setUsername(env.getProperty("db.datasource.username"));
//		ds.setPassword(env.getProperty("db.datasource.password"));
		ds.setUrl("jdbc:hsqldb:hsql://localhost/testbatchdb");
		ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
		ds.setUsername("SA");
		ds.setPassword("");
		return ds;
	}

}
