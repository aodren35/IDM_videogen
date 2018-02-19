import org.eclipse.emf.common.util.URI

import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import org.xtext.example.mydsl.videoGen.*
import utils.Randomiser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.List
import java.util.ArrayList
import java.io.PrintWriter
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import java.io.FileWriter

class VideoGen {
	
	private static VideoGeneratorModel videoGenUpdated
	private static List<VideoDescription> allVideos = new ArrayList<VideoDescription>()
	
	def static void main(String[] args) {
    	var videogen = generate("example1.videogen")
    	writeInFile("playlist.txt", videogen)
    	generateVideo()
    	generateThumbnails("example1.videogen")
    	println("getlongestvar " + getLongestVar("example1.videogen") )
    	writeStatsToCsv()
    	// new VideoGenHelper().saveVideoGenerator(URI.createURI("example1.videogen"), videoGenUpdated)
  	}
	
	
	// TODO : ajouter duration si absentes
	static def String generate(String uri) {
		val videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri))
		// and then visit the model
		// eg access video sequences: 
		val playlist = newArrayList()
		videoGen.medias.forEach [media |
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					playlist.add("file '" + desc.location + "'")
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					val rd = new Randomiser();
					rd.setChoices(1);
					if (rd.randomize() == 1) {
						playlist.add("file '" + desc.location + "'")
					}
					
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					val rd = new Randomiser()
					rd.setChoices(alts.videodescs.size)
					val selected = rd.randomize()
					val videodesc = alts.videodescs.get(selected)
					playlist.add("file '" + videodesc.location + "'")
				}	
			}
		]
		
		var playlistStr = ""
		for (String pl : playlist)
			playlistStr += pl + "\n"
		
		videoGenUpdated = videoGen
		return playlistStr
		
		
	}

	static def void generateThumbnail(String id, String loc) {
		var cmd = "ffmpeg -i " + loc + " -ss 00:00:01.000 -vframes 1 " + "ressources/gen/vignettes/" + id + ".jpg -y"
		var p = Runtime.getRuntime().exec(cmd);
		p.waitFor();

	}
	
	static def void generateThumbnails(String uri) {
		val videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri))
		// and then visit the model
		// eg access video sequences: 
		videoGen.medias.forEach [media |
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq ) {
					val desc =  video.description
					video.description.duration = readDuration(desc.location)
					generateThumbnail(desc.videoid, desc.location)
					allVideos.add(desc)		
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					video.description.duration = readDuration(desc.location)
					generateThumbnail(desc.videoid, desc.location)
					allVideos.add(desc)		
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					for (VideoDescription videodesc: alts.videodescs) {
						val desc = videodesc
						videodesc.duration = readDuration(desc.location)
						generateThumbnail(desc.videoid, desc.location)
						allVideos.add(desc)		
					}
				}	
			}
		]
		videoGenUpdated = videoGen
	}
	
	
	static def int getLongestVar(String uri) {
		val videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri))
		// and then visit the model
		// eg access video sequences: 
		var duration = 0
		for (Media media: videoGen.medias) {
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					duration += readDuration(desc.location)
							
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					duration += readDuration(desc.location)
						
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					
					var longest = -1;
					var index = 0;
					var longestDuration = 0;
					for (VideoDescription videodesc: alts.videodescs) {
						val durationTemp = readDuration(videodesc.location)
						if (durationTemp > longestDuration) {
							longestDuration = durationTemp
						}
						index ++
					}
					duration += longestDuration
				}	
			}
		}
		return duration
	}
	
	static def void writeInFile(String filename, String data) {
		val buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"))
		try {
			buffer.write(data)
		}
		catch(IOException e) {
			throw e
		}
		finally {
			buffer.flush()
			buffer.close()
		}
	}
	
	static def void generateVideo() {
		var Process p 
		var ffmpegCmd = ffmpegConcatenateCommand("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/playlist.txt", "ressources/gen/ro.mp4").toString 
		println(ffmpegCmd)
		
	 	p = Runtime.runtime.exec(ffmpegCmd)
		p.waitFor	
	}
	
	static def ffmpegConcatenateCommand(String mpegPlaylistFile, String outputPath) '''
		 ffmpeg -y -f concat -safe 0 -i «mpegPlaylistFile» -c copy «outputPath»
	'''
	
	static def ffmpegComputeDuration(String locationVideo) '''
		ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 «locationVideo»
	'''
	
	static def int readDuration(String locationVideo) {
			
			var Process p
			var ffmpegCmd = ffmpegComputeDuration(locationVideo).toString 
			println(ffmpegCmd)
		
			p = Runtime.getRuntime().exec(ffmpegCmd);
			p.waitFor();
			
			var BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			var String line = "";
			var String lines = "";
		    while ((line = reader.readLine()) != null) {
		        lines = lines + line;
		    }
		    return Math.round(Float.parseFloat(lines))-1;
	}
	
	static def List<List<VideoDescription>> generateAllVars() {
		
		var List<List<VideoDescription>> result = new ArrayList<List<VideoDescription>>()
		var List<VideoDescription> lempty = new ArrayList<VideoDescription>()
		result.add(lempty)
		for (Media media : videoGenUpdated.medias)	{
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq ) {
					val desc =  video.description
					for (List<VideoDescription> l: result){
						l.add(desc)
					}	
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description	
						
					var partial = new ArrayList<List<VideoDescription>>()
					for(List<VideoDescription>l : result) {
						var lTemp = new ArrayList<VideoDescription>()
						lTemp.addAll(l)
						partial.add(lTemp)
						l.add(desc)
					}
					result.addAll(partial)
					
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					var partial = new ArrayList<List<VideoDescription>>()
					for (var it = result.iterator(); it.hasNext();){
						var l = it.next()
						it.remove()
						for (VideoDescription videodesc: alts.videodescs) {
							val desc = videodesc
							var lTemp = new ArrayList<VideoDescription>()
							lTemp.addAll(l)
							lTemp.add(desc)
							partial.add(lTemp)
						}
					}
					result.addAll(partial)
				}	
			}
		}
		return result
	}

	static def void writeStatsToCsv() {
		var PrintWriter pw = null
		var File file = new File("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/ressources/stats/vars_size.csv")
		var fl = new FileWriter(file);
		
     
        fl.append("id")
        fl.append(",")
        
        for(VideoDescription desc: allVideos) {
            fl.append(desc.videoid)
            fl.append(",")
        }
        
        fl.append("size")
        fl.append("\n")
		
        var index = 1;
		for(List<VideoDescription> v : generateAllVars()) {
			var size = 0
			fl.append(index + "")
			fl.append(",")
			
			for(VideoDescription desc : allVideos) {
				if(hasVideo(v, desc)) {
					fl.append("TRUE")
					
					var path = Paths.get(desc.location)
					var byte[] data = Files.readAllBytes(path)
					
					size += data.length
				} else {
					fl.append("FALSE")
				}
				fl.append(",")
			}
			
			fl.append(size +"")
			fl.append("\n")
			
			index++
		}
		fl.flush()
		fl.close()
	}

	static def boolean hasVideo(List<VideoDescription> l, VideoDescription v){
		for (VideoDescription vTemp: l) {
			if (vTemp.videoid == v.videoid) {
				return true;
			}
		}
		return false;
	}
}