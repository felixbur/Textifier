package com.felix.textifier.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.log4j.Logger;

import com.felix.textifier.Constants;
import com.felix.textifier.Textifier;
import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
/**
 * Main class for the GUI.
 * @author felix
 *
 */
public class MainFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final int DIM_WIDTH = 600;
	private final int DIM_HEIGHT = 400;
	private Textifier _textifier;
	private JCheckBox _useStopwordsCheckbox;
	private JCheckBox _useStemmerCheckbox;
	private JCheckBox _useKeywordsCheckbox;
	private JRadioButton _smo;
	private JRadioButton _naiveBayes;
	private JRadioButton _j48;
	private ButtonGroup _classifierButtonGroup;
	private JLabel _outputLabel;
	private JEditorPane _infoEditorPane;
	private JEditorPane _infoHtmlEditorPane;
	private JSpinner _numOfWordsSpinner;
	private JTextArea _inputString;
	private String _outputPreText = "";
	private KeyValues _config;
	private Logger _logger;

	public MainFrame(KeyValues config, Textifier textifier) {
		super(config.getString("mainframe.windowTitle"));
		_logger = Logger.getLogger("textifyer.MainFrame");
		this._config = config;
		this._textifier = textifier;
		_outputPreText = config.getString("mainframe.outputPreText");
		JPanel labelPane = new JPanel();
		labelPane.add(new JLabel(Constants.PROGRAM_TITLE+" version " + Constants.VERSION));
		labelPane.add(makeInputPanel());
		labelPane.add(makeButtonsPanel());
		labelPane.add(makeClassifierPanel());
		labelPane.add(makeOptionsPanel());
		labelPane.add(makeOutputPanel());
		labelPane.add(makeInfoPane());
		this.getContentPane().add(labelPane);
		this.getContentPane().setPreferredSize(
				new Dimension(DIM_WIDTH, DIM_HEIGHT));
	}

	public void actionPerformed(ActionEvent e) {
		if (Constants.GUI_TEXTIFY_ID.equals(e.getActionCommand())) {
			classify();
		} else if (Constants.GUI_TRAINMODEL_ID.equals(e.getActionCommand())) {
			trainModel();
		} else if (Constants.GUI_TESTMODEL_ID.equals(e.getActionCommand())) {
			_textifier.runTest();
		} else if (Constants.GUI_TESTMULTIMODEL_ID.equals(e.getActionCommand())) {
			_textifier.runMultiClassTest();
		} else if (Constants.GUI_SAVEMODEL_ID.equals(e.getActionCommand())) {
			_textifier.saveModel();
		} else if (Constants.GUI_OPENKEYWORDS_ID.equals(e.getActionCommand())) {
			openKeywords();
		} else if (Constants.GUI_SAVEKEYWORDS_ID.equals(e.getActionCommand())) {
			saveKeywords();
		} else if (Constants.GUI_OPENSTOPWORDS_ID.equals(e.getActionCommand())) {
			openStopwords();
		} else if (Constants.GUI_SAVESTOPWORDS_ID.equals(e.getActionCommand())) {
			saveStopwords();
		}
	}

	public void setInfoText(String text) {
		_infoEditorPane.setText(text);
	}


	private JPanel makeInputPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JLabel inputLabel = new JLabel(_config
				.getString("mainframe.inputLabel"));
		_inputString = new JTextArea(20, 50);
		JScrollPane inputScrollPane = new JScrollPane(_inputString);
		inputScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inputScrollPane.setPreferredSize(new Dimension(450, 50));
		panel.add(inputLabel);
		panel.add(inputScrollPane);
		return panel;
	}

	private JPanel makeOutputPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		_infoHtmlEditorPane = new JEditorPane();
		_infoHtmlEditorPane.setEditable(false);
		_infoHtmlEditorPane.setPreferredSize(new Dimension(450, 50));
//		HTMLEditorKit hTMLEditorKit =;
		_infoHtmlEditorPane.setEditorKit( new HTMLEditorKit());
		_outputLabel = new JLabel(_outputPreText);
		JScrollPane scrollPane = new JScrollPane(_infoHtmlEditorPane);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(450, 50));
		panel.add(_outputLabel);
		panel.add(scrollPane);
		return panel;
	}

	private JPanel makeInfoPane() {
		// _infoLabel.setEditable(false);
		// Put the editor pane in a scroll pane.
		_infoEditorPane = new JEditorPane();
		_infoEditorPane.setContentType(Constants.CHAR_ENCODING);
		JScrollPane editorScrollPane = new JScrollPane(_infoEditorPane);
		editorScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(450, 145));
		editorScrollPane.setMinimumSize(new Dimension(20, 50));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(editorScrollPane);
		panel.add(makeStringButtonPanel());
		return panel;
	}

	private JPanel makeButtonsPanel() {
		JButton textifyButton;
		JButton trainModelButton;
		JButton testModelButton;
		JButton testMultiModelButton;
		JButton saveModelButton;
		JPanel panel = new JPanel(new GridLayout(1, 0));
		textifyButton = getNewButton(Constants.GUI_TEXTIFY_ID);
		trainModelButton = getNewButton(Constants.GUI_TRAINMODEL_ID);
		testModelButton = getNewButton(Constants.GUI_TESTMODEL_ID);
		testMultiModelButton = getNewButton(Constants.GUI_TESTMULTIMODEL_ID);
		saveModelButton = getNewButton(Constants.GUI_SAVEMODEL_ID);
		panel.add(textifyButton);
		panel.add(trainModelButton);
		panel.add(testModelButton);
		panel.add(testMultiModelButton);
		panel.add(saveModelButton);
		return panel;
	}
	private JPanel makeClassifierPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		_smo = new JRadioButton(Constants.CLASSIFIER_SMO);
		_naiveBayes= new JRadioButton(Constants.CLASSIFIER_NAIVE_BAYES);
		_j48 = new JRadioButton(Constants.CLASSIFIER_J48);
		_classifierButtonGroup = new ButtonGroup();
		_classifierButtonGroup.add(_smo);
		_classifierButtonGroup.add(_naiveBayes);
		_classifierButtonGroup.add(_j48);
		if (_config.getString("defaultClassifier").compareTo(Constants.CLASSIFIER_SMO)==0) {
			_smo.setSelected(true);
		} else if (_config.getString("defaultClassifier").compareTo(Constants.CLASSIFIER_NAIVE_BAYES)==0) {
			_naiveBayes.setSelected(true);
		} else if (_config.getString("defaultClassifier").compareTo(Constants.CLASSIFIER_J48)==0) {
			_j48.setSelected(true);
		}
		panel.add(_smo);
		panel.add(_naiveBayes);
		panel.add(_j48);
		return panel;
	}

	private JPanel makeStringButtonPanel() {
		JButton openKeywordsButton;
		JButton saveKeywordsButton;
		JButton openStopwordsButton;
		JButton saveStopwordsButton;
		JPanel panel = new JPanel(new GridLayout(0, 1));
		openKeywordsButton = getNewButton(Constants.GUI_OPENKEYWORDS_ID);
		openKeywordsButton.setPreferredSize(new Dimension(20, 30));
		saveKeywordsButton = getNewButton(Constants.GUI_SAVEKEYWORDS_ID);
		openStopwordsButton = getNewButton(Constants.GUI_OPENSTOPWORDS_ID);
		saveStopwordsButton = getNewButton(Constants.GUI_SAVESTOPWORDS_ID);
		panel.add(openKeywordsButton);
		panel.add(saveKeywordsButton);
		panel.add(openStopwordsButton);
		panel.add(saveStopwordsButton);
		return panel;
	}

	private JPanel makeOptionsPanel() {
		JPanel optionsPanel = new JPanel(new GridLayout(1, 0));
		_useStopwordsCheckbox = new JCheckBox(_config
				.getString("mainframe.filterStopwordsText"));
		_useStemmerCheckbox = new JCheckBox(_config
				.getString("mainframe.stemmerText"));
		_useKeywordsCheckbox = new JCheckBox(_config
				.getString("mainframe.useKeywordsText"));
		SpinnerModel numOfWordsModel = new SpinnerNumberModel(1000, // initial
				// value
				100, // min
				4000, // max
				100); // step
		_numOfWordsSpinner = new JSpinner(numOfWordsModel);
		JLabel numOfWordsLabel = new JLabel(_config
				.getString("mainframe.numOfWordsLabel"));
		optionsPanel.add(_useKeywordsCheckbox);
		optionsPanel.add(_useStopwordsCheckbox);
		optionsPanel.add(_useStemmerCheckbox);
		optionsPanel.add(_numOfWordsSpinner);
		optionsPanel.add(numOfWordsLabel);
		return optionsPanel;
	}

	private void classify() {
		_outputLabel.setText(_outputPreText
				+ _textifier.classifiy(_inputString.getText()));
		_infoHtmlEditorPane.setText("<html><body>"+_textifier.markKeywords(_inputString
				.getText()+"</body></html>"));
	}

	private void trainModel() {
		int numOfWords = (Integer) _numOfWordsSpinner.getValue();
		if (_smo.isSelected()) {
			_textifier.setClassifier(Constants.CLASSIFIER_SMO);
		} else if (_naiveBayes.isSelected()) {
			_textifier.setClassifier(Constants.CLASSIFIER_NAIVE_BAYES);
		}else if (_j48.isSelected()) {
			_textifier.setClassifier(Constants.CLASSIFIER_J48);
		} 
		_textifier.trainModel(_useStopwordsCheckbox.isSelected(),_useStemmerCheckbox.isSelected(),
				_useKeywordsCheckbox.isSelected(), numOfWords);
	}

	private JButton getNewButton(String configString) {
		JButton returnButton = new JButton(_config.getString(configString));
		returnButton.setActionCommand(configString);
		returnButton.addActionListener(this);
		return returnButton;
	}

	private void debugOut(String message) {
		_logger.debug(message);
		setInfoText(message);
	}
	private void openKeywords() {
		try {
			setInfoText(FileUtil.getFileText(_config.getPathValue("keywords"),
					Constants.CHAR_ENCODING));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveKeywords() {
		try {
			FileUtil.writeFileContent(_config.getPathValue("keywords"),
					_infoEditorPane.getText(), Constants.CHAR_ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		}
		_textifier.loadKeywords();
		_textifier.loadCategories();
	}

	private void openStopwords() {
		try {
			setInfoText(FileUtil.getFileText(_config.getPathValue("stopwords")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveStopwords() {
		try {
			FileUtil.writeFileContent(_config.getPathValue("stopwords"),
					_infoEditorPane.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
