import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.xtext.example.mydsl.videoGen.*;

public class VideoGenTransfo {
	
	private String uri;
	private List<List<String>> allConfs ;
	private EList<Media> medias;
	
	public VideoGenTransfo(String u) {
		uri = u;
		allConfs = new ArrayList<List<String>>();
	}
	
	public void transfo() {
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI(uri));
		EList<Media> medias = videoGen.getMedias();
		for (Media media : medias) {
			if (media instanceof VideoSeq) {				
				VideoSeq video = (VideoSeq) media;				
					
					if (video instanceof MandatoryVideoSeq) {
						String id = ((MandatoryVideoSeq)video).getDescription().getVideoid();
						allConfs = addVideoIdToAllConfigurations(id, allConfs);
					}	
					
					if (video instanceof OptionalVideoSeq) {
						for (List<String> partialConf : allConfs) {
							List<String> nPartialConf = new ArrayList<String>();
							nPartialConf.addAll(partialConf);
							nPartialConf.add(((OptionalVideoSeq)video).getDescription().getVideoid()); 
							allConfs.add(nPartialConf);	
						}
					}
					if (video instanceof AlternativeVideoSeq) {
						for (List<String> partialConf : allConfs) {
							 AlternativeVideoSeq alt = (AlternativeVideoSeq) video;
							for (VideoDescription desc : alt.getVideodescs()) {
								List<String> nPartialConf = new ArrayList<String>();
								nPartialConf.addAll(partialConf);
								nPartialConf.add(desc.getVideoid()); 
								allConfs.add(nPartialConf);							
							}
						}
					}
				
			}
			
			
		}
	}

	private List<List<String>> addVideoIdToAllConfigurations(String id, List<List<String>> allConfs) {
		
		if (allConfs.isEmpty()) {
			List<String> nPartialConf = new ArrayList<String>();
			nPartialConf.add(id);
			allConfs.add(nPartialConf);
		}
		else {	
			for (List<String> partialConf : allConfs) {
				partialConf.add(id); 
			}	
		}
		List<List<String>> nAllConfs = new ArrayList<List<String>>(allConfs);
		return  nAllConfs;
	}

}
