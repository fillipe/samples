package com.fill.remotechunkbatch.slave;

import org.springframework.batch.item.ItemProcessor;

import com.fill.remotechunkbatch.model.Client;

public class ClientProcessor implements ItemProcessor<Client, Client> {

	@Override
	public Client process(Client item) throws Exception {
		item.setStatus("PROCESSED");
		return item;
	}

}
