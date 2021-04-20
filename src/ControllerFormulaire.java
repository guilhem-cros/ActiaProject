import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class ControllerFormulaire implements Initializable{
    
    /*Le controller parent du formulaire actuellement ouvert*/
    private static ControllerAffichage originControl;
    
    
    /*Attributs FXML des fomulaires*/

    @FXML
    private TextField columnTitle;

    @FXML 
    private Button createColButt;

    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnTitle.setText("");
    }

    
     /*Fonctions FXML de sous-onglets*/

     /**
      * Fonction appelée lors de l'appuie sur le bouton "Ajouter la colonne"
      * Renvoie une alerte si le nom de la colonne est invalide
      * Sinon, enregistre le titre de colonne et met à jour la page d'affichage
      * @param action
      */
    @FXML
    public void newColumn(ActionEvent action){
        String title = columnTitle.getText();
        if(isInTitles(title)){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, nom de colonne déjà existant", "Le titre saisi pour la création d'une nouvelle colonne correspond déjà à une autre colonne.", "Erreur", alert);
        }
        else if(title.equals(null)||title.length()<4){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, nom de colonne invalide", "Veuillez saisir au moins 4 caractères pour le titre de la colonne.", "Erreur", alert);
        } 
        /*Si le titre de colonne saisi est valide*/
        else{
            Test.addTitle(title);
            (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
            Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
            originControl.initialize(null, null); //mise à jour immédiate de la page d'affichage parente du formulaire
            Alert alert = new Alert(AlertType.INFORMATION); //mise en place d'un message de confirmation
            Controller.setAlert("Mise à jour effectuée", "La colonne a bien été ajoutée", "Confirmation", alert);
        }
    }

    /**
     * Vérifie la présence d'une chaine de caractère dans la liste
     * des titres de colonnes
     * @param chain la chaine vérifié
     * @return true si la chaine est présente, false sinon
     */
    public boolean isInTitles(String chain){
        for(String s: ControllerAffichage.getParamTitles()){
            if(s.equals(chain)){
                return true;
            }
        }
        return false;
    }


    /*Getter et setter */

    public static ControllerAffichage getOriginControl() {
        return originControl;
    }

    public static void setOriginControl(ControllerAffichage originControl) {
        ControllerFormulaire.originControl = originControl;
    }

}
