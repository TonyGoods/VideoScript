package script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Zhihu {
	private String web;
	private String filePath;

	public Zhihu(String web, String filePath) {
		this.web = web;
		this.filePath = filePath;
	}

	public boolean download() {
		WebDriver webDriver = new HtmlUnitDriver();
		webDriver.get(web);
		String title = webDriver.getTitle();
		String html = webDriver.getPageSource();
		Document soup = Jsoup.parse(html);
		Elements as = soup.getElementsByTag("a");
		String webLinkTitle = "https://lens.zhihu.com/api/videos/";
		int fileIndex = 0;
		for (Element a : as) {
			if (a.hasAttr("data-lens-id")) {
				fileIndex++;
				try {
					URL url = new URL(webLinkTitle + a.attr("data-lens-id"));
					HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
					if (httpsURLConnection.getResponseCode() == 200) {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(httpsURLConnection.getInputStream()));
						String line = reader.readLine();
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(line);
						JSONObject playlist = (JSONObject) json.get("playlist");
						JSONObject ld = (JSONObject) playlist.get("ld");
						String m3u8Link = (String) ld.get("play_url");
						String tsWebLinkTitle = m3u8Link.substring(0, m3u8Link.lastIndexOf('/') + 1);
						URL m3u8URL = new URL(m3u8Link);
						HttpsURLConnection m3u8Https = (HttpsURLConnection) m3u8URL.openConnection();
						if (m3u8Https.getResponseCode() == 200) {
							BufferedReader m3u8Reader = new BufferedReader(
									new InputStreamReader(m3u8Https.getInputStream()));
							String m3u8Line;
							while ((m3u8Line = m3u8Reader.readLine()) != null) {
								if (m3u8Line.indexOf('#') != 0) {
									String fileName = filePath + "\\" + title.replace(" ", "") + fileIndex + ".ts";
									Runtime rt = Runtime.getRuntime();
									Process proc = rt.exec(
											"python downloadM3u8.py " + tsWebLinkTitle + m3u8Line + " " + fileName);
									BufferedReader readerPython = new BufferedReader(
											new InputStreamReader(proc.getInputStream()));
									String linePython;
									while ((linePython = readerPython.readLine()) != null) {
										if (linePython.equals("wrong")) {
											return false;
										}
									}
								}
							}
						}
					}
				} catch (MalformedURLException e1) {
					return false;
				} catch (IOException e1) {
					return false;
				} catch (ParseException e1) {
					return false;
				}
			}
		}
		return true;
	}

}
