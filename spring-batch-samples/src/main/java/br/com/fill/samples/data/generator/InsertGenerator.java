package br.com.fill.samples.data.generator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertGenerator {

	private static final double MIN_VALUE = 1.0;
	private static final double MAX_VALUE = 1000.0;
	
	private static final String START_DATE = "01/01/2016";
	private static final String END_DATE = "31/12/2018";
	
	private static final int[] BANKS = {237, 33, 341, 1};

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	public static void main(String[] args) {
		
		for (int i = 0; i < 1000; i++) {
			Integer bank = randomizeBank();
			String date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(randomizeDate());
			BigDecimal value = new BigDecimal(randomize(MIN_VALUE, MAX_VALUE).doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			String insert = "INSERT INTO TB_TRANSACTION (TRANS_DATE, BANK, VALUE, TAX) VALUES ('%s', %s, %s, %s);\n";
			
			System.out.printf(insert, date, bank, value, null);
		}
	}

	private static Date randomizeDate() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DD_MM_YYYY);
			long beginTime = dateFormat.parse(START_DATE).getTime();
			long endTime = dateFormat.parse(END_DATE).getTime();
			
			long randomTime = (long) (beginTime + Math.random() * (endTime - beginTime));
			
			return new Date(randomTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static int randomizeBank() {
		
		return BANKS[randomize(0, BANKS.length - 1).intValue()];
	}
	
	private static Number randomize(Number rngMax, Number rngMin) {
		return rngMax.doubleValue() + Math.random() * rngMin.doubleValue();
	}

}
