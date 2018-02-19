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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
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
  private static VideoGeneratorModel videoGenUpdated;
  
  private static List<VideoDescription> allVideos = new ArrayList<VideoDescription>();
  
  public static void main(final String[] args) {
    String videogen = VideoGen.generate("example1.videogen");
    VideoGen.writeInFile("playlist.txt", videogen);
    VideoGen.generateVideo();
    VideoGen.generateThumbnails("example1.videogen");
    int _longestVar = VideoGen.getLongestVar("example1.videogen");
    String _plus = ("getlongestvar " + Integer.valueOf(_longestVar));
    InputOutput.<String>println(_plus);
    VideoGen.writeStatsToCsv();
  }
  
  public static String generate(final String uri) {
    final VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri));
    final ArrayList<String> playlist = CollectionLiterals.<String>newArrayList();
    final Consumer<Media> _function = (Media media) -> {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          String _location = desc.getLocation();
          String _plus = ("file \'" + _location);
          String _plus_1 = (_plus + "\'");
          playlist.add(_plus_1);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          final Randomiser rd = new Randomiser();
          rd.setChoices(1);
          int _randomize = rd.randomize();
          boolean _equals = (_randomize == 1);
          if (_equals) {
            String _location_1 = desc_1.getLocation();
            String _plus_2 = ("file \'" + _location_1);
            String _plus_3 = (_plus_2 + "\'");
            playlist.add(_plus_3);
          }
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          final Randomiser rd_1 = new Randomiser();
          rd_1.setChoices(alts.getVideodescs().size());
          final int selected = rd_1.randomize();
          final VideoDescription videodesc = alts.getVideodescs().get(selected);
          String _location_2 = videodesc.getLocation();
          String _plus_4 = ("file \'" + _location_2);
          String _plus_5 = (_plus_4 + "\'");
          playlist.add(_plus_5);
        }
      }
    };
    videoGen.getMedias().forEach(_function);
    String playlistStr = "";
    for (final String pl : playlist) {
      String _playlistStr = playlistStr;
      playlistStr = (_playlistStr + (pl + "\n"));
    }
    VideoGen.videoGenUpdated = videoGen;
    return playlistStr;
  }
  
  public static void generateThumbnail(final String id, final String loc) {
    try {
      String cmd = ((((("ffmpeg -i " + loc) + " -ss 00:00:01.000 -vframes 1 ") + "ressources/gen/vignettes/") + id) + ".jpg -y");
      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static void generateThumbnails(final String uri) {
    final VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri));
    final Consumer<Media> _function = (Media media) -> {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          VideoDescription _description = ((MandatoryVideoSeq)video).getDescription();
          _description.setDuration(VideoGen.readDuration(desc.getLocation()));
          VideoGen.generateThumbnail(desc.getVideoid(), desc.getLocation());
          VideoGen.allVideos.add(desc);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          VideoDescription _description_1 = ((OptionalVideoSeq)video).getDescription();
          _description_1.setDuration(VideoGen.readDuration(desc_1.getLocation()));
          VideoGen.generateThumbnail(desc_1.getVideoid(), desc_1.getLocation());
          VideoGen.allVideos.add(desc_1);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription videodesc : _videodescs) {
            {
              final VideoDescription desc_2 = videodesc;
              videodesc.setDuration(VideoGen.readDuration(desc_2.getLocation()));
              VideoGen.generateThumbnail(desc_2.getVideoid(), desc_2.getLocation());
              VideoGen.allVideos.add(desc_2);
            }
          }
        }
      }
    };
    videoGen.getMedias().forEach(_function);
    VideoGen.videoGenUpdated = videoGen;
  }
  
  public static int getLongestVar(final String uri) {
    final VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri));
    int duration = 0;
    EList<Media> _medias = videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          int _duration = duration;
          int _readDuration = VideoGen.readDuration(desc.getLocation());
          duration = (_duration + _readDuration);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          int _duration_1 = duration;
          int _readDuration_1 = VideoGen.readDuration(desc_1.getLocation());
          duration = (_duration_1 + _readDuration_1);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          int longest = (-1);
          int index = 0;
          int longestDuration = 0;
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription videodesc : _videodescs) {
            {
              final int durationTemp = VideoGen.readDuration(videodesc.getLocation());
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
  
  public static void writeInFile(final String filename, final String data) {
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
  
  public static void generateVideo() {
    try {
      Process p = null;
      String ffmpegCmd = VideoGen.ffmpegConcatenateCommand("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/playlist.txt", "ressources/gen/ro.mp4").toString();
      InputOutput.<String>println(ffmpegCmd);
      p = Runtime.getRuntime().exec(ffmpegCmd);
      p.waitFor();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static CharSequence ffmpegConcatenateCommand(final String mpegPlaylistFile, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -y -f concat -safe 0 -i ");
    _builder.append(mpegPlaylistFile);
    _builder.append(" -c copy ");
    _builder.append(outputPath);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public static CharSequence ffmpegComputeDuration(final String locationVideo) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 ");
    _builder.append(locationVideo);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public static int readDuration(final String locationVideo) {
    try {
      Process p = null;
      String ffmpegCmd = VideoGen.ffmpegComputeDuration(locationVideo).toString();
      InputOutput.<String>println(ffmpegCmd);
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
  
  public static List<List<VideoDescription>> generateAllVars() {
    List<List<VideoDescription>> result = new ArrayList<List<VideoDescription>>();
    List<VideoDescription> lempty = new ArrayList<VideoDescription>();
    result.add(lempty);
    EList<Media> _medias = VideoGen.videoGenUpdated.getMedias();
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
    return result;
  }
  
  public static void writeStatsToCsv() {
    try {
      PrintWriter pw = null;
      File file = new File("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/ressources/stats/vars_size.csv");
      FileWriter fl = new FileWriter(file);
      fl.append("id");
      fl.append(",");
      for (final VideoDescription desc : VideoGen.allVideos) {
        {
          fl.append(desc.getVideoid());
          fl.append(",");
        }
      }
      fl.append("size");
      fl.append("\n");
      int index = 1;
      List<List<VideoDescription>> _generateAllVars = VideoGen.generateAllVars();
      for (final List<VideoDescription> v : _generateAllVars) {
        {
          int size = 0;
          String _plus = (Integer.valueOf(index) + "");
          fl.append(_plus);
          fl.append(",");
          for (final VideoDescription desc_1 : VideoGen.allVideos) {
            {
              boolean _hasVideo = VideoGen.hasVideo(v, desc_1);
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
          fl.append("\n");
          index++;
        }
      }
      fl.flush();
      fl.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static boolean hasVideo(final List<VideoDescription> l, final VideoDescription v) {
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
}
