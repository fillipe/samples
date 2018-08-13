package br.com.fill.samples.data.generator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import br.com.fill.samples.config.EnvironmentConfiguration;
import br.com.fill.samples.entity.TransactionType;

public class InsertGenerator {

	private static final double MIN_VALUE = 1.0;
	private static final double MAX_VALUE = 1000.0;
	
	private static final String START_DATE = "01/01/2016";
	private static final String END_DATE = "31/12/2018";
	
	private static final int[] BANKS = {237, 33, 341, 1};
	private static final TransactionType[] TYPES = {TransactionType.DEBIT, TransactionType.CREDIT};

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	public static void main(String[] args) {
		GenericApplicationContext context = new AnnotationConfigApplicationContext(EnvironmentConfiguration.class);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(context.getBean(DataSource.class));
		
		int qtdInserts = 1000;
		String[] inserts = new String[qtdInserts];
		for (int i = 0; i < qtdInserts; i++) {
			Integer bank = randomizeBank();
			String date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(randomizeDate());
			BigDecimal value = new BigDecimal(randomize(MIN_VALUE, MAX_VALUE).doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
			TransactionType type = randomizeType();
			
			String insert = "INSERT INTO TB_TRANSACTION (TRANS_DATE, BANK, TRANS_VALUE, TRANS_TYPE, TAX) VALUES ('%s', %s, %s, '%s', %s);\n";
			inserts[i] = String.format(insert, date, bank, value, type, null);
		}
		jdbcTemplate.batchUpdate(inserts);
		
		context.close();
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
		return BANKS[Math.round(randomize(0, BANKS.length - 1).floatValue())];
	}
	
	private static TransactionType randomizeType() {
		return TYPES[Math.round(randomize(0, TYPES.length - 1).floatValue())];
	}
	
	private static Number randomize(Number rngMax, Number rngMin) {
		return rngMax.doubleValue() + Math.random() * rngMin.doubleValue();
	}

}
