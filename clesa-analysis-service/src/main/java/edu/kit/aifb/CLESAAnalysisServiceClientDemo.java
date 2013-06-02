package edu.kit.aifb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class CLESAAnalysisServiceClientDemo {

	// CLESA web service configuration information
//	public final static String SERVICE_URL = "http://localhost:8080/clesa-analysis";
	public final static String SERVICE_URL = "http://km.aifb.kit.edu/services/clesa-analysis/";
	public final static String PARAM_TEXT = "doc";
	public final static String PARAM_INPUT_LANGUAGE = "lang";
	public final static String PARAM_NUMBER = "num";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String language = "de";
		String num = "50";

		String text = "Verordnung (EWG, Euratom, EGKS) Nr. 261/68 des Rates vom 29. Februar 1968 zur Änderung der Verordnung Nr. 423/67/EWG, Nr. 6/67/Euratom des Rates vom 25. Juli 1967 über die Regelung der Amtsbezüge für die Mitglieder der EWG-Kommission und der EAG-Kommission sowie der Hohen Behörde, die nicht zu Mitgliedern der gemeinsamen Kommission der Europäischen Gemeinschaften ernannt worden sind (Veröffentlichungsbedürftige Rechtsakte) VERORDNUNG (EWG, EURATOM, EGKS) Nr. 261/68 DES RATES vom 29. Februar 1968 zur Änderung der Verordnung Nr. 423/67/EWG, Nr. 6/67/Euratom des Rates vom 25. Juli 1967 über die Regelung der Amtsbezuege für die Mitglieder der EWG-Kommission und der EAG-Kommission sowie der Hohen Behörde, die nicht zu Mitgliedern der gemeinsamen Kommission der Europäischen Gemeinschaften ernannt worden sind DER RAT DER EUROPÄISCHEN GEMEINSCHAFTEN gestützt auf den Vertrag zur Einsetzung eines gemeinsamen Rates und einer gemeinsamen Kommission der Europäischen Gemeinschaften insbesondere auf Artikel 34, in der Erwägung, daß es dem Rat obliegt, die Regelung für die Bezuege derjenigen ehemaligen Mitglieder der Hohen Behörde und der Kommissionen der Europäischen Wirtschaftsgemeinschaft und der Europäischen Atomgemeinschaft festzulegen, die aus ihrem Amt ausgeschieden und nicht zu Mitgliedern der Kommission ernannt worden sind HAT FOLGENDE VERORDNUNG ERLASSEN: Artikel Artikel der Verordnung Nr. 423/67/EWG, Nr. 6/67/Euratom des Rates vom 25. Juli 1967 über die Regelung der Amtsbezuege für die Mitglieder der EWG-Kommission und der EAG-Kommission sowie der Hohen Behörde, die nicht zu Mitgliedern der gemeinsamen Kommission der Europäischen Gemeinschaften ernannt worden sind wird mit Wirkung vom Januar 1968 durch einen Absatz ergänzt, der wie folgt lautet: %quot%Abweichend von Artikel der Verordnung Nr. 422/67/EWG, Nr. 5/67/Euratom darf das Ruhegehalt der in Artikel genannten ehemaligen Mitglieder der Hohen Behörde und der Kommissionen der Europäischen Wirtschaftsgemeinschaft und der Europäischen Atomgemeinschaft, die mindestens zwei Jahre im Amt waren, 15 v.H. des letzten Grundgehalts nicht unterschreiten.%quot% Artikel Diese Verordnung tritt am dritten Tag nach ihrer Veröffentlichung im Amtsblatt der Europäischen Gemeinschaften in Kraft. Diese Verordnung ist in allen ihren Teilen verbindlich und gilt unmittelbar in jedem Mitgliedstaat. Geschehen zu Brüssel am 29. Februar 1968. Im Namen des Rates Der Präsident COUVE DE MURVILLE ABl. Nr. 152 vom 13.7.1967, ABl. Nr. 187 vom 8.8.1967";
		
		String[] paramName = { PARAM_TEXT, PARAM_INPUT_LANGUAGE, PARAM_NUMBER };
		String[] paramValue = { text, language, num };
		
		String httpResponse = httpGet(SERVICE_URL, paramName, paramValue);
		System.out.println("Http Response:");
		System.out.println(httpResponse);
	}

	public static String httpPost(String urlRequest, String[] paramName, String[] paramVal) {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(urlRequest);
		InputStream rstream = null;
		// Add POST parameters
		for (int i = 0; i < paramName.length; i++) {
			method.addParameter(paramName[i], paramVal[i]);
		}
		// Send POST request
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			// Get the response body
			rstream = method.getResponseBodyAsStream();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getResponse(rstream);
	}

	protected static String getResponse(InputStream rstream) {
		String response = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				response += line;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	public static String httpGet(String urlRequest, String[] paramName, String[] paramVal) {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(urlRequest);
		NameValuePair[] nameValuepairs = new NameValuePair[paramName.length];
		for (int i = 0; i < paramName.length; i++) {
			nameValuepairs[i] = new NameValuePair(paramName[i], paramVal[i]);
		}
		method.setQueryString(nameValuepairs);

		InputStream rstream = null;
		// Send GET request
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			rstream = method.getResponseBodyAsStream();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getResponse(rstream);
	}

}
