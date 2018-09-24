package window;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import script.Zhihu;

import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtWeb;
	private JTextField txtField;
	private final String[] websites = new String[] { "知乎", "百度" };
	private String website = websites[0];

	public static void main(String[] args) {
		String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
				}
			}
		});
	}

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
				if (txtField.getText().isEmpty() || txtWeb.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "请将信息填写完整！");
					return;
				} else {
					if (website.equals("知乎")) {
						Zhihu zhihu = new Zhihu(txtWeb.getText(), txtField.getText());
						if (zhihu.download()) {
							JOptionPane.showMessageDialog(null, "下载成功");
						} else {
							JOptionPane.showMessageDialog(null, "下载失败，请检查网址、文件地址和网络！");
						}
					}
				}

			}
		});
		pnlSave.add(btnStart);
	}
}