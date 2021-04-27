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

    /*Liste des champs complétables des paramètres de l'outils*/
    private ArrayList<TextField> listParamField;
    
    
    /*Attributs FXML des fomulaires*/

    @FXML
    private TextField columnTitle;

    @FXML 
    private Button createColButt;

    @FXML
    private CheckBox hyperlinkBox;


    @FXML
    private Label titleLabel;

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


    @FXML
    private TextField colTitle;

    @FXML
    private ComboBox<Integer> colPosition;

    @FXML
    private CheckBox linkBox;

    @FXML
    private Label title;

    @FXML
    private Button saveChangeButton;

    @FXML
    private Button cancelChangeButton;

    @FXML 
    private Label position;

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
        else if(Controller.getForm().equals("modifColForm")){
            setComboBoxPosition();
            fillColForm();
            title.setText("Modification de colonne");
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
            ArrayList<Boolean> hyperlinks = ControllerAffichage.unserializeLinks();
            hyperlinks.add(hyperlinkBox.isSelected());
            ControllerAffichage.serialHyperlink(hyperlinks);
            (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
            Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
            originControl.initialize(null, null); //mise à jour immédiate de la page d'affichage parente du formulaire
            Alert alert = new Alert(AlertType.INFORMATION);
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
        if(listMoyensGene.getValue()!=null && detailMoyen.getText().length()!=0 && testMode.getValue()!=null){
            if(doOutilByForm()!=null){
                Element e = originControl.getSelectedElt();
                for(int i=0; i<e.getOutils().size(); i++){
                    if(e.getOutils().get(i).equals(originControl.getCurrentOutil())){
                        e.getOutils().set(i, doOutilByForm());
                        i = e.getOutils().size(); //sortie de la boucle
                    }
                }
                titleLabel.setText("Formulaire de modification");
                Element.serializeAllElements(Controller.getAllElements());
                Alert alert = new Alert(AlertType.INFORMATION);
                Controller.setAlert("Modifications effectuées", "Les modifications apportées ont bien été enregistrées.", "Confirmation", alert);
                originControl.initialize(null, null);
                Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
                (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
            }
        }
        else{
            Alert alert = new Alert(AlertType.ERROR);
            Controller.setAlert("Erreur : formulaire incomplet", "Veuillez remplir tous les champs précédés d'un * .", "Erreur", alert);
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

    /**
     * Appelée lors de l'appuie sur le bouton "enregistrer" du fomulaire de 
     * modification de colonne
     * Met à jour la colonne en fonction du formulaire rempli et envoie un message de confirmation
     * Si le formulaire n'est pas correctement rempli : message d'erreur
     * @param action
     */
    @FXML
    public void setColumn(ActionEvent action){
        if(colTitle.getText().length()<4){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur de saisi", "Veuillez saisir au moins 4 caractères pour le titre de la colonne", "Erreur", alert);
        }
        else{
            SetCol();
            Alert alert = new Alert(AlertType.INFORMATION);
                Controller.setAlert("Modifications effectuées", "Les modifications apportées ont bien été enregistrées.", "Confirmation", alert);
                originControl.initialize(null, null);
                Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1);//décrémentation du compteur de formulaires
                (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
        }
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

    /**
     * Instancie la ComboBox testMode avec les choix Auto et Manuel
     */
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
        listParamField = new ArrayList<TextField>();
        /*Pour toutes les colonnes du tableau, un champs de saisi et de description ajoutés au form*/
        for(int i=4; i<Outil.getParamTitle().size(); i++){
            Label title = new Label(Outil.getParamTitle().get(i));
            setLabelParam(title);
            TextField tf = new TextField();
            setTextFParam(tf);
            listParamField.add(tf);
            grid.getRowConstraints().add(row);
            grid.add(title,0, i);
            grid.add(tf, 1, i);
            /*Si la colonne correspondant au champs courrant est une colonne de liens hypertexte*/
            if(ControllerAffichage.unserializeLinks().get(i)){
                FileChooser fl = new FileChooser();
                fl.setTitle("Sélection de fichier");
                Button selectFileButton = new Button("...");
                /*Evenenement du bouton : sélection d'un fichier et conservation de son chemin absolu*/
                selectFileButton.setOnAction(event -> {
                    File file = fl.showOpenDialog(new Stage());
                    if(file!=null){
                        tf.setText(file.getAbsolutePath());
                    }
                });
                grid.add(selectFileButton, 2, i);
            }
        }
        /*pré-remplissage des champs s'il s'agit d'une modification d'Outil existant*/
        if(Controller.getForm().equals("modifOutilForm")){
            fillFields(); 
        }
    }

    /**
     * Pré-rempli les champs du formulaire avec les données de l'Outil
     * courrament sélectionné.
     */
    public void fillFields(){
        Outil outil = originControl.getCurrentOutil();
        ArrayList<String> param = outil.getListParam();
        listMoyensGene.setValue(outil.getMoyenGenerique());
        quantite.setText("" + outil.getQuantite());
        if(outil.isUtilisationAuto()){
            testMode.setValue("Auto");
        }
        else{
            testMode.setValue("Manuel");
        }
        detailMoyen.setText(outil.getDetailMoyen());
        for(int i=4; i<param.size();i++){
            listParamField.get(i-4).setText(param.get(Outil.unserializeOrdre().get(i)));
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
        MoyenGenerique.sortMoyenGen(moyens);
        for(MoyenGenerique m : moyens){
            if(!m.getNom().equals("Tous")){
                String chaine = m.getNom();
                listMoyensGene.getItems().add(chaine);
            }
        } 
    }

    /**
     * Récupère l'ensemble des données saisies dans le formulaire et crée
     * un Outil à partir de celles-ci
     * @return l'outil crée possédant toutes les informations saisies
     */
    public Outil doOutilByForm(){
        if(!MoyenGenerique.isAlrdyInList(MoyenGenerique.unserializeMoyenGene(), listMoyensGene.getValue())){
            MoyenGenerique.addMoyen(listMoyensGene.getValue());
        }
        Outil outil = new Outil(listMoyensGene.getValue(), detailMoyen.getText());
        outil.setUtilisationAuto(testMode.getValue().equals("Auto"));
        outil.setQuantite(quantite.getText());
        for(int i=0; i<Outil.unserializeTitles().size()-4; i++){
            outil.getListParam().set(i+4, listParamField.get(Outil.unserializeOrdre().indexOf(i+4)-4).getText());
        }
        return outil;
    }


    /*Fonctions relative à la modifications de colonnes*/

    /**
     * Intilialise la comboBox de permettant de sélectionner une position
     * pour la colonne courante avec des valeurs allant de 4 à la taille du tableau
     * Les 4 premieres colonnes du tableau ne peuvent pas être déplacées
     */
    public void setComboBoxPosition(){
        colPosition.getItems().clear();
        colPosition.setStyle("-fx-font-f: 16px;");
        for(int i=4; i<Outil.unserializeTitles().size(); i++){
            colPosition.getItems().add(i+1); 
        }
        setVisibilityForUpdates();
    }

    /**
     * Adapte la visibilité de la combobox de sélection de position en fonction
     * de la colonne sélectionné : inivisible pour les 4 premières colonnes
     */
    public void setVisibilityForUpdates(){
        for(int i=0; i<Outil.unserializeTitles().size(); i++){
            if(originControl.getCurrentTitle().equals(Outil.unserializeTitles().get(i))){
                if(i<4){
                    colPosition.setVisible(false);
                    position.setVisible(false);
                }
                else{
                    colPosition.setVisible(true);
                    position.setVisible(true);
                }
            }
        }
    }

    /**
     * pré rempli les champs du formulaire de modification avec les valeurs
     * actuelles de la colonne sélectionnée
     */
    public void fillColForm(){
        String title = originControl.getCurrentTitle();
        int pos = Outil.unserializeTitles().indexOf(title);
        colTitle.setText(title);
        colPosition.setValue(pos+1);
        if(ControllerAffichage.unserializeLinks().get(pos)){
            linkBox.setSelected(true);
        }
        else{
            linkBox.setSelected(false);
        }
    }

    /**
     * Modifie les valeurs de la colonne par les valeurs entrées dans le formulaire
     * et les enregistre
     * Met à jour le tableau en fonction du changement de position de la colonne
     */
    public void SetCol(){
        ArrayList<String> titles = Outil.unserializeTitles();
        ArrayList<Boolean> links = ControllerAffichage.unserializeLinks();
        String title = originControl.getCurrentTitle();
        int pos = titles.indexOf(title);
        titles.set(pos, colTitle.getText());
        links.set(pos, linkBox.isSelected());
        Outil.serializeAllTitles(titles);
        ControllerAffichage.serialHyperlink(links);
        /*Mise à jour de l'affichage, la colonne est déplacée et toutes les autres également*/
        if(colPosition.getValue() != pos+1 && colPosition.getValue()>4){
            if(colPosition.getValue()-1 > pos){
                for(int i=pos; i!=colPosition.getValue()-1; i++){
                    swapCols(i, i+1);
                }
            }
            else{
                for(int i=pos; i!=colPosition.getValue()-1; i--){
                    swapCols(i, i-1);
                }
            }
        }
    }

    /**
     * Echange les valeurs de deux colonnes entres elles
     * @param pos1 la position de la première colonne
     * @param pos2 la position de la deuxième colonne
     */
    public void swapCols(int pos1, int pos2){
        ArrayList<String> titles = Outil.unserializeTitles();
        ArrayList<Boolean> links = ControllerAffichage.unserializeLinks();
        ArrayList<Integer> ordre = Outil.unserializeOrdre();
        String auxTitle = titles.get(pos1);
        Boolean auxLink = links.get(pos1);
        int auxPos = ordre.get(pos1);
        titles.set(pos1, titles.get(pos2));
        titles.set(pos2, auxTitle);
        links.set(pos1, links.get(pos2));
        links.set(pos2, auxLink);
        ordre.set(pos1, ordre.get(pos2));
        ordre.set(pos2, auxPos);
        Outil.serializeAllTitles(titles);
        ControllerAffichage.serialHyperlink(links);
        Outil.serializeOrdre(ordre);
    }



    /*Getter et setter */

    public static ControllerAffichage getOriginControl() {
        return originControl;
    }

    public static void setOriginControl(ControllerAffichage originControl) {
        ControllerFormulaire.originControl = originControl;
    }

}
