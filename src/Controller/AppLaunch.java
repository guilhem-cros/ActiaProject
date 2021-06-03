package Controller;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * Classe appelé au lancement de l'application depuis le jar
 * Permet le chargement des librairies (en particulier javafx)
 * avant de commencer l'appel de fonctions liées à ces librairies
 * @author Guilhem Cros
 */
public class AppLaunch {

    private static String currentPath;

    public static void main(String[] args){
        setPath();
        Lanceur.main(args);
    }

    /**
     * Récupère l'emplacement du fichier depuis lequel est lancé le soft.
     * Adapte le lien vers le document d'aide en fonction de cet emplacement.
     */
    public static void setPath(){
        try {
            String path = AppLaunch.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            String folderPath = decodedPath.substring(0, decodedPath.lastIndexOf('/') + 1);
            currentPath = folderPath;
        }
        catch (IOException e){
            e.printStackTrace();;
        }
    }

    public static String getCurrentPath(){ return  currentPath; }
}
