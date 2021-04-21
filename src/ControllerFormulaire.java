import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControllerFormulaire implements Initializable{
    
    /*Le controller parent du formulaire actuellement ouvert*/
    private static ControllerAffichage originControl;

    private ArrayList<TextField> listParam;
    
    
    /*Attributs FXML des fomulaires*/

    @FXML
    private TextField columnTitle;

    @FXML 
    private Button createColButt;

    @FXML
    private CheckBox hyperlinkBox;


    @FXML
    private Label titleLable;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private GridPane grid; 

    @FXML 
    private ComboBox<String> listMoyensGene;

    @FXML
    private TextField quantite;

    @FXML
    private ComboBox<String> testMode;

    @FXML
    private TextArea detailMoyen;


    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables en fonction du formulaire ouvert
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Controller.getForm().equals("newColForm")){
            columnTitle.setText("");
        }
        else if(Controller.getForm().equals("modifOutilForm")){
            listMoyensGene.getItems().clear();
            setComboBoxMoyenGene();
            setForm();
            setCbMode();
        }
        
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
            Outil.addTitle(title);
            ArrayList<Boolean> hyperlinks = ControllerAffichage.getListHyperlink();
            hyperlinks.add(hyperlinkBox.isSelected());
            ControllerAffichage.setListHyperlink(hyperlinks);
            (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
            Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
            originControl.initialize(null, null); //mise à jour immédiate de la page d'affichage parente du formulaire
            Alert alert = new Alert(AlertType.INFORMATION); //mise en place d'un message de confirmation
            Controller.setAlert("Mise à jour effectuée", "La colonne a bien été ajoutée", "Confirmation", alert);
        }
    }

    /**
     * Fonction appelé depuis l'appuie sur le bouton "Enregister" du form
     * de création / modification de ligne
     * Modifie l'outils sélectionné précédemment, selon les données saisi,
     * ou ajoute un nouvel Outil à l'élèment
     * @param action
     */
    @FXML
    public void setOutil(ActionEvent action){
        if(doOutilByForm()!=null){
            Element e = originControl.getSelectedElt();
            for(int i=0; i<e.getOutils().size(); i++){
                if(e.getOutils().get(i).equals(originControl.getCurrentOutil())){
                    e.getOutils().set(i, doOutilByForm());
                }
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            Controller.setAlert("Modifications effectuées", "Les modifications apportées ont bien été enregistrées.", "Confirmation", alert);
            originControl.initialize(null, null);
            Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
            (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
        }
    }
    
    /**
     * Fonction appelée lors d'un appuie sur le bouton "annuler"
     * décrémente le nombre de formulaire ouvert et ferme la fenêtre
     * @param action
     */
    @FXML
    public void closeForm(ActionEvent action){
        Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
        (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
    }


    /*Fonctions relatives à la création de colonne*/

    /**
     * Vérifie la présence d'une chaine de caractère dans la liste des titres de colonnes
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



    /*Fonctions relative à l'ajout/modification de lignes*/

    public void setCbMode(){
        testMode.getItems().clear();
        testMode.getItems().add("Auto");
        testMode.getItems().add("Manuel");
    }

   /**
    * Met en place le formulaire comportant tous l'ensemble des champs
    * permettant la modification de paramètres de la ligne sélectionnée
    */
    public void setForm(){
        RowConstraints row = new RowConstraints(65);
        listParam = new ArrayList<TextField>();
        /*Pour toutes les colonnes du tableau, un champs de saisi et de description ajoutés au form*/
        for(int i=4; i<Outil.getParamTitle().size(); i++){
            Label title = new Label(Outil.getParamTitle().get(i));
            setLabelParam(title);
            TextField tf = new TextField();
            setTextFParam(tf);
            listParam.add(tf);
            grid.getRowConstraints().add(row);
            grid.add(title,0, i);
            grid.add(tf, 1, i);
            /*Si la colonne correspondant au champs courrant est une colonne de liens hypertexte*/
            if(ControllerAffichage.getListHyperlink().get(i)){
                FileChooser fl = new FileChooser();
                fl.setTitle("Sélection de fichier");
                Button selectFileButton = new Button("..."); //ajout d'un bouton
                /*Evenenement du bouton : sélection d'un fichier et conservation de son chemin absolu*/
                selectFileButton.setOnAction(event -> {
                    File file = fl.showOpenDialog(new Stage());
                    if(file!=null){
                        tf.setText(file.getAbsolutePath());
                    }
                });
                grid.add(selectFileButton, 2, i); //ajout du bouton dans la grille
            }
        }

    }

    /**
     * Modifie les paramètres visuels d'un label pour l'adapter à la grille
     * @param l le label dont on modifie l'aspect
     */
    public void setLabelParam(Label l){
        l.setFont(Font.font(16));
        l.prefHeight(65);
        l.prefWidth(400);
        l.setMaxHeight(65);
        l.setMaxWidth(400);
        l.setWrapText(true);
        l.setAlignment(Pos.CENTER);
        l.setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * Initialise les paramètre visuels d'un textfield pour l'adapter à la grille
     * @param t le Textfield dont on modifie l'apparence
     */
    public void setTextFParam(TextField t){
        t.setFont(Font.font(16));
        t.prefHeight(37);
        t.prefWidth(250);
        t.setMaxHeight(37);
        t.setMaxWidth(250);
    }

    /**
     * Crée une comboBox contenant la liste des Moyens génériques enregistrés
     * @return la combo box contenant tous les moyens génériques
     */
    public void setComboBoxMoyenGene(){
        listMoyensGene.getItems().clear();
        listMoyensGene.setStyle("-fx-font-f: 16px;");
        ArrayList<MoyenGenerique> moyens = ControllerAffichage.getMoyensGene();
        MoyenGenerique.sortMoyenGen(moyens); //tri de la liste de moyens
        for(MoyenGenerique m : moyens){
            String chaine = m.getNom();
            listMoyensGene.getItems().add(chaine);
        } 
    }

    public Outil doOutilByForm(){
        System.out.println(listMoyensGene.getValue());
        System.out.println(detailMoyen.getText());
        Outil outil = new Outil(listMoyensGene.getValue(), detailMoyen.getText());
        outil.setUtilisationAuto(testMode.getValue().equals("Auto"));
        if(!isInteger(quantite.getText())){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur de saisie", "La valeur saisi pour \"Quantité\" n'est pas un entier.", "Erreur", alert);
            return null;
        }
        else{
            outil.setQuantite(Integer.parseInt(quantite.getText()));
        }
        return outil;
    }

    public boolean isInteger(String str) {
        int size = str.length();
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return size>0;
    }


    /*Getter et setter */

    public static ControllerAffichage getOriginControl() {
        return originControl;
    }

    public static void setOriginControl(ControllerAffichage originControl) {
        ControllerFormulaire.originControl = originControl;
    }

}
