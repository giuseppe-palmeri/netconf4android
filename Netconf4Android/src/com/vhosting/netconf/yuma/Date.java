package com.vhosting.netconf.yuma;

/**
 * The Yuma Date typedef.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public class Date
{

	private String date;

	/**
	 * Create the YUMA Date typedef.
	 * 
	 * @param date
	 *            The date in the form: YYYY-MM-DD.
	 * @throws IllegalArgumentException
	 *             Throw this exception if the date is not in a correct format.
	 */
	public Date(String date) throws IllegalArgumentException
	{
		if (date != null)
			if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
				throw new IllegalArgumentException(
						"Format: YYYY-MM-DD; Invalid format: " + date);
		this.date = date;
	}

	/**
	 * Get the canonical value of the date.
	 * 
	 * @return The canonical value of the date.
	 */
	public String getCanonicalValue() {
		return date;
	}

	/**
	 * Get the date.
	 * 
	 * @return The date.
	 */
	public java.util.Date getValue() {
		if (date == null)
			return null;
		return new java.util.Date(date);

	}

}