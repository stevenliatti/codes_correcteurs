import images.PGM;
import reedmuller.Bit;
import reedmuller.ReedMuller;
import reedmuller.Word;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import static reedmuller.Word.bigIntToWord;
import static reedmuller.Word.log2;
import static reedmuller.Word.wordToBigInt;
import static images.PGM.*;

public class Main {



    public static void testReader() throws IOException {
        System.out.println("testReader Lena");
        PGM pgm = read("data/lena_128x128_64.pgm");
        System.out.println(pgm);

        write(pgm, "data/lena_copy.pgm");
        write(pgm.encode(), "data/lena_encoded.pgm");

        PGM pgm2 = read("data/lena_encoded.pgm");
        write(pgm2.decode(), "data/lena_decoded.pgm");
        write(pgm2.noise(0.1), "data/lena_noised.pgm");

        PGM pgm3 = read("data/lena_noised.pgm");
        write(pgm3.denoise(), "data/lena_denoised.pgm");
        write(pgm3.denoise().decode(), "data/lena_denoised_decoded.pgm");
    }

    static void testFunction() {
        System.out.println(log2(63));
        System.out.println(log2(64));
        BigInteger bg = new BigInteger("41");
        BigInteger bg2 = new BigInteger("64");
        System.out.println("bg " + bg.bitLength());
        System.out.println("bg2 " + bg2.bitLength());
        System.out.println(bg2.toString(2));
        Bit b = new Bit('0');
        Bit b1 = new Bit('1');
        Bit b2 = new Bit('1');
        System.out.println("bbbb : " + b + "," + b1 + "," + b2 + " ");

        System.out.println("------------- Word ---------------");
        Word word = bigIntToWord(bg);

        Word word2 = new ReedMuller(5).encode(word);

        System.out.println(word);
        System.out.println(wordToBigInt(word));

        System.out.println(word2);
        System.out.println(wordToBigInt(word2));


        System.out.println("Table");
        int rr = 5;
        ReedMuller rm = new ReedMuller(rr);

        for (Integer i = 0; i < Math.pow(2, rr + 1); i++) {
            System.out.print("\ndecimal :\t" + i + "\nbinary  :\t");
            Word goodSize = bigIntToWord(new BigInteger(i.toString()), rr + 1);
            System.out.println(goodSize);
            System.out.print("encoded :\t");
            Word wordCoded = rm.encode(goodSize);
            System.out.println(wordCoded);
            System.out.println("decimal :\t" + wordToBigInt(wordCoded));
//            System.out.print("decoded :\t");
//            System.out.println(rm.decode(wordCoded));
//            System.out.println("decimal :\t" + wordToBigInt(goodSize));
        }

    }

    public static void main(String[] args) throws IOException {
//        System.out.println(log2(63));
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









