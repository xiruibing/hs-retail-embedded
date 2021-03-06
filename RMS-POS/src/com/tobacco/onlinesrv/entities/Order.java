package com.tobacco.onlinesrv.entities;

import com.tobacco.onlinesrv.provider.OrderProvider;
import android.net.Uri;

public class Order {
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ OrderProvider.CONTENT_URI + "/order");
	public static final String KEY_ID = "id";
	public static final String KEY_ORDER_ID = "orderid";
	public static final String KEY_DATE = "date";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_AGENTCYID = "agencyid";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_STATUS = "status";
	public static final String KEY_RECIEVE = "recieve";
}
