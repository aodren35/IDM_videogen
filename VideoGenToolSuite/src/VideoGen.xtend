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

/*
 * Classe métier du générateur 
 * 
 */

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
	
	private FFMPEGCall ffmpeg
	
	private final static String PATH_TOOL = ""
	// private final static String PATH_TOOL = "C:/Users/aodre/Documents/Cours/M2/IDM/IDM_videogen/VideoGenToolSuite/"
	private final static String PATH_GEN_RELATIVE = "ressources/gen/"
	private final static String PATH_RESSOURCES = "ressources/"
	private final static String PATH_TEMP_PALETTE = "c:/temp/palette.png"
	private final static String PATH_GEN_GIF_RELATIVE = "ressources/gen/gif/"
	private final static String PATH_GEN_VIDEOS_RELATIVE = "ressources/gen/videos/"	
	

 	/**
 	 * Constructeur à partir d'une URI de spécification videogen
 	 */ 	
  	new(String uri) {
  		videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri))
  		tag = uri.split("\\.").get(0)
  		ffmpeg = new FFMPEGCall(tag)
  		this.uri = uri
  		numberOfThumbnail = 0
    }
	
	/*
	 * Récupère le nombre de vignettes générées
	 */
	def int getNumberOfThumbnail() {
		return numberOfThumbnail
	}
		
	/*
	 * Récupère le nombre de média
	 */
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
	
	
	/*
	 * Récupère l'ensemble des Video présentes dans la spécification videogen
	 */
	def List<VideoDescription> getAllVideos() {
		return allVideos
	}
	
	
	/*
	 * "Nettoie" la spécification videogen, en clarifiant les probabilités, les durées et les id de media
	 */
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
			val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
			desc.duration = dur
		} else {
			if (desc.duration < 0) {
				val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
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
			val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
			desc.duration = dur
		} else {
			if (desc.duration < 0) {
				val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
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
			val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
			desc.duration = dur
		} else {
			if (desc.duration < 0) {
				val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
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
	
	/*
	 * Crée une variante de vidéo sous la forme d'une liste de VideoDescription
	 */
	def List<VideoDescription> generateRandomVideo() {
		var List<VideoDescription> result = new ArrayList<VideoDescription>()
		var index = 0
		for (Media media: videoGenUpdated.medias){
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					result.add(desc)
					index ++
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					val rd = new Randomiser();
					rd.setChoices(0);
					if (rd.randomize() == 1) {				
						result.add(desc)
						index ++
						
					}
					
					
				}
				if (video instanceof AlternativeVideoSeq) {
					var List<VideoDescription> lempty = new ArrayList<VideoDescription>()
					val alts = (video as AlternativeVideoSeq)
					val rd = new Randomiser()
					rd.setChoices(alts.videodescs.size)
					
					val selected = rd.randomize()
					println("CHOICES : "+alts.videodescs.size + " SELECTED : "+ selected)
					val videodesc = alts.videodescs.get(selected)
					result.add(videodesc)
					index ++
				}	
			}
		}
		return result
	}
	/*
	 * Crée une variante de vidéo sous la forme d'un string exploitable par ffmpeg
	 * 
	 * !! fonction dépréciée !!
	 */
	def String generate() {
		val playlist = newArrayList()
		var index = 0
		for (Media media: videoGen.medias){
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					val dur =  (ffmpeg.videoDuration(desc.location) + 0) as Integer
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
						val dur = (ffmpeg.videoDuration(desc.location) + 0) as Integer
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
					val dur = (ffmpeg.videoDuration(videodesc.location) + 0) as Integer
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

	/*
	 * Génére une vidéo au format .mkv par appels ffmpeg succéssifs, correspondant à une liste de VideoDescription précédemment générée aléatoirement 
	 */
	def void generateAndCrushFromVideoDescriptions(List<VideoDescription> l) {
		val playlist = newArrayList()
		for (VideoDescription v: l) {
			var newLoc = tag + "_" + v.videoid + ".mkv"
			ffmpeg.copy(v.location, newLoc)
			if (v.text !== null){
				if (v.text.content !== "") {
					val loc = newLoc
					newLoc = 'text_filtered_' + loc
					ffmpeg.generateVideoFilteredWithText(v, loc, newLoc)
				}
			}
			if (v.filter instanceof FlipFilter) {
				println("FLIPFILTER")
				val loc = newLoc
				newLoc = 'flip_filtered_' + loc
				switch ((v.filter as FlipFilter).orientation) {
					case 'h' : { ffmpeg.applyFilterFilpH(loc, newLoc)}
					case 'horizontal' : {ffmpeg.applyFilterFilpH(loc, newLoc)}
					case 'v' : {ffmpeg.applyFilterFilpV(loc, newLoc)}
					case 'vertical' : {ffmpeg.applyFilterFilpV(loc, newLoc)}
					default : {}
				}
				
			}
			if (v.filter instanceof NegateFilter) {
				val loc = newLoc
				newLoc = 'negate_filtered_' + loc
				ffmpeg.applyFilterNegate(loc, newLoc)
			}
			if (v.filter instanceof BlackWhiteFilter) {
				val loc = newLoc
				newLoc = 'bw_filtered_' + loc				
				ffmpeg.applyFilterBN(loc, newLoc)
			}
			playlist.add("file '"  + PATH_GEN_VIDEOS_RELATIVE + newLoc + "'")
		}
		var playlistStr = ""
		for (String pl : playlist)
			playlistStr += pl + "\n"
		val source = tag+'_playlisttemp.txt'
		writeInFile(source, playlistStr)
		ffmpeg.generateVideo(source, tag + '_generated'+'.mkv')
		ffmpeg.generateGif(tag + '_generated'+'.mkv', tag + '_generated'+'.gif',-1,-1,-1)
		// Files.delete(Paths.get(source))
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
	
	/*
	 * Génére une vidéo au format .mkv par appels ffmpeg succéssifs, correspondant à une liste de VideoDescription précédemment générée aléatoirement 
	 * 
	 *  !! fonction dépréciée !!
	 */
	def String generateFromVideoDescriptions(List<VideoDescription> l) {
		val playlist = newArrayList()
		for (VideoDescription v: l) {
			var newLoc = tag + "_" + v.videoid + ".mkv"
			ffmpeg.copy(v.location, newLoc)
			if (v.text !== null){
				if (v.text.content !== "") {
					val loc = newLoc
					newLoc = 'text_filtered_' + loc
					ffmpeg.generateVideoFilteredWithText(v, loc, newLoc)
				}
			}
			if (v.filter instanceof FlipFilter) {
				println("FLIPFILTER")
				val loc = newLoc
				newLoc = 'flip_filtered_' + loc
				switch ((v.filter as FlipFilter).orientation) {
					case 'h' : { ffmpeg.applyFilterFilpH(loc, newLoc)}
					case 'horizontal' : {ffmpeg.applyFilterFilpH(loc, newLoc)}
					case 'v' : {ffmpeg.applyFilterFilpV(loc, newLoc)}
					case 'vertical' : {ffmpeg.applyFilterFilpV(loc, newLoc)}
					default : {}
				}
				
			}
			if (v.filter instanceof NegateFilter) {
				val loc = newLoc
				newLoc = 'negate_filtered_' + loc
				ffmpeg.applyFilterNegate(loc, newLoc)
			}
			if (v.filter instanceof BlackWhiteFilter) {
				val loc = newLoc
				newLoc = 'bw_filtered_' + loc				
				ffmpeg.applyFilterBN(loc, newLoc)
			}
			playlist.add("file '"  + newLoc + "'"+ " duration "+ v.duration + " inpoint " + "0")
		}
		var playlistStr = ""
		for (String pl : playlist)
			playlistStr += pl + "\n"
		return playlistStr
	}
	
	

	/**
	 * Fonction pour générer l'ensemble des vignettes par média de la spécification
	 */
	def void generateThumbnails() {
		// and then visit the model
		// eg access video sequences:
		var index = 0; 
		for (Media media: videoGen.medias) {
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq ) {
					val desc =  video.description
					video.description.duration = ffmpeg.videoDuration(desc.location) + 0 
					ffmpeg.generateThumbnail(index, desc.location)
					numberOfThumbnail ++
					index ++
					allVideos.add(desc)		
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					video.description.duration = ffmpeg.videoDuration(desc.location) + 0 
					ffmpeg.generateThumbnail(index, desc.location)
					numberOfThumbnail ++
					index ++
					allVideos.add(desc)		
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					for (VideoDescription videodesc: alts.videodescs) {
						val desc = videodesc
						videodesc.duration = ffmpeg.videoDuration(desc.location) + 0 
						ffmpeg.generateThumbnail(index, desc.location)
						numberOfThumbnail ++
						index ++
						allVideos.add(desc)		
					}
				}	
			}
			
		}
		// videoGenUpdated = videoGen
	}


	/**
	 * Récupère la durée de la potentielle plus longue variante de vidéo
	 */
	def int getLongestVar() {
		// and then visit the model
		// eg access video sequences: 
		var duration = 0
		for (Media media: videoGen.medias) {
			
			if (media instanceof VideoSeq) {			
				val video = (media as VideoSeq)
				
				if (video instanceof MandatoryVideoSeq) {
					val desc =  video.description
					duration += ffmpeg.videoDuration(desc.location)
							
						
				}
				if (video instanceof OptionalVideoSeq) {
					val desc =  video.description
					duration += ffmpeg.videoDuration(desc.location)
						
				}
				if (video instanceof AlternativeVideoSeq) {
					val alts = (video as AlternativeVideoSeq)
					
					var longest = -1;
					var index = 0;
					var longestDuration = 0;
					for (VideoDescription videodesc: alts.videodescs) {
						val durationTemp = ffmpeg.videoDuration(videodesc.location)
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
	
	
	
	/** 
	 * Génére l'ensemble des variantes possible sous forme d'une liste de liste de VideoDescription
	 */
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


	/**
	 * 
	 * Fonction d'analyse, écrivant le résultat dans un fichier CSV
	 * 
	 */
	def void writeStatsToCsv() {
		if (!allVideos.isEmpty()) {
			var PrintWriter pw = null
			var File file = new File("ressources/stats/"+ tag + "_"+"vars_size.csv")
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
	        var format = ".mkv"
	        var gif = ".gif"
	        
			for(List<VideoDescription> v : allVars) {
				var size = 0
				fl.append(index + "")
				fl.append(",")
				
				val playlistTemp = generateFromVideoDescriptions(v)
				Utils.writeInFile(source, playlistTemp)
				ffmpeg.generateVideo(source, target+index+format)

				ffmpeg.generateGif(target+index+format, target+index+gif, -1, -1, -1)
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
				
				var pathVarGif = Paths.get(PATH_GEN_RELATIVE + 'gif/' + target+index+gif)
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
	
	
	/**
	 * 
	 * Fonction générant un fichier HTML, à partir d'un template, pour afficher l'ensemble des vignettes précédemment générées
	 * 
	 */
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

	

	def boolean hasVideo(List<VideoDescription> l, VideoDescription v){
		for (VideoDescription vTemp: l) {
			if (vTemp.videoid == v.videoid) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	def boolean idExists(String id) {
		for (String s : listId) {
			if (s == id) return true
		}
		return false
	}

}