import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

@SuppressWarnings("all")
public class VideoGenTest {
  private VideoGen vg;
  
  private final static String PATH_TOOL = "";
  
  private final static String PATH_GEN_RELATIVE = "ressources/gen/";
  
  private final static String PATH_STATS_RELATIVE = "ressources/stats/";
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }
  
  @Before
  public void setUp() throws Exception {
    this.vg = null;
    InputOutput.<String>println("new test");
  }
  
  @After
  public void tearDown() throws Exception {
  }
  
  @Test
  public void testExample1() {
    try {
      final String file = "example1.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.generateAllVars().size(), this.vg.nbVariantes());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example1_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample2() {
    final String file = "example2.videogen";
    VideoGen _videoGen = new VideoGen(file);
    this.vg = _videoGen;
    this.vg.clean();
    this.vg.generate();
    InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
    Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
    this.vg.generateThumbnails();
    this.vg.writeStatsToCsv();
    this.vg.generateHtml();
    Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
  }
  
  @Test
  public void testExample3() {
    try {
      final String file = "example3.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example3_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample4() {
    try {
      final String file = "example4.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example4_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample5() {
    try {
      final String file = "example5.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example5_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample6() {
    try {
      final String file = "example6.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example6_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample7() {
    try {
      final String file = "example7.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example7_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample8() {
    try {
      final String file = "example8.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example8_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample9() {
    try {
      final String file = "example9.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.nbVariantes(), this.vg.generateAllVars().size());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example9_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample10() {
    try {
      final String file = "example10.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.generateAllVars().size(), this.vg.nbVariantes());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example10_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample11() {
    try {
      final String file = "example11.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.generateAllVars().size(), this.vg.nbVariantes());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example11_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample12() {
    try {
      final String file = "example12.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.generateAllVars().size(), this.vg.nbVariantes());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example12_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testExample13() {
    try {
      final String file = "example13.videogen";
      VideoGen _videoGen = new VideoGen(file);
      this.vg = _videoGen;
      this.vg.clean();
      this.vg.generate();
      InputOutput.<Integer>println(Integer.valueOf(this.vg.nbVariantes()));
      Assert.assertEquals(this.vg.generateAllVars().size(), this.vg.nbVariantes());
      this.vg.generateThumbnails();
      this.vg.writeStatsToCsv();
      this.vg.generateHtml();
      int _nbVariantes = this.vg.nbVariantes();
      int _plus = (_nbVariantes + 1);
      Assert.assertEquals(_plus, Utils.countLine(((VideoGenTest.PATH_TOOL + VideoGenTest.PATH_STATS_RELATIVE) + "example13_vars_size.csv")));
      Assert.assertEquals(this.vg.getNumberMedias(), this.vg.getNumberOfThumbnail());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
