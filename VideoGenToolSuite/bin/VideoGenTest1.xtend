

import org.junit.Test
import org.eclipse.emf.common.util.URI

import static org.junit.Assert.*
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import org.xtext.example.mydsl.videoGen.VideoSeq
import org.xtext.example.mydsl.videoGen.*
import utils.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Before
import org.junit.After

class VideoGenTest1 {

	var VideoGen vg;
	private final static String PATH_TOOL = "C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/"
	private final static String PATH_GEN_RELATIVE = "ressources/gen/"
	private final static String PATH_STATS_RELATIVE = "ressources/stats/"

	@BeforeClass
	def static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	def static void tearDownAfterClass() throws Exception {
	}

	@Before
	def void setUp() throws Exception {
		vg = null
		println("new test")
		
	}

	@After
	def void tearDown() throws Exception {
	}

	@Test
	def void testExample1() {
		val file = "example1.videogen"
		vg = new VideoGen(file)
				vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals( vg.generateAllVars().size, vg.nbVariantes)
		
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example1_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)
	}
	@Test
	def void testExample2() {
		val file = "example2.videogen"
		vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		// assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example2_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)

	}
	@Test
	def void testExample3() {
		val file = "example3.videogen"
		vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example3_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)
	}
	@Test
	def void testExample4() {
		val file = "example4.videogen"
				vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example4_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)
	}
	@Test
	def void testExample5() {
		val file = "example5.videogen"
				vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example5_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)	
	}

	@Test
	def void testExample6() {
		val file = "example6.videogen"
				vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example6_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)		
	}
	@Test
	def void testExample7() {
		val file = "example7.videogen"
				vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example7_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)	
	}
	@Test
	def void testExample8() {
		val file = "example8.videogen"
				vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example8_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)	
	}
	@Test
	def void testExample9() {
		val file = "example9.videogen"
		vg = new VideoGen(file)
		vg.clean()
		vg.generate()
		println(vg.nbVariantes())
		assertEquals(vg.nbVariantes(), vg.generateAllVars().size)
		vg.generateThumbnails()
		vg.writeStatsToCsv
		vg.generateHtml
		
		assertEquals(vg.nbVariantes() + 1, Utils.countLine(PATH_TOOL + PATH_STATS_RELATIVE + "example9_vars_size.csv"))
		assertEquals(vg.getNumberMedias, vg.numberOfThumbnail)
	}
	

}