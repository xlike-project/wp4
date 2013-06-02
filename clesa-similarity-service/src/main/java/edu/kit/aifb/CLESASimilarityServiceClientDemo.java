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

public class CLESASimilarityServiceClientDemo {

	// CLESA web service configuration information
//	public final static String SERVICE_URL = "http://localhost:8080/clesa-similarity/";
	public final static String SERVICE_URL = "http://km.aifb.kit.edu/services/clesa-similarity/";
	public final static String PARAM_LANGUAGE_A = "lang1";
	public final static String PARAM_LANGUAGE_B = "lang2";
	public final static String PARAM_TEXT_A = "doc1";
	public final static String PARAM_TEXT_B = "doc2";
	public final static String PARAM_OUTPUT = "output";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String language_a = "en";
		String language_b = "de";
		String text_a = "THE COUNCIL OF THE EUROPEAN ECONOMIC COMMUNITY, Having regard to Article 191 of the Treaty establishing the European Economic Community;Having regard to the proposals from the President of the European Parliament and the Presidents of the High Authority, the Commission of the European Economic Community and the Commission of the European Atomic Energy Community;Whereas the European Economic Community, the European Coal and Steel Community and the European Atomic Energy Community should have a joint official journal; HAS DECIDED: to create, as the official journal of the Community within the meaning of Article 191 of the Treaty establishing the European Economic Community, the Official Journal of the European Communities.";
		String text_b = "DER RAT DER EUROPÄISCHEN WIRTSCHAFTSGEMEINSCHAFT, gestützt auf Artikel 191 des Vertrages zur Gründung der Europäischen Wirtschaftsgemeinschaft, gestützt auf die Vorschläge des Präsidenten des Europäischen Parlaments sowie der Präsidenten der Hohen Behörde, der Kommission der Europäischen Wirtschaftsgemeinschaft und der Kommission der Europäischen Atomgemeinschaft, in der Erwägung, daß es zweckmäßig ist, daß die Europäische Wirtschaftsgemeinschaft, die Europäische Gemeinschaft für Kohle und Stahl und die Europäische Atomgemeinschaft über ein gemeinsames Amtsblatt verfügen, BESCHLIESST: als Amtsblatt der Gemeinschaft im Sinne des Artikels 191 des Vertrages zur Gründung der Europäischen Wirtschaftsgemeinschaft das Amtsblatt der Europäischen Gemeinschaften zu gründen.";
//		String text_a = "Agreement in the form of an Exchange of Letters extending the Protocol setting out for the period from June 2005 to 31 May 2006 the fishing opportunities and the financial contribution provided for in the Agreement between the European Economic Community and the Government of the Democratic Republic of São Tomé and Príncipe on fishing off the coast of São Tomé and Príncipe Letter from the Community Sirs have the honour to confirm that pending negotiations on amendments to be made to the Protocol currently in force June 2002 to 31 May 2005 setting out the fishing opportunities and financial contribution provided for in the Fisheries Agreement between the European Economic Community and the Government of the Democratic Republic of São Tomé and Príncipe we agree to the following interim arrangements From June 2005 to 31 May 2006 the arrangements applicable over the last three years will continue in operation The Community financial contribution under the interim arrangements will correspond to the yearly amount provided for in Article of the Protocol currently in force EUR 637500 That amount is to be treated entirely as financial compensation and will be paid no later than 31 January 2006 The Community will also provide financing of EUR 50000 during the year for an evaluation study on deep-water crab During this period fishing licences will be granted within the limits set in Article of the Protocol currently in force by means of fees or advances corresponding to those set in point of the Annex to the Protocol should be obliged if you would acknowledge receipt of this letter and confirm that you are in agreement with its terms Please accept Sirs the assurance of my highest consideration On behalf of the Council of the European Union Letter from the Government of the Democratic Republic of São Tomé and Príncipe Sirs have the honour to acknowledge receipt of your letter of today date which reads as follows have the honour to confirm that pending negotiations on amendments to be made to the Protocol currently in force June 2002 to 31 May 2005 setting out the fishing opportunities and financial contribution provided for in the Fisheries Agreement between the European Economic Community and the Government of the Democratic Republic of São Tomé and Príncipe we agree to the following interim arrangements From June 2005 to 31 May 2006 the arrangements applicable over the last three years will continue in operation The Community financial contribution under the interim arrangements will correspond to the yearly amount provided for in Article of the Protocol currently in force EUR 637500 That amount is to be treated entirely as financial compensation and will be paid no later than 31 January 2006 The Community will also provide financing of EUR 50000 during the year for an evaluation study on deep-water crab During this period fishing licences will be granted within the limits set in Article of the Protocol currently in force by means of fees or advances corresponding to those set in point of the Annex to the Protocol have the honour to confirm that the contents of your letter are acceptable to the Government of the Democratic Republic of São Tomé and Príncipe and that your letter and this one constitute an agreement in accordance with your proposal Please accept Sirs the assurance of my highest consideration For the Government of the Democratic Republic of São Tomé and Príncipe";
//		String text_b = "Abkommen in Form eines Briefwechsels über die Verlängerung des Protokolls zur Festlegung der Fangmöglichkeiten und der finanziellen Gegenleistung nach dem Abkommen zwischen der Europäischen Wirtschaftsgemeinschaft und der Regierung der Demokratischen Republik São Tomé und Príncipe über die Fischerei vor der Küste von São Tomé und Príncipe für die Zeit vom Juni 2005 bis zum 31 Mai 2006 Schreiben der Gemeinschaft Herr ich beehre mich zu bestätigen dass wir bis zum Abschluss der Verhandlungen über die zu vereinbarenden Änderungen des geltenden Protokolls vom Juni 2002 bis zum 31 Mai 2005 zur Festlegung der Fangmöglichkeiten und der finanziellen Gegenleistung nach dem Abkommen zwischen der Europäischen Wirtschaftsgemeinschaft und der Regierung der Demokratischen Republik São Tomé und Príncipe folgende Übergangsregelung für die Verlängerung des genannten Protokolls vereinbart haben Die während der vorangegangenen drei Jahre angewandte Regelung wird vom Juni 2005 bis zum 31 Mai 2006 beibehalten Die finanzielle Gegenleistung der Gemeinschaft für die Übergangsregelung entspricht dem in Artikel des derzeit geltenden Protokolls vorgesehenen Betrag 637500 EUR Dieser Betrag versteht sich in voller Höhe als finanzielle Gegenleistung die Zahlung wird bis spätestens 31 Januar 2006 geleistet Die Gemeinschaft finanziert außerdem während jenes Zeitraums eine Studie zur Beurteilung des Taschenkrebsbestands mit einem Betrag von 50000 EUR Während jenes Zeitraums werden Fanglizenzen innerhalb der in Artikel des derzeit geltenden Protokolls festgesetzten Grenzen mit Gebühren und Vorschüssen ausgestellt die denen entsprechen die unter Nummer im Anhang des Protokolls festgelegt sind Ich wäre Ihnen dankbar wenn Sie den Eingang dieses Schreibens bestätigen und Ihre Zustimmung zu seinem Inhalt mitteilen würden Genehmigen Sie Herr den Ausdruck meiner ausgezeichnetsten Hochachtung Im Namen des Rates der Europäischen Union Schreiben der Regierung der Demokratischen Republik São Tomé und Príncipe Herr ich beehre mich den Eingang Ihres heutigen Schreibens zu bestätigen das wie folgt lautet Ich beehre mich zu bestätigen dass wir bis zum Abschluss der Verhandlungen über die zu vereinbarenden Änderungen des geltenden Protokolls vom Juni 2002 bis zum 31 Mai 2005 zur Festlegung der Fangmöglichkeiten und der finanziellen Gegenleistung nach dem Abkommen zwischen der Europäischen Wirtschaftsgemeinschaft und der Regierung der Demokratischen Republik São Tomé und Príncipe folgende Übergangsregelung für die Verlängerung des genannten Protokolls vereinbart haben Die während der vorangegangenen drei Jahre angewandte Regelung wird vom Juni 2005 bis zum 31 Mai 2006 beibehalten Die finanzielle Gegenleistung der Gemeinschaft für die Übergangsregelung entspricht dem in Artikel des derzeit geltenden Protokolls vorgesehenen Betrag 637500 EUR Dieser Betrag versteht sich in voller Höhe als finanzielle Gegenleistung die Zahlung wird bis spätestens 31 Januar 2006 geleistet Die Gemeinschaft finanziert außerdem während jenes Zeitraums eine Studie zur Beurteilung des Taschenkrebsbestands mit einem Betrag von 50000 EUR Während jenes Zeitraums werden Fanglizenzen innerhalb der in Artikel des derzeit geltenden Protokolls festgesetzten Grenzen mit Gebühren und Vorschüssen ausgestellt die denen entsprechen die unter Nummer im Anhang des Protokolls festgelegt sind Ich beehre mich zu bestätigen dass die Regierung der Demokratischen Republik São Tomé und Príncipe dem Inhalt Ihres Schreibens zustimmen kann und dass Ihr Schreiben sowie das vorliegende Schreiben ein Abkommen gemäß Ihrem Vorschlag bilden Genehmigen Sie Herr den Ausdruck meiner ausgezeichnetsten Hochachtung Für die Regierung der Demokratischen Republik São Tomé und Príncipe Abkommen in Form eines Briefwechsels über die Verlängerung des Protokolls zur Festlegung der Fangmöglichkeiten und der finanziellen Gegenleistung nach dem Abkommen zwischen der Europäischen Wirtschaftsgemeinschaft und der Regierung der Demokratischen Republik São Tomé und Príncipe über die Fischerei vor der Küste von São Tomé und Príncipe für die Zeit vom Juni 2005 bis zum 31 Mai 2006";
//		String text_a = "to create, as the official journal of the Community within the meaning of Article 191 of the Treaty establishing the European Economic Community the Official Journal of the European Communities.";
//		String text_b = "als Amtsblatt der Gemeinschaft im Sinne des Artikels 163 des Vertrages zur Gründung der Europäischen Atomgemeinschaft das Amtsblatt der Europäischen Gemeinschaften zu gründen.";
		
//		String text_a = "car";
//		String text_b = "wagen";
		
		String output = "xml";
				
		String[] paramName = { PARAM_LANGUAGE_A, PARAM_TEXT_A, PARAM_LANGUAGE_B, PARAM_TEXT_B, PARAM_OUTPUT};
		String[] paramValue = { language_a, text_a, language_b, text_b, output};
		
		String httpResponse = httpGet(SERVICE_URL, paramName, paramValue);
		System.out.println("Http Response:");
		if(output.equals("sim")) 
			System.out.println(httpResponse);
		else if(output.equals("xml"))
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
