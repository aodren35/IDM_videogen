package utils;

import com.google.common.base.Objects;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import utils.StreamGobbler;

/**
 * Classe permettant les appels aux commandes FFMPEG
 */
@SuppressWarnings("all")
public class FFMPEGCall {
  private final static String PATH_TOOL = "";
  
  private final static String PATH_GEN_RELATIVE = "ressources/gen/";
  
  private final static String PATH_GEN_VIGNETTES_RELATIVE = "ressources/gen/vignettes/";
  
  private final static String PATH_GEN_GIF_RELATIVE = "ressources/gen/gif/";
  
  private final static String PATH_GEN_VIDEOS_RELATIVE = "ressources/gen/videos/";
  
  private final static String PATH_RESSOURCES = "ressources/";
  
  private String tag = "";
  
  public FFMPEGCall(final String tag) {
    this.tag = tag;
  }
  
  public int launchFfmpegCmd(final String ffmpegCmd) {
    try {
      InputOutput.<String>println(ffmpegCmd);
      Process p = null;
      FileOutputStream fos = new FileOutputStream("logger.txt");
      p = Runtime.getRuntime().exec(ffmpegCmd);
      InputStream _errorStream = p.getErrorStream();
      StreamGobbler errorGobbler = new StreamGobbler(_errorStream, "ERROR");
      InputStream _inputStream = p.getInputStream();
      StreamGobbler outputGobbler = new StreamGobbler(_inputStream, "OUTPUT", fos);
      errorGobbler.start();
      outputGobbler.start();
      int exitVal = p.waitFor();
      fos.flush();
      fos.close();
      return exitVal;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void copy(final String source, final String target) {
    String ffmpegCmd = this.ffmpegCopyCommand((FFMPEGCall.PATH_TOOL + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target)).toString();
    this.launchFfmpegCmd(ffmpegCmd);
  }
  
  public void generateVideo(final String source, final String target) {
    String ffmpegCmd = this.ffmpegConcatenateCommand((FFMPEGCall.PATH_TOOL + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target)).toString();
    this.launchFfmpegCmd(ffmpegCmd);
  }
  
  public int applyFilterFilpH(final String loc, final String newLoc) {
    int _xblockexpression = (int) 0;
    {
      String ffmpegCmd = this.ffmpegFlipH(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + loc), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + newLoc)).toString();
      _xblockexpression = this.launchFfmpegCmd(ffmpegCmd);
    }
    return _xblockexpression;
  }
  
  public int applyFilterFilpV(final String loc, final String newLoc) {
    int _xblockexpression = (int) 0;
    {
      String ffmpegCmd = this.ffmpegFlipV(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + loc), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + newLoc)).toString();
      _xblockexpression = this.launchFfmpegCmd(ffmpegCmd);
    }
    return _xblockexpression;
  }
  
  public int applyFilterNegate(final String loc, final String newLoc) {
    int _xblockexpression = (int) 0;
    {
      String ffmpegCmd = this.ffmpegNegate(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + loc), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + newLoc)).toString();
      _xblockexpression = this.launchFfmpegCmd(ffmpegCmd);
    }
    return _xblockexpression;
  }
  
  public int applyFilterBN(final String loc, final String newLoc) {
    int _xblockexpression = (int) 0;
    {
      String ffmpegCmd = this.ffmpegBN(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + loc), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + newLoc)).toString();
      _xblockexpression = this.launchFfmpegCmd(ffmpegCmd);
    }
    return _xblockexpression;
  }
  
  public void generateVideoFilteredWithText(final VideoDescription desc, final String source, final String target) {
    Process p = null;
    String text = desc.getText().getContent();
    int x = 50;
    int y = 0;
    String color = desc.getText().getColor();
    int size = desc.getText().getSize();
    String ffmpegCmd = null;
    if ((size == 0)) {
      size = 20;
    }
    String _position = desc.getText().getPosition();
    if (_position != null) {
      switch (_position) {
        case "TOP":
          ffmpegCmd = this.ffmpegDrawTextTOP(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target), text, color, size).toString();
          break;
        case "BOTTOM":
          ffmpegCmd = this.ffmpegDrawTextBOTTOM(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target), text, color, size).toString();
          break;
        case "CENTER":
          ffmpegCmd = this.ffmpegDrawTextCENTER(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target), text, color, size).toString();
          break;
        default:
          ffmpegCmd = this.ffmpegDrawTextCENTER(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target), text, color, size).toString();
          break;
      }
    } else {
      ffmpegCmd = this.ffmpegDrawTextCENTER(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + source), (FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE + target), text, color, size).toString();
    }
    this.launchFfmpegCmd(ffmpegCmd);
  }
  
  public void generateGif(final String source, final String target, final int t, final int w, final int l) {
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
    String ffmpegCmd = this.ffmpegVideoToGif(((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_VIDEOS_RELATIVE) + source), ((FFMPEGCall.PATH_TOOL + FFMPEGCall.PATH_GEN_GIF_RELATIVE) + target), time, width, length).toString();
    this.launchFfmpegCmd(ffmpegCmd);
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
      int _round = Math.round(this.ParseFloat(lines));
      return (_round - 1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public float ParseFloat(final String strNumber) {
    if (((!Objects.equal(strNumber, null)) && (strNumber.length() > 0))) {
      try {
        return Float.parseFloat(strNumber);
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          return (-1);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    } else {
      return 0;
    }
  }
  
  public void generateThumbnail(final int id, final String loc) {
    String ffmpegCmd = this.ffmpegThumbnail(id, loc).toString();
    this.launchFfmpegCmd(ffmpegCmd);
  }
  
  public CharSequence ffmpegThumbnail(final int id, final String loc) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(loc);
    _builder.append(" -ss 00:00:01.000 -vframes 1 ");
    _builder.append((((FFMPEGCall.PATH_GEN_VIGNETTES_RELATIVE + this.tag) + "_") + Integer.valueOf(id)));
    _builder.append(".jpg -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegFlipH(final String inputPath, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"hflip\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegFlipV(final String inputPath, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"vflip\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegNegate(final String inputPath, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"negate\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegBN(final String inputPath, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"hue=s=0\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegDrawTextTOP(final String inputPath, final String outputPath, final String text, final String color, final int size) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"drawtext=fontfile=\'C\\:\\\\Windows\\\\fonts\\\\Arial.ttf\':text=");
    _builder.append(text);
    _builder.append(":x=(w-text_w)/2:fontsize=");
    _builder.append(size);
    _builder.append(":fontcolor=");
    _builder.append(color);
    _builder.append("\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegDrawTextCENTER(final String inputPath, final String outputPath, final String text, final String color, final int size) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"drawtext=fontfile=\'C\\:\\\\Windows\\\\fonts\\\\Arial.ttf\':text=");
    _builder.append(text);
    _builder.append(":x=(w-text_w)/2:y=(h-text_h)/2:fontsize=");
    _builder.append(size);
    _builder.append(":fontcolor=");
    _builder.append(color);
    _builder.append("\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegDrawTextBOTTOM(final String inputPath, final String outputPath, final String text, final String color, final int size) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -i ");
    _builder.append(inputPath);
    _builder.append(" -vf \"drawtext=fontfile=\'C\\:\\\\Windows\\\\fonts\\\\Arial.ttf\':text=");
    _builder.append(text);
    _builder.append(":x=(w-text_w)/2:y=(h-text_h):fontsize=");
    _builder.append(size);
    _builder.append(":fontcolor=");
    _builder.append(color);
    _builder.append("\" -acodec copy ");
    _builder.append(outputPath);
    _builder.append(" -y");
    _builder.newLineIfNotEmpty();
    return _builder;
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
  
  public CharSequence ffmpegCopyCommand2(final String inputPath, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -y -i ");
    _builder.append(inputPath);
    _builder.append("  -c:v libx264 -preset ultrafast ");
    _builder.append(outputPath);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence ffmpegCopyCommand(final String inputPath, final String outputPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ffmpeg -y -i ");
    _builder.append(inputPath);
    _builder.append("  -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -pix_fmt yuv420p ");
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
  
  public String ffmpegVideoToGif(final String videoLocation, final String outputPath, final int time, final int w, final int l) {
    if ((time != 0)) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("ffmpeg -i ");
      _builder.append(videoLocation);
      _builder.append(" -y -ss 0 -pix_fmt rgb8 -r 10 -t ");
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
      _builder_1.append(" -y -ss 0 -pix_fmt rgb8 -r 10 -s ");
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
