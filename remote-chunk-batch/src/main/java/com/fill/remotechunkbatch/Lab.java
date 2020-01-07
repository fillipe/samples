package com.fill.remotechunkbatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;

public class Lab {

	public static void main(String[] args) {
		int tbrows = 436;
		int grid = 12;
		
		int qtd = tbrows / grid;
		boolean isMoreGrids = false;
		if (tbrows < grid) {
			isMoreGrids = true;
			qtd = 1;
		}
		List<ExecutionContext> ctxs = new ArrayList<>(grid);
		int initialId = 1;
		for (int i = 1; i <= grid; i++) {
			if (isMoreGrids && i > tbrows) {
				ExecutionContext ctx = new ExecutionContext();
				ctx.putLong("initialId", 999999999);
				ctx.putLong("finalId", 999999999);
				ctxs.add(ctx);
			} else {
				ExecutionContext ctx = new ExecutionContext();
				ctx.putLong("initialId", initialId);
				
				int finalId = 0;
				if (i == grid) {
					finalId = tbrows;
				} else {
					finalId = initialId + qtd -1;
				}
				
				if (finalId > tbrows) {
					finalId = tbrows;
				}
				
				ctx.putLong("finalId", finalId);
				ctxs.add(ctx);
				initialId += qtd;
			}
		}
		
		for (ExecutionContext ec : ctxs) {
			System.out.println("Init: " + ec.get("initialId") + " final: " + ec.get("finalId"));
		}
	}
	
}
