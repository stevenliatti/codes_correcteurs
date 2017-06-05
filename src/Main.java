import images.PGM;
import reedmuller.ReedMuller;
import reedmuller.Word;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import static images.PGM.read;
import static images.PGM.write;
import static reedmuller.Word.bigIntToWord;
import static reedmuller.Word.wordToBigInt;

public class Main {
	private static final boolean DEBUG_MAIN = false;

	/**
	 * Fonction de "debug", utile pour avoir la table selon le ReedMuller donné.
	 *
	 * @param rm code de ReedMuller
	 */
    public static void printTable(ReedMuller rm) {
        System.out.println("Table pour r = " + rm.getR());
        for (Integer i = 0; i < Math.pow(2, rm.getStartDim()); i++) {
            System.out.print("\nx 'chapeau' :\t" + i + "\nbinaire  :\t\t");
            Word goodSize = bigIntToWord(new BigInteger(i.toString()), rm.getStartDim());
            System.out.println(goodSize);
            System.out.print("y :\t\t\t\t");
            Word wordCoded = rm.encode(goodSize);
            System.out.println(wordCoded);
            System.out.println("y en décimal :\t" + wordToBigInt(wordCoded));
            System.out.print("y décodé :\t\t");
            System.out.println(rm.decode(wordCoded));
            System.out.println("x 'chapeau' :\t" + wordToBigInt(goodSize));
        }
    }

    public static void main(String[] args) throws IOException {
        // permet de prendre les entrées pour le menu
        // soit du clavier, d'un fichier ou de la ligne de commande
        Scanner in;
        switch (args.length) {
            case 0:
                in = new Scanner(System.in);
                break;
            case 1:
                in = new Scanner(new File(args[0]));
                break;
            default:
                String source = args[0];
                for (int i = 1; i < args.length; i++) source += " " + args[i];
                in = new Scanner(source);
        }

        // les impressions des menus sont envoyées sur le canal d'erreur
        // pour les différencier des sorties de l'application
        // lesquelles sont envoyées sur la sortie standard

        // choix des paramètres
        System.err.println("Mot en clair de (r+1)-bits à encoder sur (2^r)-bits.");
        System.err.println("Choisir la valeur de r: ");
        int r = in.nextInt();
        System.err.println("\nLe seuil de bruit est la probabilité d'inverser un bit.");
        System.err.println("Choisir un seuil de bruit (nombre entre 0.0 et 1.0): ");
        double seuil = in.nextDouble();

        ReedMuller rm = new ReedMuller(r);
        BigInteger mot;
        Word intToWord = null;
        Word current = null;

	    // Pour rapidement contrôler la table pour un r donné
	    if (DEBUG_MAIN) {
		    printTable(rm);
	    }

        // traiter un mot ou une image
        System.err.println("\nMenu initial");
        System.err.println("0: Quitter");
        System.err.println("1: Traiter un mot");
        System.err.println("2: Traiter une image");
        int mode = in.nextInt();

        // opération à effectuer sur le mot ou l'image
        String menu = "\nMenu opérations\n"
                + "0: Quitter\n"
                + "1: Encoder\n"
                + "2: Décoder\n"
                + "3: Bruiter\n"
                + "4: Débruiter\n"
                + "5: Débruiter + décoder (rapide)\n"
                + "6: Réinitialiser\n"
                + "Opération choisie:";
        int choix = 6;
        if (mode == 1) {
            do {
                switch (choix) {
                    case 1:
                        current = rm.encode(intToWord);
                        break;
                    case 2:
                        current = rm.decode(current);
                        break;
                    case 3:
                        current = rm.noise(current, seuil);
                        break;
                    case 4:
                        current = rm.semiExhaustiveSearch(current);
                        break;
                    case 5:
                        current = rm.fastSearch(current);
                        break;
                    case 6:
                        System.err.println("\nEntrer un mot (en décimal)");
                        mot = new BigInteger(in.next());
                        intToWord = bigIntToWord(mot, r + 1);
                        break;
                }
                if (choix != 6) {
                    System.err.println("Valeur du mot courant (en décimal):");
                    System.out.println(wordToBigInt(current).intValue());
                    System.out.println(current);
                }
                System.err.println(menu);
                choix = in.nextInt();
            } while (choix != 0);
        } else if (mode == 2) {
            choix = 6;
            String fileName;
            PGM pgm = null;
            do {
                switch (choix) {
                    case 1:
                        pgm = pgm.encode();
                        break;
                    case 2:
                        pgm = pgm.decode();
                        break;
                    case 3:
                        pgm = pgm.noise(seuil);
                        break;
                    case 4:
                        pgm = pgm.denoise();
                        break;
                    case 5:
                        pgm = pgm.denoiseAndDecode();
                        break;
                    case 6:
                        System.err.println("Nom du fichier de l'image à charger (format pgm):");
                        fileName = in.next();
                        pgm = read(fileName);
                        break;
                }
                if (choix != 6) {
                    System.err.println("Nom du fichier où sauver l'image courante (format pgm):");
                    fileName = in.next();
                    write(pgm, fileName);
                }
                System.err.println(menu);
                choix = in.nextInt();
            } while (choix != 0);
        }
    }
}

//Encoder => Décoder ou Bruiter ou Réinitialiser
//Décoder => Encoder ou Réinitialiser
//Bruiter => Débruiter ou Bruiter ou Réinitialiser
//Débruiter => Bruiter ou Décoder ou Réinitialiser
