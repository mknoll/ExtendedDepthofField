import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import EDF_headless.ParseParameters;

public class TestXMLParameterFile {
	@Test
	void validXMLEasy_1() {
		//assertEquals();
		String parameterFile = ParseParameters.class.getClassLoader().getResource("validEasy_1.xml").getFile();
		assertTrue(ParseParameters.validateXMLSchema(parameterFile));
	}
	
	@Test
	void validXMLEasy_2() {
		//assertEquals();
		String parameterFile = ParseParameters.class.getClassLoader().getResource("validEasy_1.xml").getFile();
		ParseParameters pp = new ParseParameters(parameterFile);
		assertEquals(pp.getParamString(), "edfMethod=1|daubechielength=6|splineOrder=3|varWindowSize=5|medianWindowSize=3|colorConversionMethod=0|sigma=2.0|sigmaDenoising=2.0|rateDenoising=10.0|reassignment=false|subBandCC=false|majCC=false|doMorphoOpen=false|doMorphoClose=false|doGaussian=false|doDenoising=false|doMedian=false|quality=1|topology=-1|");
	}
	
	
	@Test
	void invalidXMLEasy_1() {
		//assertEquals();
		String parameterFile = ParseParameters.class.getClassLoader().getResource("invalidEasy_1.xml").getFile();
		//TODO:  throw exception?
		assertFalse(ParseParameters.validateXMLSchema(parameterFile));
	}
}
