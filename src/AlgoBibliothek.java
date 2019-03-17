import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlgoBibliothek {

	private static ArrayList<Spieler> spielerHatKeinTrainer = new ArrayList<Spieler>();

	public static ArrayList<Spieler> getSpielerHatKeinTrainer() {
		return spielerHatKeinTrainer;
	}

	private static ArrayList<Spieler> spielerKeineGruppeZuo = new ArrayList<Spieler>();

	public static ArrayList<Spieler> getSpielerKeineGruppeZuo() {
		return spielerKeineGruppeZuo;
	}

	private static ArrayList<Spieler> zweierSpielerKeineGruppeZuo = new ArrayList<Spieler>();

	public static ArrayList<Spieler> getZweierSpielerKeineGruppeZuo() {
		return zweierSpielerKeineGruppeZuo;
	}

	private static ArrayList<Gruppe> zweierGruppe = new ArrayList<Gruppe>();

	public static ArrayList<Gruppe> getZweierGruppe() {
		return zweierGruppe;
	}

	private static ArrayList<ArrayList<Zeiten>> vorLaeufigeMoeglichkeiten = new ArrayList<ArrayList<Zeiten>>();

	public static ArrayList<ArrayList<Zeiten>> getVorLaeufigeMoeglichkeiten() {
		return vorLaeufigeMoeglichkeiten;
	}

	private static ArrayList<Zeiten> zeitenKopie = new ArrayList<Zeiten>();

	private static ArrayList<Zeiten> zeiten = new ArrayList<Zeiten>();

	@SuppressWarnings("unchecked")
	public static ArrayList<Zeiten> ErstellePlan(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
			ArrayList<Platz> plaetze) {

		ArrayList<ArrayList<Gruppe>> gruppen = SpielerGruppeZuo(spieler);
		gruppen = GruppenLoeschen(gruppen);
		gruppen = GruppenSplitten(gruppen);
		gruppen = KontrolleSpielerBereitsZuo(gruppen);
		gruppen = WiederBefuellungZweierGruppe(gruppen);
		gruppen = KontrolleSpielerBereitsZuo(gruppen);// nochmal wegen Spieler ohne Zuo
		zeiten = PruefePlatzUndTrainer(plaetze, trainer, gruppen);
		zeiten = LoscheZeitenOhneTrainer(zeiten);
		spielerHatKeinTrainer = SpielerOhneZuo(spieler, zeiten);

		zeitenKopie = (ArrayList<Zeiten>) zeiten.clone();

		Algorythmus(spieler, trainer, plaetze);

		return zeiten;

	}

	private static void Algorythmus(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer, ArrayList<Platz> plaetze) {

		SpielerOhneZuo(spieler, zeitenKopie);
		SpielerHatFuerAlleTermineTraining(spieler, trainer, plaetze);
		TrainerMinTermineTraining(spieler, trainer, plaetze);
		TrainerPauseZwischenTrainingszeitenProTag(spieler, trainer, plaetze);

		BesteZuo();
		berechneHash();

	}

	@SuppressWarnings("unchecked")
	private static void berechneHash() {
		ArrayList<ArrayList<Zeiten>> dummy = (ArrayList<ArrayList<Zeiten>>) getVorLaeufigeMoeglichkeiten().clone();

		for (int i = 0; i < dummy.size(); i++) {
			for (int j = 0; j < dummy.get(i).size(); j++) {
				Hash dummyHash = null;
				dummy.get(i).get(j).setHash(dummyHash);
			}

		}

	}

	@SuppressWarnings("unchecked")
	private static void BesteZuo() {

		for (int z = 0; z < 7; z++) {

			for (int i = z * 24; i < zeiten.size(); i++) {

				if (zeiten.get(z * 24) == null)
					continue;
				for (int j = 0; j < Zeiten.getGroesstegruppe(); j++) {
					ArrayList<Zeiten> dummy = GruppenZuo(i, j);
					vorLaeufigeMoeglichkeiten.add(dummy);
				}
			}
			for (int i = 0; i < z * 24; i++) {
				if (zeiten.get(z * 24) == null)
					continue;
				for (int j = 0; j < Zeiten.getGroesstegruppe(); j++) {
					ArrayList<Zeiten> dummy = GruppenZuo(i, j);
					vorLaeufigeMoeglichkeiten.add(dummy);
				}
			}
		}

		ArrayList<Spieler> dummy = new ArrayList<>();
		dummy = (ArrayList<Spieler>) Spieler.getAlleSpieler().clone();
		for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
			dummy.remove(spielerHatKeinTrainer.get(i));
		}

		for (int i = vorLaeufigeMoeglichkeiten.size() - 1; i >= 0; i--) {
			if (!SpielerOhneZuo(dummy, vorLaeufigeMoeglichkeiten.get(i)).isEmpty())
				vorLaeufigeMoeglichkeiten.remove(i);
		}

	}

	@SuppressWarnings({ "unchecked" })
	private static ArrayList<Zeiten> GruppenZuo(int var, int var2) {
		// ArrayList dummy = new ArrayList<Zeiten>();
		ArrayList<Spieler> copyAlleSpieler = (ArrayList<Spieler>) Spieler.getAlleSpieler().clone();
		for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
			copyAlleSpieler.remove(spielerHatKeinTrainer.get(i));
		}

		ArrayList<Spieler> copyAlleSpielerMehrTraining = (ArrayList<Spieler>) Spieler.getAlleSpielerMehrTraining()
				.clone();
		for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
			copyAlleSpielerMehrTraining.remove(spielerHatKeinTrainer.get(i));
		}

		ArrayList<Zeiten> zeitendummy = new ArrayList<Zeiten>();
		for (int i = var; i < zeiten.size(); i++) {
			if (zeiten.get(i) != null)
				outerloop: for (int j = var2; j < zeiten.get(i).getGruppen().size(); j++) {

					if (j - var2 > zeiten.get(i).getTrainer().size())
						break;

					for (int z = 0; z < zeiten.get(i).getGruppen().get(j).getSpieler().size(); z++) {

						if (!copyAlleSpieler.contains(zeiten.get(i).getGruppen().get(j).getSpieler().get(z))
								|| !copyAlleSpielerMehrTraining
										.contains(zeiten.get(i).getGruppen().get(j).getSpieler().get(z)))
							continue outerloop;

						zeitendummy.add(zeiten.get(i));

						if (copyAlleSpieler.contains(zeiten.get(i).getGruppen().get(j).getSpieler().get(z))) {
							copyAlleSpieler.remove(zeiten.get(i).getGruppen().get(j).getSpieler().get(z));
						} else {
							copyAlleSpielerMehrTraining.remove(zeiten.get(i).getGruppen().get(j).getSpieler().get(z));
						}
					}

				}
		}

		return zeitendummy;

	}

	private static void TrainerPauseZwischenTrainingszeitenProTag(ArrayList<Spieler> spieler,
			ArrayList<Trainer> trainer, ArrayList<Platz> plaetze) {

		for (int i = 0; i < trainer.size(); i++) {

			ArrayList<ArrayList<Pause>> pausen = new ArrayList<ArrayList<Pause>>();
			ArrayList<Pause> dummy = new ArrayList<Pause>();
			ArrayList<Integer> training = new ArrayList<Integer>();
			int trainingDummy = 0;
			int pause = 0;
			boolean pauseAktiv = false;
			for (int j = 0; j < zeiten.size(); j++) {
				if (pauseAktiv)
					pause++;
				if (j != 0 && j % 24 == 0) {
					pause = 0;
					pauseAktiv = false;
					pausen.add(dummy);
					dummy = new ArrayList<Pause>();

					training.add(trainingDummy);
					trainingDummy = 0;

				}
				if (zeiten.get(j) == null)
					continue;

				if (zeiten.get(j).getTrainer().contains(trainer.get(i))) {
					trainingDummy++;
					if (pause == 1) {
						pauseAktiv = false;

					}
					if (pauseAktiv) {
						pause--;
						if (pause > 0) {
							dummy.add(new Pause(j - pause + 1, j));
							pauseAktiv = false;
							pause = 0;
						}
					} else {
						pauseAktiv = true;
						pause = 0;
					}

				}
			}
			trainer.get(i).setMinTrainingProTag(training);
			trainer.get(i).setPauseProTag(pausen);
			LoscheGewolltePausen(trainer.get(i));
		}

	}

	private static void LoscheGewolltePausen(Trainer trainer) {
		for (int i = 0; i < trainer.getPauseProTag().size(); i++) {
			for (int j = trainer.getPauseProTag().get(i).size() - 1; j >= 0; j--) {

				for (int z = trainer.getPauseProTag().get(i).get(j).getVonZeit() - 1; z <= trainer.getPauseProTag()
						.get(i).get(j).getBisZeit(); z++) {

					if (z > trainer.getZeiten().length)
						break;
					if (trainer.getZeiten()[z] == 0) {
						trainer.getPauseProTag().get(i).remove(j);
						break;
					}

				}
			}
		}

	}

	private static void TrainerMinTermineTraining(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
			ArrayList<Platz> plaetze) {
		for (int i = 0; i < trainer.size(); i++) {
			int zaehlerAnzTraining = 0;
			for (int j = 0; j < zeiten.size(); j++) {
				if (zeiten.get(j) != null)
					if (zeiten.get(j).getTrainer().contains(trainer.get(i))) {
						zaehlerAnzTraining++;
					}

			}

			trainer.get(i).setAktAnzTraining(zaehlerAnzTraining);
			if (trainer.get(i).getMinAnzTraining() < zaehlerAnzTraining) {
				continue;
			}
		}

	}

	private static void SpielerHatFuerAlleTermineTraining(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
			ArrayList<Platz> plaetze) {
		for (int i = 0; i < spieler.size(); i++) {
			if (spieler.get(i).getTrainingsAnzahl() == 1)
				continue;
			int var = 0;
			int zeit = 0;
			for (int j = 0; j < zeiten.size(); j++) {
				if (zeiten.get(j) != null)
					for (int z = 0; z < zeiten.get(j).getGruppen().size(); z++) {

						if (zeiten.get(j).getGruppen().get(z).getSpieler().contains(spieler.get(i))) {

							if (zeit + 14 > zeiten.get(j).getZeit())
								continue;
							var++;
							zeit = zeiten.get(j).getZeit();
						}
					}

			}
			if (var < spieler.get(i).getTrainingsAnzahl()) {
				spieler.get(i).setTrainingsAnzahlAktuell(var);
			}
			if (var > spieler.get(i).getTrainingsAnzahl()) {
				continue;
			}
		}

	}

	private static ArrayList<Spieler> SpielerOhneZuo(ArrayList<Spieler> spieler, ArrayList<Zeiten> zeiten) {

		ArrayList<Spieler> dummy = new ArrayList<>();
		outerloop: for (int i = 0; i < spieler.size(); i++) {
			zeiten.size();

			for (int j = zeiten.size() - 1; j >= 0; j--) {
				if (zeiten.get(j) != null)
					for (int z = 0; z < zeiten.get(j).getGruppen().size(); z++) {

						if (zeiten.get(j).getGruppen().get(z).getSpieler().contains(spieler.get(i)))
							continue outerloop;

					}

			}
			dummy.add(spieler.get(i));
		}

		return dummy;
	}

	private static ArrayList<Zeiten> LoscheZeitenOhneTrainer(ArrayList<Zeiten> zeiten) {
		for (int i = zeiten.size() - 1; i >= 0; i--) {
			if (zeiten.get(i).getTrainer().isEmpty()) {
				zeiten.set(i, null);
			}
		}
		return zeiten;
	}

	private static ArrayList<ArrayList<Gruppe>> SpielerGruppeZuo(ArrayList<Spieler> spieler) {
		// Die Funktion erstellt alle Moeglichen Untergruppen und Teilt diesen alle
		// Moeglichen Spieler zu

		ArrayList<ArrayList<Gruppe>> gruppen = new ArrayList<ArrayList<Gruppe>>();

		for (int i = 0; i < Spieler.getZeitenArraygroesse(); i++) {

			ArrayList<Gruppe> untergruppen = new ArrayList<Gruppe>();

			untergruppen.add(new Gruppe("U6", i + 1, 6, 0, 6, 'O'));
			untergruppen.add(new Gruppe("U10 SpielStaerke 7 - 9", i + 1, 5, 7, 9, 'O'));
			untergruppen.add(new Gruppe("U10 SpielStaerke 8 - 10", i + 1, 5, 8, 10, 'O'));

			for (int j = 11; j < 20; j++) {
				untergruppen.add(new Gruppe("M SpielStaerke " + j + " - " + (j + 2), i + 1, 5, j, (j + 2), 'm'));
				untergruppen.add(new Gruppe("W SpielStaerke " + j + " - " + (j + 2), i + 1, 5, j, (j + 2), 'w'));
			}

			gruppen.add(untergruppen);

		}

		for (int i = 0; i < spieler.size(); i++) {

			int[] dummy = spieler.get(i).getZeiten();

			for (int j = 0; j < Spieler.getZeitenArraygroesse(); j++) {

				if (dummy[j] == 1) {

					ArrayList<Gruppe> untergruppen = gruppen.get(j);
					for (int z = 0; z < untergruppen.size(); z++) {

						if (spieler.get(i).verrechneSpielerStaerkeMitAlter() >= gruppen.get(j).get(z)
								.getSpielerStaerkeVon()
								&& spieler.get(i).verrechneSpielerStaerkeMitAlter() <= gruppen.get(j).get(z)
										.getSpielerStaerkeBis()) {

							if (spieler.get(i).verrechneSpielerStaerkeMitAlter() < 11) {
								gruppen.get(j).get(z).getSpieler().add(spieler.get(i));
							} else {
								if (spieler.get(i).getGeschlecht() == gruppen.get(j).get(z).getGeschlecht())
									gruppen.get(j).get(z).getSpieler().add(spieler.get(i));
							}
						}

					}
				}
			}
		}

		return gruppen;
	}

	private static ArrayList<ArrayList<Gruppe>> GruppenLoeschen(ArrayList<ArrayList<Gruppe>> gruppen) {
		// Diese Funktion entfernt die nicht benoetigten Gruppen aus der Gesammtliste
		// also die Untergruppen mit 0/1/2 Spielern

		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = gruppen.get(i).size() - 1; j >= 0; j--) {
				if (gruppen.get(i).get(j).getSpieler().isEmpty()) {
					gruppen.get(i).remove(j);
					continue;
				}
				if (gruppen.get(i).get(j).getSpieler().size() == 1) {
					if (!spielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(0)))
						spielerKeineGruppeZuo.add(gruppen.get(i).get(j).getSpieler().get(0));
					gruppen.get(i).remove(j);
					continue;
				}
				if (gruppen.get(i).get(j).getSpieler().size() == 2) {
					if (!zweierSpielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(0)))
						zweierSpielerKeineGruppeZuo.add(gruppen.get(i).get(j).getSpieler().get(0));
					zweierGruppe.add(gruppen.get(i).get(j));
					gruppen.get(i).remove(j);
					continue;
				}

			}
		}

		return gruppen;
	}

	/*
	 * private static ArrayList<ArrayList<Gruppe>>
	 * GruppenSplitten(ArrayList<ArrayList<Gruppe>> gruppen) { // Die Funktion
	 * trennt erstellt Untergruppen (mit Spielern) die, die vorgegebene //
	 * Gruppengroeﬂe ueberschreiten
	 * 
	 * for (int i = 0; i < gruppen.size(); i++) { for (int j = gruppen.get(i).size()
	 * - 1; j >= 0; j--) {
	 * 
	 * if (gruppen.get(i).get(j).getSpieler().size() <=
	 * gruppen.get(i).get(j).getMaxAnzahlSpieler()) continue; // folgend wird die
	 * neue Gruppengroeﬂe berechnet
	 * 
	 * int anzahlGruppen = gruppen.get(i).get(j).getSpieler().size() /
	 * gruppen.get(i).get(j).getMaxAnzahlSpieler(); int restLetzteGruppe =
	 * gruppen.get(i).get(j).getSpieler().size() %
	 * gruppen.get(i).get(j).getMaxAnzahlSpieler();
	 * 
	 * if (restLetzteGruppe > 0) { anzahlGruppen++; }
	 * 
	 * int spielerProGruppe = gruppen.get(i).get(j).getSpieler().size() /
	 * anzahlGruppen; int restSpielerProGruppe =
	 * gruppen.get(i).get(j).getSpieler().size() % anzahlGruppen;
	 * 
	 * // gruppen.get(i).get(j).getMaxAnzahlSpieler(); Gruppe gruppe =
	 * gruppen.get(i).get(j);
	 * 
	 * for (int z = 1; z < anzahlGruppen; z++) {
	 * 
	 * Gruppe dummy = new Gruppe((z + 1) + "/" + gruppe.getName(),
	 * gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
	 * gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(),
	 * gruppe.getGeschlecht());
	 * 
	 * // die Spieler werden neu eingeordnet int restHinzufuegen = 0; if
	 * (restSpielerProGruppe > 0) { restHinzufuegen++; }
	 * 
	 * for (int k = spielerProGruppe + restHinzufuegen; k > 0; k--) {
	 * 
	 * dummy.getSpieler().add(gruppe.getSpieler().get(k));
	 * gruppen.get(i).get(j).getSpieler().remove(k); } gruppen.get(i).add(dummy);
	 * 
	 * restSpielerProGruppe--;
	 * 
	 * } gruppen.get(i).get(j).setName("1/" + gruppe.getName());
	 * 
	 * } } return gruppen; }
	 * 
	 * 
	 */

	private static int calcGruppenAnz(int gruppenGroesse, Gruppe gruppe) {
		int zaehler = 0;

		if (gruppe != null) {
			int dummy2 = (int) Math.pow(gruppe.getSpieler().size(), 2);

			String[] dummy = new String[dummy2];

			for (int i = 1; i < dummy2; i++) {
				dummy[i] = Integer.toBinaryString(i);
			}
			for (int i = 0; i < dummy.length; i++) {
				if (dummy[i] == null)
					continue;
				long dummyZaehler = dummy[i].chars().filter(ch -> ch == '1').count();
				if (dummyZaehler == gruppenGroesse)
					zaehler++;
			}

		}

		return zaehler;
	}

	private static ArrayList<Gruppe> setDreierGruppen(Gruppe gruppe) {
		int var1 = 0, var2 = 1, var3 = 2;
		int sVar1 = 0, sVar2 = 1, sVar3 = 2;

		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		for (int i = 0; i < calcGruppenAnz(3, gruppe); i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}

		for (int i = 0; i < dummy.size(); i++) {

			if (var3 < gruppe.getSpieler().size()) {
				dummy.get(i).addSpieler(gruppe.getSpieler().get(var1));
				dummy.get(i).addSpieler(gruppe.getSpieler().get(var2));
				dummy.get(i).addSpieler(gruppe.getSpieler().get(var3));

				var3++;
			} else {
				if (var2 < gruppe.getSpieler().size() - 1) {
					sVar3++;
					var3 = sVar3;
					var2++;
					dummy.get(i).addSpieler(gruppe.getSpieler().get(var1));
					dummy.get(i).addSpieler(gruppe.getSpieler().get(var2));
					dummy.get(i).addSpieler(gruppe.getSpieler().get(var3));

				} else {
					if (var1 < gruppe.getSpieler().size() - 2) {
						sVar2++;
						var2 = sVar2;
						var1++;
						dummy.get(i).addSpieler(gruppe.getSpieler().get(var1));
						dummy.get(i).addSpieler(gruppe.getSpieler().get(var2));
						dummy.get(i).addSpieler(gruppe.getSpieler().get(var3));

					} else {
						sVar1++;
						var1 = sVar1;
					}
				}
			}
		}

		return dummy;

	}

	private static ArrayList<Gruppe> setViererGruppen(Gruppe gruppe) {
		int var1 = 0, var2 = 1, var3 = 2, var4 = 3;
		int sVar1 = 0, sVar2 = 1, sVar3 = 2, sVar4 = 3;

		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		for (int i = 0; i < calcGruppenAnz(4, gruppe); i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}

		for (int i = 0; i < dummy.size(); i++) {

			dummy.get(i).addSpieler(gruppe.getSpieler().get(var1));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var2));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var3));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var4));

			if (var4 < gruppe.getSpieler().size())
				var4++;
			else {
				sVar4++;
				var4 = sVar4;
				if (var3 < gruppe.getSpieler().size() - 1)
					var3++;
				else {
					sVar3++;
					var3 = sVar3;
					if (var2 < gruppe.getSpieler().size() - 2)
						var2++;
					else {
						sVar2++;
						var2 = sVar2;
						if (var1 < gruppe.getSpieler().size() - 3)
							var1++;
						else if (var1 > gruppe.getSpieler().size() - 3)
							continue;
						else {
							sVar1++;
							var1 = sVar1;
						}
					}
				}

			}
		}

		return dummy;

	}

	private static ArrayList<Gruppe> setFuenferGruppen(Gruppe gruppe) {
		int var1 = 0, var2 = 1, var3 = 2, var4 = 3, var5 = 4;
		int sVar1 = 0, sVar2 = 1, sVar3 = 2, sVar4 = 3, sVar5 = 4;

		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		for (int i = 0; i < calcGruppenAnz(5, gruppe); i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}

		for (int i = 0; i < dummy.size(); i++) {

			dummy.get(i).addSpieler(gruppe.getSpieler().get(var1));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var2));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var3));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var4));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var5));

			if (var5 < gruppe.getSpieler().size())
				var5++;
			else {
				sVar5++;
				var5 = sVar5;
				if (var4 < gruppe.getSpieler().size() - 1)
					var4++;
				else {
					sVar4++;
					var4 = sVar4;
					if (var3 < gruppe.getSpieler().size() - 2)
						var3++;
					else {
						sVar3++;
						var3 = sVar3;
						if (var2 < gruppe.getSpieler().size() - 3)
							var2++;
						else {
							sVar2++;
							var2 = sVar2;
							if (var1 < gruppe.getSpieler().size() - 4)
								var1++;
							else if (var1 > gruppe.getSpieler().size() - 4)
								continue;
							else {
								sVar1++;
								var1 = sVar1;
							}
						}
					}
				}
			}

		}

		return dummy;
	}

	private static ArrayList<Gruppe> setSechserGruppen(Gruppe gruppe) {
		int var1 = 0, var2 = 1, var3 = 2, var4 = 3, var5 = 4, var6 = 5;
		int sVar1 = 0, sVar2 = 1, sVar3 = 2, sVar4 = 3, sVar5 = 4, sVar6 = 5;

		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		for (int i = 0; i < calcGruppenAnz(6, gruppe); i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}

		for (int i = 0; i < dummy.size(); i++) {

			dummy.get(i).addSpieler(gruppe.getSpieler().get(var1));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var2));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var3));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var4));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var5));
			dummy.get(i).addSpieler(gruppe.getSpieler().get(var6));

			if (var6 < gruppe.getSpieler().size())
				var6++;
			else {
				sVar5++;
				var6 = sVar6;
				if (var5 < gruppe.getSpieler().size() - 1)
					var6++;
				else {
					sVar5++;
					var5 = sVar5;
					if (var4 < gruppe.getSpieler().size() - 2)
						var4++;
					else {
						sVar4++;
						var4 = sVar4;
						if (var3 < gruppe.getSpieler().size() - 3)
							var3++;
						else {
							sVar3++;
							var3 = sVar3;
							if (var2 < gruppe.getSpieler().size() - 4)
								var2++;
							else {
								sVar2++;
								var2 = sVar2;
								if (var1 < gruppe.getSpieler().size() - 5)
									var1++;
								else if (var1 > gruppe.getSpieler().size() - 5)
									continue;
								else {
									sVar1++;
									var1 = sVar1;
								}
							}
						}
					}
				}
			}

		}

		return dummy;
	}

	private static ArrayList<ArrayList<Gruppe>> GruppenSplitten(ArrayList<ArrayList<Gruppe>> gruppen) {

		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = gruppen.get(i).size() - 1; j >= 0; j--) {

				if (gruppen.get(i).get(j).getSpieler().size() <= 3)
					continue;

				else if (gruppen.get(i).get(j).getSpieler().size() == 4) {
					ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
					dummy = setViererGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
				}

				else if (gruppen.get(i).get(j).getSpieler().size() == 5) {
					ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
					dummy = setViererGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
					dummy = setFuenferGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
				}

				else if (gruppen.get(i).get(j).getMaxAnzahlSpieler() == 6) {
					if (gruppen.get(i).get(j).getSpieler().size() == 6) {
						ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setViererGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setFuenferGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setSechserGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
					}

				}

				Gruppe gruppe = gruppen.get(i).get(j);

				gruppen.get(i).get(j).setName("1/" + gruppe.getName());

			}
		}
		return gruppen;
	}

	private static ArrayList<ArrayList<Gruppe>> WiederBefuellungZweierGruppe(ArrayList<ArrayList<Gruppe>> gruppen) {
		// wenn in der Kontrollfunktion herraus kommt das ein Zweierspieler in keiner
		// anderen Gruppe ist werden seine Gruppen wieder in die Hauptgruppe aufgenommen

		if (zweierSpielerKeineGruppeZuo.isEmpty())
			return gruppen;

		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();

		for (int i = zweierSpielerKeineGruppeZuo.size() - 1; i >= 0; i--) {
			for (int j = zweierGruppe.size() - 1; j >= 0; j--) {

				if (zweierGruppe.get(j).getSpieler().contains(zweierSpielerKeineGruppeZuo.get(i))) {
					dummy.add(zweierGruppe.get(j));

				}

				zweierGruppe.remove(j);

			}

			zweierSpielerKeineGruppeZuo.remove(i);
		}
		gruppen.add(dummy);

		return gruppen;
	}

	private static ArrayList<ArrayList<Gruppe>> KontrolleSpielerBereitsZuo(ArrayList<ArrayList<Gruppe>> gruppen) {
		// Kontrolliert die Liste der Einzelspieler und Zweierspieler ob diese Spieler
		// in einer anderen Gruppe eingeteillt sind

		if (spielerKeineGruppeZuo.isEmpty() && zweierSpielerKeineGruppeZuo.isEmpty())
			return gruppen;
		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = gruppen.get(i).size() - 1; j >= 0; j--) {
				for (int k = 0; k < gruppen.get(i).get(j).getSpieler().size(); k++) {
					if (spielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(k)))
						spielerKeineGruppeZuo.remove(gruppen.get(i).get(j).getSpieler().get(k));
					if (zweierSpielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(k)))
						zweierSpielerKeineGruppeZuo.remove(gruppen.get(i).get(j).getSpieler().get(k));

					if (spielerKeineGruppeZuo.isEmpty() && zweierSpielerKeineGruppeZuo.isEmpty())
						return gruppen;
				}

			}
		}
		return gruppen;
	}

	private static ArrayList<Zeiten> PruefePlatzUndTrainer(ArrayList<Platz> plaetze,
			// Die Funktion Erstellt eine neue Liste in dieser werden alle Plaetze, Trainer
			// und Untergruppen verglichen und in die entsprechenden Felder eingeordnet

			ArrayList<Trainer> trainer, ArrayList<ArrayList<Gruppe>> gruppen) {

		for (int i = 0; i < Spieler.getZeitenArraygroesse(); i++) {
			zeiten.add(new Zeiten(i));
		}

		for (int i = 0; i < zeiten.size(); i++) {
			zeiten.get(i).setGruppen(gruppen.get(i));
		}

		for (int i = 0; i < trainer.size(); i++) {
			for (int j = 0; j < zeiten.size(); j++) {
				if (trainer.get(i).getZeiten()[j] == 1) {
					zeiten.get(j).getTrainer().add(trainer.get(i));
				}
			}
		}

		for (int i = 0; i < plaetze.size(); i++) {
			for (int j = 0; j < zeiten.size(); j++) {
				if (plaetze.get(i).getZeiten()[j] == 1) {
					zeiten.get(j).getPlatz().add(plaetze.get(i));
				}

			}
		}

		for (int i = 0; i < zeiten.size(); i++) {

			Collections.sort(zeiten.get(i).getTrainer(), new Comparator<Trainer>() {
				@Override
				public int compare(Trainer t1, Trainer t2) {
					return t1.getPrio() - t2.getPrio(); // Ascending
				}

			});

			if (zeiten.get(i).getTrainer().size() > zeiten.get(i).getTrainingsGruppenMoeglichkeiten()) {
				int trainerSize = zeiten.get(i).getTrainer().size();
				int moeglicheTrainer = zeiten.get(i).getTrainingsGruppenMoeglichkeiten();

				for (int k = zeiten.get(i).getTrainer().size() - 1; k > trainerSize - moeglicheTrainer; k--) {
					zeiten.get(i).getTrainer().remove(k);
				}

			}

		}

		return zeiten;
	}

}