package test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis2.AxisFault;

import test.ClassificationStub.ClassificationForText;
import test.ClassificationStub.ClassificationResult;
import test.ClassificationStub.ClassificationXMLForText;
import test.ClassificationStub.ClassificationXMLForTextResponse;
import test.ClassificationStub.DescriptionResponse;
import test.ClassificationStub.KeyValue;
import test.ClassificationStub.VersionResponse;


public class TestWebservice {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Fill Test Data
		String text = "einfachter Text um den Service zu testen";
		HashMap<String,String> map = new HashMap<String,String>();
		
		String xmlResult = null;
		ClassificationResult r = null;
		
		try {
			ClassificationStub stb = new ClassificationStub("http://127.0.0.1:8080/Classification/services/Classification");

			VersionResponse a = stb.Version();
			System.out.println("\nVersion: " + a.get_return());
			
			DescriptionResponse b = stb.Description();
			System.out.println("\nDescription: " + b.get_return());
			
			ClassificationXMLForText inp1 = new ClassificationXMLForText();
			inp1.setText(text);
			KeyValue[] kvset = convertMapToKeyValue(map);
			inp1.setProperties(kvset);
			ClassificationXMLForTextResponse r1 = stb.ClassificationXMLForText(inp1);
			System.out.println("\nXMLResponse: " + r1.get_return());

			ClassificationForText inp2 = new ClassificationForText();
			inp2.setText(text);
			//KeyValue[] kvset = convertMapToKeyValue(map);
			inp2.setProperties(kvset);
			ClassificationResult r2 = stb.ClassificationForText(inp2).get_return();
			System.out.println("\nClassificationResponse Status: " + r2.getStatus());
			System.out.println("\nClassificationResponse Description: " + r2.getDescription());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	private static KeyValue[] convertMapToKeyValue(Map<String,String> conv) {
	        KeyValue[] result = new KeyValue[conv.size()];
	        int i = 0;
	        Iterator<Entry<String,String>> iter = conv.entrySet().iterator();

	        while (iter.hasNext()) {
	          Map.Entry<String,String> item = (Map.Entry<String,String>) iter.next();
	          KeyValue kv = new KeyValue();
	          kv.setKey(item.getKey());
	          kv.setValue(item.getValue());
	          result[i++] = kv;
	        }
	        return result;
	}
			

}
