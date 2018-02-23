import com.google.common.base.Objects;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.BlackWhiteFilter;
import org.xtext.example.mydsl.videoGen.Filter;
import org.xtext.example.mydsl.videoGen.FlipFilter;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.NegateFilter;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.Text;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;
import utils.FFMPEGCall;
import utils.Randomiser;

@SuppressWarnings("all")
public class VideoGen {
  private VideoGeneratorModel videoGen;
  
  private VideoGeneratorModel videoGenUpdated;
  
  private int numberOfThumbnail;
  
  private List<VideoDescription> allVideos = new ArrayList<VideoDescription>();
  
  private List<List<VideoDescription>> videoGenListed = new ArrayList<List<VideoDescription>>();
  
  private List<List<VideoDescription>> allVars = new ArrayList<List<VideoDescription>>();
  
  private List<String> listId = new ArrayList<String>();
  
  private String tag = "";
  
  private String uri = "";
  
  private FFMPEGCall ffmpeg;
  
  private final static String PATH_TOOL = "";
  
  private final static String PATH_GEN_RELATIVE = "ressources/gen/";
  
  private final static String PATH_RESSOURCES = "ressources/";
  
  private final static String PATH_TEMP_PALETTE = "c:/temp/palette.png";
  
  public VideoGen(final String uri) {
    this.videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri));
    this.tag = uri.split("\\.")[0];
    FFMPEGCall _fFMPEGCall = new FFMPEGCall(this.tag);
    this.ffmpeg = _fFMPEGCall;
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
  
  public void clean() {
    int index = 0;
    this.videoGenUpdated = this.videoGen;
    EList<Media> _medias = this.videoGenUpdated.getMedias();
    for (final Media media : _medias) {
      {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          this.cleanMandatory(((MandatoryVideoSeq)video).getDescription(), index);
          index++;
        }
        if ((video instanceof OptionalVideoSeq)) {
          this.cleanOptional(((OptionalVideoSeq)video).getDescription(), index);
          index++;
        }
        if ((video instanceof AlternativeVideoSeq)) {
          int total = 0;
          int _size = ((AlternativeVideoSeq)video).getVideodescs().size();
          boolean _equals = (_size == 0);
          if (_equals) {
            this.videoGenUpdated.getMedias().remove(video);
          }
          this.cleanId(((AlternativeVideoSeq)video), index);
          index++;
          EList<VideoDescription> _videodescs = ((AlternativeVideoSeq)video).getVideodescs();
          for (final VideoDescription v : _videodescs) {
            {
              int _tal = total;
              int _cleanAlternative = this.cleanAlternative(v, index, total, ((AlternativeVideoSeq)video).getVideodescs().size());
              total = (_tal + _cleanAlternative);
              index++;
            }
          }
        }
      }
    }
  }
  
  public void cleanAndGenerateFilter(final VideoDescription desc) {
  }
  
  public void cleanId(final AlternativeVideoSeq desc, final int index) {
    String _videoid = desc.getVideoid();
    boolean _tripleEquals = (_videoid == "");
    if (_tripleEquals) {
      desc.setVideoid((((("alternative" + "_") + this.tag) + "_") + Integer.valueOf(index)));
    }
    while (this.idExists(desc.getVideoid())) {
      String _videoid_1 = desc.getVideoid();
      String _plus = (_videoid_1 + "_");
      String _plus_1 = (_plus + Integer.valueOf(index));
      desc.setVideoid(_plus_1);
    }
  }
  
  public String cleanMandatory(final VideoDescription desc, final int index) {
    String _videoid = desc.getVideoid();
    boolean _tripleEquals = (_videoid == "");
    if (_tripleEquals) {
      desc.setVideoid((((("video_mandatory" + "_") + this.tag) + "_") + Integer.valueOf(index)));
    }
    while (this.idExists(desc.getVideoid())) {
      String _videoid_1 = desc.getVideoid();
      String _plus = (_videoid_1 + "_");
      String _plus_1 = (_plus + Integer.valueOf(index));
      desc.setVideoid(_plus_1);
    }
    int _duration = desc.getDuration();
    boolean _tripleEquals_1 = (((Integer) Integer.valueOf(_duration)) == null);
    if (_tripleEquals_1) {
      int _videoDuration = this.ffmpeg.videoDuration(desc.getLocation());
      int _plus = (_videoDuration + 0);
      final Integer dur = ((Integer) Integer.valueOf(_plus));
      desc.setDuration((dur).intValue());
    } else {
      int _duration_1 = desc.getDuration();
      boolean _lessThan = (_duration_1 < 0);
      if (_lessThan) {
        int _videoDuration_1 = this.ffmpeg.videoDuration(desc.getLocation());
        int _plus_1 = (_videoDuration_1 + 0);
        final Integer dur_1 = ((Integer) Integer.valueOf(_plus_1));
        desc.setDuration((dur_1).intValue());
      }
    }
    desc.setProbability(100);
    String _description = desc.getDescription();
    boolean _tripleEquals_2 = (_description == "");
    if (_tripleEquals_2) {
      desc.setDescription(((("video_mandatory_" + Integer.valueOf(index)) + "_") + this.tag));
    }
    return "";
  }
  
  public String cleanOptional(final VideoDescription desc, final int index) {
    String _videoid = desc.getVideoid();
    boolean _tripleEquals = (_videoid == "");
    if (_tripleEquals) {
      desc.setVideoid((((("video_optional" + "_") + this.tag) + "_") + Integer.valueOf(index)));
    }
    while (this.idExists(desc.getVideoid())) {
      String _videoid_1 = desc.getVideoid();
      String _plus = (_videoid_1 + "_");
      String _plus_1 = (_plus + Integer.valueOf(index));
      desc.setVideoid(_plus_1);
    }
    int _duration = desc.getDuration();
    boolean _tripleEquals_1 = (((Integer) Integer.valueOf(_duration)) == null);
    if (_tripleEquals_1) {
      int _videoDuration = this.ffmpeg.videoDuration(desc.getLocation());
      int _plus = (_videoDuration + 0);
      final Integer dur = ((Integer) Integer.valueOf(_plus));
      desc.setDuration((dur).intValue());
    } else {
      int _duration_1 = desc.getDuration();
      boolean _lessThan = (_duration_1 < 0);
      if (_lessThan) {
        int _videoDuration_1 = this.ffmpeg.videoDuration(desc.getLocation());
        int _plus_1 = (_videoDuration_1 + 0);
        final Integer dur_1 = ((Integer) Integer.valueOf(_plus_1));
        desc.setDuration((dur_1).intValue());
      }
    }
    if (((desc.getProbability() >= 100) || (desc.getProbability() <= 0))) {
      desc.setProbability(50);
    }
    String _description = desc.getDescription();
    boolean _tripleEquals_2 = (_description == "");
    if (_tripleEquals_2) {
      desc.setDescription(((("video_optional_" + Integer.valueOf(index)) + "_") + this.tag));
    }
    return "";
  }
  
  public int cleanAlternative(final VideoDescription desc, final int index, final int tot, final int size) {
    String _videoid = desc.getVideoid();
    boolean _tripleEquals = (_videoid == "");
    if (_tripleEquals) {
      desc.setVideoid((((("video_alternative" + "_") + this.tag) + "_") + Integer.valueOf(index)));
    }
    while (this.idExists(desc.getVideoid())) {
      String _videoid_1 = desc.getVideoid();
      String _plus = (_videoid_1 + "_");
      String _plus_1 = (_plus + Integer.valueOf(index));
      desc.setVideoid(_plus_1);
    }
    int prob = 0;
    int _duration = desc.getDuration();
    boolean _tripleEquals_1 = (((Integer) Integer.valueOf(_duration)) == null);
    if (_tripleEquals_1) {
      int _videoDuration = this.ffmpeg.videoDuration(desc.getLocation());
      int _plus = (_videoDuration + 0);
      final Integer dur = ((Integer) Integer.valueOf(_plus));
      desc.setDuration((dur).intValue());
    } else {
      int _duration_1 = desc.getDuration();
      boolean _lessThan = (_duration_1 < 0);
      if (_lessThan) {
        int _videoDuration_1 = this.ffmpeg.videoDuration(desc.getLocation());
        int _plus_1 = (_videoDuration_1 + 0);
        final Integer dur_1 = ((Integer) Integer.valueOf(_plus_1));
        desc.setDuration((dur_1).intValue());
      }
    }
    if (((desc.getProbability() >= 100) || (desc.getProbability() <= 0))) {
      desc.setProbability(((1 / size) * 100));
    }
    int _probability = desc.getProbability();
    int _plus_2 = (_probability + tot);
    boolean _greaterThan = (_plus_2 > 100);
    if (_greaterThan) {
      desc.setProbability((100 - tot));
      int _probability_1 = desc.getProbability();
      boolean _lessThan_1 = (_probability_1 < 0);
      if (_lessThan_1) {
        desc.setProbability(0);
      }
    }
    String _description = desc.getDescription();
    boolean _tripleEquals_2 = (_description == "");
    if (_tripleEquals_2) {
      desc.setDescription(((("video_alternative_" + Integer.valueOf(index)) + "_") + this.tag));
    }
    return desc.getProbability();
  }
  
  public String generate() {
    final ArrayList<String> playlist = CollectionLiterals.<String>newArrayList();
    int index = 0;
    EList<Media> _medias = this.videoGen.getMedias();
    for (final Media media : _medias) {
      if ((media instanceof VideoSeq)) {
        final VideoSeq video = ((VideoSeq) media);
        if ((video instanceof MandatoryVideoSeq)) {
          final VideoDescription desc = ((MandatoryVideoSeq)video).getDescription();
          int _videoDuration = this.ffmpeg.videoDuration(desc.getLocation());
          int _plus = (_videoDuration + 0);
          final Integer dur = ((Integer) Integer.valueOf(_plus));
          String newLoc = desc.getLocation();
          desc.setDuration((dur).intValue());
          playlist.add((((("file \'" + newLoc) + "\'") + " duration ") + dur));
          List<VideoDescription> lempty = new ArrayList<VideoDescription>();
          lempty.add(desc);
          this.videoGenListed.add(lempty);
          index++;
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          final Randomiser rd = new Randomiser();
          rd.setChoices(0);
          int _randomize = rd.randomize();
          boolean _equals = (_randomize == 1);
          if (_equals) {
            int _videoDuration_1 = this.ffmpeg.videoDuration(desc_1.getLocation());
            int _plus_1 = (_videoDuration_1 + 0);
            final Integer dur_1 = ((Integer) Integer.valueOf(_plus_1));
            desc_1.setDuration((dur_1).intValue());
            String _location = desc_1.getLocation();
            String _plus_2 = ("file \'" + _location);
            String _plus_3 = (_plus_2 + "\'");
            String _plus_4 = (_plus_3 + " duration ");
            String _plus_5 = (_plus_4 + dur_1);
            playlist.add(_plus_5);
          }
          List<VideoDescription> lempty_1 = new ArrayList<VideoDescription>();
          lempty_1.add(desc_1);
          this.videoGenListed.add(lempty_1);
          index++;
        }
        if ((video instanceof AlternativeVideoSeq)) {
          List<VideoDescription> lempty_2 = new ArrayList<VideoDescription>();
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          final Randomiser rd_1 = new Randomiser();
          rd_1.setChoices(alts.getVideodescs().size());
          final int selected = rd_1.randomize();
          int _size = alts.getVideodescs().size();
          String _plus_6 = ("CHOICES : " + Integer.valueOf(_size));
          String _plus_7 = (_plus_6 + " SELECTED : ");
          String _plus_8 = (_plus_7 + Integer.valueOf(selected));
          InputOutput.<String>println(_plus_8);
          final VideoDescription videodesc = alts.getVideodescs().get(selected);
          int _videoDuration_2 = this.ffmpeg.videoDuration(videodesc.getLocation());
          int _plus_9 = (_videoDuration_2 + 0);
          final Integer dur_2 = ((Integer) Integer.valueOf(_plus_9));
          videodesc.setDuration((dur_2).intValue());
          String _location_1 = videodesc.getLocation();
          String _plus_10 = ("file \'" + _location_1);
          String _plus_11 = (_plus_10 + "\'");
          String _plus_12 = (_plus_11 + " duration ");
          String _plus_13 = (_plus_12 + dur_2);
          playlist.add(_plus_13);
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription va : _videodescs) {
            lempty_2.add(va);
          }
          this.videoGenListed.add(lempty_2);
          index++;
        }
      }
    }
    String playlistStr = "";
    for (final String pl : playlist) {
      String _playlistStr = playlistStr;
      playlistStr = (_playlistStr + (pl + "\n"));
    }
    return playlistStr;
  }
  
  public String generateFromVideoDescriptions(final List<VideoDescription> l) {
    final ArrayList<String> playlist = CollectionLiterals.<String>newArrayList();
    for (final VideoDescription v : l) {
      {
        String newLoc = v.getLocation();
        InputOutput.<String>println(newLoc);
        Text _text = v.getText();
        boolean _tripleNotEquals = (_text != null);
        if (_tripleNotEquals) {
          String _content = v.getText().getContent();
          boolean _tripleNotEquals_1 = (_content != "");
          if (_tripleNotEquals_1) {
            String _videoid = v.getVideoid();
            String _plus = ((this.tag + "_") + _videoid);
            String _plus_1 = (_plus + ".mp4");
            newLoc = _plus_1;
            this.ffmpeg.generateVideoFilteredWithText(v, v.getLocation(), newLoc);
          }
        }
        Filter _filter = v.getFilter();
        if ((_filter instanceof FlipFilter)) {
          InputOutput.<String>println("FLIPFILTER");
          Filter _filter_1 = v.getFilter();
          String _orientation = ((FlipFilter) _filter_1).getOrientation();
          if (_orientation != null) {
            switch (_orientation) {
              case "h":
                this.ffmpeg.applyFilterFilpH(newLoc);
                break;
              case "horizontal":
                this.ffmpeg.applyFilterFilpH(newLoc);
                break;
              case "v":
                this.ffmpeg.applyFilterFilpV(newLoc);
                break;
              case "vertical":
                this.ffmpeg.applyFilterFilpV(newLoc);
                break;
              default:
                break;
            }
          } else {
          }
        }
        Filter _filter_2 = v.getFilter();
        if ((_filter_2 instanceof NegateFilter)) {
          InputOutput.<String>println("NEGATEFILTER");
          this.ffmpeg.applyFilterNegate(newLoc);
        }
        Filter _filter_3 = v.getFilter();
        if ((_filter_3 instanceof BlackWhiteFilter)) {
          InputOutput.<String>println("BNFILTER");
          this.ffmpeg.applyFilterBN(newLoc);
        }
        int _duration = v.getDuration();
        String _plus_2 = (((("file \'" + newLoc) + "\'") + " duration ") + Integer.valueOf(_duration));
        String _plus_3 = (_plus_2 + " inpoint ");
        String _plus_4 = (_plus_3 + "0");
        playlist.add(_plus_4);
      }
    }
    String playlistStr = "";
    for (final String pl : playlist) {
      String _playlistStr = playlistStr;
      playlistStr = (_playlistStr + (pl + "\n"));
    }
    return playlistStr;
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
          int _videoDuration = this.ffmpeg.videoDuration(desc.getLocation());
          int _plus = (_videoDuration + 0);
          _description.setDuration(_plus);
          this.ffmpeg.generateThumbnail(index, desc.getLocation());
          this.numberOfThumbnail++;
          index++;
          this.allVideos.add(desc);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          VideoDescription _description_1 = ((OptionalVideoSeq)video).getDescription();
          int _videoDuration_1 = this.ffmpeg.videoDuration(desc_1.getLocation());
          int _plus_1 = (_videoDuration_1 + 0);
          _description_1.setDuration(_plus_1);
          this.ffmpeg.generateThumbnail(index, desc_1.getLocation());
          this.numberOfThumbnail++;
          index++;
          this.allVideos.add(desc_1);
        }
        if ((video instanceof AlternativeVideoSeq)) {
          final AlternativeVideoSeq alts = ((AlternativeVideoSeq) video);
          EList<VideoDescription> _videodescs = alts.getVideodescs();
          for (final VideoDescription videodesc : _videodescs) {
            {
              final VideoDescription desc_2 = videodesc;
              int _videoDuration_2 = this.ffmpeg.videoDuration(desc_2.getLocation());
              int _plus_2 = (_videoDuration_2 + 0);
              videodesc.setDuration(_plus_2);
              this.ffmpeg.generateThumbnail(index, desc_2.getLocation());
              this.numberOfThumbnail++;
              index++;
              this.allVideos.add(desc_2);
            }
          }
        }
      }
    }
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
          int _videoDuration = this.ffmpeg.videoDuration(desc.getLocation());
          duration = (_duration + _videoDuration);
        }
        if ((video instanceof OptionalVideoSeq)) {
          final VideoDescription desc_1 = ((OptionalVideoSeq)video).getDescription();
          int _duration_1 = duration;
          int _videoDuration_1 = this.ffmpeg.videoDuration(desc_1.getLocation());
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
              final int durationTemp = this.ffmpeg.videoDuration(videodesc.getLocation());
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
        String format = ".mp4";
        String gif = ".gif";
        for (final List<VideoDescription> v : this.allVars) {
          {
            int size = 0;
            String _plus = (Integer.valueOf(index) + "");
            fl.append(_plus);
            fl.append(",");
            final String playlistTemp = this.generateFromVideoDescriptions(v);
            this.writeInFile(source, playlistTemp);
            this.ffmpeg.generateVideo(source, ((target + Integer.valueOf(index)) + format));
            this.ffmpeg.generateGif(((target + Integer.valueOf(index)) + format), ((target + Integer.valueOf(index)) + gif), (-1), (-1), (-1));
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
  
  public boolean idExists(final String id) {
    for (final String s : this.listId) {
      boolean _equals = Objects.equal(s, id);
      if (_equals) {
        return true;
      }
    }
    return false;
  }
}
