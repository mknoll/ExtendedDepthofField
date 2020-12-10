import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import EDF_headless.ParseParameters;

public class TestXMLParameterFile {
	@Test
	void validXMLEasy_1() throws SAXException, IOException {
		String parameterFile = ParseParameters.class.getClassLoader().getResource("validEasy_1.xml").getFile();
		assertTrue(ParseParameters.validateXMLSchema(parameterFile));
	}
	
	@Test
	void validXMLEasy_2() {
		String parameterFile = ParseParameters.class.getClassLoader().getResource("validEasy_1.xml").getFile();
		ParseParameters pp = new ParseParameters(parameterFile);
		assertEquals(pp.getParamString(), 
				"edfMethod=1|daubechielength=6|splineOrder=3|varWindowSize=5|"
				+ "medianWindowSize=3|colorConversionMethod=0|sigma=2.0|sigmaDenoising=2.0|"
				+ "rateDenoising=10.0|reassignment=false|subBandCC=false|majCC=false|"
				+ "doMorphoOpen=false|doMorphoClose=false|doGaussian=false|"
				+ "doDenoising=false|doMedian=false|quality=1|topology=-1|");
	}
	
	
	@Test
	void invalidXMLEasy_1() {
		final String parameterFile = ParseParameters.class.getClassLoader().getResource("invalidEasy_1.xml").getFile();
		Assertions.assertThrows(SAXException.class, () -> { 
			ParseParameters.validateXMLSchema(parameterFile);
			});
	}

	@Test
	void invalidXML() {
		final String parameterFile =  "nonExistantFile";
		Assertions.assertThrows(IOException.class, () -> { 
			ParseParameters.validateXMLSchema(parameterFile);
			});
	}
	
	
	@Test
	void overwriteWithEasy_1() {
		String parameterFile = ParseParameters.class.getClassLoader().getResource("overwriteWithEasy_1.xml").getFile();
		ParseParameters pp = new ParseParameters(parameterFile);
		assertEquals(pp.getParamString(), 
				"edfMethod=3|daubechielength=14|splineOrder=3|varWindowSize=3|"
				+ "medianWindowSize=3|colorConversionMethod=0|sigma=2.0|sigmaDenoising=2.0|"
				+ "rateDenoising=10.0|reassignment=true|subBandCC=true|majCC=true|"
				+ "doMorphoOpen=false|doMorphoClose=false|doGaussian=false|"
				+ "doDenoising=false|doMedian=false|quality=4|topology=-1|");
	}
	
	@Test
	void overwriteWithEasy_2() {
		String parameterFile = ParseParameters.class.getClassLoader().getResource("overwriteWithEasy_2.xml").getFile();
		ParseParameters pp = new ParseParameters(parameterFile);
		assertEquals(pp.getParamString(), 
				"edfMethod=3|daubechielength=14|splineOrder=3|varWindowSize=3|"
				+ "medianWindowSize=3|colorConversionMethod=0|sigma=2.0|sigmaDenoising=2.0|"
				+ "rateDenoising=10.0|reassignment=true|subBandCC=true|majCC=true|"
				+ "doMorphoOpen=false|doMorphoClose=false|doGaussian=false|"
				+ "doDenoising=false|doMedian=false|quality=4|topology=-1|");
	}
}
