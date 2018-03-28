package br.com.fill.samples.entity.provider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.jdbc.core.PreparedStatementSetter;

public class TaxCalculatorReaderPreparedStatementSetter implements PreparedStatementSetter {
	
	private Date dtTransaction;
	
	private Long bank;

	public TaxCalculatorReaderPreparedStatementSetter(Date dtTransaction, Long bank) {
		super();
		this.dtTransaction = dtTransaction;
		this.bank = bank;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		ps.setDate(1, java.sql.Date.valueOf(dtTransaction.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
		ps.setInt(2, bank.intValue());
	}

}
