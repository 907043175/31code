package com.code31.common.baseservice.utils;

import com.google.common.base.Preconditions;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class TimeUtils {
	/** 毫秒 */
	public static final long MILLI_SECOND = TimeUnit.MILLISECONDS.toMillis(1);
	/** 秒 */
	public static final long SECOND = TimeUnit.SECONDS.toMillis(1);
	/** 分 */
	public static final long MIN = TimeUnit.MINUTES.toMillis(1);
	/** 时 */
	public static final long HOUR = TimeUnit.HOURS.toMillis(1);
	/** 天 */
	public static final long DAY = TimeUnit.DAYS.toMillis(1);

	/** 每分钟秒数 */
	public static final int SECONDS_MIN = (int) (MIN / SECOND);
	/** 每小时秒数 */
	public static final int SECONDS_HOUR = (int) (HOUR / SECOND);
	/** 每天小时数 */
	public static final int HOUR_DAY = (int) (DAY / HOUR);
	/** 每小时数分钟 */
	public static final int MIN_HOUR = (int) (HOUR / MIN);
	/** 每天秒数 */
	public static final int DAY_SECONDS = (int) (DAY / SECOND);
	
	/** 一周的天数 */
	private static final int DAYOFWEEK_CARDINALITY = 7;

	/** 年月日 时分, 格式如: 2011-01-11 01:10 */
	private static final DateFormat ymdhmFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	/** 年月日，格式如1970-07-10 */
	private static final DateFormat ymdFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	/** 小时和分钟数，格式如10:20 */
	private static final DateFormat hmFormat = new SimpleDateFormat("HH:mm");
	private static final Calendar calendar = Calendar.getInstance();
	public static TimeZone TIME_ZONE;
	
//	private static final SimpleDateFormat TodayBeginformat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
//	private static final SimpleDateFormat TodayEndformat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

	private static final SimpleDateFormat hmsFormat = new SimpleDateFormat(
			"HH:mm:ss");

	/**
	 * 年月日时分秒 格式：2012-04-13 16：00：00
	 */
	private static final DateFormat ymdhmsFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	
	public static long now() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 判断是否合法的时间格式(HH:mm:ss)
	 * 
	 * @param dayTime
	 * @return
	 */
	public static boolean isValidDayTime(String dayTime) {
		try {
			String[] _timeStr = dayTime.split(":");
			int _hour = Integer.parseInt(_timeStr[0]);
			int _minute = Integer.parseInt(_timeStr[1]);
			int _second = Integer.parseInt(_timeStr[2]);
			if (_hour < 0 || _hour > 23) {
				return false;
			}

			if (_minute < 0 || _minute > 59) {
				return false;
			}

			if (_second < 0 || _second > 59) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断是否合法的时间格式(HH:mm)
	 * 
	 * @param hhmm
	 * @return
	 */
	public static boolean isValidHhMmTime(String hhmm) {
		try {
			String[] _timeStr = hhmm.split(":");
			int _hour = Integer.parseInt(_timeStr[0]);
			int _minute = Integer.parseInt(_timeStr[1]);
			if (_hour < 0 || _hour > 23) {
				return false;
			}

			if (_minute < 0 || _minute > 59) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 根据创建时间和有效时间计算截止时间
	 * 
	 * @param start
	 *            物品的创建时间
	 * @param validTime
	 *            物品的有效时间长度
	 * @param timeUnit
	 *            有效时间的单位 {@link TimeUtils#MILLI_SECOND} ~ {@link TimeUtils#DAY}
	 * @return 物品的截止时间
	 */
	public static long getDeadLine(Timestamp start, long validTime,
			long timeUnit) {
		return TimeUtils.getDeadLine(start.getTime(), validTime, timeUnit);
	}

	/**
	 * 根据创建时间和有效时间计算截止时间
	 * 
	 * @param start
	 *            物品的创建时间
	 * @param validTime
	 *            物品的有效时间长度
	 * @param timeUnit
	 *            有效时间的单位 {@link TimeUtils#MILLI_SECOND} ~ {@link TimeUtils#DAY}
	 * @return 物品的截止时间
	 */
	public static long getDeadLine(long start, long validTime, long timeUnit) {
		return start + validTime * timeUnit;
	}

	/**
	 * 获取当天零点时间
	 * 
	 * @return
	 */
	public static long getTodayBegin(long now) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(now);
		_calendar.set(Calendar.HOUR_OF_DAY, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.SECOND, 0);
		_calendar.set(Calendar.MILLISECOND, 0);
		return _calendar.getTimeInMillis();
	}
	
	/**
	 * 获取当前的最后时间
	 * @param now
	 * @return
	 */
	public static long getTodayEnd(long now){
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(now);
		_calendar.set(Calendar.HOUR_OF_DAY, 23);
		_calendar.set(Calendar.MINUTE, 59);
		_calendar.set(Calendar.SECOND, 59);
		_calendar.set(Calendar.MILLISECOND, 0);
		return _calendar.getTimeInMillis();
	}

//	public static long  getTodayBegein()
//	{
//		try{
//			return TodayBeginformat.parse(TodayBeginformat.format(now())).getTime();
//		}catch(Exception e){
//			return getTodayBegin(now());
//		}
//	}
	
	/**
	 * 获取特定日期当天的零点时间
	 * 
	 * @return
	 */
	public static long getBeginOfDay(long time) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(time);
		_calendar.set(Calendar.HOUR_OF_DAY, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.SECOND, 0);
		_calendar.set(Calendar.MILLISECOND, 0);
		return _calendar.getTimeInMillis();
	}

	/**
	 * 获取时间戳字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getUrlTimeStamp(Date date) {
		DateFormat _format = new SimpleDateFormat("yyyyMMddHHmmss");
		return _format.format(date);
	}

	/**
	 * 是否是同一天
	 * 
	 * @param src
	 * @param target
	 * @return
	 */
	public static boolean isSameDay(long src, long target) {
		int offset = TIME_ZONE.getRawOffset(); // 只考虑了时区，没考虑夏令时
		return (src + offset) / DAY == (target + offset) / DAY;
	}

	/**
	 * 将分钟数转换为小时数和分钟数的数组 如80分钟转换为1小时20分
	 * 
	 * @param mins
	 * @return
	 */
	public static int[] toTimeArray(int mins) {
		int[] _result = new int[2];
		_result[0] = (int) (mins * MIN / HOUR);
		_result[1] = (int) (mins - _result[0] * HOUR / MIN);
		return _result;
	}

	/**
	 * 以格式{@link TimeUtils#hmFormat}解析数据，返回其表示的毫秒数
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static long getHMTime(String source) throws ParseException {
		Date date = hmFormat.parse(source);
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return hour * TimeUtils.HOUR + minute * TimeUtils.MIN;
	}

	/**
	 * 返回小时：分钟格式的时间
	 * 
	 * @param time
	 * @return
	 */
	public static String formatHMTime(long time) {
		return hmFormat.format(new Date(time));
	}

	/**
	 * 返回小时：分钟：秒格式的时间
	 * 
	 * @param time
	 * @return
	 */
	public static String formatHMSTime(long time) {
		return hmsFormat.format(new Date(time));
	}

	/**
	 * 以格式{@link TimeUtils#ymdFormat}解析数据，返回其表示的毫秒数
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static long getYMDTime(String source) throws ParseException {
		Date date = ymdFormat.parse(source);
		return date.getTime();
	}

	/**
	 * 返回 <b>年份-月份-日期</b> 格式的时间. 例如: 2012-12-24
	 * 
	 * @param time
	 * @return
	 */
	public static String formatYMDTime(long time) {
		return ymdFormat.format(time);
	}

	/**
	 * 以格式{@link TimeUtils#ymdhmFormat}解析数据，返回其表示的毫秒数
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static long getYMDHMTime(String source) throws ParseException {
		Date date = ymdhmFormat.parse(source);
		return date.getTime();
	}

	/**
	 * 以格式{@link TimeUtils#ymdhmFormat}解析数据，返回其对应的Calendar的实例
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static Calendar getCalendarByYMDHM(String source)
			throws ParseException {
		Calendar calendar = Calendar.getInstance();
		Date date = ymdhmFormat.parse(source);
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 返回 <b>年份-月份-日期 小时:分钟</b> 格式的时间. 例如: 2012-12-24 15:01
	 * 
	 * @param time
	 * @return
	 */
	public static String formatYMDHMTime(long time) {
		return ymdhmFormat.format(time);
	}

	/**
	 * 返回 <b>年份-月份-日期 小时:分钟:秒</b> 格式的时间. 例如: 2012-12-24 15:01:01
	 * 
	 * @param time
	 * @return
	 */
	public static String formatYMDHMSTime(long time) {
		return ymdhmsFormat.format(time);
	}
	

	/**
	 * 返回按时间单位计算后的ms时间，该时间必须足够小以致可用整型表示
	 * 
	 * @param time
	 * @param fromTimeUnit
	 * @return
	 */
	public static long translateTime(int time, long fromTimeUnit) {
		return TimeUtils.translateTime(time, fromTimeUnit, MILLI_SECOND);
	}

	/**
	 * 将指定的时间值转化为期望单位的时间值
	 * 
	 * @param time
	 * @param fromTimeUnit
	 * @param toTimeUnit
	 * @return
	 */
	public static long translateTime(long time, long fromTimeUnit,
			long toTimeUnit) {
		Preconditions.checkArgument(time >= 0);
		
		long milliTime = time * fromTimeUnit / toTimeUnit;
		Preconditions.checkArgument(milliTime <= Long.MAX_VALUE,
				String.format("The time value %d is too big!", time));
		return milliTime;
	}

	/**
	 * 获得指定时间的小时数
	 * 
	 * @param time
	 * @return
	 */
	public static int getHourTime(long time) {
		calendar.setTimeInMillis(time);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinTime(long time) {
		calendar.setTimeInMillis(time);
		return calendar.get(Calendar.MINUTE);
	}
	/**
	 * 设置指定时间的设置为给定的时间数(不改变的时间数可填-1)
	 * 
	 * @param time
	 * @param year
	 * @param month
	 * @param day
	 *            (月中的天数)
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getTime(long time, int year, int month, int day,
			int hour, int minute, int second) {
		calendar.setTimeInMillis(time);
		int _unChange = -1;
		if (year != _unChange) {
			calendar.set(Calendar.YEAR, year);
		}
		if (month != _unChange) {
			calendar.set(Calendar.MONTH, month);
		}
		if (day != _unChange) {
			calendar.set(Calendar.DAY_OF_MONTH, day);
		}
		if (hour != _unChange) {
			calendar.set(Calendar.HOUR_OF_DAY, hour);
		}
		if (minute != _unChange) {
			calendar.set(Calendar.MINUTE, minute);
		}
		if (second != _unChange) {
			calendar.set(Calendar.SECOND, second);
		}
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTimeInMillis();
	}

	/**
	 * 获得修正后的时间
	 * 
	 * @param originTime
	 * @param changeYear
	 * @param changeMonth
	 * @param changeDay
	 * @param changeHour
	 * @param changeMinute
	 * @param changeSecond
	 * @return
	 */
	public static long getChangeTime(long originTime, int changeYear,
			int changeMonth, int changeDay, int changeHour, int changeMinute,
			int changeSecond) {
		calendar.setTimeInMillis(originTime);
		int _unChange = 0;
		if (changeYear != _unChange) {
			calendar.add(Calendar.YEAR, changeYear);
		}
		if (changeMonth != _unChange) {
			calendar.add(Calendar.MONTH, changeMonth);
		}
		if (changeDay != _unChange) {
			calendar.add(Calendar.DAY_OF_MONTH, changeDay);
		}
		if (changeHour != _unChange) {
			calendar.add(Calendar.HOUR_OF_DAY, changeHour);
		}
		if (changeMinute != _unChange) {
			calendar.add(Calendar.MINUTE, changeMinute);
		}
		if (changeSecond != _unChange) {
			calendar.add(Calendar.SECOND, changeSecond);
		}
		return calendar.getTimeInMillis();
	}

	/**
	 * 判断start和end是否在同一个星期内(周一为一周开始)
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @author haijiang.jin
	 * @date 2009-02-04
	 */
	public static boolean isInSameWeek(long start, long end) {
		Calendar st = Calendar.getInstance();
		st.setTimeInMillis(start);
		Calendar et = Calendar.getInstance();
		et.setTimeInMillis(end);
		int days = Math.abs(TimeUtils.getSoFarWentDays(st, et));
		if (days < TimeUtils.DAYOFWEEK_CARDINALITY) {
			// 设置Monday为一周的开始
			st.setFirstDayOfWeek(Calendar.MONDAY);
			et.setFirstDayOfWeek(Calendar.MONDAY);
			if (st.get(Calendar.WEEK_OF_YEAR) == et.get(Calendar.WEEK_OF_YEAR)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 以日期中的日为实际计算单位，计算两个时间点实际日的差距 比如 12-1 23:00 和12-2 01:00，相差1天，而不是小于24小时就算做0天
	 * 如果(now - st)为正，则表示now在st之后
	 * 
	 * @param st
	 * @param now
	 * @return
	 */
	public static int getSoFarWentDays(Calendar st, Calendar now) {

		int sign = st.before(now) ? 1 : -1;
		if (now.before(st)) {
			Calendar tmp = now;
			now = st;
			st = tmp;
		}
		int days = now.get(Calendar.DAY_OF_YEAR) - st.get(Calendar.DAY_OF_YEAR);
		if (st.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			Calendar cloneSt = (Calendar) st.clone();
			while (cloneSt.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
				days += cloneSt.getActualMaximum(Calendar.DAY_OF_YEAR);
				cloneSt.add(Calendar.YEAR, 1);
			}
		}

		return days * sign;
	}

	public static int getSoFarWentHours(long time1, long time2) {
		Calendar st = Calendar.getInstance();
		st.setTimeInMillis(time1);

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(time2);

		if (now.before(st)) {
			Calendar tmp = now;
			now = st;
			st = tmp;
		}

		st.clear(Calendar.MILLISECOND);
		st.clear(Calendar.SECOND);
		st.clear(Calendar.MINUTE);

		int diffHour = 0;
		Calendar cloneSt = (Calendar) st.clone();
		while (cloneSt.before(now)) {
			cloneSt.add(Calendar.HOUR, 1);
			diffHour++;
		}

		if (diffHour != 0) {
			return diffHour - 1;
		} else {
			return diffHour;
		}
	}

	/**
	 * 获取两个时间的小时相差数, 经修改getSoFarWentHours(long time1, long time2)而来.
	 * 例：8:59:59与9:00:00相差1小时
	 * 
	 * @param time1
	 *            时间1
	 * @param time2
	 *            时间2
	 * @return 两个时间的小时相差数
	 */
	public static int getSoFarWentHours2(long time1, long time2) {
		Calendar st = Calendar.getInstance();
		st.setTimeInMillis(time1);

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(time2);

		if (now.before(st)) {
			Calendar tmp = now;
			now = st;
			st = tmp;
		}

		st.clear(Calendar.MILLISECOND);
		st.clear(Calendar.SECOND);
		st.clear(Calendar.MINUTE);

		now.clear(Calendar.MILLISECOND);
		now.clear(Calendar.SECOND);
		now.clear(Calendar.MINUTE);

		int diffHour = 0;
		Calendar cloneSt = (Calendar) st.clone();
		while (cloneSt.before(now)) {
			cloneSt.add(Calendar.HOUR, 1);
			diffHour++;
		}

		return diffHour;
	}

	/**
	 * 获得两个日期是否在同一月内
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean inSameMonth(long time1, long time2) {
		Calendar st = Calendar.getInstance();
		st.setTimeInMillis(time1);

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(time2);

		if (now.before(st)) {
			Calendar tmp = now;
			now = st;
			st = tmp;
		}

		if (st.get(Calendar.YEAR) == now.get(Calendar.YEAR)
				&& st.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
			return true;
		}
		return false;
	}

	/**
	 * specTime is in [st,now] or not?
	 * 
	 * @param st
	 * @param now
	 * @param specTime
	 * @return
	 */
	private static boolean hasSpecTimeBetween(long st, long now, long specTime) {
		if (st <= specTime && specTime <= now) {
			return true;
		}
		return false;
	}

	/**
	 * 得到从time1 到time2 中,specTime所指定的时分秒的时刻,有几次
	 * 
	 * @param time1
	 * @param time2
	 * @param specTime
	 * @return
	 */
	public static int getSpecTimeCountBetween(long time1, long time2,
			long specTime) {
		Calendar st = Calendar.getInstance();
		st.setTimeInMillis(time1);

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(time2);

		Calendar spec = Calendar.getInstance();
		spec.setTimeInMillis(specTime);

		if (now.before(st)) {
			Calendar tmp = now;
			now = st;
			st = tmp;
		}

		// 第一个时间的年月日和被比较时间的时间部分合成
		Calendar st_spec = mergeDateAndTime(st, spec);

		if (isSameDay(time1, time2)) {
			if (hasSpecTimeBetween(time1, time2, st_spec.getTimeInMillis())) {
				return 1;
			} else {
				return 0;
			}
		}

		int diffDay = 0;
		Calendar cloneSt = (Calendar) st_spec.clone();
		while (cloneSt.before(now)) {
			cloneSt.add(Calendar.DATE, 1);
			diffDay++;
		}

		if (st.after(st_spec)) {
			diffDay--;
		}

		return diffDay;
	}

	public static void main(String[] args) throws Exception {

		long triggerTime = System.currentTimeMillis() - TimeUtils.HOUR * 5;

		long lastLoginTime = System.currentTimeMillis() - TimeUtils.HOUR * 36;

		long now = System.currentTimeMillis();

		System.out.println(getSpecTimeCountBetween(lastLoginTime, now,
				triggerTime));

	}

	/**
	 * 把日期和时间合并
	 * 
	 * @param date
	 *            代表一个日期，方法其只取日期部分
	 * @param time
	 *            代表一个时间，方法其只取时间部分
	 * @return
	 */
	public static Calendar mergeDateAndTime(Calendar date, Calendar time) {
		Calendar cal = Calendar.getInstance();
		cal.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
				date.get(Calendar.DATE), time.get(Calendar.HOUR_OF_DAY),
				time.get(Calendar.MINUTE), time.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/**
	 * 获取几天后的当前时间点
	 * 
	 * @param day
	 * @return
	 */
	public static Date getAfterToday(int day) {
		Calendar c = Calendar.getInstance();

		c.add(Calendar.DATE, day);

		return c.getTime();
	}

	/**
	 * 设置几分钟之后的时间点
	 * 
	 * @param minutes
	 * @return
	 */
	public static Date getAfterMinutes(int minutes) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minutes);

		return c.getTime();
	}

	/**
	 * 获取当天的某个时间点
	 * 
	 * @param now
	 *            当前时间
	 * @param someTimeStr
	 *            时间字符串
	 * @return
	 * 
	 */
	public static long getTodaySomeTime(long now, String someTimeStr) {
		if (now <= 0 || someTimeStr == null || someTimeStr.isEmpty()) {
			// 如果当前时间 <= 0,
			// 或者时间字符串为空,
			// 则直接退出!
			return 0;
		}

		// 获取今天 0 点时间
		long time0 = getBeginOfDay(now);
		// 获取偏移时间
		long offsetTime = getHMSTime(someTimeStr);

		// 返回时间点
		return time0 + offsetTime;
	}

	/**
	 * 判断当前时间是否可以执行重置操作, 例如在凌晨 02:00 重置玩家征收次数
	 * 
	 * @param now
	 *            当前时间戳
	 * @param lastOpTime
	 *            上次操作时间
	 * @param resetTimeStr
	 *            重置时间字符串, 例如: 02:00
	 * @return
	 * 
	 */
	public static boolean canDoResetOp(long now, long lastOpTime,
			String resetTimeStr) {
		if (resetTimeStr == null || resetTimeStr.isEmpty()) {
			return false;
		}

		if (now - lastOpTime > DAY) {
			// 如果时间间隔已相差 1 天,
			// 则直接返回 true!
			return true;
		}

		// 根据上一次操作时间获取重置时间戳
		long lr = getTodaySomeTime(lastOpTime, resetTimeStr);

		if (canDoResetOp(now, lastOpTime, lr)) {
			return true;
		}

		// 根据当前时间获取重置时间戳
		long cr = getTodaySomeTime(now, resetTimeStr);

		if (canDoResetOp(now, lastOpTime, cr)) {
			return true;
		}

		return false;
	}

	/**
	 * 判断当前时间是否可以执行重置操作, 例如在凌晨 02:00 重置玩家征收次数
	 * 
	 * @param now
	 *            当前时间戳
	 * @param lastOpTime
	 *            上次操作时间
	 * @param resetTime
	 *            重置时间戳
	 * @return
	 * 
	 */
	public static boolean canDoResetOp(long now, long lastOpTime, long resetTime) {
		return (lastOpTime < resetTime) && (now > resetTime);
	}

	/**
	 * 获取 "小时:分钟:秒" 所表示的时间戳
	 * 
	 * @param s
	 * @return
	 */
	public static long getHMSTime(String str) {
		try {
			Date date = hmsFormat.parse(str);

			calendar.setTime(date);

			int h = calendar.get(Calendar.HOUR_OF_DAY);
			int m = calendar.get(Calendar.MINUTE);
			int s = calendar.get(Calendar.SECOND);

			return h * TimeUtils.HOUR + m * TimeUtils.MIN + s
					* TimeUtils.SECOND;
		} catch (Exception ex) {
			return 0L;
		}
	}

	public static int getNowHour() {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(now());
		return _calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int getNowDay(long times) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(times);
		int day = _calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	public static short getNowMonth(long times) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(times);
		short month = (short) _calendar.get(Calendar.MONTH);
		return month;
	}

	/**
	 * 当月最大天数
	 * 
	 * @param timeService
	 * @return
	 */
	public static short getMaxDayOfMonth(long times) {
		Calendar _calendar = GregorianCalendar.getInstance();
		_calendar.setTimeInMillis(times);
		short days = (short) _calendar.getActualMaximum(Calendar.DATE);
		return days;
	}

	/**
	 * 判断时间是否在同一个月内
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean IsSameMonth(long time1, long time2) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTimeInMillis(time1);
		short month1 = (short) _calendar.get(Calendar.MONTH);
		_calendar.setTimeInMillis(time2);
		short month2 = (short) _calendar.get(Calendar.MONTH);

		return month1 == month2;
	}
	
	public static boolean  IsInTime(Map<Long,Long>times,long time){
		if (times == null)
			return true;
		
		for (Map.Entry<Long, Long> entry:times.entrySet()){
			if (time>=entry.getKey() && time <= entry.getValue() ){
				return true;
			}
		}
		
		return false;
	}
}
