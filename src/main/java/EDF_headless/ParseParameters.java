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
	
	
	public ParseParameters(String parameterFile) {
		
		//get xml parameters
		int qual = 1; //FIXME
		int topology = 1; //FIXME
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(parameterFile);
			Element rootElement = document.getDocumentElement();
			
			//get data
			qual =  Integer.parseInt(getString("quality", rootElement));
			topology =  Integer.parseInt(getString("topology", rootElement));
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
		
		parameters = new Parameters();
		parameters.showTopology = true;


		parameters.show3dView = true; 
		parameters.colorConversionMethod = 0;		

		// value range: 0-4; 
		parameters.setQualitySettings(qual);
		parameters.setTopologySettings(topology);
		//parameters.showTopology = true; 	
		parameters.showTopology = false; 


		
		/*
		colorConversionMethod = 0;
		edfMethod = ExtendedDepthOfField.REAL_WAVELETS;
		outputColorMap = GRAYSCALE;
		
		sigma = 2.0;
		sigmaDenoising = 2.0;
		rateDenoising = 10.0;
		
		daubechielength = 6;
		splineOrder = 3;
		nScales = maxScales;
		varWindowSize = 3;
		medianWindowSize = 3;
		
		reassignment = false;
		subBandCC = false;
		majCC = false;
		doMorphoOpen = false;
		doMorphoClose = false;
		doGaussian = false;
		doDenoising = false;
		doMedian = false;
		showTopology = false;
		show3dView = false;
		log = false;		
		*/
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
}
