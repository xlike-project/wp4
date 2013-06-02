package edu.kit.aifb.clesa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import edu.kit.aifb.concept.IConceptDescription;
import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.concept.IConceptIndex;
import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.document.TextDocument;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.nlp.LanguagePair;

/**
 * Servlet implementation class ConfigurationServlet
 */
public class CLESAAnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(CLESAAnalysisServlet.class);

	public final static String PARAM_TEXT = "doc";
	public final static String PARAM_INPUT_LANGUAGE = "lang";
	public final static String PARAM_NUMBER = "num";

	private final static String SERVICE_TAG = "item";
	private final static String DOC_TAG = "text";
	private final static String ANNOS_TAG = "analyses";
	private final static String ANNO_TAG = "analysis";
	private final static String ANNO_URL_TAG = "URL";
	private final static String ANNO_DISPLAYNAME_TAG = "displayName";
	private final static String ANNO_LANG_TAG = "lang";
	private final static String ANNO_DESCRIPTIONS_TAG = "descriptions";
	private final static String ANNO_DESCRIPTION_TAG = "description";
	private final static String ANNO_WEIGHT_TAG = "weight";

	private final static String WIKI_URL = ".wikipedia.org/wiki/";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String text = null;
		Language inputLanguage = null;
		TextDocument doc = null;
		int num = 100;

		try {
			ApplicationContext context = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());

			@SuppressWarnings("unchecked")
			Map<String, String[]> parameterMap = request.getParameterMap();
			
			for (String parameter : parameterMap.keySet()) {
				if (parameter.equals(PARAM_INPUT_LANGUAGE)) {
					inputLanguage = Language.getLanguage(parameterMap.get(PARAM_INPUT_LANGUAGE)[0]);
				} else if (parameter.equals(PARAM_TEXT)) {
					String[] values = parameterMap.get(PARAM_TEXT);
					StringBuffer sb = new StringBuffer();
					for (String value : values) {
						sb.append(" " + value);
					}
					text = sb.toString().trim();
				} else if (parameter.equals(PARAM_NUMBER)) {
					num = Integer.parseInt(parameterMap.get(PARAM_NUMBER)[0]);
				}
			}

			doc = new TextDocument(PARAM_TEXT);
			doc.setText("content", inputLanguage, text);

			String llabel = Language.getLabel(inputLanguage);
			String lpair = LanguagePair.getLabel(inputLanguage, inputLanguage);
			String conceptIndexBean = "wikipedia_concept_index" + "_" + lpair + "_" + llabel;

			IConceptIndex index = (IConceptIndex) context.getBean(conceptIndexBean);
			IConceptExtractor extractor = index.getConceptExtractor();

			logger.info("Computing ESA vector of: " + doc.getText("content"));
			IConceptVector cv = extractor.extract(doc);
			IConceptIterator it = cv.orderedIterator();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document xmlDoc = docBuilder.newDocument();

			xmlDoc = addWikipediaDescriptions(index, xmlDoc, it, text, llabel, num);

			writeXML(xmlDoc, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document addWikipediaDescriptions(IConceptIndex index, Document doc, IConceptIterator it, String text,
			String llabel, int num) {
		Node response = doc.createElement(SERVICE_TAG);
		Node textNode = doc.createElement(DOC_TAG);
		textNode.appendChild(doc.createTextNode(text));
		response.appendChild(textNode);

		Node NodeAnnos = doc.createElement(ANNOS_TAG);
		int i = 0;
		while (it.next() && i++ < num) {
			Node NodeAnno = doc.createElement(ANNO_TAG);
			String pageId = index.getConceptName(it.getId());
			logger.info("pageId: " + pageId + "\t\t" + "language: " + llabel);
			double weight = it.getValue();

			List<Description> descriptions = new ArrayList<Description>();
			String wikiArticle = extractDescriptions(pageId, descriptions, llabel);
			logger.info("displayname: " + wikiArticle);

			NamedNodeMap AnnoAttrs = NodeAnno.getAttributes();
			Attr attrWikiArticle = doc.createAttribute(ANNO_DISPLAYNAME_TAG);
			attrWikiArticle.setValue(wikiArticle);
			AnnoAttrs.setNamedItem(attrWikiArticle);

			Attr attrWeight = doc.createAttribute(ANNO_WEIGHT_TAG);
			attrWeight.setValue(String.valueOf(weight));
			AnnoAttrs.setNamedItem(attrWeight);

			Node NodeDescriptions = doc.createElement(ANNO_DESCRIPTIONS_TAG);

			for (Description desc : descriptions) {
				Node NodeDesc = doc.createElement(ANNO_DESCRIPTION_TAG);
				String url = desc.getURL();
				Language lang = desc.getLanguage();

				NamedNodeMap DescAttrs = NodeDesc.getAttributes();

				Attr attrUrl = doc.createAttribute(ANNO_URL_TAG);
				attrUrl.setValue(url);
				DescAttrs.setNamedItem(attrUrl);

				Attr attrLang = doc.createAttribute(ANNO_LANG_TAG);
				attrLang.setValue(lang.toString());
				DescAttrs.setNamedItem(attrLang);

				NodeDescriptions.appendChild(NodeDesc);
			}

			NodeAnno.appendChild(NodeDescriptions);
			NodeAnnos.appendChild(NodeAnno);

		}

		response.appendChild(NodeAnnos);
		doc.appendChild(response);
		return doc;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public String extractDescriptions(String pageId, List<Description> descriptions, String llabel) {
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		IConceptDescription desc = (IConceptDescription) context.getBean("wikipedia_concept_description_" + llabel);

		String displayName = "";
		try {
			String title_en = desc.getDescription(pageId, Language.EN);
			if (title_en != null) {
				String url = "http://" + Language.EN.toString() + WIKI_URL + title_en.replace(" ", "_");
				descriptions.add(new Description(url, Language.EN));
			}
			String title_de = desc.getDescription(pageId, Language.DE);
			if (title_de != null) {
				String url = "http://" + Language.DE.toString() + WIKI_URL + title_de.replace(" ", "_");
				descriptions.add(new Description(url, Language.DE));
			}
			String title_es = desc.getDescription(pageId, Language.ES);
			if (title_es != null) {
				String url = "http://" + Language.ES.toString() + WIKI_URL + title_es.replace(" ", "_");
				descriptions.add(new Description(url, Language.ES));
			}
			String title_fr = desc.getDescription(pageId, Language.FR);
			if (title_fr != null) {
				String url = "http://" + Language.FR.toString() + WIKI_URL + title_fr.replace(" ", "_");
				descriptions.add(new Description(url, Language.ES));
			}
			String title_sl = desc.getDescription(pageId, Language.SL);
			if (title_sl != null) {
				String url = "http://" + Language.SL.toString() + WIKI_URL + title_sl.replace(" ", "_");
				descriptions.add(new Description(url, Language.SL));
			}
			String title_zh = desc.getDescription(pageId, Language.ZH);
			if (title_zh != null) {
				String url = "http://" + Language.ZH.toString() + WIKI_URL + title_zh.replace(" ", "_");
				descriptions.add(new Description(url, Language.ZH));
			}
			String title_ca = desc.getDescription(pageId, Language.CA);
			if (title_ca != null) {
				String url = "http://" + Language.CA.toString() + WIKI_URL + title_ca.replace(" ", "_");
				descriptions.add(new Description(url, Language.CA));
			}
			if (llabel.equals(Language.EN.toString())) {
				displayName = title_en;
			}
			if (llabel.equals(Language.DE.toString())) {
				displayName = title_de;
			}
			if (llabel.equals(Language.ES.toString())) {
				displayName = title_es;
			}
			if (llabel.equals(Language.FR.toString())) {
				displayName = title_fr;
			}
			if (llabel.equals(Language.SL.toString())) {
				displayName = title_sl;
			}
			if (llabel.equals(Language.ZH.toString())) {
				displayName = title_zh;
			}
			if (llabel.equals(Language.CA.toString())) {
				displayName = title_ca;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return displayName;
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
