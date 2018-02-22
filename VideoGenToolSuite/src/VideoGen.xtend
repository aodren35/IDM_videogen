import org.eclipse.emf.common.util.URI

import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import org.xtext.example.mydsl.videoGen.*
import utils.*
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
import java.util.concurrent.TimeUnit
import org.apache.commons.io.FileUtils
import java.nio.charset.Charset



class VideoGen {
	
	private  VideoGeneratorModel videoGen
	private  VideoGeneratorModel videoGenUpdated
	private int numberOfThumbnail;
	private  List<VideoDescription> allVideos = new ArrayList<VideoDescription>()
	private  List<List<VideoDescription>> videoGenListed = new ArrayList<List<VideoDescription>>()
	private  List<List<VideoDescription>> allVars = new ArrayList<List<VideoDescription>>()
	private List<String> listId = new ArrayList<String>()
	
	private String tag = ""
	private String uri = ""
	
	private final static String PATH_TOOL = "C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/"
	private final static String PATH_GEN_RELATIVE = "ressources/gen/"
	private final static String PATH_RESSOURCES = "ressources/"
	private final static String PATH_TEMP_PALETTE = "c:/temp/palette.png"
	
	def static void main(String[] args) {
//		val String example = "example1.videogen"
//		val String playlistExample = "playlist.txt"
//		val String targetExample = "ro4.mp4"
//    	var videogen = generate(example)
//    	writeInFile(playlistExample, videogen)
//    	generateVideo(playlistExample, targetExample)
//    	generateThumbnails(example)
//    	writeStatsToCsv()
    	
    	// new VideoGenHelper().saveVideoGenerator(URI.createURI("example1.videogen"), videoGenUpdated)
  	}
  	
  	new(String uri) {
  		videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri))
  		tag = uri.split("\\.").get(0)
  		this.uri = uri
  		numberOfThumbnail = 0
    }
	
	def int getNumberOfThumbnail() {
		return numberOfThumbnail
	}
	
	def int getNumberMedias() {
		var result = 0
		for (Media media :videoGen.medias) {
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq || video instanceof OptionalVideoSeq) {
					result ++
				} else {
					val alts  = (video as AlternativeVideoSeq).videodescs
					result += alts.size
				}
			}
		}
		return result
	}
	
	def List<VideoDescription> getAllVideos() {
		return allVideos
	}
	
	def void clean() {
		var index = 0
		videoGenUpdated = videoGen
		for (Media media : videoGenUpdated.medias) {
			val video = (media as VideoSeq)
				if (video instanceof MandatoryVideoSeq) {
					cleanMandatory(video.description, index)
					index ++
				}
				if (video instanceof OptionalVideoSeq) {
					cleanOptional(video.description, index)
					index ++
				}
				if (video instanceof AlternativeVideoSeq) {
					var total = 0
					if (video.videodescs.size == 0) {
						videoGenUpdated.medias.remove(video)
					}
					cleanId(video, index)
					index ++
					for (VideoDescription v: video.videodescs) {
						total += cleanAlternative(v, index, total, video.videodescs.size)
						index ++
					}
				}
		}

	}
	
	def void cleanAndGenerateFilter(VideoDescription desc) {
		
	}
	
	def void cleanId(AlternativeVideoSeq desc, int index) {
		if (desc.videoid === ""){
			desc.videoid = 	 "alternative"+"_"+tag+"_"+index
		}
		while (idExists(desc.videoid)){
			desc.videoid = desc.videoid + "_"+index
		}
		
	}
	
	def String cleanMandatory(VideoDescription desc, int index) {
		if (desc.videoid === ""){
			desc.videoid = 	 "video_mandatory"+"_"+tag+"_"+index
		}
		while (idExists(desc.videoid)){
			desc.videoid = desc.videoid + "_"+index
		}
		
		if (desc.duration as Integer === null) {
			val dur =  (videoDuration(desc.location) + 0) as Integer
			desc.duration = dur
		} else {
			if (desc.duration < 0) {
				val dur =  (videoDuration(desc.location) + 0) as Integer
				desc.duration = dur
			}
		}
		desc.probability = 100
		if (desc.description === "") {
			desc.description = "video_mandatory_"+index+"_"+tag
		}
		return ''
	}
	
	def String cleanOptional(VideoDescription desc, int index) {
		
		if (desc.videoid === ""){
			desc.videoid = 	 "video_optional"+"_"+tag+"_"+index
		}
		while (idExists(desc.videoid)){
			desc.videoid = desc.videoid + "_"+index
		}
		if (desc.duration as Integer === null) {
			val dur =  (videoDuration(desc.location) + 0) as Integer
			desc.duration = dur
		} else {
			if (desc.duration < 0) {
				val dur =  (videoDuration(desc.location) + 0) as Integer
				desc.duration = dur
			}
		}
		if (desc.probability >= 100 || desc.probability <= 0) {
			desc.probability = 50
		}
		if (desc.description === "") {
			desc.description = "video_optional_"+index+"_"+tag
		}
		return ''
	}
	
	def int cleanAlternative(VideoDescription desc, int index, int tot, int size) {
		if (desc.videoid === ""){
			desc.videoid = 	 "video_alternative"+"_"+tag+"_"+index
		}
		while (idExists(desc.videoid)){
			desc.videoid = desc.videoid + "_"+index
		}
		var int prob
		if (desc.duration as Integer === null) {
			val dur =  (videoDuration(desc.location) + 0) as Integer
			desc.duration = dur
		} else {
			if (desc.duration < 0) {
				val dur =  (videoDuration(desc.location) + 0) as Integer
				desc.duration = dur
			}
		}
		if (desc.probability >= 100 || desc.probability <= 0) {
			desc.probability = (1/size) * 100
		}
		if ((desc.probability + tot) > 100) {
			desc.probability = 100 - tot
			if (desc.probability < 0) desc.probability = 0
		}
		if (desc.description === "") {
			desc.description = "video_alternative_"+index+"_"+tag
		}
		return desc.probability
	}
	
	
	// TODO : ajouter duration si absentes (voir faire une fonction globale qui fait tout)
	def String generate() {
		// and then visit the model
		// eg access video sequences: 
		val playlist = newArrayList()
		var index = 0
		for (Media media: videoGen.medias){
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					val dur =  (videoDuration(desc.location) + 0) as Integer
					var newLoc = desc.location
					desc.duration = dur
					playlist.add("file '" + newLoc + "'"+ " duration "+ dur)
					
					var List<VideoDescription> lempty = new ArrayList<VideoDescription>()
					lempty.add(desc)
					videoGenListed.add(lempty)
					index ++
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					val rd = new Randomiser();
					rd.setChoices(0);
					if (rd.randomize() == 1) {				
						val dur = (videoDuration(desc.location) + 0) as Integer
						desc.duration = dur
						playlist.add("file '" + desc.location + "'"+ " duration "+ dur)
						
					}
					
					var List<VideoDescription> lempty = new ArrayList<VideoDescription>()
					lempty.add(desc)
					videoGenListed.add(lempty)
					index ++
					
				}
				if (video instanceof AlternativeVideoSeq) {
					var List<VideoDescription> lempty = new ArrayList<VideoDescription>()
					val alts = (video as AlternativeVideoSeq)
					val rd = new Randomiser()
					rd.setChoices(alts.videodescs.size)
					
					val selected = rd.randomize()
					println("CHOICES : "+alts.videodescs.size + " SELECTED : "+ selected)
					val videodesc = alts.videodescs.get(selected)
					val dur = (videoDuration(videodesc.location) + 0) as Integer
					videodesc.duration = dur
					playlist.add("file '" + videodesc.location + "'"+ " duration "+ dur)
					
					for (VideoDescription va: alts.videodescs) {
						lempty.add(va)
					}
					videoGenListed.add(lempty)
					index ++
				}	
			}
		}
		
		var playlistStr = ""
		for (String pl : playlist)
			playlistStr += pl + "\n"
		
		// videoGenUpdated = videoGen
		// new VideoGenHelper().saveVideoGenerator(URI.createURI(uri), videoGenUpdated)
		return playlistStr
		
		
	}

	def String generateFromVideoDescriptions(List<VideoDescription> l) {
		val playlist = newArrayList()
		for (VideoDescription v: l) {
			var newLoc = v.location
			if (v.text.content !== "") {
				newLoc = tag + "_" + v.videoid + ".mp4"
				generateVideoFilteredWithText(v, v.location, newLoc)
			}
			println(v.filter)
			if (v.filter instanceof FlipFilter) {
				println("FLIPFILTER")
				switch ((v.filter as FlipFilter).orientation) {
					case 'h' : { applyFilterFilpH(newLoc)}
					case 'horizontal' : {applyFilterFilpH(newLoc)}
					case 'v' : {applyFilterFilpV(newLoc)}
					case 'vertical' : {applyFilterFilpV(newLoc)}
					default : {}
				}
				
			}
			if (v.filter instanceof NegateFilter) {
				println("NEGATEFILTER")
				applyFilterNegate(newLoc)
			}
			if (v.filter instanceof BlackWhiteFilter) {
				println("BNFILTER")
				applyFilterBN(newLoc)
			}
			playlist.add("file '" + PATH_GEN_RELATIVE + newLoc + "'"+ " duration "+ v.duration + " inpoint " + "0")
		}
		var playlistStr = ""
		for (String pl : playlist)
			playlistStr += pl + "\n"
		return playlistStr
	}
	
	

	def void generateThumbnail(int id, String loc) {
		var cmd = "ffmpeg -i " + loc + " -ss 00:00:01.000 -vframes 1 " + PATH_GEN_RELATIVE + "vignettes/" + tag + "_"+id + ".jpg -y"
		var p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		numberOfThumbnail ++ 

	}
	
	def void generateThumbnails() {
		// and then visit the model
		// eg access video sequences:
		var index = 0; 
		for (Media media: videoGen.medias) {
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq ) {
					val desc =  video.description
					video.description.duration = videoDuration(desc.location) + 0 
					generateThumbnail(index, desc.location)
					index ++
					allVideos.add(desc)		
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					video.description.duration = videoDuration(desc.location) + 0 
					generateThumbnail(index, desc.location)
					index ++
					allVideos.add(desc)		
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					for (VideoDescription videodesc: alts.videodescs) {
						val desc = videodesc
						videodesc.duration = videoDuration(desc.location) + 0 
						generateThumbnail(index, desc.location)
						index ++
						allVideos.add(desc)		
					}
				}	
			}
			
		}
		// videoGenUpdated = videoGen
	}

	def int getLongestVar() {
		// and then visit the model
		// eg access video sequences: 
		var duration = 0
		for (Media media: videoGen.medias) {
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					duration += videoDuration(desc.location)
							
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					duration += videoDuration(desc.location)
						
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					
					var longest = -1;
					var index = 0;
					var longestDuration = 0;
					for (VideoDescription videodesc: alts.videodescs) {
						val durationTemp = videoDuration(videodesc.location)
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
	
	def void writeInFile(String filename, String data) {
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
	
	def void generateVideo(String source, String target) {
		var Process p 
		var ffmpegCmd = ffmpegConcatenateCommand(PATH_TOOL + source, PATH_GEN_RELATIVE + target).toString 
				var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();  
//		if(!p.waitFor(60, TimeUnit.SECONDS)) {
//    		//timeout - kill the process. 
//    		p.destroyForcibly()
//		}
		
	}
	
	def applyFilterFilpH(String string) {
		var Process p 
		var ffmpegCmd = ffmpegFlipH(PATH_TOOL + string, PATH_GEN_RELATIVE + "filtered_"+string).toString 
		println(ffmpegCmd)
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 			var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();  
	}
	
	def applyFilterFilpV(String string) {
		var Process p 
		var ffmpegCmd = ffmpegFlipV(PATH_TOOL +PATH_GEN_RELATIVE+ string, PATH_GEN_RELATIVE + "filtered_"+string).toString 
		println(ffmpegCmd)
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 		var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();  
	}
	
	def applyFilterNegate(String string) {
		var Process p 
		var ffmpegCmd = ffmpegNegate(PATH_TOOL +PATH_GEN_RELATIVE+ string, PATH_GEN_RELATIVE + "filtered_"+string).toString 
		println(ffmpegCmd)
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 		var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();  
	}
	
	def applyFilterBN(String string) {
		var Process p 
		var ffmpegCmd = ffmpegBN(PATH_TOOL +PATH_GEN_RELATIVE+ string, PATH_GEN_RELATIVE + "filtered_"+string).toString 
		println(ffmpegCmd)
	 	p = Runtime.runtime.exec(ffmpegCmd)
			var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();  
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
  				ffmpegCmd = ffmpegDrawTextTOP(PATH_TOOL + source, PATH_GEN_RELATIVE + target, text,  color, size).toString 
  			}
  			case 'BOTTOM' : {
  				ffmpegCmd = ffmpegDrawTextBOTTOM(PATH_TOOL + source, PATH_GEN_RELATIVE + target, text, color, size).toString 
  			}
  			case 'CENTER': {
  				ffmpegCmd = ffmpegDrawTextCENTER(PATH_TOOL + source, PATH_GEN_RELATIVE + target, text, color, size).toString 
  			}
  			default : ffmpegCmd = ffmpegDrawTextCENTER(PATH_TOOL + source, PATH_GEN_RELATIVE + target, text, color, size).toString 
		}


		
		var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();      
		
		    
	}
	
	def void generateGif(String source, String target, int t, int w, int l) {
		var Process p
		var int time = 0
		var int width = 320
		var int length = 240
		if (t != -1) time = t
		if (w != -1) width = w
		if (l != -1) length = l
		var ffmpegCmd = ffmpegVideoToGif(PATH_TOOL+ PATH_GEN_RELATIVE + source, PATH_TOOL+PATH_GEN_RELATIVE + target, time, width, length).toString 
		println(ffmpegCmd)
		
	 	p = Runtime.runtime.exec(ffmpegCmd)
		var FileOutputStream fos = new FileOutputStream('logger.txt');
	 	p = Runtime.runtime.exec(ffmpegCmd)
	 	
	 	println("filter text " + ffmpegCmd)
	 	
	 	// any error message?
        var StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
		// any output?
        var StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", fos);
                
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        var int exitVal = p.waitFor
        
         System.out.println("ExitValue: " + exitVal);
            fos.flush();
            fos.close();  
	}
	
	def List<List<VideoDescription>> generateAllVars() {
		
		var List<List<VideoDescription>> result = new ArrayList<List<VideoDescription>>()
		var List<VideoDescription> lempty = new ArrayList<VideoDescription>()
		result.add(lempty)
		for (Media media : videoGen.medias)	{
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
		allVars = result;
		return result
	}

	def void writeStatsToCsv() {
		if (!allVideos.isEmpty()) {
			var PrintWriter pw = null
			var File file = new File("C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/ressources/stats/"+ tag + "_"+"vars_size.csv")
			var fl = new FileWriter(file);
			
	     
	        fl.append("id")
	        fl.append(",")
	        
	        for(VideoDescription desc: allVideos) {
	            fl.append(desc.videoid)
	            fl.append(",")
	        }
	        
	        fl.append("size")
	        fl.append(",")
	        fl.append("realSize")
	        fl.append(",")
	        fl.append("realSizeGif")
	        fl.append("\n")
			
	        var index = 1;
	        val source = tag + "_"+"playlistTemp.txt"
	        var target = tag + "_"+"gen"
	        var format = ".mp4"
	        var gif = ".gif"
	        
			for(List<VideoDescription> v : allVars) {
				var size = 0
				fl.append(index + "")
				fl.append(",")
				
				val playlistTemp = generateFromVideoDescriptions(v)
				writeInFile(source, playlistTemp)
				generateVideo(source, target+index+format)

				generateGif(target+index+format, target+index+gif, -1, -1, -1)
	//			Files.delete(Paths.get(source))
				
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
				fl.append(",")
				
				var pathVar = Paths.get(PATH_GEN_RELATIVE + target+index+format)
				var realSize = 0
				if (pathVar.toFile().exists){
					var byte[] dataVar = Files.readAllBytes(pathVar)
					realSize = dataVar.length
				}

				fl.append(realSize+"")
				fl.append(",")
				
				var pathVarGif = Paths.get(PATH_GEN_RELATIVE + target+index+gif)
				var realSizeGif = 0
				if (pathVar.toFile().exists){
					var byte[] dataVarGif = Files.readAllBytes(pathVarGif)
					realSizeGif = dataVarGif.length
				}
				fl.append(realSizeGif+"")
				
				fl.append("\n")
				
				index++
			}
			fl.flush()
			fl.close()
		}
	}
	
	def void generateHtml() {
		var File htmlTemplateFile = new File(PATH_RESSOURCES + "template.html");
		var String htmlString = FileUtils.readFileToString(htmlTemplateFile, Charset.forName("UTF-8") );
		var String body = "";
		var index = 0
		for (List<VideoDescription> lv: videoGenListed) {
			body += openDiv()
			if (lv.size > 1) {
				for (VideoDescription v: lv) {
					body += videoGenToHtml(v, index, lv.size)
					index ++
				}
			} else {
				body += videoGenToHtml(lv.get(0), index, 1)
				index ++
			}
			body+= closeDiv()
		}
		htmlString = htmlString.replace("$body", body);
		var File newHtmlFile = new File(PATH_GEN_RELATIVE+ tag +"_new.html");
		FileUtils.writeStringToFile(newHtmlFile, htmlString, Charset.forName("UTF-8") );
	}
	
	
	def openDiv(){
		'''
		<div class="row">
		'''
	}
	def closeDiv(){
		'''
		</div>
		'''
	}
	def videoGenToHtml(VideoDescription v, int id, int col){
		'''
		<div class="col-md-«12/col»">
		<h1>Video : «v.description»</h1>
						<p>
							<img src="«PATH_TOOL + PATH_GEN_RELATIVE + "vignettes/" + tag + "_" + id + ".jpg"»" alt="">
						</p>
		</div>
		'''
	}


	// UTILS
	
	def int nbVariantes() {
		var result = 1
		for (Media media: videoGen.medias){
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					
				}
				if (video instanceof OptionalVideoSeq) {
					result = result * 2
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					result = result *  alts.videodescs.size
				}	
			}
		}
		return result
	}

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
		    return Math.round(Float.parseFloat(lines))-1;
	}

	def boolean hasVideo(List<VideoDescription> l, VideoDescription v){
		for (VideoDescription vTemp: l) {
			if (vTemp.videoid == v.videoid) {
				return true;
			}
		}
		return false;
	}
	
	def ffmpegFlipH(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "hflip" «outputPath» -y
	'''
	
	def ffmpegFlipV(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "vflip" «outputPath» -y
	'''
	
	def ffmpegNegate(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "negate" «outputPath» -y
	'''
	
	def ffmpegBN(String inputPath, String outputPath) '''
		ffmpeg -i «inputPath» -vf "hue=s=0" «outputPath» -y
	'''

	def ffmpegDrawTextTOP(String inputPath, String outputPath, String text,   String color, int size) '''
		ffmpeg -i «inputPath» -vf "drawtext=fontfile='C\:\\Windows\\fonts\\Arial.ttf':text=«text»:x=(w-text_w)/2:fontsize=«size»:fontcolor=«color»" «outputPath» -y
	'''
	
	def ffmpegDrawTextCENTER(String inputPath, String outputPath, String text,  String color, int size) '''
		ffmpeg -i «inputPath» -vf "drawtext=fontfile='C\:\\Windows\\fonts\\Arial.ttf':text=«text»:x=(w-text_w)/2:y=(h-text_h)/2:fontsize=«size»:fontcolor=«color»" «outputPath» -y
	'''
	def ffmpegDrawTextBOTTOM(String inputPath, String outputPath, String text, String color, int size) '''
		ffmpeg -i «inputPath» -vf "drawtext=fontfile='C\:\\Windows\\fonts\\Arial.ttf':text=«text»:x=(w-text_w)/2:y=(h-text_h):fontsize=«size»:fontcolor=«color»" «outputPath» -y
	'''

	def ffmpegConcatenateCommand(String mpegPlaylistFile, String outputPath) '''
			 ffmpeg -y -f concat -safe 0 -i «mpegPlaylistFile» -c copy -r 24 «outputPath»
		'''
		
	def ffmpegComputeDuration(String locationVideo) '''
		ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 «locationVideo»
	'''
	
	def ffmpegVideoToPalette(String videoLocation) '''
		 ffmpeg -v warning -ss  0 -i «videoLocation» -vf "fps=15,scale=-1:-1:flags=lanczos,palettegen" -y «PATH_TEMP_PALETTE»
	'''
	def ffmpegPaletteToGif(String videoLocation, String outputPath) 	/*'''
		 ffmpeg -i «videoLocation» -ss 00:00:00.000 -pix_fmt rgb24 -r 10 -s 320x240   «outputPath»
	'''*/
	'''
		 ffmpeg -v warning -ss 0 -i «videoLocation» -i «PATH_TEMP_PALETTE» -lavfi "fps=15,scale=-1:-1:flags=lanczos, [x]; [x][1:v] paletteuse" -y  «outputPath»
	'''


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
	
	
	def boolean idExists(String id) {
		for (String s : listId) {
			if (s == id) return true
		}
		return false
	}

}