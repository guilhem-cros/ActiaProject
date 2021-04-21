import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Config implements Serializable{
    
    /*Attributs*/

    /*L'identifiant à saisir pour passer en mode admin */
    private static String login;

    /*Le mot de passe corresponant à l'identifiant*/
    private static String password;


    /*Constructeurs*/

    public Config(String login, String password){
        Config.login = login;
        Config.password = password;
    }



    /*Getter et setter*/

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        Config.login = login;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Config.password = password;
    }



    /*Sérialisation des objets Config*/

    /**
	 * Stocke la config saisi dans un fichier .ser et modifie la config actuelle
	 * @param passW le mot de passe de la config
     * @param log le login de la config
	 */
    public static void serializeConfig(String passW, String log){
        try {
			FileOutputStream fichier = new FileOutputStream("data/config.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
            //Modification de la config actuelle
            Config.setLogin(log);
            Config.setPassword(passW);
            //Tous les attributs de la config sont sérializés dans le fichier précédent
            oos.writeUTF(Config.login);
            oos.writeUTF(Config.password);
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	
    }

    /**
	 * Récupère la config stockée dans le fichier config.ser 
     * et met à jour à la config courante en fct de celle-ci
	 */
    public static void unserializeConfig(){
        try (ObjectInputStream ois = 
			new ObjectInputStream(new FileInputStream("data/config.ser"))) {
                /*Lecture de l'ensemble du fichier*/
                while (true) {
                    /*Modification de la confic actuelle*/
                    Config.setLogin(ois.readUTF());   
                    Config.setPassword(ois.readUTF());     
                } 		
		    } catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
