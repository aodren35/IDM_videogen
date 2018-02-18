import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Assert;
import org.junit.Test;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;
import utils.Randomiser;

@SuppressWarnings("all")
public class VideoGenTest1 {
  @Test
  public void testLoadModel() {
    try {
      final VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
      Assert.assertNotNull(videoGen);
      InputOutput.<String>println(videoGen.getInformation().getAuthorName());
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
      this.writeInFile("playlist.txt", playlistStr);
      Process p = null;
      String ffmpegCmd = this.ffmpegConcatenateCommand("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/playlist.txt", "ressources/gen/ro.mp4").toString();
      InputOutput.<String>println(ffmpegCmd);
      p = Runtime.getRuntime().exec(ffmpegCmd);
      p.waitFor();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
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
  
  public CharSequence ffmpegConcatenateCommand(final String mpegPlaylistFile, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -y -f concat -safe 0 -i ");
    _builder.append(mpegPlaylistFile);
    _builder.append(" -c copy ");
    _builder.append(outputPath);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}
