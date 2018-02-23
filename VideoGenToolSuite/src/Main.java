
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Execution jar : ");
		for (String a: args) {
			System.out.println(a);
		}
		String videogenSpecification = args[0];
		VideoGen vg = new VideoGen(videogenSpecification);
				vg.clean();
				vg.generate();
				System.out.println((vg.nbVariantes()));
				System.out.println(vg.nbVariantes() + " " +vg.generateAllVars().size());
				vg.generateThumbnails();
				vg.writeStatsToCsv();
				vg.generateHtml();
				
	}

}
