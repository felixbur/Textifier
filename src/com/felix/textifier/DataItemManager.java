package com.felix.textifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.felix.util.FileUtil;
import com.felix.util.Util;

public class DataItemManager {
	Vector<DataItem> _testData;
	Vector<DataItem> _trainData;
	CategoryManager _categoryManager;
	Logger _logger;

	public DataItemManager(CategoryManager categoryManager) {
		_categoryManager = categoryManager;
		_logger = Logger
				.getLogger("de.telekom.laboratories.services.classification.textifier.DataItemManager");
	}

	public Vector<DataItem> getTestData() {
		return _testData;
	}

	public Vector<DataItem> getTrainData() {
		return _trainData;
	}

	public void loadTrain(String path) {
		_trainData = loadDataFromARFFFile(path);
		_logger.debug("loaded train: " + _trainData.size());
	}

	public void loadTest(String path) {
		_testData = loadDataFromARFFFile(path);
		_logger.debug("loaded test: " + _testData.size());
	}

	public void writeTrainARRF(String arffHeader, String path) {
		writeARRF(_trainData, arffHeader, path);
	}

	public void writeTestARRF(String arffHeader, String path) {
		writeARRF(_testData, arffHeader, path);
	}

	private void writeARRF(Vector<DataItem> dis, String arffHeader, String path) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path), Constants.CHAR_ENCODING));
			bw.write(arffHeader + "\n");
			for (Iterator<DataItem> iterator = dis.iterator(); iterator
					.hasNext();) {
				DataItem di = (DataItem) iterator.next();
				bw.write(di.getARFFLines());
			}
			bw.close();
		} catch (Exception e) {
			System.err.println("error trying to get file content"
					+ e.toString());
			e.printStackTrace();
		} finally {
			bw = null;
		}

	}

	private Vector<DataItem> loadDataFromARFFFile(String path) {
		Vector<DataItem> returnVec = new Vector<DataItem>();
		try {
			Vector<String> lines = FileUtil.getFileLines(path);
			boolean inData = false;
			for (Iterator<String> iterator = lines.iterator(); iterator
					.hasNext();) {
				String line = (String) iterator.next();
				if (line.trim().startsWith("@data")) {
					inData = true;
				}
				if (inData) {
					StringTokenizer st = new StringTokenizer(line, ",");
					try {
						String ent = st.nextToken();
						String catS = st.nextToken().trim();
						Category cat = _categoryManager.getCategoryForId(catS);
						DataItem di = new DataItem(ent.substring(1, ent
								.length() - 1), cat);
						returnVec.add(di);

					} catch (Exception e) {
						// wrong format
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVec;
	}

	public void printTrain() {
		for (Iterator<DataItem> iterator = _trainData.iterator(); iterator.hasNext();) {
			DataItem type = (DataItem) iterator.next();
			System.out.println(type.toString());
		}
	}
	public void printTest() {
		for (Iterator<DataItem> iterator = _testData.iterator(); iterator.hasNext();) {
			DataItem type = (DataItem) iterator.next();
			System.out.println(type.toString());
		}
	}
}
