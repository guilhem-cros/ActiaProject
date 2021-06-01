package Model;

import Controller.AppLaunch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Logs implements Serializable{
    
    /*Attributs*/
   
    private static final long serialVersionUID = 1L;

    private String paragraphe;
    private String login;
    private String password;

    /*La liste des chiffres de 0 à 9 sous forme de Character*/
    private static ArrayList<Character> listNb = Logs.getListNb();
    

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
    

    /**
     * Vérifie l'égalité de l'ensemble des valeurs de 2 logs afin de savoir s'ils 
     * sont égaux
     * @param log le log comparé à this
     * @return true si les paramètres des Logs sont égaux, faux sinon
     */
    public boolean equals(Logs log){
        if(this.paragraphe.equals(log.paragraphe) && this.password.equals(log.password) && this.login.equals(log.login)){
            return true;
        }
        return false;
    }



    /*Fonctions relatives au tri d'une liste d'objets Logs*/

    /**
     * Tri une liste de Logs en fonction du paragraphe
     * @param listLogs la liste de Logs triée
     */
    public static void sortLog(ArrayList<Logs> listLogs){
        for(int i=0; i<listLogs.size()-1; i++){
            for(int j=i+1;j<listLogs.size(); j++){
                if(listLogs.get(i).compareByParagraphe(listLogs.get(j))>0){
                    Logs aux = listLogs.get(i);
                    listLogs.set(i, listLogs.get(j));
                    listLogs.set(j, aux);
                }
            }
        }
    }

    /**
     * Lis le paragraphe de this et socke chaque numéro de celui-ci sous forme de liste
     * Ajoute 0 à la liste si le paragraphe ne contient pas de numéro
     * @return la liste de chaque numéros du paragraphe de this
     */
    public ArrayList<Integer> getParaToCompare(){
        int i=0;
        ArrayList<Integer> listPara = new ArrayList<Integer>();
        String para = this.paragraphe;
        while(para.length()>i){
            String current = new String();
            while(i<para.length() && listNb.contains(para.charAt(i))){
                current += para.charAt(i);
                para = para.substring(1);
            }
            if(current.length()>0){
                listPara.add(Integer.parseInt(current));
            }
            else{
                listPara.add(0);
            }
            i++;
        }
        return listPara;
    }

    /**
     * Compare deux objets Logs en fonction de leur position de paragraphe
     * @param logs le Logs comparé à this
     * @return 1 si this>logs, -1 si this < logs ou 0 s'ils sont équivalents
     */
    public int compareByParagraphe(Logs logs){
        /*Les cas ou l'un des deux paragraphes est "vide"*/
        if(this.paragraphe ==null || this.paragraphe.length()==0){
            return -1;
        }
        if(logs.paragraphe ==null || logs.paragraphe.length()==0){
            return 1;
        }
        /*Comparaison des paragraphes à partir des listes de leurs numéros*/
        int length = Integer.min(this.getParaToCompare().size(),logs.getParaToCompare().size());
        for(int i=0; i<length; i++){
            if(this.getParaToCompare().get(i).compareTo(logs.getParaToCompare().get(i))==0){
                if(i==length-1){
                    if(this.getParaToCompare().size() > logs.getParaToCompare().size()){
                        return 1;
                    }
                    return -1;
                }
                continue;
            }
            return this.getParaToCompare().get(i).compareTo(logs.getParaToCompare().get(i));
        }
        return 0;
    }


    /**
     * Instancie une liste de chiffre allant de 0 à 9 sous forme de charactère
     * @return la liste des chiffres de 0 à 9
     */
    public static ArrayList<Character> getListNb(){
        ArrayList<Character> listNb = new ArrayList<>();
        for(int i=0; i<10; i++){
            listNb.add(Character.forDigit(i, 10));
        }
        return listNb;
    }

    @Override
    public String toString() {
        return "Logs [login=" + login + ", paragraphe=" + paragraphe + ", password=" + password + "]";
    }

}
