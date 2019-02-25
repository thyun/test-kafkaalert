package com.skp.kafkaalert.datastore;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.kafkaalert.entity.AlarmStatus;
import com.skp.util.CommonHelper;

import lombok.Data;

@Data
public class AlarmStatusDatastore {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static AlarmStatusDatastore instance = null;

	ConcurrentHashMap<String, AlarmStatus> hashMap = new ConcurrentHashMap<>();
	public static AlarmStatusDatastore getInstance() {
		if (instance == null) {
			instance = new AlarmStatusDatastore();
		}
		return instance;
	}

	public synchronized AlarmStatus get(String key) {
		return hashMap.get(key);
/*		String lkey = getLastKey(tkey, ttimestamp);		// Last key (ex) host-2018-12-06T05:57:00.000Z
		String lvalue = getLastValue(tvalue, ttimestamp);	// Last value (ex) 127.0.0.1-2018-12-06T05:57:00.000Z

		MetricEvent me = hashMap.get(lvalue);
		if (me == null) {
			logger.debug("getMetricEvent(): " + "New MetricEvent");

			me = new MetricEvent(lkey);
			me.put(tkey, tvalue);
			me.setTimestamp(ttimestamp);

			hashMap.put(lvalue, me);
		} else
			logger.debug("getMetricEvent(): " + "Existing MetricEvent");

		return me; */
	}

	private String getLastKey(String tkey, Date ttimestamp) {
		return tkey + "-" + CommonHelper.timestamp2Str(ttimestamp);
	}

	private String getLastValue(String tvalue, Date ttimestamp) {
		return tvalue + "-" + CommonHelper.timestamp2Str(ttimestamp);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (AlarmStatus me : hashMap.values()) {
			sb.append("\n");
//			sb.append(me.toString());
		}
		return sb.toString();
	}

	public AlarmStatus getOrCreate(String key) {
		AlarmStatus status = hashMap.get(key);
		if (status == null) {
			status = new AlarmStatus();
			hashMap.put(key, status);
		}
		return status;
	}

	public AlarmStatus getAndDelete(String key) {
		AlarmStatus status = hashMap.get(key);
		if (status != null)
			hashMap.remove(key);
		return status;
	}

}
