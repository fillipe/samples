package br.com.fill.samples.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import br.com.fill.samples.entity.Transaction;
import br.com.fill.samples.entity.TransactionType;

public class TransactionRowMapper implements RowMapper<Transaction> {

	@Override
	public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		Transaction t = new Transaction();
		t.setId(rs.getLong("TRANS_ID"));
		t.setDate(rs.getDate("TRANS_DATE"));
		t.setBank(rs.getInt("BANK"));
		t.setType(TransactionType.valueOf(rs.getString("TRANS_TYPE")));
		t.setValue(rs.getBigDecimal("TRANS_VALUE"));
		return t;
	}

}
