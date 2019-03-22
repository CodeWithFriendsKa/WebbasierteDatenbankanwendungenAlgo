
import java.util.ArrayList;

public class MainTest {
	
	public static void main(String[] args) {
		
		ArrayList<SpielerModel> spieler = TestList.testListSpieler();
		ArrayList<TrainerModel> trainer = TestList.testListTrainer();
		ArrayList<Platz> plaetze = TestList.testListPlaetze();
		AlgoBibliothek.ErstellePlan(spieler, trainer, plaetze);
		ArrayList<ZeitEndModel> vorMoeg = AlgoBibliothek.Algorythmus(spieler, trainer, plaetze);
		
		System.out.println("anz moeglichkeiten: " + AlgoBibliothek.gruppenZuordnen(spieler, trainer, plaetze).size());
		
		for (int i = 0; i < vorMoeg.size(); i++) {
			System.out.println("Stunde: " + ZeitenModel.intToString(vorMoeg.get(i).getZeit()) + "\n");
			
			System.out.println("Name des Trainers: " + vorMoeg.get(i).getTrainer().getName());
			System.out.println("Name des Platzes: " + vorMoeg.get(i).getPlatz().getName() + "\n");
			
			System.out.println("Name der Gruppe: " + vorMoeg.get(i).getGruppe().getName());
			for (int k = 0; k < vorMoeg.get(i).getGruppe().getSpieler().size(); k++) {
				System.out.println("Name von Spieler " + (k + 1) + " :" + vorMoeg.get(i).getGruppe().getSpieler().get(k).getName());
			}
			
			System.out.println("-- -- -- -- --");
			
		}
		
		System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	
		for (int i = 0; i <  AlgoBibliothek.alleMoeglichenTrainingsZeiten(plaetze,trainer).length; i++) {
			System.out.println("" + AlgoBibliothek.alleMoeglichenTrainingsZeiten(plaetze,trainer)[i]);
			
		}
	
	}
}
