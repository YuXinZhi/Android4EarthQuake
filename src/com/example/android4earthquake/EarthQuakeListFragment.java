package com.example.android4earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ListFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;

public class EarthQuakeListFragment extends ListFragment {
	private static final String TAG = "EARTHQUKE";
	ArrayAdapter<Quake> aa;
	ArrayList<Quake> earthquakes = new ArrayList<Quake>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int layoutID = android.R.layout.simple_expandable_list_item_1;
		aa = new ArrayAdapter<Quake>(getActivity(), layoutID, earthquakes);
		setListAdapter(aa);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				refreshEarthquakes();
			}
		});
		thread.start();
	}

	private Handler handler = new Handler();

	public void refreshEarthquakes() {
		URL url;

		try {
			String quakeFeed = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=xml&starttime=2014-01-01&endtime=2014-01-02&minmagnitude=5";
			url = new URL(quakeFeed);
			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
			int responseCode = httpURLConnection.getResponseCode();
			System.out.println("responseCode=" + responseCode);
			if (responseCode == httpURLConnection.HTTP_OK) {
				System.out.println("OK");
				InputStream in = httpURLConnection.getInputStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// 分析地震源
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				// 清除旧的地震数据
				earthquakes.clear();

				// 获得每个地震的列表
				NodeList nl = docEle.getElementsByTagName("entry");
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element entry = (Element) nl.item(i);
						Element title = (Element) entry.getElementsByTagName("title").item(0);
						Element g = (Element) entry.getElementsByTagName("georss:point").item(0);
						Element when = (Element) entry.getElementsByTagName("updated").item(0);
						Element link = (Element) entry.getElementsByTagName("link").item(0);

						String details = title.getFirstChild().getNodeValue();
						String hostname = "http://earthquake.usgs.gov";
						String linkString = hostname + link.getAttribute("href");

						String point = g.getFirstChild().getNodeValue();
						String dt = when.getFirstChild().getNodeValue();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
						Date qdate = new GregorianCalendar(0, 0, 0).getTime();
						qdate.parse(details);

						String[] location = point.split(" ");
						Location l = new Location("dummyGPS");
						l.setLatitude(Double.parseDouble(location[0]));
						l.setLongitude(Double.parseDouble(location[1]));
						String magnitubeString = details.split(" ")[1];
						int end = magnitubeString.length() - 1;
						double magnitube = Double.parseDouble(magnitubeString.substring(0, end));
						details = details.split(".")[1].trim();
						final Quake quake = new Quake(qdate, details, l, magnitube, linkString);

						// 处理一个发现的地震
						handler.post(new Runnable() {

							@Override
							public void run() {
								addNewQuake(quake);
							}
						});
					}

				}
			}
		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException");
		} catch (IOException e) {
			Log.d(TAG, "IOException");
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "ParserConfigurationException");
		} catch (SAXException e) {
			Log.d(TAG, "SAXException");
		}

	}

	private void addNewQuake(Quake quake) {
		// 将地震添加到数组列表中
		earthquakes.add(quake);
		aa.notifyDataSetChanged();
	}
}
