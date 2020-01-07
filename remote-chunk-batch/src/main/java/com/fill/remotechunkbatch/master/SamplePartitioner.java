package com.fill.remotechunkbatch.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SamplePartitioner implements Partitioner {

	private static final String PARTITION_KEY = "partition";
	
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		// TODO: Trocar para o gridSize
		int grid = 3;

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("STATUS", "PENDING");
		Integer tbrows = jdbcTemplate.queryForObject("select count(1) from TB_CLIENT where status = :STATUS", params, Integer.class);
		
		int qtd = tbrows / grid;
		boolean isMoreGrids = false;
		if (tbrows < grid) {
			isMoreGrids = true;
			qtd = 1;
		}
		System.out.println(qtd);
		
		List<Integer> partitionedRowNums = new ArrayList<>(grid);
		
		int initialId = 1;
		for (int i = 1; i <= grid; i++) {
			if (isMoreGrids && i > tbrows) {
				partitionedRowNums.add(999999999);
				partitionedRowNums.add(999999999);
			} else {
				int finalId = 0;
				if (i == grid) {
					finalId = tbrows;
				} else {
					finalId = initialId + qtd -1;
				}
				
				if (finalId > tbrows) {
					finalId = tbrows;
				}
				
				partitionedRowNums.add(initialId);
				partitionedRowNums.add(finalId);
				initialId += qtd;
			}
		}
		
		String sql = new StringBuilder()
				.append("select ID from (")
				.append("	select ROWNUM as line, ID from TB_CLIENT where STATUS = 'PENDING'")
				.append(") clients where line in (:ROWS)")
				.toString();
		params = new MapSqlParameterSource();
		params.addValue("ROWS", partitionedRowNums);
		List<Long> partitionedIds = jdbcTemplate.queryForList(sql, params, Long.class);
		Iterator<Long> iterator = partitionedIds.iterator();
		
		Map<String, ExecutionContext> partitions = new HashMap<>(grid);
		for (int partitioId = 0; partitioId < grid; partitioId++) {
			if (iterator.hasNext()) {
				ExecutionContext ctx = new ExecutionContext();
				ctx.putLong("initialIdPartition", iterator.next());
				ctx.putLong("finalIdPartition", iterator.next());
				partitions.put(PARTITION_KEY + partitioId, ctx);
			} else {
				ExecutionContext ctx = new ExecutionContext();
				ctx.putLong("initialIdPartition", 999999999);
				ctx.putLong("finalIdPartition", 999999999);
				partitions.put(PARTITION_KEY + partitioId, ctx);
			}
		}
		
		return partitions;
	}

}
