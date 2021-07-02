package modelCheckCTL.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import modelCheckCTL.model.Model;

public class MainGui implements Runnable {
	private JLabel filenameLabel;
	private JComboBox<String> stateSelector;
	private JTextField formula;
	private JTextArea results;
	private final JFileChooser loadDialog = new JFileChooser();
	private JTextArea filecontent;
	private String modelinput;
	private Model model;

	public MainGui() {

		FileFilter filter = new FileFilter() {
			public String getDescription() {
				return "Model (*.txt)";
			}

			public boolean accept(File file) {
				if (file.isDirectory())
					return true;
				Pattern pattern = Pattern.compile("^.*" + Pattern.quote(".") + "txt$");
				Matcher matcher = pattern.matcher(file.getName());
				return matcher.find();
			}
		};
		loadDialog.addChoosableFileFilter(filter);
		loadDialog.setFileFilter(filter);

		javax.swing.SwingUtilities.invokeLater(this);
	}

	private void addMenu(final JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.setFont(getMenuFont());
		menuBar.add(menu);
		Color c = new Color(110, 131, 135);
		menuBar.setBackground(c);

		JMenuItem item = new JMenuItem("Select Model");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int val = loadDialog.showOpenDialog(null);
				if (val == JFileChooser.APPROVE_OPTION) {
					loadContent(loadDialog.getSelectedFile());
				}
			}
		});
		menu.add(item);

		menu.addSeparator();

		item = new JMenuItem("Exit Model Checker");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}

		});
		menu.add(item);

		// Help Menu
		menu = new JMenu("Help");
		menu.setFont(getMenuFont());
		menuBar.add(menu);
		item = new JMenuItem("CTL syntax");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "The following CTL syntax is used:\n\n"
						+ "phi ::= T | p | (!phi) | (phi && phi) | (phi || phi) | (phi -> phi)\n         | AXphi | EXphi | AFphi | EFphi | AGphi | EGphi | A[phiUphi] | E[phiUphi]\n\n"
						+ "Parentheses are strictly enforced and white space is ignored.", "CTL syntax",
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		menu.add(item);

		item = new JMenuItem("About");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame,
						"Author: Bhargav Balusu, Prathap Bathula, Baoxin Si\n" + "For: CS 5392\n"
								+ "Class: Formal Methods in Software Engineering",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(item);

		frame.setJMenuBar(menuBar);
	}

	private void addChecker(JPanel frame) {
		JPanel panel = new JPanel();
		Color c = new Color(207, 216, 215);
		panel.setBorder(BorderFactory.createTitledBorder(""));
		panel.setLayout(new GridLayout(3, 2));
		panel.setPreferredSize(new Dimension(200, 100));
		panel.setBackground(c);

		JLabel label = new JLabel("State: ");
		panel.add(label);

		stateSelector = new JComboBox();
		// stateSelector.addItem("----");
		panel.add(stateSelector);

		label = new JLabel("Formula: ");
		panel.add(label);

		formula = new JTextField(10);
		panel.add(formula);

		JLabel emptylabel = new JLabel("");
		panel.add(emptylabel);

		JButton button = new JButton("check");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				checkModel();
			}
		});
		button.setOpaque(true);
		button.setBackground(c);
		panel.add(button);

		JPanel panel2 = new JPanel();
		panel2.add(panel);
		panel2.setBorder(null);
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

		results = new JTextArea();
		results.setPreferredSize(new Dimension(300, 500));
		results.setBackground(panel.getBackground());
		results.setBorder(BorderFactory.createTitledBorder("Results"));
		results.setMargin(new Insets(5, 5, 5, 5));
		results.setWrapStyleWord(true);
		results.setLineWrap(true);
		results.setFont(getFileContentFont());
		panel2.add(results);
		frame.add(panel2);
	}

	private void addModelView(JPanel frame) {
		JPanel panel = new JPanel();

		Color c = new Color(181, 201, 195);
		panel.setBorder(BorderFactory.createTitledBorder("Model"));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBackground(c);

		JPanel titleBox = new JPanel();
		titleBox.setLayout(new GridLayout(2, 2));
		JLabel label = new JLabel("Loaded Model: ");
		titleBox.add(label);
		titleBox.setBackground(c);

		filenameLabel = new JLabel("none");
		titleBox.add(filenameLabel);

		label = new JLabel("Model Content: ");
		titleBox.add(label);

		panel.add(titleBox);

		filecontent = new JTextArea();
		filecontent.setPreferredSize(new Dimension(500, 500));
		filecontent.setFont(getFileContentFont());
		filecontent.setBackground(panel.getBackground());
		filecontent.setBorder(BorderFactory.createTitledBorder(""));
		filecontent.setMargin(new Insets(5, 5, 5, 5));
		filecontent.setWrapStyleWord(true);
		filecontent.setLineWrap(true);
		panel.add(filecontent);
		frame.add(panel);
	}

	public void run() {
		JFrame frame = new JFrame("Model Checker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 0));

		addMenu(frame);
		addChecker(panel);
		addModelView(panel);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private void loadContent(File file) {

		filenameLabel.setText(file.getName());
		filecontent.setText("");
		stateSelector.removeAllItems();
		try (FileInputStream fstream = new FileInputStream(file)) {

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

			String strLine;
			StringBuffer sb = new StringBuffer();

			while ((strLine = br.readLine()) != null) {
				sb.append(strLine);
			}
			modelinput = sb.toString();
			model = new Model(modelinput);
			model.getKripkeModel().stateList.forEach(x -> stateSelector.addItem(x.stateName));
			filecontent.append(model.getKripkeModel().toString());
			results.setText("");
			fstream.close();
		} catch (Exception e) {
			results.setText("Exception while reading input file : " + e.getMessage());
		}
	}

	private void checkModel() {
		if (stateSelector.getSelectedItem().toString().equals("----")) {
			results.setText("Please select the state");
			return;
		}
		if (formula.getText().equals("")) {
			results.setText("Please enter the formula to be verified");
			return;
		}
		try {
			results.setText("");

			model.setExpression(formula.getText());
			model.setState(stateSelector.getSelectedItem().toString());

			if (model.verifyFormula()) {
				results.setText(
						"Property " + formula.getText() + " for state " + stateSelector.getSelectedItem() + " HOLDS!");
			} else {
				results.setText("Property " + formula.getText() + " for state " + stateSelector.getSelectedItem()
						+ " DOES NOT HOLD.");
			}

		} catch (Exception e) {
			results.setText(e.getMessage());
		}
	}

	private Font getFileContentFont() {
		Map<TextAttribute, Object> attributes = new HashMap<>();

		attributes.put(TextAttribute.FAMILY, Font.DIALOG);
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		attributes.put(TextAttribute.SIZE, 14);

		return new Font(attributes);

	}

	private Font getMenuFont() {
		Map<TextAttribute, Object> attributes = new HashMap<>();

		attributes.put(TextAttribute.FAMILY, Font.BOLD);
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		attributes.put(TextAttribute.SIZE, 16);
		attributes.put(TextAttribute.FOREGROUND, Color.BLACK);

		return new Font(attributes);

	}

}
