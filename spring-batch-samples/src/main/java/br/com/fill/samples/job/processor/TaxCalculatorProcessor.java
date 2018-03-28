package br.com.fill.samples.job.processor;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;

import br.com.fill.samples.entity.Transaction;
import br.com.fill.samples.entity.TransactionType;

public class TaxCalculatorProcessor implements ItemProcessor<Transaction, Transaction> {

	@Override
	public Transaction process(Transaction transaction) throws Exception {
		if (TransactionType.DEBIT.equals(transaction.getType())) {
			transaction.setTax(new BigDecimal(0.1));
		} else {
			transaction.setTax(new BigDecimal(0.3));
		}
		return transaction;
	}

}
