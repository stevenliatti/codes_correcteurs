import java.io.*;
import java.util.*;
import java.math.*;

import images.PGM;
import reedmuller.Bit;
import reedmuller.ReedMuller;
import reedmuller.Word;

import static reedmuller.ReedMuller.*;
import static reedmuller.Bit.*;
import static reedmuller.Word.*;

public class Main {

    public static void testFunction() {
        System.out.println("testFunction");
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
        Bit bitArray[] =  {zero, zero, zero, one};
        Bit bitArray2[] = {one, one, zero, one};
        Word word = new Word(bitArray);
        Word word2 = new Word(bitArray2);
        Word wordEncoded = rm.encode(word);
        System.out.println(word);
        System.out.println(wordEncoded);

        for (int i = 0; i < 64; i++) {
            System.out.println("log2 de " + i + " : " + log2(i));
        }

        for (int i = 0; i < 33; i++) {
            System.out.print("i : " + i + ", word : ");
            System.out.println(intToWord(i));
        }

        System.out.println();

        for (int i = 0; i < 33; i++) {
            System.out.print("i : " + i + ", word : ");
            System.out.println(wordAtSize(intToWord(i), 8));
        }

        for (int i = 0; i < Math.pow(2, rr + 1); i++) {
            System.out.print("\ndecimal :\t" + i + "\nbinary  :\t");
            Word goodSize = intToWord(i, rr + 1);
            System.out.println(goodSize);
            System.out.print("encoded :\t");
            Word wordCoded = rm.encode(goodSize);
            System.out.println(wordCoded);
            System.out.print("decoded :\t");
            System.out.println(rm.decode(wordCoded));
            System.out.println("decimal :\t" + wordToInt(goodSize));
        }

        System.out.println(hammingDistance(ONE, ONE));
        System.out.println(hammingDistance(ONE, ZERO));

        System.out.println(hammingDistance(word, word2));
        System.out.println(hammingDistance(word, word));

        Word w = new Word(bitArray);
        for (int i = 0; i < 11; i++) {
            System.out.println(w);
            w = w.plusOne();
        }

        System.out.println("Test de semiExhaustiveSearch");
        Bit array[] = {zero, one, one, one, one, zero, one, zero};
        Word noised = new Word(array);
        System.out.println(noised);
        Word good = rm.semiExhaustiveSearch(noised);
        System.out.println(good);

        System.out.println("Test noise");
        System.out.println(rm.noise(good, 0.5));
    }

    public static void testReader() throws IOException {
        System.out.println("testReader");
        PGM pgm = PGM.read("data/lena_128x128_64.pgm");
        System.out.println(pgm);
        System.out.println(PGM.read("data/mars-crat.enc.alt_0.07"));
        System.out.println(PGM.read("data/mars-crat.enc.alt_0.10"));
        pgm.write("data/lena_copy.pgm");
    }

    public static void main(String[] args) throws IOException {
        System.out.println(log2(64));
        testReader();
        testFunction();

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
        BigInteger mot = new BigInteger("0");
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
                        current = rm.decode(intToWord);
                        break;
                    case 3:
                        current = rm.noise(intToWord, seuil);
                        break;
                    case 4:
                        current = rm.semiExhaustiveSearch(intToWord);
                        break;
                    case 5:
                        System.err.println("\nEntrer un mot (en décimal)");
                        mot = new BigInteger(in.next());
                        intToWord = intToWord(mot.intValue(), r + 1);
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
                        // lire le fichier contenant l'image images
                        break;
                }
                if (choix != 5) {
                    System.err.println("Nom du fichier où sauver l'image courante (format png):");
                    fileName = in.next();
                    // sauver l'image courante au format images
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









