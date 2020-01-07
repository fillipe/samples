package com.fill.remotechunkbatch.master;

import lombok.Data;

@Data
public class ContextObject {
	
	private Long initialIdPartition;
	private Long finalIdPartition;

}
