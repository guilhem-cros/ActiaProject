package Controller;

import java.util.ArrayList;

import Model.Element;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class ControllerFormEnsembles {
    
    /*L'ensemble sélectionné depuis le menu d'accueil*/
    public Element selectedElement;

    /*Liste des champs complétables des paramètres de l'ensemble*/
    public ArrayList<TextField> listParamField;

    /*Le controller ayant ouvert le formulaire*/
    public static Controller originControll;


    /*Attributs FXML des formulaires*/

    @FXML
    private Label titleLabel;

    @FXML
    private TextField codeField;

    @FXML
    private TextField nameField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;



    /*Fonctions FXML*/

    /**
     * Enregistre l'élément saisi dans le formulaire de modif/création et met
     * à jour la liste d'objets Element du Controller ainsi que le fichier de stockage
     * Ouvre un onglet d'alerte si es champs ne sont pas correctement rempli ou si
     * l'element créé possède un code déjà existant
     */
    @FXML
    public void saveElement(ActionEvent action){
        if(codeField.getText().length()<3 || nameField.getText().length()<3){
            Alert alert  = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur : champs invalide(s)", "Veuillez saisir au moins 3 caractères pour chaque champs du formulaire", "Erreur", alert);
        }
        else if(isAlreadyDefine()){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur : ensemble déjà existant", "Le code saisi pour l'ensemble correspond à un ensemble déjà existant.", "Erreur", alert);
        }
        else if(selectedElement != null){

        }
        else{
            Element e = makeElementByForm();
            ArrayList<Element> allElt = Controller.getAllElements();
            allElt.add(e);
            Element.sortElements(allElt);
            Controller.setAllElements(allElt);
            Element.serializeAllElements(Controller.getAllElements());
            finalize(action);
        }
    }

    /**
     * Appelé lors d'un click sur le bouton "Annuler"
     * Décrémente le nombre de formulaire ouvert et ferme le formulaire courant
     * @param action
     */
    @FXML
    public void closeForm(ActionEvent action){
        Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1); //décrémentation du nb de form ouverts
        (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
    }



    /*Construcion et modification d'objets Element */

    /**
     * Construit un objet Element avec les données entrées dans les champs de 
     * textes du formulaire de création d'ensemble courant
     */
    public Element makeElementByForm(){
        String code = codeField.getText();
        String nom = nameField.getText();
        return new Element(nom, code);
    }

    /**
     * Vérifie la présence d'un code d'element egal à la valeur du chaps de texte "code"
     * dans la liste de tous les éléments
     * @return true si un element avec le code existe, faux sinon
     */
    public boolean isAlreadyDefine(){
        for(Element e: Controller.getAllElements()){
            if(e.getCodeElt().equals(codeField.getText())){
                return true;
            }
        }
        return false;
    }

    

    /*Fonctions diverses*/

    /**
     * Rafraichi la page d'accueil et ferme le formulaire d'ensemble courant
     * @param action
     */
    public void finalize(ActionEvent action){
        originControll.initialize(null, null);
        Controller.setAdmin(true);
        closeForm(action);
    }



    /*Getter et setter*/

    public static Controller getOriginControll() {
        return originControll;
    }

    public static void setOriginControll(Controller originControll) {
        ControllerFormEnsembles.originControll = originControll;
    }

    

}
