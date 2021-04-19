
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Logs implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private String paragraphe;
    private String login;
    private String password;
    

    /*Constructeurs*/

    public Logs(String paragraphe, String login, String password){
        this.paragraphe = paragraphe;
        this.login = login;
        this.password = password;
    }


    /*Getter et setter*/

    public String getParagraphe() {
        return paragraphe;
    }

    public void setParagraphe(String paragraphe) {
        this.paragraphe = paragraphe;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    

   /*Sérialisation des objets Logs*/

    /**
	 * Stocke les éléments dans un fichier .ser
	 * @param allElements ArrayList de l'ensemble des éléments
	 */
    public void serializeElementLogs(ArrayList<Logs> elementLogs){
        try {
			FileOutputStream fichier = new FileOutputStream("data/logs.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
            //Tous les éléments de la liste sont sérializés dans le fichier précédent
            for(Logs l: elementLogs){
                oos.writeObject(l); //séralise tous les attributs de l'élément
            }
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	
    }

    /**
	 * Récupère les ensembles stockés dans le fichier element.ser
	 * @return arraylist contentant l'ensemble des éléments lu dans le fichier
	 */
    public ArrayList<Logs> unserializeLogs(){
        ArrayList<Logs> list = new ArrayList<Logs>();
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream("data/logs.ser"))) {
			// Lecture complète du fichier
			while (true) {
				list.add((Logs) ois.readObject());
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return list; 
    }
}
