import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class Xml {

    private static final Logger LOGGER = LogManager.getLogger(Xml.class);
    private static final String PATH_TO_XML_FILE = "1.xml";
    private static final String PATH_TO_XML_FILE_AFTER_TRANSFORM = "2.xml";
    private static final String PATH_TO_XSL_FILE = "template.xsl";
    private List<String> list;

    public Xml(List<String> list) {
        this.list = list;
    }

    //Формируем файл XML из списка данных
    public void createXml() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element entries = doc.createElement("entries");
            doc.appendChild(entries);

            for (String s : list) {
                Element entry = doc.createElement("entry");
                entries.appendChild(entry);
                Element field = doc.createElement("field");
                field.appendChild(doc.createTextNode(s));
                entry.appendChild(field);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
            DOMSource source = new DOMSource(doc);
            File file = new File(PATH_TO_XML_FILE);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    //Преобразовываем наш XML файл при помощи XSLT
    public void xslTransform() {

        try {

            Source xslt = new StreamSource(getClass().getClassLoader().getResourceAsStream(PATH_TO_XSL_FILE));
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslt);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            Source xml = new StreamSource(new File(PATH_TO_XML_FILE));
            transformer.transform(xml, new StreamResult(new File(PATH_TO_XML_FILE_AFTER_TRANSFORM)));

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    //Парсим преобразованный XML файл и получаем сумму из значений
    public BigInteger getSumXml() {
        BigInteger sum = new BigInteger("0");
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(PATH_TO_XML_FILE_AFTER_TRANSFORM));
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("entry");

            for (int i = 0; i < nodeList.getLength(); i++) {
                NamedNodeMap field = nodeList.item(i).getAttributes();
                sum = sum.add(new BigInteger(field.getNamedItem("field").getNodeValue()));
            }

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        return sum;
    }


    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
