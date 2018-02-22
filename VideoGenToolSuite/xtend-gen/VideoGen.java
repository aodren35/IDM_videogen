import com.google.common.base.Objects;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;
import utils.Randomiser;

@SuppressWarnings("all")
public class VideoGen {
  private VideoGeneratorModel videoGen;
  
  private VideoGeneratorModel videoGenUpdated;
  
  private int numberOfThumbnail;
  
  private List<VideoDescription> allVideos = new ArrayList<VideoDescription>();
  
  private List<List<VideoDescription>> videoGenListed = new ArrayList<List<VideoDescription>>();
  
  private List<List<VideoDescription>> allVars = new ArrayList<List<VideoDescription>>();
  
  private String tag = "";
  
  private String uri = "";
  
  private final static String PATH_TOOL = "C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/";
  
  private final static String PATH_GEN_RELATIVE = "ressources/gen/";
  
  private final static String PATH_RESSOURCES = "ressources/";
  
  private final static String PATH_TEMP_PALETTE = "c:/temp/palette.png";
  
  public static void main(final String[] args) {
  }
  
  public VideoGen(final String uri) {
    this.videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri));
    this.tag = uri.split("\\.")[0];
    this.uri = uri;
    this.numberOfThumbnail = 0;
  }
  
  public int getNumberOfThumbnail() {
    return this.numberOfThumbnail;
  }
  
  public int getNumberMedias() {
    int result = 0;
    EList<Media> _medias = this.videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if (((video instanceof MandatoryVideoSeq) || (video instanceof OptionalVideoSeq))) {
          result++;
        } else {
          final EList<VideoDescription> alts = ((AlternativeVideoSeq) video).getVideodescs();
          int _result = result;
          int _size = alts.size();
          result = (_result + _size);
        }
      }
    }
    return result;
  }
  
  public List<VideoDescription> getAllVideos() {
    return this.allVideos;
  }
  
  public String generate() {
    final ArrayList<String> playlist = CollectionLiterals.<String>newArrayList();
    final Consumer<Media> _function = (Media media) -> {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          int _videoDuration = this.videoDuration(desc.getLocation());
          int _plus = (_videoDuration + 0);
          final Integer dur = ((Integer) Integer.valueOf(_plus));
          desc.setDuration((dur).intValue());
          String _location = desc.getLocation();
          String _plus_1 = ("file \'" + _location);
          String _plus_2 = (_plus_1 + "\'");
          String _plus_3 = (_plus_2 + " duration ");
          String _plus_4 = (_plus_3 + dur);
          playlist.add(_plus_4);
          List<VideoDescription> lempty = new ArrayList<VideoDescription>();
          lempty.add(desc);
          this.videoGenListed.add(lempty);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          final Randomiser rd = new Randomiser();
          rd.setChoices(0);
          int _randomize = rd.randomize();
          boolean _equals = (_randomize == 1);
          if (_equals) {
            int _videoDuration_1 = this.videoDuration(desc_1.getLocation());
            int _plus_5 = (_videoDuration_1 + 0);
            final Integer dur_1 = ((Integer) Integer.valueOf(_plus_5));
            desc_1.setDuration((dur_1).intValue());
            String _location_1 = desc_1.getLocation();
            String _plus_6 = ("file \'" + _location_1);
            String _plus_7 = (_plus_6 + "\'");
            String _plus_8 = (_plus_7 + " duration ");
            String _plus_9 = (_plus_8 + dur_1);
            playlist.add(_plus_9);
          }
          List<VideoDescription> lempty_1 = new ArrayList<VideoDescription>();
          lempty_1.add(desc_1);
          this.videoGenListed.add(lempty_1);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          List<VideoDescription> lempty_2 = new ArrayList<VideoDescription>();
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          final Randomiser rd_1 = new Randomiser();
          rd_1.setChoices(alts.getVideodescs().size());
          final int selected = rd_1.randomize();
          int _size = alts.getVideodescs().size();
          String _plus_10 = ("CHOICES : " + Integer.valueOf(_size));
          String _plus_11 = (_plus_10 + " SELECTED : ");
          String _plus_12 = (_plus_11 + Integer.valueOf(selected));
          InputOutput.<String>println(_plus_12);
          final VideoDescription videodesc = alts.getVideodescs().get(selected);
          int _videoDuration_2 = this.videoDuration(videodesc.getLocation());
          int _plus_13 = (_videoDuration_2 + 0);
          final Integer dur_2 = ((Integer) Integer.valueOf(_plus_13));
          videodesc.setDuration((dur_2).intValue());
          String _location_2 = videodesc.getLocation();
          String _plus_14 = ("file \'" + _location_2);
          String _plus_15 = (_plus_14 + "\'");
          String _plus_16 = (_plus_15 + " duration ");
          String _plus_17 = (_plus_16 + dur_2);
          playlist.add(_plus_17);
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription va : _videodescs) {
            lempty_2.add(va);
          }
          this.videoGenListed.add(lempty_2);
        }
      }
    };
    this.videoGen.getMedias().forEach(_function);
    String playlistStr = "";
    for (final String pl : playlist) {
      String _playlistStr = playlistStr;
      playlistStr = (_playlistStr + (pl + "\n"));
    }
    this.videoGenUpdated = this.videoGen;
    return playlistStr;
  }
  
  public String generateFromVideoDescriptions(final List<VideoDescription> l) {
    final ArrayList<String> playlist = CollectionLiterals.<String>newArrayList();
    for (final VideoDescription v : l) {
      String _location = v.getLocation();
      String _plus = ("file \'" + _location);
      String _plus_1 = (_plus + "\'");
      String _plus_2 = (_plus_1 + " duration ");
      int _duration = v.getDuration();
      String _plus_3 = (_plus_2 + Integer.valueOf(_duration));
      String _plus_4 = (_plus_3 + " inpoint ");
      String _plus_5 = (_plus_4 + "0");
      playlist.add(_plus_5);
    }
    String playlistStr = "";
    for (final String pl : playlist) {
      String _playlistStr = playlistStr;
      playlistStr = (_playlistStr + (pl + "\n"));
    }
    return playlistStr;
  }
  
  public void generateThumbnail(final int id, final String loc) {
    try {
      String cmd = (((((((("ffmpeg -i " + loc) + " -ss 00:00:01.000 -vframes 1 ") + VideoGen.PATH_GEN_RELATIVE) + "vignettes/") + this.tag) + "_") + Integer.valueOf(id)) + ".jpg -y");
      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();
      this.numberOfThumbnail++;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void generateThumbnails() {
    int index = 0;
    EList<Media> _medias = this.videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          VideoDescription _description = ((MandatoryVideoSeq)video).getDescription();
          int _videoDuration = this.videoDuration(desc.getLocation());
          int _plus = (_videoDuration + 0);
          _description.setDuration(_plus);
          this.generateThumbnail(index, desc.getLocation());
          index++;
          this.allVideos.add(desc);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          VideoDescription _description_1 = ((OptionalVideoSeq)video).getDescription();
          int _videoDuration_1 = this.videoDuration(desc_1.getLocation());
          int _plus_1 = (_videoDuration_1 + 0);
          _description_1.setDuration(_plus_1);
          this.generateThumbnail(index, desc_1.getLocation());
          index++;
          this.allVideos.add(desc_1);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription videodesc : _videodescs) {
            {
              final VideoDescription desc_2 = videodesc;
              int _videoDuration_2 = this.videoDuration(desc_2.getLocation());
              int _plus_2 = (_videoDuration_2 + 0);
              videodesc.setDuration(_plus_2);
              this.generateThumbnail(index, desc_2.getLocation());
              index++;
              this.allVideos.add(desc_2);
            }
          }
        }
      }
    }
    this.videoGenUpdated = this.videoGen;
  }
  
  public int getLongestVar() {
    int duration = 0;
    EList<Media> _medias = this.videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          int _duration = duration;
          int _videoDuration = this.videoDuration(desc.getLocation());
          duration = (_duration + _videoDuration);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          int _duration_1 = duration;
          int _videoDuration_1 = this.videoDuration(desc_1.getLocation());
          duration = (_duration_1 + _videoDuration_1);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          int longest = (-1);
          int index = 0;
          int longestDuration = 0;
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription videodesc : _videodescs) {
            {
              final int durationTemp = this.videoDuration(videodesc.getLocation());
              if ((durationTemp > longestDuration)) {
                longestDuration = durationTemp;
              }
              index++;
            }
          }
          int _duration_2 = duration;
          duration = (_duration_2 + longestDuration);
        }
      }
    }
    return duration;
  }
  
  public void writeInFile(final String filename, final String data) {
    try {
      FileOutputStream _fileOutputStream = new FileOutputStream(filename);
      OutputStreamWriter _outputStreamWriter = new OutputStreamWriter(_fileOutputStream, "utf-8");
      final BufferedWriter buffer = new BufferedWriter(_outputStreamWriter);
      try {
        buffer.write(data);
      } catch (final Throwable _t) {
        if (_t instanceof IOException) {
          final IOException e = (IOException)_t;
          throw e;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      } finally {
        buffer.flush();
        buffer.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void generateVideo(final String source, final String target) {
    try {
      Process p = null;
      String ffmpegCmd = this.ffmpegConcatenateCommand((VideoGen.PATH_TOOL + source), (VideoGen.PATH_GEN_RELATIVE + target)).toString();
      InputOutput.<String>println(ffmpegCmd);
      p = Runtime.getRuntime().exec(ffmpegCmd);
      boolean _waitFor = p.waitFor(20, TimeUnit.SECONDS);
      boolean _not = (!_waitFor);
      if (_not) {
        p.destroyForcibly();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void generateGif(final String source, final String target, final int t, final int w, final int l) {
    try {
      Process p = null;
      int time = 0;
      int width = 320;
      int length = 240;
      if ((t != (-1))) {
        time = t;
      }
      if ((w != (-1))) {
        width = w;
      }
      if ((l != (-1))) {
        length = l;
      }
      String ffmpegCmd = this.ffmpegVideoToGif(((VideoGen.PATH_TOOL + VideoGen.PATH_GEN_RELATIVE) + source), ((VideoGen.PATH_TOOL + VideoGen.PATH_GEN_RELATIVE) + target), time, width, length).toString();
      InputOutput.<String>println(ffmpegCmd);
      p = Runtime.getRuntime().exec(ffmpegCmd);
      boolean _waitFor = p.waitFor(20, TimeUnit.SECONDS);
      boolean _not = (!_waitFor);
      if (_not) {
        p.destroyForcibly();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public List<List<VideoDescription>> generateAllVars() {
    List<List<VideoDescription>> result = new ArrayList<List<VideoDescription>>();
    List<VideoDescription> lempty = new ArrayList<VideoDescription>();
    result.add(lempty);
    EList<Media> _medias = this.videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          for (final List<VideoDescription> l : result) {
            l.add(desc);
          }
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          ArrayList<List<VideoDescription>> partial = new ArrayList<List<VideoDescription>>();
          for (final List<VideoDescription> l_1 : result) {
            {
              ArrayList<VideoDescription> lTemp = new ArrayList<VideoDescription>();
              lTemp.addAll(l_1);
              partial.add(lTemp);
              l_1.add(desc_1);
            }
          }
          result.addAll(partial);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          ArrayList<List<VideoDescription>> partial_1 = new ArrayList<List<VideoDescription>>();
          for (Iterator<List<VideoDescription>> it = result.iterator(); it.hasNext();) {
            {
              List<VideoDescription> l_2 = it.next();
              it.remove();
              EList<VideoDescription> _videodescs = alts.getVideodescs();
              for (final VideoDescription videodesc : _videodescs) {
                {
                  final VideoDescription desc_2 = videodesc;
                  ArrayList<VideoDescription> lTemp = new ArrayList<VideoDescription>();
                  lTemp.addAll(l_2);
                  lTemp.add(desc_2);
                  partial_1.add(lTemp);
                }
              }
            }
          }
          result.addAll(partial_1);
        }
      }
    }
    this.allVars = result;
    return result;
  }
  
  public void writeStatsToCsv() {
    try {
      boolean _isEmpty = this.allVideos.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        PrintWriter pw = null;
        File file = new File(((("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/ressources/stats/" + this.tag) + "_") + "vars_size.csv"));
        FileWriter fl = new FileWriter(file);
        fl.append("id");
        fl.append(",");
        for (final VideoDescription desc : this.allVideos) {
          {
            fl.append(desc.getVideoid());
            fl.append(",");
          }
        }
        fl.append("size");
        fl.append(",");
        fl.append("realSize");
        fl.append(",");
        fl.append("realSizeGif");
        fl.append("\n");
        int index = 1;
        final String source = ((this.tag + "_") + "playlistTemp.txt");
        String target = ((this.tag + "_") + "gen");
        String format = ".MTS";
        String gif = ".gif";
        for (final List<VideoDescription> v : this.allVars) {
          {
            int size = 0;
            String _plus = (Integer.valueOf(index) + "");
            fl.append(_plus);
            fl.append(",");
            final String playlistTemp = this.generateFromVideoDescriptions(v);
            this.writeInFile(source, playlistTemp);
            this.generateVideo(source, ((target + Integer.valueOf(index)) + format));
            this.generateGif(((target + Integer.valueOf(index)) + format), ((target + Integer.valueOf(index)) + gif), (-1), (-1), (-1));
            for (final VideoDescription desc_1 : this.allVideos) {
              {
                boolean _hasVideo = this.hasVideo(v, desc_1);
                if (_hasVideo) {
                  fl.append("TRUE");
                  Path path = Paths.get(desc_1.getLocation());
                  byte[] data = Files.readAllBytes(path);
                  int _size = size;
                  int _length = data.length;
                  size = (_size + _length);
                } else {
                  fl.append("FALSE");
                }
                fl.append(",");
              }
            }
            String _plus_1 = (Integer.valueOf(size) + "");
            fl.append(_plus_1);
            fl.append(",");
            Path pathVar = Paths.get((((VideoGen.PATH_GEN_RELATIVE + target) + Integer.valueOf(index)) + format));
            int realSize = 0;
            boolean _exists = pathVar.toFile().exists();
            if (_exists) {
              byte[] dataVar = Files.readAllBytes(pathVar);
              realSize = dataVar.length;
            }
            String _plus_2 = (Integer.valueOf(realSize) + "");
            fl.append(_plus_2);
            fl.append(",");
            Path pathVarGif = Paths.get((((VideoGen.PATH_GEN_RELATIVE + target) + Integer.valueOf(index)) + gif));
            int realSizeGif = 0;
            boolean _exists_1 = pathVar.toFile().exists();
            if (_exists_1) {
              byte[] dataVarGif = Files.readAllBytes(pathVarGif);
              realSizeGif = dataVarGif.length;
            }
            String _plus_3 = (Integer.valueOf(realSizeGif) + "");
            fl.append(_plus_3);
            fl.append("\n");
            index++;
          }
        }
        fl.flush();
        fl.close();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void generateHtml() {
    try {
      File htmlTemplateFile = new File((VideoGen.PATH_RESSOURCES + "template.html"));
      String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.forName("UTF-8"));
      String body = "";
      int index = 0;
      for (final List<VideoDescription> lv : this.videoGenListed) {
        {
          String _body = body;
          CharSequence _openDiv = this.openDiv();
          body = (_body + _openDiv);
          int _size = lv.size();
          boolean _greaterThan = (_size > 1);
          if (_greaterThan) {
            for (final VideoDescription v : lv) {
              {
                String _body_1 = body;
                CharSequence _videoGenToHtml = this.videoGenToHtml(v, index, lv.size());
                body = (_body_1 + _videoGenToHtml);
                index++;
              }
            }
          } else {
            String _body_1 = body;
            CharSequence _videoGenToHtml = this.videoGenToHtml(lv.get(0), index, 1);
            body = (_body_1 + _videoGenToHtml);
            index++;
          }
          String _body_2 = body;
          CharSequence _closeDiv = this.closeDiv();
          body = (_body_2 + _closeDiv);
        }
      }
      htmlString = htmlString.replace("$body", body);
      File newHtmlFile = new File(((VideoGen.PATH_GEN_RELATIVE + this.tag) + "_new.html"));
      FileUtils.writeStringToFile(newHtmlFile, htmlString, Charset.forName("UTF-8"));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public CharSequence openDiv() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<div class=\"row\">");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence closeDiv() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("</div>");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence videoGenToHtml(final VideoDescription v, final int id, final int col) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<div class=\"col-md-");
    _builder.append((12 / col));
    _builder.append("\">");
    _builder.newLineIfNotEmpty();
    _builder.append("<h1>Video : ");
    String _description = v.getDescription();
    _builder.append(_description);
    _builder.append("</h1>");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t\t");
    _builder.append("<p>");
    _builder.newLine();
    _builder.append("\t\t\t\t\t");
    _builder.append("<img src=\"");
    _builder.append(((((((VideoGen.PATH_TOOL + VideoGen.PATH_GEN_RELATIVE) + "vignettes/") + this.tag) + "_") + Integer.valueOf(id)) + ".jpg"), "\t\t\t\t\t");
    _builder.append("\" alt=\"\">");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t\t");
    _builder.append("</p>");
    _builder.newLine();
    _builder.append("</div>");
    _builder.newLine();
    return _builder;
  }
  
  public int nbVariantes() {
    int result = 1;
    EList<Media> _medias = this.videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
        }
        if ((video instanceof OptionalVideoSeq)) {
          result = (result * 2);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          int _size = alts.getVideodescs().size();
          int _multiply = (result * _size);
          result = _multiply;
        }
      }
    }
    return result;
  }
  
  public int videoDuration(final String locationVideo) {
    try {
      Process p = null;
      String ffmpegCmd = this.ffmpegComputeDuration(locationVideo).toString();
      p = Runtime.getRuntime().exec(ffmpegCmd);
      p.waitFor();
      InputStream _inputStream = p.getInputStream();
      InputStreamReader _inputStreamReader = new InputStreamReader(_inputStream);
      BufferedReader reader = new BufferedReader(_inputStreamReader);
      String line = "";
      String lines = "";
      while ((!Objects.equal((line = reader.readLine()), null))) {
        lines = (lines + line);
      }
      int _round = Math.round(Float.parseFloat(lines));
      return (_round - 1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public boolean hasVideo(final List<VideoDescription> l, final VideoDescription v) {
    for (final VideoDescription vTemp : l) {
      String _videoid = vTemp.getVideoid();
      String _videoid_1 = v.getVideoid();
      boolean _equals = Objects.equal(_videoid, _videoid_1);
      if (_equals) {
        return true;
      }
    }
    return false;
  }
  
  public CharSequence ffmpegConcatenateCommand(final String mpegPlaylistFile, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -y -f concat -safe 0 -i ");
    _builder.append(mpegPlaylistFile);
    _builder.append(" -c copy -r 24 ");
    _builder.append(outputPath);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegComputeDuration(final String locationVideo) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ");
    _builder.append(locationVideo);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegVideoToPalette(final String videoLocation) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -v warning -ss  0 -i ");
    _builder.append(videoLocation);
    _builder.append(" -vf \"fps=15,scale=-1:-1:flags=lanczos,palettegen\" -y ");
    _builder.append(VideoGen.PATH_TEMP_PALETTE);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegPaletteToGif(final String videoLocation, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -v warning -ss 0 -i ");
    _builder.append(videoLocation);
    _builder.append(" -i ");
    _builder.append(VideoGen.PATH_TEMP_PALETTE);
    _builder.append(" -lavfi \"fps=15,scale=-1:-1:flags=lanczos, [x]; [x][1:v] paletteuse\" -y  ");
    _builder.append(outputPath);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public String ffmpegVideoToGif(final String videoLocation, final String outputPath, final int time, final int w, final int l) {
    if ((time != 0)) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("ffmpeg -i ");
      _builder.append(videoLocation);
      _builder.append(" -y -ss 0 -pix_fmt rgb24 -r 10 -t ");
      _builder.append(time);
      _builder.append(" -s ");
      _builder.append(w);
      _builder.append("x");
      _builder.append(l);
      _builder.append("  ");
      _builder.append(outputPath);
      _builder.newLineIfNotEmpty();
      return _builder.toString();
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("ffmpeg -i ");
      _builder_1.append(videoLocation);
      _builder_1.append(" -y -ss 0 -pix_fmt rgb24 -r 10 -s ");
      _builder_1.append(w);
      _builder_1.append("x");
      _builder_1.append(l);
      _builder_1.append("  ");
      _builder_1.append(outputPath);
      _builder_1.newLineIfNotEmpty();
      return _builder_1.toString();
    }
  }
}