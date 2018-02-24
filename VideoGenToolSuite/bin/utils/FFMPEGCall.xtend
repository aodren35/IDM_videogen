package utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileOutputStream
import org.xtext.example.mydsl.videoGen.VideoDescription

/*
 * Classe permettant les appels aux commandes FFMPEG
 */

public class FFMPEGCall {
	
	// private final static String PATH_TOOL = "C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/"
	private final static String PATH_TOOL = ""
	private final static String PATH_GEN_RELATIVE = "ressources/gen/"
	private final static String PATH_GEN_VIGNETTES_RELATIVE = "ressources/gen/vignettes/"
		private final static String PATH_GEN_GIF_RELATIVE = "ressources/gen/gif/"
			private final static String PATH_GEN_VIDEOS_RELATIVE = "ressources/gen/videos/"
	private final static String PATH_RESSOURCES = "ressources/"
	
	private String tag = ""
	
	new (String tag) {
		this.tag = tag
	}
	
	def int launchFfmpegCmd(String ffmpegCmd) {
		println(ffmpegCmd)
		var Process p 
		var FileOutputStream fos = new FileOutputStream('logger.txt')
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	// error
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR")           
		// output
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos)   
        // vide les streams
        errorGobbler.start()
        outputGobbler.start()
        var int exitVal = p.waitFor
        fos.flush()
        fos.close()
        return exitVal
	}
	
	def void copy(String source, String target) {
		var ffmpegCmd = ffmpegCopyCommand(PATH_TOOL + source, PATH_GEN_VIDEOS_RELATIVE + target).toString 
		launchFfmpegCmd(ffmpegCmd)		
	}
	
	def void generateVideo(String source, String target) {
		var ffmpegCmd = ffmpegConcatenateCommand(PATH_TOOL + source, PATH_GEN_VIDEOS_RELATIVE + target).toString 
		launchFfmpegCmd(ffmpegCmd)
	}
	
	def applyFilterFilpH(String loc, String newLoc) {
		var ffmpegCmd = ffmpegFlipH(PATH_TOOL + PATH_GEN_VIDEOS_RELATIVE+ loc, PATH_GEN_VIDEOS_RELATIVE +newLoc).toString 
		launchFfmpegCmd(ffmpegCmd)
	}
	
	def applyFilterFilpV(String loc, String newLoc) {
		var ffmpegCmd = ffmpegFlipV(PATH_TOOL +PATH_GEN_VIDEOS_RELATIVE+ loc, PATH_GEN_VIDEOS_RELATIVE + newLoc).toString 
		launchFfmpegCmd(ffmpegCmd)
	}
	
	def applyFilterNegate(String loc, String newLoc) {
		var ffmpegCmd = ffmpegNegate(PATH_TOOL +PATH_GEN_VIDEOS_RELATIVE+ loc, PATH_GEN_VIDEOS_RELATIVE +newLoc).toString 
		launchFfmpegCmd(ffmpegCmd)
	}
	
	def applyFilterBN(String loc, String newLoc) {
		var ffmpegCmd = ffmpegBN(PATH_TOOL +PATH_GEN_VIDEOS_RELATIVE+ loc, PATH_GEN_VIDEOS_RELATIVE +newLoc).toString 
		launchFfmpegCmd(ffmpegCmd)
	}	
	
	def void generateVideoFilteredWithText(VideoDescription desc, String source, String target) {
		var Process p 
		var text = desc.text.content
		var x = 50
		var y = 0
		var color = desc.text.color
		var size = desc.text.size
		var String ffmpegCmd
		if (size == 0) { 
			size = 20
		}
		switch desc.text.position {
  			case 'TOP' : {
  				ffmpegCmd = ffmpegDrawTextTOP(PATH_TOOL +PATH_GEN_VIDEOS_RELATIVE+ source, PATH_GEN_VIDEOS_RELATIVE + target, text,  color, size).toString 
  			}
  			case 'BOTTOM' : {
  				ffmpegCmd = ffmpegDrawTextBOTTOM(PATH_TOOL + PATH_GEN_VIDEOS_RELATIVE+ source, PATH_GEN_VIDEOS_RELATIVE + target, text, color, size).toString 
  			}
  			case 'CENTER': {
  				ffmpegCmd = ffmpegDrawTextCENTER(PATH_TOOL +PATH_GEN_VIDEOS_RELATIVE+ source, PATH_GEN_VIDEOS_RELATIVE + target, text, color, size).toString 
  			}
  			default : ffmpegCmd = ffmpegDrawTextCENTER(PATH_TOOL + PATH_GEN_VIDEOS_RELATIVE+source, PATH_GEN_VIDEOS_RELATIVE + target, text, color, size).toString 
		}
		
		launchFfmpegCmd(ffmpegCmd)
		    
	}
	
	def void generateGif(String source, String target, int t, int w, int l) {
		var Process p
		var int time = 0
		var int width = 320
		var int length = 240
		if (t != -1) time = t
		if (w != -1) width = w
		if (l != -1) length = l
		var ffmpegCmd = ffmpegVideoToGif(PATH_TOOL+ PATH_GEN_VIDEOS_RELATIVE + source, PATH_TOOL+PATH_GEN_GIF_RELATIVE + target, time, width, length).toString 
		launchFfmpegCmd(ffmpegCmd)
	}
	
	// Différent car on attend un résultat de l'execution de la commande
	def int videoDuration(String locationVideo) {
			
			var Process p
			var ffmpegCmd = ffmpegComputeDuration(locationVideo).toString 
		
			p = Runtime.getRuntime().exec(ffmpegCmd);
			p.waitFor();
			
			var BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			var String line = "";
			var String lines = "";
		    while ((line = reader.readLine()) != null) {
		        lines = lines + line;
		    }
		    return Math.round(ParseFloat(lines))-1;
	}
	
	def float ParseFloat(String strNumber) {
   		if (strNumber != null && strNumber.length() > 0) {
	       try {
	          return Float.parseFloat(strNumber);
	       } catch(Exception e) {
	          return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
	       }
   		}
   		else return 0;
}
	
	def void generateThumbnail(int id, String loc) {
		var ffmpegCmd = ffmpegThumbnail(id, loc).toString 
		launchFfmpegCmd(ffmpegCmd)
	}	
	
	def ffmpegThumbnail (int id, String loc) '''
		ffmpeg -i «loc» -ss 00:00:01.000 -vframes 1 «PATH_GEN_VIGNETTES_RELATIVE+tag+"_"+id».jpg -y
	'''
 	
	def ffmpegFlipH(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "hflip" -acodec copy «outputPath» -y
	'''
	
	def ffmpegFlipV(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "vflip" -acodec copy «outputPath» -y
	'''
	
	def ffmpegNegate(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "negate" -acodec copy «outputPath» -y
	'''
	
	def ffmpegBN(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "hue=s=0" -acodec copy «outputPath» -y
	'''

	def ffmpegDrawTextTOP(String inputPath, String outputPath, String text,   String color, int size) '''
		ffmpeg -i «inputPath» -vf "drawtext=fontfile='C\:\\Windows\\fonts\\Arial.ttf':text=«text»:x=(w-text_w)/2:fontsize=«size»:fontcolor=«color»" -acodec copy «outputPath» -y
	'''
	
	def ffmpegDrawTextCENTER(String inputPath, String outputPath, String text,  String color, int size) '''
		ffmpeg -i «inputPath» -vf "drawtext=fontfile='C\:\\Windows\\fonts\\Arial.ttf':text=«text»:x=(w-text_w)/2:y=(h-text_h)/2:fontsize=«size»:fontcolor=«color»" -acodec copy «outputPath» -y
	'''
	def ffmpegDrawTextBOTTOM(String inputPath, String outputPath, String text, String color, int size) '''
		ffmpeg -i «inputPath» -vf "drawtext=fontfile='C\:\\Windows\\fonts\\Arial.ttf':text=«text»:x=(w-text_w)/2:y=(h-text_h):fontsize=«size»:fontcolor=«color»" -acodec copy «outputPath» -y
	'''

	def ffmpegConcatenateCommand(String mpegPlaylistFile, String outputPath) '''
			 ffmpeg -y -f concat -safe 0 -i «mpegPlaylistFile» -c copy «outputPath»
		'''
		
		def ffmpegCopyCommand2(String inputPath, String outputPath) '''
			 ffmpeg -y -i «inputPath»  -c:v libx264 -preset ultrafast «outputPath»
		'''
		def ffmpegCopyCommand(String inputPath, String outputPath) '''
			 ffmpeg -y -i «inputPath»  -c:v libx264 -preset slow -crf 18 -c:a aac -b:a 192k -pix_fmt yuv420p «outputPath»
		'''
		
	def ffmpegComputeDuration(String locationVideo) '''
		ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 «locationVideo»
	'''
	
//	def ffmpegVideoToPalette(String videoLocation) '''
//		 ffmpeg -v warning -ss  0 -i «videoLocation» -vf "fps=15,scale=-1:-1:flags=lanczos,palettegen" -y «PATH_TEMP_PALETTE»
//	'''
//	def ffmpegPaletteToGif(String videoLocation, String outputPath) 	/*'''
//		 ffmpeg -i «videoLocation» -ss 00:00:00.000 -pix_fmt rgb24 -r 10 -s 320x240   «outputPath»
//	'''*/
//	'''
//		 ffmpeg -v warning -ss 0 -i «videoLocation» -i «PATH_TEMP_PALETTE» -lavfi "fps=15,scale=-1:-1:flags=lanczos, [x]; [x][1:v] paletteuse" -y  «outputPath»
//	'''


	def String ffmpegVideoToGif(String videoLocation, String outputPath, int time, int w, int l){
		if (time != 0) { 
			return 
				''' 
		 		ffmpeg -i «videoLocation» -y -ss 0 -pix_fmt rgb8 -r 10 -t «time» -s «w»x«l»  «outputPath»
				'''
		} else {
			return 
				''' 
				 ffmpeg -i «videoLocation» -y -ss 0 -pix_fmt rgb8 -r 10 -s «w»x«l»  «outputPath»
				'''
		}
	}
}