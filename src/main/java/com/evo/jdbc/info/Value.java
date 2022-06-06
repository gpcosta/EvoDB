package com.evo.jdbc.info;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Class that represents some value for PreparedStatement
 * It can escape the value to inject into the Statement.
 * This is useful when it is needed to log the PreparedStatement with the values.
 *
 * Never use this class and its escape method to simulate a PreparedStatement that will go be sent to the Database
 */
public class Value {
	
	private Object value;
	
	public Value(Object value) {
		this();
		this.value = value;
	}
	
	public Value() {}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.convertToString(value);
	}
	
	private String convertToString(Object value) {
		if (value == null)
			return "NULL";
		
		String res;
		if (value instanceof byte[]) {
			res = Base64.getEncoder().encodeToString((byte[]) value);
		} else if (value instanceof Timestamp) {
			res = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
		} else if (value instanceof Date) {
			res = new SimpleDateFormat("yyyy-MM-dd").format(value);
		} else if (value instanceof Boolean) {
			res = Boolean.TRUE.equals(value) ? "1" : "0";
		} else {
			res = value.toString();
		}
		
		if (Number.class.isAssignableFrom(value.getClass()) || Boolean.class.isAssignableFrom(value.getClass()))
			return res;
		
		return "'" + escape(res) + "'";
	}
	
	private String escape(String str) {
		return Pattern.compile("'").matcher(str).replaceAll("\\'");
	}
}
