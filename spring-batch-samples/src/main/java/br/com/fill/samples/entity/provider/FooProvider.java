package br.com.fill.samples.entity.provider;

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import br.com.fill.samples.entity.Transaction;

public class FooProvider implements ItemSqlParameterSourceProvider<Transaction> {

	@Override
	public SqlParameterSource createSqlParameterSource(Transaction item) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("TRANS_ID", item.getId());
		params.addValue("FOO_DATE", item.getDate());
		params.addValue("TAX", item.getTax());
		return params;
	}

}
