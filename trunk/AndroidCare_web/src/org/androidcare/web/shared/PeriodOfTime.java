package org.androidcare.web.shared;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public abstract class PeriodOfTime {
	public static final int HOUR = 0;
	public static final int DAY = 1;
	public static final int WEEK = 2;
	public static final int MONTH = 3;
	public static final int YEAR = 4;
	
	public static final int NEVER_ENDS = 0;
	public static final int UNTIL_DATE = 1;
	public static final int ITERATIONS = 2;
}
