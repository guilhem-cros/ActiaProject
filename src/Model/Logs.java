package Model;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Logs implements Serializable{
    
    /*Attributs*/
   
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
    

    public boolean equals(Logs log){
        if(this.paragraphe.equals(log.paragraphe) && this.password.equals(log.password) && this.login.equals(log.login)){
            return true;
        }
        return false;
    }

   /*Sérialisation des objets Logs*/

    /**
	 * Sérialise des objets Logs afin qu'ils puissent être sérialisés en même temps 
	 * que l'objets Element auquels ils appartiennent
	 */
	public void serializeLogs(){
        try {
			FileOutputStream fichier = new FileOutputStream("data/logs.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(this);
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	
    }

	/**
	 * Lis des objets Logs sérialisés afin qu'ils puissent être lu et traduits en même temps 
	 * que l'objets Element auquels ils apparatiennent
	 */
	public void unserializeLogs(){
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream("data/logs.ser"))) {
			/* Lecture du fichier*/
			while (true) {
				ois.readObject();
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}   
    }
}
