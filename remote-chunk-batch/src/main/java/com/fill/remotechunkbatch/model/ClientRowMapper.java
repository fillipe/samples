package com.fill.remotechunkbatch.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ClientRowMapper implements RowMapper<Client> {

	@Override
	public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
		Client c = new Client();
		c.setId(rs.getLong("ID"));
		c.setName(rs.getString("NAME"));
		c.setStatus(rs.getNString("STATUS"));
		return c;
	}

}
