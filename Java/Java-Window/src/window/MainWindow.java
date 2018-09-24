package window;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.net.ssl.HttpsURLConnection;
import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtWeb;
	private JTextField txtField;
	private final String[] websites = new String[] { "知乎", "百度" };
	private String website = websites[0];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		JPanel pnlFactor = new JPanel();
		contentPane.add(pnlFactor);
		pnlFactor.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JLabel lblWebsite = new JLabel("视频所在网站：");
		pnlFactor.add(lblWebsite);

		JComboBox<String> webList = new JComboBox<String>();
		webList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				website = websites[webList.getSelectedIndex()];
			}
		});
		webList.setModel(new DefaultComboBoxModel<String>(websites));
		pnlFactor.add(webList);

		JLabel lblWeblink = new JLabel("视频网址：");
		pnlFactor.add(lblWeblink);

		txtWeb = new JTextField();
		pnlFactor.add(txtWeb);
		txtWeb.setColumns(55);

		JPanel pnlSave = new JPanel();
		contentPane.add(pnlSave);
		pnlSave.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JLabel lblSave = new JLabel("视频保存地址：");
		pnlSave.add(lblSave);

		txtField = new JTextField();
		txtField.setEditable(false);
		pnlSave.add(txtField);
		txtField.setColumns(44);

		JButton btnField = new JButton("选择文件夹");
		btnField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				jfc.showOpenDialog(null);
				File file = jfc.getSelectedFile();
				if (file == null || !file.exists()) {
					JOptionPane.showMessageDialog(null, "不存在的保存地址，请重新选择！");
					return;
				}
				txtField.setText(file.getAbsolutePath());
			}
		});
		pnlSave.add(btnField);

		JButton btnStart = new JButton("开始下载");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// txtField.getText().isEmpty() || 完了之后加到判断中去
				if (txtWeb.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "请将信息填写完整！");
					return;
				} else {
					WebDriver webDriver = new HtmlUnitDriver();
					webDriver.get("https://www.zhihu.com/question/56101818/answer/485899657");
					String title = webDriver.getTitle();
					System.out.println(webDriver.getPageSource());
					String html = webDriver.getPageSource();
					Document soup = Jsoup.parse(html);
					Elements as = soup.getElementsByTag("a");
					String webLinkTitle = "https://lens.zhihu.com/api/videos/";
					for (Element a : as) {
						if (a.hasAttr("data-lens-id")) {
							System.out.println(a.attr("data-lens-id"));
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
									System.out.println(tsWebLinkTitle);
									System.out.println(m3u8Link);
									URL m3u8URL = new URL(m3u8Link);
									HttpsURLConnection m3u8Https = (HttpsURLConnection) m3u8URL.openConnection();
									int index = 1;
									if (m3u8Https.getResponseCode() == 200) {
										System.out.println(System.getProperty("user.dir"));
										File dir = new File("../../Python");
										System.out.println(dir.getAbsolutePath());
										System.out.println(dir.getCanonicalPath());
										BufferedReader m3u8Reader = new BufferedReader(
												new InputStreamReader(m3u8Https.getInputStream()));
										String m3u8Line;

										while ((m3u8Line = m3u8Reader.readLine()) != null) {
											if (m3u8Line.indexOf('#') != 0) {
												/*
												 * (webDriver.get(tsWebLinkTitle + m3u8Line);
												 * 
												 * File file = new File(
												 * "C:\\Users\\Administrator\\Desktop\\" + title + index + ".ts"); if
												 * (file.exists()) { file.delete(); } file.createNewFile();
												 */
												URL tsURL = new URL(tsWebLinkTitle + m3u8Line);
												System.out.println(tsWebLinkTitle + m3u8Line);
												/*
												 * .openConnection(); if (tsUrlConnection.getResponseCode() == 200) {
												 * BufferedReader tsReader = new BufferedReader( new
												 * InputStreamReader(tsUrlConnection.getInputStream())); BufferedWriter
												 * writer = new BufferedWriter(new FileWriter(file)); String tsLine;
												 * while ((tsLine = tsReader.readLine()) != null) {
												 * writer.write(webDriver.getPageSource()); // writer.write(tsLine); //
												 * System.out.println(tsLine); } writer.flush(); writer.close();
												 * tsReader.close(); }
												 */
												Runtime rt = Runtime.getRuntime();
												Process proc = rt.exec("python downloadM3u8.py " + tsWebLinkTitle
														+ m3u8Line + " " + "C:\\Users\\Administrator\\Desktop\\1.ts");
												BufferedReader reader1 = new BufferedReader(
														new InputStreamReader(proc.getInputStream()));
												String line1;
												while ((line1 = reader1.readLine()) != null) {
													System.out.println(line1);
												}
											}
										}
									}
								}
							} catch (MalformedURLException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
					}
				}

			}
		});
		pnlSave.add(btnStart);
	}

}
