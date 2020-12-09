package EDF_headless;

import edf.Tools;
import edfgui.ExtendedDepthOfField_headless;
import edfgui.Parameters;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;

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

public class EDF_runHeadless {
	//Image of interest
	private ImagePlus imp = null;
	//TODO use File 
	private String outFile = null;

	private Parameters parameters = null;
	private String parameterFile = null;

	private ExtendedDepthOfField_headless edf = null;

	/**
	 * Constructor
	 * 
	 * @param filePath Path of image stack file to load
	 * @param parameterPath Path of parameter file for EDF
	 */
	public EDF_runHeadless(String filePath, String parameterFile, String outFile) {
		//load file
		Opener opener = new Opener();
		imp = opener.openImage(filePath);

		//validate xml parameter file
		boolean valid = validateXMLSchema(parameterFile);
		this.parameterFile = parameterFile;
		System.out.println("Parameter xml valid: " + valid);

		//obtain parameters
		readParameters();

		this.outFile = outFile;
	}

	protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
	
	private void readParameters() {
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
		parameters.color = getColorMode();
		parameters.showTopology = true;

		int[] nScalesAndSize = Tools.computeScaleAndPowerTwoSize(imp.getWidth(), imp.getHeight()); 
		int nScales = nScalesAndSize[0];

		parameters.maxScales = nScales;
		parameters.show3dView = true; 
		parameters.outputColorMap = getColorMode() ? Parameters.COLOR_RGB : Parameters.GRAYSCALE;
		parameters.colorConversionMethod = 0;		

		// value range: 0-4; 
		parameters.setQualitySettings(qual);
		parameters.setTopologySettings(topology);
		//parameters.showTopology = true; 	
		parameters.showTopology = false; 

		if (imp.getType() == ImagePlus.COLOR_RGB) {
			parameters.outputColorMap = Parameters.COLOR_RGB;
		}
	}

	private boolean getColorMode() {
		//TODO: exception handling
		boolean color = false;
		if (imp.getType() == ImagePlus.COLOR_RGB)
			color = true;
		else if (imp.getType() == ImagePlus.GRAY8 || 
				imp.getType() == ImagePlus.GRAY16 || 
				imp.getType() == ImagePlus.GRAY32) {
			color = false;
		} else {
			IJ.error("Only process 8-bits, 16-bits, 32-bits and RGB images");
		}
		return color;
	}

	public void run() {
		edf = new ExtendedDepthOfField_headless(imp, parameters);
		edf.process(); 
		System.gc();
	}

	public void save() {
		ImagePlus out = edf.getComposite();
		//add metadata from xml
		out.setProperty("Info", "ABC"); //TODO
		
		FileSaver fs = new FileSaver(out);
		fs.saveAsTiff(outFile);
	}
	
	private boolean validateXMLSchema(String xmlPath){	
		try {
			SchemaFactory factory = 
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//Schema schema = factory.newSchema(new File(xsdPath));
			
			Source schemaFile = new StreamSource(getClass().getClassLoader()
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

	
	public static void main(String[] args) {	
		//params: infile, parameter file, outfile		
		EDF_runHeadless erh = new EDF_runHeadless(args[0], args[1], args[2]);
		erh.run();
		erh.save();
	}

}
