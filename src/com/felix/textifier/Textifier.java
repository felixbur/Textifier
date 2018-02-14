package com.felix.textifier;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFrame;
import javax.xml.soap.Text;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.examples.PrintAnnotations;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.XMLInputSource;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.tartarus.snowball.ext.germanStemmer;

import psk.cmdline.ApplicationSettings;
import psk.cmdline.BooleanToken;
import psk.cmdline.StringToken;
import psk.cmdline.TokenOptions;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.unsupervised.attribute.StringToWordVectorWithKeywords;

import com.felix.textifier.evaluation.Evaluator;
import com.felix.textifier.evaluation.MultiClassInstance;
import com.felix.textifier.gui.MainFrame;
import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
import com.felix.util.StringUtil;

/**
 * Main class to classify String into categories and mark important keywords.
 * 
 * @author felix
 * 
 */
public class Textifier {
	private final String _configPath = "res/textifier.properties";
	private KeyValues _config = null;
	private static Textifier _textifierRef = null;
	private String _arffHeader = null;
	private MainFrame _mainFrame = null;
	private FilteredClassifier _model = null;
	private String _modelPath = "";
	private String _appPath = null;
	private ApplicationSettings _aps = null;
	private boolean _useGui = false;
	private Logger _logger = null;
	private Vector<String> _keywords = null;
	private SMO _smo = null;
	private NaiveBayes _naiveBayes = null;
	private J48 _j48 = null;
	private String _classifierType = "na";
	private CategoryManager _categoryManager = null;
	private ClassificationResult _lastResult = null;
	private Vector<String> _lastKeywords = null;
	private DataItemManager _dataItemManager;

	/**
	 * Constructor.
	 * 
	 * @param args
	 *            Command line style arguments.
	 */
	public Textifier(String[] args) {
		super();
		try {
			_aps = new ApplicationSettings();
			BooleanToken useGUI = new BooleanToken("useGui",
					"display the graphical interface", "",
					TokenOptions.optSwitch, false);
			BooleanToken showUsage = new BooleanToken("h", "print usage", "",
					TokenOptions.optSwitch, false);
			StringToken appPath = new StringToken("appRoot",
					"Root directory of config files.", "",
					TokenOptions.optDefault, "./webroot");
			_aps.addToken(showUsage);
			_aps.addToken(useGUI);
			_aps.addToken(appPath);
			_aps.parseArgs(args);
			_useGui = useGUI.getValue();
			_appPath = appPath.getValue();
			if (showUsage.getValue()) {
				System.out.println("Textifyer version " + Constants.VERSION);
				_aps.printUsage();
				System.exit(0);
			}
			loadConfig(_appPath + File.separator + _configPath);
			_logger = Logger.getLogger("com.tlabs.textifier.Textifyer");
			DOMConfigurator.configure(_config.getPathValue("logConfig"));
			loadCategories();
			_arffHeader = _config.getString("arffHeader_part1")
					+ _categoryManager.getCategoryIds()
					+ _config.getString("arffHeader_part2");
			_arffHeader = _arffHeader.replace("\\n", "\n");
			// debugOut(_arffHeader);
			_modelPath = _config.getPathValue("modelFile");
			_classifierType = _config.getString("defaultClassifier");
			loadModel();
			_dataItemManager = new DataItemManager(_categoryManager);
			_dataItemManager.loadTrain(_config.getPathValue("trainData"));
			_dataItemManager.loadTest(_config.getPathValue("testData"));
			if (_useGui) {
				showMainframe();
			}
			_textifierRef = this;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to get a singloton instance.
	 * 
	 * @return The instance of this class.
	 */
	public static Textifier getInstance() {
		return _textifierRef;
	}

	/**
	 * Categorize an input String.
	 * 
	 * @param inputString
	 *            The input String.
	 * @return A String describing the results.
	 */
	public String classifiy(String inputString) {
		try {
			markKeywords(inputString);
			String classifyString = inputString.trim();
			// classifyString = classifyString.replaceAll("\n", " ");
			classifyString = classifyString.replaceAll("[ \t\n]", " ");
			String input = _arffHeader + "\"" + classifyString + "\",noap";
			Instances classifyInsts = new Instances(new StringReader(input));
			classifyInsts.setClassIndex(1);
			classifyInsts.instance(0).setClassMissing();
			double cls = _model.classifyInstance(classifyInsts.instance(0));
			classifyInsts.instance(0).setClassValue(cls);
			String resId = classifyInsts.instance(0).stringValue(1);
			Category wekaCategory = _categoryManager.getCategoryForId(resId);
			wekaCategory.set_weight(classifyInsts.instance(0).weight());
			wekaCategory.set_keywords(_lastKeywords);
			Category keywordCategory = _categoryManager
					.getCategoryForString(inputString);
			keywordCategory.set_keywords(_lastKeywords);
			_lastResult = new ClassificationResult(null, classifyString);
			if (wekaCategory.isClassifiable()) {
				_lastResult.addCategory(wekaCategory);
				if (!keywordCategory.isSame(wekaCategory)
						&& keywordCategory.isClassifiable()) {
					_lastResult.addCategory(keywordCategory);
				}
			} else {
				_lastResult.addCategory(keywordCategory);
			}
			debugOut("Trextifier: input: " + inputString + ", output: " + resId
					+ " (" + _categoryManager.getCategoryLongname(resId)
					+ "),\ncategory based on keywords: "
					+ keywordCategory.toString());
			debugOut(getXMLInterpretation());
			return resId + " (" + _categoryManager.getCategoryLongname(resId)
					+ ") <> " + keywordCategory.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * (Re) load the categories from keyword text file.
	 */
	public void loadCategories() {
		_categoryManager = new CategoryManager(_config.getPathValue("keywords"));
	}

	/**
	 * Set statistical classifier type, e.g. SMO, NaiveBayes or J48 from weka.
	 * 
	 * @param type
	 */
	public void setClassifier(String type) {
		_classifierType = type;
	}

	/**
	 * Train a new model.
	 * 
	 * @param useStopwords
	 *            Whether to ignore list of stopwords.
	 * @param useKeywords
	 *            Whether to only use list of Keywords.
	 * @param numOfWords
	 *            Number of words to keep for each class (default: 1000).
	 */

	public void trainModel(boolean useStopwords, boolean useStemmer,
			boolean useKeywords, int numOfWords) {
		try {
			debugOut("training model ...");
			FileUtil.copyFile(_config.getFileHandler("trainData"), _config.getFileHandler("tempTrainData"));
			_model = new FilteredClassifier();
			// Filter
			StringToWordVectorWithKeywords stringToWordVector = new StringToWordVectorWithKeywords();
			if (useKeywords) {
				stringToWordVector.setStopwords(new File(_config
						.getPathValue("keywords")));
				stringToWordVector.setUseKeyWordsOption(true);
			}
			if (useStemmer) {
				/**
				 *This is how you'd use the weka integration of a stemmer:
				 *
				 * SnowballStemmer stemmer = new SnowballStemmer(); String[]
				 * options = { "-s", "german" }; stemmer.setOptions(options);
				 * stringToWordVector.setStemmer(stemmer);
				 **/
				runSnowballStemmerOnTrain();
				_dataItemManager.writeTrainARRF(_arffHeader, _config
						.getPathValue("tempTrainData"));
			}
			if (numOfWords != Constants.NUM_OF_WORDS_TO_USE) {
				stringToWordVector.setWordsToKeep(numOfWords);
			}
			if (useStopwords) {
				stringToWordVector.setStopwords(new File(_config
						.getPathValue("stopwords")));
			}
			BufferedReader trainReader = new BufferedReader(new FileReader(
					_config.getPathValue("tempTrainData")));// File with text
			// examples
			Instances trainInsts = new Instances(trainReader);
			trainInsts.setClassIndex(1);
			// Classifier
			if (_classifierType.compareTo(Constants.CLASSIFIER_SMO) == 0) {
				if (_smo == null) {
					_smo = new SMO();
				}
				_model.setClassifier(_smo);
			} else if (_classifierType
					.compareTo(Constants.CLASSIFIER_NAIVE_BAYES) == 0) {
				if (_naiveBayes == null) {
					_naiveBayes = new NaiveBayes();
				}
				_model.setClassifier(_naiveBayes);
			} else if (_classifierType.compareTo(Constants.CLASSIFIER_J48) == 0) {
				if (_j48 == null) {
					_j48 = new J48();
				}
				_model.setClassifier(_j48);
			} else {
				errorOut("unknown classifier type: " + _classifierType);
			}
			_model.setFilter(stringToWordVector);
			_model.buildClassifier(trainInsts);
			debugOut("training model finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get result from last classification in XML format.
	 * 
	 * @return A String in XML format.
	 */

	public String getXMLInterpretation() {
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		return outputter.outputString(_lastResult.getXMlDocument());
	}

	/**
	 * Run a test run of the current model. Outputs to debug out and Gui.
	 */
	public void runTest() {
		try {
			BufferedReader trainReader = new BufferedReader(new FileReader(
					_config.getPathValue("trainData")));// File with text
			// examples
			Instances trainInsts = new Instances(trainReader);
			trainInsts.setClassIndex(1);
			BufferedReader classifyReader = new BufferedReader(new FileReader(
					_config.getPathValue("testData")));
			Instances classifyInsts = new Instances(classifyReader);
			classifyInsts.setClassIndex(1);
			Evaluation eval = new Evaluation(trainInsts);
			eval.evaluateModel(_model, classifyInsts);
			debugOut(eval.toSummaryString(false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	public void runSnowballStemmerOnTrain() {
		runSnowballStemmer(_dataItemManager.getTrainData());
	}

	public void runSnowballStemmerOnTest() {
		runSnowballStemmer(_dataItemManager.getTestData());
	}

	public String runSnowballStemmer(String in) {
		germanStemmer stemmer = new germanStemmer();
		String newString = " ";
		Vector<String> inVec = StringUtil.stringToVector(in);
		for (Iterator<String> iterator = inVec.iterator(); iterator.hasNext();) {
			String word = (String) iterator.next();
			stemmer.setCurrent(word);
			for (int i = Constants.SNOWBALL_STEMMER_REPEAT; i > 0; i--) {
				stemmer.stem();
			}
			String out = stemmer.getCurrent();
			newString += out + " ";
			// System.out.println("\"" + word + "\" -> \"" + out + "\"");

		}
		return newString.trim();
	}

	public void runSnowballStemmer(Vector<DataItem> dataItems) {
		germanStemmer stemmer = new germanStemmer();
		for (Iterator<DataItem> iterator = dataItems.iterator(); iterator
				.hasNext();) {
			DataItem dataItem = (DataItem) iterator.next();
			String newString = " ";
			for (Iterator iterator2 = dataItem.getWords().iterator(); iterator2
					.hasNext();) {
				String word = (String) iterator2.next();
				stemmer.setCurrent(word);
				for (int i = Constants.SNOWBALL_STEMMER_REPEAT; i > 0; i--) {
					stemmer.stem();
				}
				String out = stemmer.getCurrent();
				newString += out + " ";
				// System.out.println("\"" + word + "\" -> \"" + out + "\"");

			}
			dataItem.setString(newString.trim());
		}

	}

	public void runUIMAPorterStemmer() {
		try {
			XMLInputSource in = new XMLInputSource(_config
					.getPathValue("snowballAnnotator"));
			ResourceSpecifier specifier = UIMAFramework.getXMLParser()
					.parseResourceSpecifier(in);
			// create AE here
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
			// create a CAS
			CAS cas = ae.newCAS();

			Vector<String> entries = Util.getTextFromARFF(_config
					.getPathValue("uimaTest"));
			for (Iterator<String> iterator = entries.iterator(); iterator
					.hasNext();) {
				String string = (String) iterator.next();
				System.out.println("orig: " + string);
				Vector<String> words = StringUtil.stringToVector(string);
				// germanStemmer stemmer = new germanStemmer();
				for (Iterator iterator2 = words.iterator(); iterator2.hasNext();) {
					String word = (String) iterator2.next();
					// stemmer.setCurrent(word);
					// int repeat = 1;
					// for (int i = repeat; i > 0; i--) {
					// stemmer.stem();
					// }
					// String out = stemmer.getCurrent();
					// System.out
					// .println("\"" + string2 + "\" -> \"" + out + "\"");
					cas.setDocumentText(word);
					cas.setDocumentLanguage("de");
					ae.process(cas);
					// print annotations to System.out
					// PrintAnnotations.printAnnotations(cas, System.out);
					// reset the CAS to prepare it for processing the next
					// documen
					Type annotationType = cas.getTypeSystem().getType(
							CAS.TYPE_NAME_ANNOTATION);
					Util.printAnnotations(cas, annotationType, System.out);
					cas.reset();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runUIMARegEx() {
		try {
			XMLInputSource in = new XMLInputSource(_config
					.getPathValue("regExAnnotator"));
			ResourceSpecifier specifier = UIMAFramework.getXMLParser()
					.parseResourceSpecifier(in);
			// create AE here
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
			// create a CAS
			CAS cas = ae.newCAS();

			Vector<String> entries = Util.getTextFromARFF(_config
					.getPathValue("uimaTest"));
			for (Iterator<String> iterator = entries.iterator(); iterator
					.hasNext();) {
				String string = (String) iterator.next();
				cas.setDocumentText(string);
				cas.setDocumentLanguage("de");
				ae.process(cas);
				// print annotations to System.out
				System.out.println("orig: " + string);
				// PrintAnnotations.printAnnotations(cas, System.out);
				Type annotationType = cas.getTypeSystem().getType(
						CAS.TYPE_NAME_ANNOTATION);
				Util.printAnnotations(cas, annotationType, System.out);
				cas.reset();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run a test run of the current model. Outputs to debug out and Gui.
	 */
	public void runMultiClassTest() {
		try {
			Evaluator evaluator = new Evaluator(_categoryManager
					.getCategories());
			BufferedReader trainReader = new BufferedReader(new FileReader(
					_config.getPathValue("trainData")));// File with text
			// examples
			Instances trainInsts = new Instances(trainReader);
			trainInsts.setClassIndex(1);
			BufferedReader classifyReader = new BufferedReader(new FileReader(
					_config.getPathValue("testData")));
			Instances classifyInsts = new Instances(classifyReader);
			classifyInsts.setClassIndex(1);
			for (int i = 0; i < classifyInsts.numInstances(); i++) {
				Instance test = classifyInsts.instance(i);
				Category groundTruth = _categoryManager.getCategoryForId(test
						.stringValue(1));
				double cls = _model.classifyInstance(test);
				test.setClassValue(cls);
				String resId = test.stringValue(1);
				Category wekaCategory = _categoryManager
						.getCategoryForId(resId);
				String testWords = test.stringValue(0);
				Category keywordCategory = _categoryManager
						.getCategoryForString(testWords);
				MultiClassInstance mci = new MultiClassInstance(groundTruth);
				mci.addCategory(wekaCategory);
				mci.addCategory(keywordCategory);
				mci.setText(testWords);
				evaluator.addInstance(mci);
			}
			for (Iterator iterator = evaluator.getInstances().iterator(); iterator
					.hasNext();) {
				MultiClassInstance mci = (MultiClassInstance) iterator.next();
				debugOut(mci.toString());
			}
			debugOut(evaluator.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	/**
	 * Save the current statistical model to file specified in configuration.
	 */
	public void saveModel() {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(_modelPath));
			oos.writeObject(_model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		debugOut("model saved to file: " + _modelPath);
	}

	/**
	 * Load the model specified in configuration.
	 */
	public void loadModel() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(_modelPath)));
			_model = (FilteredClassifier) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * (Re-) Load the keywords file.
	 */
	public void loadKeywords() {
		try {
			_keywords = FileUtil.getFileLines(_config.getPathValue("keywords"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mark keywords in a String.
	 * 
	 * @param input
	 *            The String.
	 * @return The String with keywords marked specified by configuration.
	 */
	public String markKeywords(String input) {
		String ret = input;
		_lastKeywords = new Vector<String>();
		if (_keywords == null) {
			loadKeywords();
		}
		String prefix = _config.getString("keywordMarkPrefix");
		String suffix = _config.getString("keywordMarkSuffix");
		for (Iterator<String> iterator = _keywords.iterator(); iterator
				.hasNext();) {
			String keyword = (String) iterator.next();
			if (keyword.trim().length() > 0) {
				ret = StringUtil.insertBeforeIgnoreCase(prefix, keyword, ret);
				ret = StringUtil.insertAfterIgnoreCase(suffix, keyword, ret);
				if (input.toLowerCase().contains(keyword.toLowerCase())) {
					_lastKeywords.add(keyword);
				}
			}
		}
		return ret;
	}

	private void errorOut(String message) {
		_logger.error(message);
		if (_useGui) {
			_mainFrame.setInfoText(message);
		}
	}

	private void debugOut(String message) {
		_logger.debug(message);
		if (_useGui & _mainFrame != null) {
			_mainFrame.setInfoText(message);
		}
	}

	private void showMainframe() {
		try {
			// UIManager
			// .setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		_mainFrame = new MainFrame(_config, this);
		_mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_mainFrame.pack();
		_mainFrame.setVisible(true);
	}

	private void loadConfig(String configPath) {
		try {
			_config = new KeyValues(configPath, "=");
			_config.setPathBase(_appPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Textifier t = new Textifier(args);
		// t.runUIMAPorterStemmer();
		// t.runUIMARegEx();
		// t.runSnowballStemmerOnTrain();
	}
}
