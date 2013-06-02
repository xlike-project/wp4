package edu.kit.aifb.clesa;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.kit.aifb.concept.ConceptVectorSimilarity;
import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.concept.IConceptIndex;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IMappedConceptExtractor;
import edu.kit.aifb.concept.scorer.CosineScorer;
import edu.kit.aifb.document.TextDocument;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.nlp.LanguagePair;

/**
 * Servlet implementation class ConfigurationServlet
 */
public class CLESASimilarityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(CLESASimilarityServlet.class);

	public final static String PARAM_LANGUAGE_A = "lang1";
	public final static String PARAM_LANGUAGE_B = "lang2";
	public final static String PARAM_TEXT_A = "doc1";
	public final static String PARAM_TEXT_B = "doc2";
	public final static String PARAM_OUTPUT = "output";

	private final static String SERVICE_TAG = "item";
	private final static String DOC1_TAG = "doc1";
	private final static String DOC2_TAG = "doc2";
	private final static String LANGUAGE_TAG = "lang";
	private final static String SIMILARITY_TAG = "similarity";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Language languageA = null;
		Language languageB = null;
		String textA = null;
		String textB = null;
		TextDocument docA = null;
		TextDocument docB = null;
		String output = "sim";

		try {
			ApplicationContext context = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());

			@SuppressWarnings("unchecked")
			Map<String, String[]> parameterMap = request.getParameterMap();
			for (String parameter : parameterMap.keySet()) {
				if (parameter.equals(PARAM_LANGUAGE_A)) {
					languageA = Language.getLanguage(parameterMap.get(PARAM_LANGUAGE_A)[0]);
					logger.debug("language_a: " + languageA);
				} else if (parameter.equals(PARAM_LANGUAGE_B)) {
					languageB = Language.getLanguage(parameterMap.get(PARAM_LANGUAGE_B)[0]);
					logger.debug("language_b: " + languageB);
				} else if (parameter.equals(PARAM_TEXT_A)) {
					textA = parameterMap.get(PARAM_TEXT_A)[0];
				} else if (parameter.equals(PARAM_TEXT_B)) {
					textB = parameterMap.get(PARAM_TEXT_B)[0];
				} else if (parameter.equals(PARAM_OUTPUT)) {
					output = parameterMap.get(PARAM_OUTPUT)[0];
				}
			}

			docA = new TextDocument(PARAM_TEXT_A);
			docB = new TextDocument(PARAM_TEXT_B);
			docA.setText("content", languageA, textA);
			docB.setText("content", languageB, textB);

			String llabelA = Language.getLabel(languageA);
			String llabelB = Language.getLabel(languageB);
			String lpair = LanguagePair.getLabel(languageA, languageB);

			String[] conceptExtractorBeans = new String[2];
			conceptExtractorBeans[0] = "mlc_concept_extractor" + "_" + lpair + "_" + llabelA;
			conceptExtractorBeans[1] = "mlc_concept_extractor" + "_" + lpair + "_" + llabelB;

			IMappedConceptExtractor extractorA = (IMappedConceptExtractor) context.getBean(conceptExtractorBeans[0]);
			IMappedConceptExtractor extractorB = (IMappedConceptExtractor) context.getBean(conceptExtractorBeans[1]);

			logger.debug("Computing ESA vector of: " + docA.getText("content"));
			IConceptVector cvA = extractorA.extract(docA);

			logger.debug("Computing ESA vector of: " + docB.getText("content"));
			IConceptVector cvB = extractorB.extract(docB);

			ConceptVectorSimilarity sim = new ConceptVectorSimilarity(new CosineScorer());

			double value = sim.calcSimilarity(cvA, cvB);
			logger.debug("Computing similarity: " + value);

			if (output.equals("xml")) {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();

				Node item = doc.createElement(SERVICE_TAG);
				
				Node doc1Node = doc.createElement(DOC1_TAG);
				doc1Node.appendChild(doc.createTextNode(textA));
				NamedNodeMap doc1Attrs = doc1Node.getAttributes();
				Attr lang1Attr = doc.createAttribute(LANGUAGE_TAG);
				lang1Attr.setValue(languageA.toString());
				doc1Attrs.setNamedItem(lang1Attr);
				item.appendChild(doc1Node);
				
				Node doc2Node = doc.createElement(DOC2_TAG);
				doc2Node.appendChild(doc.createTextNode(textB));
				NamedNodeMap doc2Attrs = doc2Node.getAttributes();
				Attr lang2Attr = doc.createAttribute(LANGUAGE_TAG);
				lang2Attr.setValue(languageB.toString());
				doc2Attrs.setNamedItem(lang2Attr);
				item.appendChild(doc2Node);
				
				Node simNode = doc.createElement(SIMILARITY_TAG);
				simNode.appendChild(doc.createTextNode(String.valueOf(value)));
				item.appendChild(simNode);
				
				doc.appendChild(item);
				
				writeXML(doc, response);
			} else
				outputSimilarity(value, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#init()
	 */
	public void init() {

	}

	private void outputSimilarity(double similarity, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().print(String.valueOf(similarity));
	}

	public void writeXML(Document doc, HttpServletResponse response) throws TransformerFactoryConfigurationError,
			TransformerException, IOException {
		response.setContentType("text/xml; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(response.getOutputStream());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
	}

}
