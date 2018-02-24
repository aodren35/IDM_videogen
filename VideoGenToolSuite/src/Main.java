import java.util.List;

import org.xtext.example.mydsl.videoGen.VideoDescription;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Execution jar : ");
		for (String a: args) {
			System.out.println(a);
		}
		String videogenSpecification = args[0];
		String init = args[1];
		if (!init.equals("init")) {
				VideoGen vg = new VideoGen(videogenSpecification);
				vg.clean();
				List<VideoDescription> rdVideo = vg.generateRandomVideo();
				vg.generateAndCrushFromVideoDescriptions(rdVideo);
		} else {
			VideoGen vg = new VideoGen(videogenSpecification);
			vg.clean();
			vg.generateThumbnails();
		}
				
	}

}
