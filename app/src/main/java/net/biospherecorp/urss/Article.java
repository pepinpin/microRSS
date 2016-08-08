package net.biospherecorp.urss;

import android.content.Context;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


// This class deals with the Articles Object
// 6 main fields accessible by getters:
// Title, Url, Description, the site where the article is from (siteName),
// the date and the time of publication.
//
// This class uses the Builder Design Pattern
class Article implements Comparable<Article> {

	private Context _ctxt;

	private String _title, _url, _description, _siteName, _date, _time;
	private Date _dateRFC;

	// private constructor,
	// accessible by the static builder class
	private Article(Builder builder){

		this._ctxt = builder._ctxt;

		this._title = builder._title;
		this._url = builder._url;
		this._description = builder._description;
		this._siteName = builder._siteName;
		this._date = _parseDate(builder._date);
	}


	// This method parses a string (representing a date and time)
	// and returns either "Today", "Yesterday" or the date in the
	// dd/mm/yyyy format and the time in the hh:mm format
	//
	// Solely used by the constructor()
	private String _parseDate(String dateRaw){

		// if the date is NOT equal to the filter
		// (meaning that the article created is empty)
		if(!dateRaw.equals(ListFragment._filter)){

			// gets the date in the RFC format from the article's date
			_dateRFC = _parseDateInMilli(dateRaw);

			Calendar c= Calendar.getInstance();

			// get today's _date
			String format = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
			String today = sdf.format(c.getTime());

			//get yesterday's _date
			c.add(Calendar.DATE, - 1);
			String yesterday = sdf.format(c.getTime());

			// set Calendar with the Article's _date
			c.setTime(_dateRFC);

			// format the article's _date like dd/MM/yyyy
			String articleDate = _stringifyDate(c, false);

			// Compare article's _date with today's and yesterday's _date
			// and changes articleDate if needed
			if (articleDate.equals(today)){

				//if the article is from today, changes the _date to the "Today" string
				articleDate = _ctxt.getResources().getString(R.string.today);

			}else if (articleDate.equals(yesterday)){

				//if the article is from yesterday, changes the _date to the "Yesterday" string
				articleDate =  _ctxt.getResources().getString(R.string.yesterday);
			}

			// gets the time from the article's date in milliseconds
			_time = _stringifyTime(c, false);

			return articleDate;
		}

		return dateRaw;
	}

	// this method parses a string representing a date and time in the RSS format
	// and return its value in milliseconds
	//
	// Solely used by the method _parseDate()
	private Date _parseDateInMilli(String dateRaw){

		L.m(">> date : " + dateRaw);

		// different format of date to get the best chances of parsing it
		String format_1 = "EEE, dd MMM yyyy kk:mm:ss Z";
		String format_2 = "EEE, dd MMM yyyy kk:mm:ss z";
		String format_3 = "EEE, dd MMM yyyy kk:mm:ss";

		Date tmpDate = null;

		// let's try to parse the date with the 1st format
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format_1, Locale.US);
			tmpDate = sdf.parse(dateRaw);
		}catch (ParseException e){
			e.printStackTrace();
		}
		// if it doesn't work, let's parse it with the 2nd format
		if (tmpDate == null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format_2, Locale.US);
				tmpDate = sdf.parse(dateRaw);
			}catch (ParseException e){
				e.printStackTrace();
			}
		}
		// if it doesn't work either, let's parse it with the 3rd format
		if (tmpDate == null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format_3, Locale.US);
				tmpDate = sdf.parse(dateRaw);
			}catch (ParseException e){
				e.printStackTrace();
			}
		}
		// if nothing works, just return the date of the day
		if (tmpDate == null){
			tmpDate = new Date();
		}

		L.m(">>>> date parsed: " +  tmpDate.toString());

		return tmpDate;
	}


	// This method turns a calendar object into a string representing
	// the date the calendar object is set on
	//
	// Solely used by the method _parseDate()
	private String _stringifyDate(Calendar c, boolean withTime){

		String stringDate = _nullify(c.get(Calendar.DAY_OF_MONTH))
				+"/"+ _nullify(c.get(Calendar.MONTH)+1) // Month start at 0 = January... don't ask why ^^
				+"/"+c.get(Calendar.YEAR);

		if(withTime){
			stringDate += _stringifyTime(c, false);
		}

		return stringDate;
	}

	// This method turns a calendar object into a string representing
	// the time the calendar object is set on
	//
	// Used by the methods _parseDate() & _stringifyDate()
	private String _stringifyTime(Calendar c, boolean withSeconds){

		String stringTime = _nullify(c.get(Calendar.HOUR_OF_DAY))
				+":"+ _nullify(c.get(Calendar.MINUTE));

		if (withSeconds){
			stringTime += ":"+ _nullify(c.get(Calendar.SECOND));
		}

		return stringTime;
	}

	// This method adds a leading 0 to any int between -9 and 9
	// (useful for hours and day/month representation) and
	// returns a String
	//
	// Used by the methods _stringifyDate() & _stringifyTime()
	private String _nullify(int value){
		return value > -10 && value < 10 ?
				"0" + value //adds a 0 if the value is < 10
				: "" + value;
	}

	// Comparison method using the date in milliseconds
	//
	// Used to sort the articles by date in the ListAdapter
	@Override
	public int compareTo(@NonNull Article art) {

		if (_dateRFC.getTime() < art._dateRFC.getTime()) {
			return 1;
		}
		else if (_dateRFC.getTime() > art._dateRFC.getTime()) {
			return -1;
		}
		else {
			return 0;
		}
	}



	// Getters

	String getTitle() {
		return _title;
	}

	String getUrl() {
		return _url;
	}

	String getDescription() {
		return _description;
	}

	String getSite() {
		return _siteName;
	}

	String getDate() {
		return _date;
	}

	String getTime() {
		return _time;
	}



	// Static Class used to build the Article object
	static class Builder{

		private Context _ctxt;

		private String _date;
		private String _title ="", _description="", _url="", _siteName ="";

		Builder(Context ctxt){
			this._ctxt = ctxt;
		}

		Builder setDate(String date){
			this._date = date;
			return this;
		}

		Builder setTitle(String title){
			this._title = title;
			return this;
		}

		Builder setUrl(String url){
			this._url = url;
			return this;
		}

		Builder setDescription(String description){
			this._description = description;
			return this;
		}

		Builder setSiteName(String site){
			this._siteName = site;
			return this;
		}

		Article build(){
			return new Article(this);
		}
	}
}
