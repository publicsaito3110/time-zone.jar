package org;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

/**
 * @author saito
 *
 */
public class TimeZone {

	//format(hh)
	private String hour;
	//format(mm)
	private String minute;
	//format(ss)
	private String second;
	//format(ffffff)
	private String miliSecond;


	//Const
	private static int MILI_SECOND_LENGTH = 6;
	private static double HOUR24_FMT_SECOND = 86400.000000;
	private static int HOUR_24 = 24;


	/**
	 * {Constructor}
	 *
	 * <p>
	 * Need 4 parameter and set.<br>
	 * When prams are out of range, will be Exception.
	 * </p>
	 *
	 * @param hour 0-23
	 * @param minute 0-60
	 * @param second 0-60
	 * @param milliSecond 0-999999
	 */
	public TimeZone(int hour, int minute, int second, int miliSecond) {
		super();

		//Validation
		if (this.isNotValiHour(hour)) {
			this.executeException("parameter is out of range");
		}
		if (this.isNotValiMinutes(minute)) {
			this.executeException("parameter is out of range");
		}
		if (this.isNotValiSecond(second)) {
			this.executeException("parameter is out of range");
		}
		if (this.isNotValiMiliSecond(miliSecond)) {
			this.executeException("parameter is out of range");
		}

		//Set fields
		this.hour = String.format("%02d", hour);
		this.minute = String.format("%02d", minute);
		this.second = String.format("%02d", second);

		//adjust miliSecond format(ffffff)
		String miliSecondStr = String.valueOf(miliSecond);
		while (miliSecondStr.length() < MILI_SECOND_LENGTH) {
			miliSecondStr += "0";
		}
		this.miliSecond = miliSecondStr;
	}


	/**
	 * {Constructor}
	 *
	 * <p>
	 * Need 1 parameter. Param{second} convert to time and set.<br>
	 * When pram is out of range, will be Exception.
	 * </p>
	 *
	 * @param second 0-86399.999999
	 */
	public TimeZone(double second) {
		super();

		//Validation
		if (!(0 <= second && second < HOUR24_FMT_SECOND)) {
			this.executeException("parameter is out of range");
		}

		//Get time by second (hh:mm:ss.ffffff)
		String timeZone = this.chgTimeZoneBySecond(second);

		//Set fields
		this.hour = timeZone.substring(0, 2);
		this.minute = timeZone.substring(3, 5);
		this.second = timeZone.substring(6, 8);
		this.miliSecond = timeZone.substring(9, 15);
	}


	/**
	 * {Defoult Constructor}
	 *
	 * <p>
	 * No parameter.<br>
	 * Get current time and set.
	 * </p>
	 *
	 * @param void
	 */
	public TimeZone() {

		//Get time now
		LocalTime nowLt = LocalTime.now();

		//Set fields
		this.hour = String.format("%02d",nowLt.getHour());
		this.minute = String.format("%02d",nowLt.getMinute());
		this.second = String.format("%02d",nowLt.getSecond());

		//adjust miliSecond format(ffffff)
		String miliSecond = String.valueOf(nowLt.getNano());
		miliSecond = miliSecond.substring(0, MILI_SECOND_LENGTH);
		this.miliSecond = miliSecond;
	}


	/**
	 * Get Time
	 *
	 * <p>
	 * Get fields and change format<br>
	 * hh:mm:ss.ffffff
	 * </p>
	 *
	 * @param void
	 * @return hh:mm:ss.ffffff
	 */
	public String time() {
		return hour + ":" + minute + ":" + second + "." + miliSecond;
	}


	/**
	 * Get hour
	 *
	 * <p>
	 * Get field time of hour.
	 * </p>
	 *
	 * @param void
	 * @return hour
	 */
	public int getHour() {
		return Integer.parseInt(hour);
	}


	/**
	 * Get minute
	 *
	 * <p>
	 * Get field time of minute.
	 * </p>
	 *
	 * @param void
	 * @return minute
	 */
	public int getMinute() {
		return Integer.parseInt(minute);
	}

	/**
	 * Get second
	 *
	 * <p>
	 * Get field time of second.
	 * </p>
	 *
	 * @param void
	 * @return second
	 */
	public int getSecond() {
		return Integer.parseInt(second);
	}

	/**
	 * Get miliSecond
	 *
	 * <p>
	 * Get field time of miliSecond.
	 * </p>
	 *
	 * @param void
	 * @return miliSecond
	 */
	public int getMiliSecond() {
		return Integer.parseInt(miliSecond);
	}


	/**
	 * Convert second
	 *
	 * <p>
	 * Get fields, calculate and convert to second.
	 * </p>
	 *
	 * @param void
	 * @return sssss.ffffff
	 */
	public double convertSecond() {

		//hour, minute and miliSecond convert second
		int hourInt = Integer.parseInt(this.hour) * 60 * 60;
		int minuteInt = Integer.parseInt(this.minute) * 60;
		String miliSecondFmtMS = "0." + this.miliSecond;

		//Calc by BigDecimal
		BigDecimal hourBd = new BigDecimal(String.valueOf(hourInt));
		BigDecimal minuteBd = new BigDecimal(String.valueOf(minuteInt));
		BigDecimal secondBd = new BigDecimal(this.second);
		BigDecimal miliSecondBd = new BigDecimal(miliSecondFmtMS);
		BigDecimal calcHmsfSecondBd = hourBd.add(minuteBd).add(secondBd).add(miliSecondBd);

		return calcHmsfSecondBd.doubleValue();
	}


	/**
	 * Calc time difference
	 *
	 * <p>
	 * Calculate difference timeZone to argTimeZone<br>
	 * When across day, calculate time. But, Acrossing day can't calculate.
	 * </p>
	 *
	 * @param argTimeZone
	 * @return After calculate time (hh:mm:ss.ffffff)
	 */
	public String substract(TimeZone argTimeZone) {

		//thisTimeZone and argTimeZone convert second
		double thisSecondDb = this.convertSecond();
		double argTimeZoneSecondDb = argTimeZone.convertSecond();

		//thisSecondDb is before than argTimeZoneSecondDb
		if (argTimeZoneSecondDb < thisSecondDb) {

			//Convert more than 24 hour
			argTimeZoneSecondDb += HOUR24_FMT_SECOND;
		}

		//Calc by BigDecimal
		BigDecimal thisSecondBd = new BigDecimal(String.valueOf(thisSecondDb));
		BigDecimal argTimeZoneSecondBd = new BigDecimal(String.valueOf(argTimeZoneSecondDb));
		BigDecimal calcTimeSecondfBd = argTimeZoneSecondBd.subtract(thisSecondBd);

		return this.chgTimeZoneBySecond(calcTimeSecondfBd.doubleValue());
	}


	/**
	 * Reset Time difference By hour
	 *
	 * <p>
	 * Calculate difference timeZone substract hour<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param hour
	 */
	public void substractHour(int hour) {

		//Validation
		if (hour < 0) {
			this.executeException("param is out of range");
		}
		if (this.getHour() < hour) {
			this.hour = "00";
			this.minute = "00";
			this.second = "00";
			this.miliSecond = "000000";
			return;
		}

		//Reset fields
		this.hour = String.format("%02d",(this.getHour() - hour));
	}


	/**
	 * Reset Time difference By minute
	 *
	 * <p>
	 * Calculate difference timeZone substract minute<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param minute
	 */
	public void substractMinute(int minute) {

		//Validation
		if (minute < 0) {
			this.executeException("param is invalid");
		}
		double thisSecond = this.convertSecond();
		double argsMinuteSecond = minute * 60;

		//When param is more than this time by second
		if (thisSecond < argsMinuteSecond) {
			this.hour = "00";
			this.minute = "00";
			this.second = "00";
			this.miliSecond = "000000";
			return;
		}

		//Calc by BigDecimal
		BigDecimal thisSecondBd = new BigDecimal(String.valueOf(thisSecond));
		BigDecimal argsMinuteSecondBd = new BigDecimal(String.valueOf(argsMinuteSecond));
		BigDecimal calcSecondBd = thisSecondBd.subtract(argsMinuteSecondBd);

		//Get time by calc result
		String calcTime = this.chgTimeZoneBySecond(calcSecondBd.doubleValue());

		//Reset fields
		this.hour = calcTime.substring(0, 2);
		this.minute = calcTime.substring(3, 5);
		this.second = calcTime.substring(6, 8);
		this.miliSecond = calcTime.substring(9, 15);
	}


	/**
	 * Reset Time difference By second
	 *
	 * <p>
	 * Calculate difference timeZone substract second<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param second
	 */
	public void substractSecond(double second) {

		//Validation
		if (second < 0) {
			this.executeException("param is invalid");
		}
		double thisSecond = this.convertSecond();

		//When param is more than this time by second
		if (thisSecond < second) {
			this.hour = "00";
			this.minute = "00";
			this.second = "00";
			this.miliSecond = "000000";
			return;
		}

		//Calc by BigDecimal
		BigDecimal thisSecondBd = new BigDecimal(String.valueOf(thisSecond));
		BigDecimal argsSecondSecondBd = new BigDecimal(String.valueOf(second));
		BigDecimal calcSecondBd = thisSecondBd.subtract(argsSecondSecondBd);

		//Get time by calc result
		String calcTime = this.chgTimeZoneBySecond(calcSecondBd.doubleValue());

		//Reset fields
		this.hour = calcTime.substring(0, 2);
		this.minute = calcTime.substring(3, 5);
		this.second = calcTime.substring(6, 8);
		this.miliSecond = calcTime.substring(9, 15);
	}


	/**
	 * Calc time increment
	 *
	 * <p>
	 * Calculate increment timeZone add argTimeZone<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param argsTimeZone
	 * @return After calculate time (hh:mm:ss.ffffff)
	 */
	public String add(TimeZone argsTimeZone) {

		//thisTimeZone and argTimeZone convert second
		double thisSecondDb = this.convertSecond();
		double argTimeZoneSecondDb = argsTimeZone.convertSecond();

		//Calc by BigDecimal
		BigDecimal thisSecondBd = new BigDecimal(String.valueOf(thisSecondDb));
		BigDecimal argTimeZoneSecondBd = new BigDecimal(String.valueOf(argTimeZoneSecondDb));
		BigDecimal calcTimeSecondBd = argTimeZoneSecondBd.add(thisSecondBd);

		//Get calced result
		double calcSecond = calcTimeSecondBd.doubleValue();

		//When calcSecond is more than HOUR24_FMT_SECOND(86400.000000)
		if (HOUR24_FMT_SECOND <= calcSecond) {
			BigDecimal hour24FmtSecondBD = new BigDecimal(String.valueOf(HOUR24_FMT_SECOND));
			calcTimeSecondBd = calcTimeSecondBd.subtract(hour24FmtSecondBD);
		}

		return this.chgTimeZoneBySecond(calcTimeSecondBd.doubleValue());
	}


	/**
	 * Reset Time increment By hour
	 *
	 * <p>
	 * Calculate increment timeZone add hour<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param hour
	 */
	public void addHour(int hour) {

		//Validation
		if (hour < 0) {
			this.executeException("param is invalid");
		}

		//When hour is more than 24
		if (HOUR_24 <= hour) {
			hour = hour % HOUR_24;
		}
		int newHour = Integer.parseInt(this.hour) + hour;

		//When newHour is over 24
		if (HOUR_24 <= newHour) {
			newHour -= HOUR_24;
		}

		//Reset fields
		this.hour = String.format("%02d",newHour);
	}


	/**
	 * Reset Time increment By minute
	 *
	 * <p>
	 * Calculate increment timeZone add minute<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param minute
	 */
	public void addMinute(int minute) {

		//Validation
		if (minute < 0) {
			this.executeException("param is invalid");
		}

		//Calc Setting hour and minute by minute
		int newHour = minute / 60 + Integer.parseInt(this.hour);
		int newMinute = minute % 60 + Integer.parseInt(this.minute);

		//newMinute is over 60
		if (60 <= newMinute) {
			newMinute = newMinute - 60;
			newHour++;
		}

		//Reset fields
		this.hour = String.format("%02d",newHour);
		this.minute = String.format("%02d",newMinute);
	}


	/**
	 * Reset Time increment By second
	 *
	 * <p>
	 * Calculate increment timeZone add second<br>
	 * When across day, calculate time. But, Acrossing day can't be calculated.
	 * </p>
	 *
	 * @param second
	 */
	public void addSecond(double second) {

		//Validation
		if (second < 0) {
			this.executeException("param is invalid");
		}

		//Change BigDecimal and adjust format s.ffffff
		BigDecimal argsSecondBd = new BigDecimal(String.valueOf(second));
		argsSecondBd = argsSecondBd.setScale(MILI_SECOND_LENGTH, RoundingMode.DOWN);

		//Get time by this fields
		double thisSecond = this.convertSecond();

		BigDecimal thisSecondBd = new BigDecimal(String.valueOf(thisSecond));
		BigDecimal calcSecondBd = thisSecondBd.add(argsSecondBd);
		String calcTime = this.chgTimeZoneBySecond(calcSecondBd.doubleValue());

		//Get hour by calcTime
		int calcHourInt = Integer.parseInt(calcTime.substring(0, 2));

		if (24 <= calcHourInt) {
			calcHourInt = calcHourInt % HOUR_24;
		}

		//Reset fields
		this.hour = String.format("%02d", calcHourInt);
		this.minute = calcTime.substring(3, 5);
		this.second = calcTime.substring(6, 8);
		this.miliSecond = calcTime.substring(9, 15);
	}



	//---------------
	//Private Method
	//---------------

	/**
	 * {Private Method}
	 *
	 * <p>
	 * Validation 0 <= hour < 24<br>
	 * When Error is return true.
	 * </p>
	 *
	 * @param hour
	 * @return true: Not applicable to Validation<br>
	 *  false: Applicable to Validation
	 */
	private boolean isNotValiHour(int hour) {

		if(!(0 <= hour && hour < HOUR_24)) {
			return true;
		}

		return false;
	}


	/**
	 * {Private Method}
	 *
	 * <p>
	 * Validation 0 <= minute < 60<br>
	 * When Error is return true.
	 * </p>
	 *
	 * @param minute
	 * @return true: Not applicable to Validation<br>
	 *  false: Applicable to Validation
	 */
	private boolean isNotValiMinutes(int minute) {

		if(!(0 <= minute && minute < 60)) {
			return true;
		}

		return false;
	}


	/**
	 * {Private Method}
	 *
	 * <p>
	 * Validation 0 <= second < 60<br>
	 * When Error is return true.
	 * </p>
	 *
	 * @param second
	 * @return true: Not applicable to Validation<br>
	 *  false: Applicable to Validation
	 */
	private boolean isNotValiSecond(int second) {

		if(!(0 <= second && second < 60)) {
			return true;
		}

		return false;
	}


	/**
	 * {Private Method}
	 *
	 * <p>
	 * Validation 0 <= miliSecond < 1000000<br>
	 * When Error is return true.
	 * </p>
	 *
	 * @param miliSecond
	 * @return true: Not applicable to Validation<br>
	 *  false: Applicable to Validation
	 */
	private boolean isNotValiMiliSecond(int miliSecond) {

		if(!(0 <= miliSecond && miliSecond < 1000000)) {
			return true;
		}

		return false;
	}


	/**
	 * {Private Method}
	 *
	 * <p>
	 * Change to time format hh:mm:ss.ffffff
	 * </p>
	 *
	 * @param argsSecond
	 * @return hh:mm:ss.ffffff
	 */
	private String chgTimeZoneBySecond(double argsSecond) {

		//Get argsSecond By BigDecimal
		BigDecimal argsSecondBd = new BigDecimal(String.valueOf(argsSecond));
		BigDecimal num3600Bd = new BigDecimal("3600");

		//Calc hour by argsSecondBd
		BigDecimal hourBd = argsSecondBd.divide(num3600Bd, 0, RoundingMode.DOWN);

		//Convert hour(hh)
		String hour = String.format("%02d", hourBd.intValue());

		//Get hour of converted Second By BigDecimal
		BigDecimal hourSecondBd = hourBd.multiply(num3600Bd);
		BigDecimal num60Bd = new BigDecimal("60");

		//Calc minute by argsSecondBd and hourSecondBd
		BigDecimal minutesBd = (argsSecondBd.subtract(hourSecondBd)).divide(num60Bd, 0, RoundingMode.DOWN);

		//Convert minute(mm)
		String minute = String.format("%02d", minutesBd.intValue());

		//Get minute of converted Second By BigDecimal
		BigDecimal minutesSecondBd = minutesBd.multiply(num60Bd);

		//Calc second by argsSecondBd, hourSecondBd and minutesSecondBd
		BigDecimal secondBd = argsSecondBd.subtract(hourSecondBd).subtract(minutesSecondBd);

		//Get only second
		secondBd = secondBd.setScale(0, RoundingMode.DOWN);

		//Convert second(ss)
		String second = String.format("%02d", secondBd.intValue());

		//Calc miliSecond by argsSecondBd, hourSecondBd, minutesSecondBd and secondBd
		BigDecimal miliSecondBd = argsSecondBd.subtract(hourSecondBd).subtract(minutesSecondBd).subtract(secondBd);

		//Convert miliSecond(fff...)
		String miliSecond = String.valueOf(miliSecondBd);
		miliSecond = miliSecond.replace("0.", "");

		//Add 0 util miliSecond convert format ffffff
		while (miliSecond.length() < MILI_SECOND_LENGTH) {
			miliSecond = miliSecond + "0";
		}

		return hour + ":" + minute + ":" + second + "." + miliSecond;
	}


	/**
	 * {Private Method}
	 *
	 * <p>
	 * Display massage in console and execute Exception<br>
	 * ex) Validation error, Can't used param etc...
	 * </p>
	 *
	 * @param massage
	 */
	private void executeException(String massage) {
		try {
			throw new UnsupportedException(massage);
		} catch(UnsupportedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
}

/**
 * Exception Class
 */
class UnsupportedException extends Exception {

	/**
	 * {Constructor}
	 *
	 * <p>
	 * Force to Exception
	 * </p>
	 *
	 * @param message
	 */
	public UnsupportedException(String message) {
		super(message);
	}
}