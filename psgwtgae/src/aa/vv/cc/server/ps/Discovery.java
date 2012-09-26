package aa.vv.cc.server.ps;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

public class Discovery {

	public Discovery() {
	}

	public String hasHub(Document doc) throws Exception {
		String hub = null;
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;
		
		try{
			xPathExpression = xPath.compile("/feed/link[@rel='hub']/@href");
			hub = (String) xPathExpression.evaluate(doc);
			if ((hub==null)||(hub.equals(""))){
				xPathExpression = xPath.compile("//link[@rel='hub']/@href");
				hub = (String) xPathExpression.evaluate(doc);			
			}	
			
			if (hub.equals("")){
				return null;
			}
			
			return hub;
		
		} catch (XPathExpressionException e) {
			return null;
		}
	}

	public String hasTopic(Document doc){
		String topic = null;
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;

		try {
			xPathExpression = xPath.compile("/feed/link[@rel='self']/@href");
			topic = (String) xPathExpression.evaluate(doc);
			if ((topic==null)||(topic.equals(""))){
				xPathExpression = xPath.compile("//link[@rel='self']/@href");
				topic = (String) xPathExpression.evaluate(doc);			
			}
			
			if (topic.equals("")){
				return null;
			}
			return topic;
			
		} catch (XPathExpressionException e) {
		    return null;
		}
		
	}
}