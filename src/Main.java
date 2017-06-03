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

public class Main {

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
        BigInteger mot = BigInteger.ZERO;
        Word intToWord = null;
        Word current = null;

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
                + "5: Réinitialiser\n"
                + "Opération choisie:";
        int choix = 5;
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
                        System.err.println("\nEntrer un mot (en décimal)");
                        mot = new BigInteger(in.next());
                        intToWord = bigIntToWord(mot, r + 1);
                        break;
                }
                if (choix != 5) {
                    System.err.println("Valeur du mot courant (en décimal):");
                    System.out.println(mot.intValue());
                    System.out.println(current);
                }
                System.err.println(menu);
                choix = in.nextInt();
            } while (choix != 0);
        } else if (mode == 2) {
            choix = 5;
            String fileName = null;
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
                        System.err.println("Nom du fichier de l'image à charger (format pgm):");
                        fileName = in.next();
                        pgm = read(fileName);
                        break;
                }
                if (choix != 5) {
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









