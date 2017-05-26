import java.io.*;
import java.util.*;
import java.math.*;

import reedmuller.Bit;
import reedmuller.ReedMuller;

import static reedmuller.ReedMuller.*;
import static reedmuller.Bit.*;

public class Main {

    public static void main(String[] args) throws IOException {

        BigInteger test = new BigInteger("42");
        System.out.println(test.byteValue());
        System.out.println(test.bitLength());
        System.out.println(test.bitCount());

        for (int i = 0; i < test.toByteArray().length; i++) {
            System.out.println(test.toByteArray()[i]);
        }

        int rr = 3;
        ReedMuller rm = new ReedMuller(rr);
        Bit zero = new Bit(0);
        Bit one = new Bit(1);
        Bit word[] = {one, zero, one, zero};
        Bit wordEncoded[] = rm.encode(word);
        printWord(word);
        printWord(wordEncoded);

        for (int i = 0; i < 33; i++) {
            System.out.print("i : " + i + ", array : ");
            printWord(intToBitArray(i));
        }

        for (int i = 0; i < 33; i++) {
            System.out.print("i : " + i + ", array : ");
            printWord(arrayAtSize(intToBitArray(i), 8));
        }

        for (int i = 0; i < Math.pow(2, rr + 1); i++) {
            System.out.print("dec : " + i + ", binary : ");
            Bit goodSize[] = arrayAtSize(intToBitArray(i), rr + 1);
            printWord(goodSize);
            System.out.print("encoded : ");
            Bit wordCoded[] = rm.encode(goodSize);
            printWord(wordCoded);
	        System.out.print("decoded : ");
	        printWord(rm.decode(wordCoded));
        }

        // ------------------------------------------------------------------------------------------------------

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
                        // vos opérations pour l'encodage du mot courant,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 2:
                        // vos opérations pour le décodage du mot courant,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 3:
                        // vos opérations pour le bruitage du mot courant,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 4:
                        // vos opérations pour le débruitage du mot courant,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 5:
                        System.err.println("\nEntrer un mot (en décimal)");
                        BigInteger mot = new BigInteger(in.next());
                        break;
                }
                if (choix != 5) {
                    System.err.println("Valeur du mot courant (en décimal):");
                    // imprimer la valeur du mot courant en décimal
                    // sur la sortie standard
                }
                System.err.println(menu);
                choix = in.nextInt();
            } while (choix != 0);
        } else if (mode == 2) {
            do {
                String fileName;
                switch (choix) {
                    case 1:
                        // vos opérations pour l'encodage de l'image courante,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 2:
                        // vos opérations pour le décodage de l'image courante,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 3:
                        // vos opérations pour le bruitage de l'image courante,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 4:
                        // vos opérations pour le débruitage de l'image courante,
                        // ne rien afficher sur la sortie standard
                        break;
                    case 5:
                        System.err.println("Nom du fichier de l'image à charger (format png):");
                        fileName = in.next();
                        // lire le fichier contenant l'image pgm
                        break;
                }
                if (choix != 5) {
                    System.err.println("Nom du fichier où sauver l'image courante (format png):");
                    fileName = in.next();
                    // sauver l'image courante au format pgm
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









