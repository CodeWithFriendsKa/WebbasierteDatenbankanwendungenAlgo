
import java.util.ArrayList;

public class MainTest {

	public static void main(String[] args) {


		ArrayList<Spieler> spieler = TestList.testListSpieler();
		ArrayList<Trainer> trainer = TestList.testListTrainer();
		ArrayList<Platz> plaetze = TestList.testListPlätze();
		ArrayList<Zeiten> test = AlgoBibliothek.ErstellePlan(spieler, trainer, plaetze);

		System.out.println("anz möglichkeiten " + AlgoBibliothek.getVorLaeufigeMoeglichkeiten().size());
		
		for(int i=0;i<test.size();i++) {
			if(test.get(i)==null)continue;
			System.out.println("Trainer:");
			for(int j=0;j<test.get(i).getTrainer().size();j++) {
				System.out.println(test.get(i).getTrainer().get(j).getName());
				System.out.println("Pausen:");
				for (int z = 0; z < test.get(i).getTrainer().get(j).getPauseProTag().size(); z++) {
					for (int k = 0; k < test.get(i).getTrainer().get(j).getPauseProTag().get(z).size(); k++) {
						System.out.println(test.get(i).getTrainer().get(j).getPauseProTag().get(z).get(k).getVonZeit() + " - " + test.get(i).getTrainer().get(j).getPauseProTag().get(z).get(k).getBisZeit());
					}
				}
				
				System.out.println("\nPausen Ende\n");
				
				System.out.println("Min Anzahl Training");
				
				
				for (int z = 0; z < test.get(i).getTrainer().get(j).getMinTrainingProTag().size(); z++) {
					System.out.println("Tag "+(z+1)+":"+test.get(i).getTrainer().get(j).getMinTrainingProTag().get(z)+" Stunden");
				}
				
				System.out.println("\nMin Anzahl Training Ende\n");
			}
			System.out.println("Plätze:");
			for(int j=0;j<test.get(i).getPlatz().size();j++) {
				System.out.println(test.get(i).getPlatz().get(j).getName());
				
			}
			System.out.println();
			System.out.println("Gruppen:");
			for(int j=0;j<test.get(i).getGruppen().size();j++) {
				
				System.out.println("Gruppen "+(j+1));
				System.out.println(Zeiten.intToString(test.get(i).getGruppen().get(j).getStunde())+"/"+test.get(i).getGruppen().get(j).getName());

				for(int z=0;z<test.get(i).getGruppen().get(j).getSpieler().size();z++) {
					System.out.println(		test.get(i).getGruppen().get(j).getSpieler().get(z).getName());
				}
				System.out.println();

			}
			System.out.println();
			System.out.println("------------------------------------------------------------------");
		}
		System.out.println(	"Spieler ohne Zuo:");
		for(int i=0;i<AlgoBibliothek.getSpielerKeineGruppeZuo().size();i++) {

			System.out.println(	AlgoBibliothek.getSpielerKeineGruppeZuo().get(i).getName());
		}

		System.out.println(	"Zweiergruppen ohne Zuo:");
		for(int i=0;i<AlgoBibliothek.getZweierGruppe().size();i++) {

			System.out.println(	AlgoBibliothek.getZweierGruppe().get(i).getName());
		}
		
		System.out.println(	"Spieler ohne Trainer:");
		for(int i=0;i<AlgoBibliothek.getSpielerHatKeinTrainer().size();i++) {

			System.out.println(	AlgoBibliothek.getSpielerHatKeinTrainer().get(i).getName());
		}

		/*System.out.println(	"Zweier Gruppen:");
		for(int i=0;i<AlgoBibliothek.getZweierGruppe().size();i++) {

			System.out.println(	AlgoBibliothek.getZweierGruppe().get(i).getName());
		}
		 */
	}

}
