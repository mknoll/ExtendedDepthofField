package EDF_headless;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edfgui.Parameters;

public class ParseParameters {
	private Parameters parameters;
	boolean expertMode = false;
	
	public ParseParameters(String parameterFile) {
		//get xml parameters
		
		//expert mode
		String paramsInt[] = {"edfMethod","daubechielength", 
				"splineOrder", "varWindowSize", "medianWindowSize",	"colorConversionMethod",};
		String paramsDouble[] = {"sigma", "sigmaDenoising", "rateDenoising"};
		String paramsBool[] = {"reassignment", "subBandCC", "majCC",
				"doMorphoOpen", "doMorphoClose", "doGaussian", "doDenoising", "doMedian"};
		
		//easy mode
		String paramsIntEasy[] = {"quality", "topology"};
		
		parameters = new Parameters();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(parameterFile);
			Element rootElement = document.getDocumentElement();
		
			//get data / int
			for (String key : paramsInt) {
				String valS = getString(key, rootElement);
				if (valS != null) {
					parameters.setValue(key, Integer.parseInt(valS));
					expertMode = true;
					System.out.println("KEY -> VAL: " + key + " -> " + valS);
				}
			}
			for (String key : paramsDouble) {
				String valS = getString(key, rootElement);
				if (valS != null) {
					parameters.setValue(key, Double.parseDouble(valS));
					expertMode = true;
					System.out.println("KEY -> VAL: " + key + " -> " + valS);
				}
			}
			for (String key : paramsBool) {
				String valS = getString(key, rootElement);
				if (valS != null) {
					parameters.setValue(key, Boolean.parseBoolean(valS));
					expertMode = true;
					System.out.println("KEY -> VAL: " + key + " -> " + valS);
				}
			}
			
			// easy mode
			for (String key : paramsIntEasy) {
				String valS = getString(key, rootElement);
				if (valS != null) {
					if (key.equals("quality")) {
						parameters.setQualitySettings(Integer.parseInt(valS));
						System.out.println("KEY -> VAL: " + key + " -> " + valS);
					} else if (key.equals("topology")) {
						parameters.setTopologySettings(Integer.parseInt(valS));
						System.out.println("KEY -> VAL: " + key + " -> " + valS);
					}
					if (expertMode) {
						System.out.println("ACHTUNG: Expert mode Parameter mit Easy mode parametern überschrieben!");
					}
				}
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// -----------------
		// FIXME
		parameters.show3dView = false; 
		parameters.showTopology = false; 
	}
	
	public Parameters getParameters() {
		return(parameters);
	}
	
	protected static String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
	
	
	public static boolean validateXMLSchema(String xmlPath){	
		try {
			SchemaFactory factory = 
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//Schema schema = factory.newSchema(new File(xsdPath));
			
			//Source schemaFile = new StreamSource(getClass().getClassLoader()
			//	      .getResourceAsStream("EDF_Parameter_Schema.xsd"));
			Source schemaFile = new StreamSource(ParseParameters.class.getClassLoader()
				      .getResourceAsStream("EDF_Parameter_Schema.xsd"));
			Schema schema = factory.newSchema(schemaFile);
			
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));
		} catch (IOException e) {
			System.out.println("Exception: "+e.getMessage());
		} catch(SAXException e) {
			System.out.println("Exception: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	public String getParamString() {
		//expert mode
		String paramsExpert[] = {"edfMethod","daubechielength", 
				"splineOrder", "varWindowSize", "medianWindowSize",	
				"colorConversionMethod","sigma", "sigmaDenoising", 
				"rateDenoising","reassignment", "subBandCC", "majCC",
				"doMorphoOpen", "doMorphoClose", "doGaussian", 
				"doDenoising", "doMedian", "quality", "topology"};
		
		String ret = "";
		for (String key : paramsExpert) {
			ret +=  key + "=" + parameters.getValue(key) + "|";
		}

		System.err.println(ret);
		
		return(ret);
	}
}
