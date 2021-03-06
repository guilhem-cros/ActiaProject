package Controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Colonne;
import Model.Element;
import Model.MoyenGenerique;
import Model.Outil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class ControllerAffichageOutils implements Initializable{

    /*L'élément sélectionné depuis l'accueil*/
    private Element selectedElt;
    
    /*La valeur de la checkBox Auto lors de la validation depuis le menu d'accueil*/
    private Boolean auto;

    /*La valeur de la checkBox Manuel lors de la validation depuis le menu d'accueil*/
    private boolean manuel;

    /*La liste des outils de selectedElt*/
    private ArrayList<Outil> listOutils;

    /*La liste de tous les Moyens génériques*/
    private  static ArrayList<MoyenGenerique> moyensGene;

    /*La liste des titres des colonnes*/
    private static ArrayList<String> paramTitles;
    
    /*La liste des lignes (des paramètres des outils) de la grille*/
    private ArrayList<ArrayList<Labeled>> clicableItems;

    /*La liste des titres de colonnes de la grille sour forme de labels*/
    private ArrayList<Label> clicableTitles;

    /*L'outil de test actuellement sélectionné pour modification/suppression*/
    private Outil currentOutil;

    /*Le titre de colonne courrament sélectionné*/
    private String currentTitle;

    
    /*Initialisation des objets XML utilisés*/

    @FXML
    private Label title;

    @FXML
    private GridPane grid;

    @FXML 
    private Pane pane;

    @FXML
    private ScrollPane sp;

    @FXML
    private Label emptyLabel;

    @FXML
    private ComboBox<String> listMoyenGene;

    @FXML
    private Button saveDataButton;

    @FXML
    private MenuBar adminMenu;


    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     * Initialise / met à jour la liste des pages d'affichages ouvertes
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*Mise en place de la liste des ControllerAffichages "ouverts"*/
        if(Controller.getOpenedController() == null){
            Controller.setOpenedController(new ArrayList<ControllerAffichageOutils>());
        }
        if(!Controller.getOpenedController().contains(this)){
            Controller.getOpenedController().add(this);
        }
        BackgroundFill bf = new BackgroundFill(Color.rgb(47, 48, 54), CornerRadii.EMPTY , Insets.EMPTY);
        pane.setBackground(new Background(bf));
        moyensGene = MoyenGenerique.unserializeMoyenGene();
        paramTitles = Outil.getParamTitle();
        setParamOfElt();
        listMoyenGene.getItems().clear();
        setComboBoxMoyenGene();
        setVisibility();
        setTable(null);
        int i = Controller.getOpenedController().indexOf(this);
        Controller.getOpenedController().set(i, this);       
    }



    /*Fonctions FXML de la fenêtre d'affichage*/

    /**
     * Récupère la valeur de la comboBox et met à jour l'affichage en fonction de celle-ci
     * @param action
     */
    @FXML
    public void selectMoyenGene(ActionEvent action){
        String value = listMoyenGene.getValue();
        setTable(value);
        currentOutil = null;
    }

    /**
     * Fonction appelée lors de l'appuie sur le bouton "Modifier ligne"
     * Ouvre la fenêtre du formulaire de modification pré-rempli si une
     * ligne a été sélectionné et si aucun autre formulaire n'est ouvert
     * @param action
     */
    @FXML
    public void openModifWindow(ActionEvent action){
        if(currentOutil == null){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, aucune ligne sélectionnée", "Veuillez sélectionner une ligne à modifier.", "Erreur", alert);
        }
        else{
            if(Controller.getCountOpenedForm()>0){
                openFormError();
            }
            else{
                Controller.setForm("modifOutilForm");
                setStage("View/formModifOutil.fxml", "Modification de ligne");
            }
        }
    }

    /**
     * Appelée lors d'un appuie sur le bouton "Nouvelle ligne"
     * Ouvre la fenêtre du formulaire de création de ligne / Outil
     * Ouvre un onglet d'erreur si un autre formulaire est déjà ouvert
     * @param action
     */
    @FXML
    public void openAddLineWindow(ActionEvent action){
        if(Controller.getCountOpenedForm()>0){
            openFormError();
        }
        else{
            Controller.setForm("addOutilForm");
            setStage("View/formModifOutil.fxml", "Ajout de ligne");
        }

    }

    /**
     * Appelée lors d'un appuie sur le bouton "Supprimer ligne"
     * Ouvre un onglet de confirmation afin de valider ou non
     * la suppression de la ligne
     * Actualise la fenêtre et supprime des données l'Outil 
     * associé à la ligne sélectionnée
     * @param action
     */
    @FXML
    public void deleteLine(ActionEvent action){
        /*Si aucun outil n'a été sélectionné*/
        if(currentOutil==null){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, sélection invalide", "Veuillez sélectionner une ligne à supprimer.", "Erreur", alert);
        }
        /*Si un formualaire de modif est déjà ouvert*/
        else if(Controller.getCountOpenedForm()>0){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Conflit possible", "Veuillez fermez les formulaires ouverts avant de supprimer la colonne afin d'éviter les conflits.", "Erreur", alert);
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION, "Supprimer la ligne ?\nL'outil associé sera définitivement supprimé des données.", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if(alert.getResult()==ButtonType.YES){ 
                ArrayList<Element> allElt = Element.unserializeElement();
                /*Recherche de l'élément sélectionné dans les données*/   
                for(int i=0; i<allElt.size();i++){
                    if(allElt.get(i).getCodeElt().equals(selectedElt.getCodeElt())){
                        selectedElt.removeOutil(currentOutil);
                        allElt.set(i, selectedElt);
                        i=allElt.size();//sortie de la boucle
                    }
                }
                Controller.setAllElements(allElt);
                Controller.setDataSaved(false);
                this.initialize(null, null);
                currentOutil = null;
            } 
        }
    }
    
    /**
     * Appelée lors d'un appuie sur le bouton "Nouvelle colonne"
     * Ouvre un formulaire de création de nouvelle colonne si 
     * aucun autre formulaire n'est déjà ouvert
     * @param action
     */
    @FXML
    public void openColumnSetUp(ActionEvent action){
        /*Si un formulaire est déjà ouvert*/
        if(Controller.getCountOpenedForm()>0){
            openFormError();
        }
        else{
            Controller.setForm("newColForm");
            setStage("View/ajoutCol.fxml", "Ajout colonne");
        }
    }

    /**
     * Appelée lors d'un appuie sur le bouton "Modifier colonne"
     * Ouvre la fenêtre du formulaire de modification de colonne
     * si une colonne a été saisie et qu'aucun autre form n'est ouvert
     * @param action
     */
    @FXML
    public void setColumn(ActionEvent action){
        if(currentTitle == null){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, aucune colonne sélectionnée", "Veuillez sélectionner une colonne à modifier.", "Erreur", alert);
        }
        else{
            if(Controller.getCountOpenedForm()>0){
                openFormError();
            }
            else{
                Controller.setForm("modifColForm");
                setStage("View/formModifCol.fxml", "Modification de colonne");
            }
        }
    }

    /**
     * Appelée lors de l'appuie sur le bouton "Supprimer colonne"
     * Ouvre une alerte de confirmation qui confirme ou non la
     * suppression de la colonne. 
     * Supprime la colonne des données et actualise la fenêtre
     */
    @FXML 
    public void deleteColumn(){
        /*Si aucune colonne n'a été sélectioné ou s'il s'agit d'un des 4 premières colonnes*/
        if(currentTitle == null || Colonne.getIndexByTitle(Colonne.unserializeCols(), currentTitle)<4){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, sélection invalide", "Veuillez sélectionner une colonne à supprimer (les 4 premières colonnes ne peuvent pa être supprimées).", "Erreur", alert);
        }
        /*Si une formulaire de modif est déjà ouvert*/
        else if(Controller.getCountOpenedForm()>0){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Conflit possible", "Veuillez fermez les formulaires ouverts avant de supprimer la colonne afin d'éviter les conflits.", "Erreur", alert);
        }
        else{
            Alert alert = new Alert(AlertType.CONFIRMATION, "Supprimer la colonne  " + currentTitle + " ?\nToutes les données associées seront également supprimées.", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if(alert.getResult()==ButtonType.YES){    
                int i = Colonne.getIndexByTitle(Colonne.unserializeCols(), currentTitle);
                int index = Outil.unserializeOrdre().get(i);
                Outil.removeFromAllOutil(index);
                Outil.removeOrdre(i);
                Colonne.removeCol(currentTitle);
                Alert alert2 = new Alert(AlertType.INFORMATION);
                Controller.setAlert("Modifications enregistrées", "La colonne a bien été supprimée", "Confirmation", alert2);
                this.initialize(null, null);
                currentTitle = null;
            } 
        }
    }

    /**
     * Appelée lors d'un appuie sur le bouton "Modifier moyen"
     * Ouvre la fenêtre du formulaire de modification de moyen générique
     * si un moyen valide a été saisi et qu'aucun autre form n'est ouvert
     * @param action
     */
    @FXML
    public void updateMoyen(ActionEvent action){
        if(listMoyenGene.getValue() == null || listMoyenGene.getValue().equals("Tous")){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur : sélection invalide", "Veuillez sélectionner un moyen générique valide à modifier", "Erreur", alert);
        }
        else{
            if(Controller.getCountOpenedForm()>0){
                openFormError();
            }
            else{
                Controller.setForm("modifMoyenForm");
                setStage("View/formModifMoyen.fxml", "Modification de moyen générique");
            }
        }
    }

    /**
     * Appelée lors de l'appuie sur le bouton Enregistrer de la
     * page d'affichage des moyens de tests.
     * Enregistre les données liées aux objets Element de la liste
     * all Ellement.
     * Renvoie un onglet confirmant l"opération
     * @param action
     */
    @FXML
    public void saveData(ActionEvent action){
        Element.serializeAllElements(Controller.getAllElements());
        Controller.setDataSaved(true);
        Alert alert = new Alert(AlertType.INFORMATION);
        Controller.setAlert("Modifications effectuées", "Les modifications apportées ont bien été enregistrées.", "Confirmation", alert);
    }



    /**
     * Récupère l'élément sélectionné depuis la page accueil
     * Récupère les moyens de tests correspondants à cet élément
     * en fonction du mode de test (auto / manuel)
     * Crée le titre de la page
     */
    public void setParamOfElt(){ 
        /*Initialisation de l'élément avec la valeur sélectionnée à l'accueil s'il est null*/
        if(selectedElt == null){
            selectedElt = Controller.getCurrentElement();
            auto = Controller.isAuto();
            manuel = Controller.isManuel();
        }
        /*Mise à jour des données de l'élèment s'il est non-null*/
        else{
            selectedElt = Controller.getElementByCode(selectedElt.getCodeElt());
        }
        /*si les 2 checkboxs ont été cochées*/
        if(auto && manuel){ 
            listOutils = selectedElt.getOutils();
        } 
        /*si une seule des checkbox a été cochée*/
        else { 
            listOutils = selectedElt.getOutilsByMode(auto);
            
        }
        title.setText(selectedElt.toString());
        Outil.sortOutilsByMoyen(listOutils);
    }



    /*Mise en place du tableau*/

    /**
     * Si la liste Outil est vide : renvoie un message indiquant qu'aucun moyen de test n'est diponible.
     * Sinon : Rempli la grille avec les attributs correspondant à chaque moyens de test de l'élément
     * en fonction du moyen générique sélectionné (renvoie le même message si l'élément ne contient 
     * aucun Outil de test pour ce moyen générique).
     * Rempli la grille selon l'ordre enregistré.
     * Met en place les liens hypertextes.
     */
    public void setTable(String selectedMoyenGene){
        clicableItems = new ArrayList<ArrayList<Labeled>>();
        grid.getChildren().clear();//la grille est réinitialisée
        ArrayList<Outil> toPrintOutils = initDisplayedOutils();
        if(toPrintOutils.isEmpty()){
            /*invisibilité de la grille et affichage du label*/
            grid.setVisible(false);
            sp.setVisible(false);
            emptyLabel.setText("Aucun outil de test correspondant à la recherche pour l'élément " + selectedElt.getCodeElt());
            emptyLabel.setVisible(true);
        }
        else{
            setVisibility();
            initColumnTitle(grid);
            int count = 1; //numéro de la ligne actuelle
            /*Parcours de tous les outils de test de l'élément*/
            for(Outil t: toPrintOutils){
                ArrayList<String> listParam = t.getListParam(); //liste des cases de la ligne courante
                ArrayList<Labeled> list = new ArrayList<Labeled>();
                int countP=0; //numéro de la colonne actuelle
                /*Parcours de tous les attributs de l'outil courant*/
                for(int i=0; i<Colonne.unserializeCols().size(); i++){ 
                    String param = listParam.get(Outil.unserializeOrdre().get(i));
                    /*s'il s'agit d'un attribut de type lien*/
                    if(Colonne.unserializeCols().get(i).isHyperLink() && param!="" && param!=null){ 
                        Hyperlink text = setLink(param);
                        list.add(text); //ajout à la liste des cases de la ligne
                        grid.add(text, countP, count); //remplissage de la case courante de grid
                    }
                    else{
                        Label text = new Label(param);
                        /*Les paramètres visuels de text*/
                        setLabelParam(text);
                        text.setFont(Font.font(16));
                        list.add(text); //ajout à la liste des cases de la ligne
                        grid.add(text, countP, count); //remplissage de la case courante de grid
                    }    
                    countP++;
                }
                sp.setPrefHeight(115*(count+1)+20); //ajustement de la hauteur du scrollpane en fct du nb de lignes de grid
                clicableItems.add(list); //ajout de la ligne courante à la liste de lignes de la grille
                count++;
            }
            /*affichage de la grille et invisibilité du label*/
            grid.setVisible(true);
            sp.setVisible(true);
            emptyLabel.setVisible(false);
        }
        selectForModif();
    }

    /**
     * Met en place les "titres" des colonnes du tableau sur la premiere ligne de grid
     */
    public void initColumnTitle(GridPane gridp){
        clicableTitles = new ArrayList<Label>();
        BackgroundFill bf = new BackgroundFill(Color.rgb(255, 248, 186), CornerRadii.EMPTY , Insets.EMPTY);
        gridp.getChildren().clear();
        int count = 0; //colonne courante
        /*Parcours de la liste des "titres"(~nom des variables de Outil) du tableau*/
        for(String s : paramTitles){
            Label txt = new Label(s);
            setLabelParam(txt);
            txt.setFont(Font.font(18));
            txt.setBackground(new Background(bf));
            setClicEvent(txt);
            clicableTitles.add(txt);
            gridp.add(txt, count, 0);
            count++;
        }
    }

    /**
     * Récupère les moyens de tests de l'élément en fonction d'un moyen générique
     * @param moyen le nom du moyen générique voulu
     * @return l'ensemble des outils de tests de l'élément 
     * pour lesquels le moyen générique correspond au paramètre
     */
    public ArrayList<Outil> getOutilsByMoyenGene(String moyen){
        ArrayList<Outil> listOutilByMoy = new ArrayList<Outil>();
        /*Parcours de l'ensemble des outils de test de l'élément*/
        for(Outil t: listOutils){
            if(moyen.equals(t.getMoyenGenerique())){
                listOutilByMoy.add(t);
            }
        }
        return listOutilByMoy;
    }

    /**
     * @return la liste d'outils correspondant à la valeur courante de la ComboBox
     */
    public ArrayList<Outil> initDisplayedOutils(){
        if(listMoyenGene.getValue()==null||listMoyenGene.getValue().equals("Tous")){
            return listOutils;
        }
        else{
            return getOutilsByMoyenGene(listMoyenGene.getValue());
        }
    }

    /**
     * Crée une comboBox contenant la liste des Moyens génériques enregistrés
     * et pour lesquels au moins un des Outils affichés correspond
     * @return la combo box contenant les moyens génériques correspondant à l'affichage courant
     */
    public void setComboBoxMoyenGene(){
        listMoyenGene.getItems().clear();
        listMoyenGene.setStyle("-fx-font-f: 16px;");
        MoyenGenerique.sortMoyenGen(moyensGene);
        /*Sélection des moyens présents dans les outils affichés*/
        for(MoyenGenerique m : moyensGene){
            for(Outil o: listOutils){
                if(o.getMoyenGenerique().equals(m.getNom()) && !listMoyenGene.getItems().contains(m.getNom())){
                    String chaine = m.getNom(); 
                    listMoyenGene.getItems().add(chaine);
                }
            }
            if(m.getNom().equals("Tous")){
                listMoyenGene.getItems().add(m.getNom());
            }
        }
        
    }


    /*Fonctions relatives aux liens hypertextes*/

    /**
     * Création d'un lien hypertexte ouvrant une fenêtre vers le fichier pointé
     * @param link chaine de caractère correspondant au 
     * lien absolu vers le fichier visé
     * @return le lien hypextexte ouvrant le fichier visé
     */
    public Hyperlink setLink(String link){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(link));
        Hyperlink text = new Hyperlink(link);
        text.setOnAction(e -> {
            Lanceur l = new Lanceur();
            File selectedFile = new File(link);
            /*si le fichier selectionné n'existe pas ou n'a pas été trouvé, un onglet alert est ouvert*/
            if(!selectedFile.isFile()){
                /*instanciation de l'alerte*/
                Alert alert = new Alert(AlertType.WARNING);
                Controller.setAlert("Erreur : fichier introuvable", "Le fichier recherché semble avoir été renommé ou déplacé. Veuillez contacter un administrateur afin de mettre à jour son emplacement.", "Erreur", alert);
            }
            /*si le fichier selectionné existe il est ouvert*/
            else{
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(selectedFile);
                } catch (IOException ioException) { //Onglet d'erreur dans le cas ou un fichier exsistant n'est pas ouvrable
                    ioException.printStackTrace();
                    Alert alert = new Alert(AlertType.WARNING);
                    Controller.setAlert("Erreur : ouverture impossible.", "Fichier existant mais impossible à lancer.", "Erreur", alert);
                }
            } 
        }); 
        /*paramètres visuels du lien*/
        setLabelParam(text);
        text.setFont(Font.font(16));
        return text;
    }



    /*Fonction nécessaires au mode administrateur*/

    /**
     * Initilialise la visibilité des élèments de la page 
     * en fonction de l'activation du mode Admin du logiciel
     */
    public void setVisibility(){
        boolean admin = Controller.isAdmin();
        adminMenu.setVisible(admin);
        saveDataButton.setVisible(admin);
    }

    /**
     * Instancie l'évènement de type clic sur toutes les lignes de la grille
     * L'Outil courant sélectionné prend la valeur de l'outil de la ligne si le logiciel
     * est en mode admin
     * Le fond de la ligne change de couleur.
     */
    public void selectForModif(){
        BackgroundFill bf = new BackgroundFill(Color.rgb(204, 255, 179), CornerRadii.EMPTY , Insets.EMPTY); //la couleur de la ligne cliqué
        /*parcours de toutes les lignes de la grille*/
        for(int i=0; i<clicableItems.size(); i++){
            int idOutil = i;
            /*Parcours de chaque case de la grille*/
            ArrayList<Labeled> listAux = clicableItems.get(i);
            for(Labeled lab : listAux){
                lab.setOnMouseClicked(event -> { //ajout de l'évènement à chaque case
                    resetLabels();
                    resetTitles();
                    /*Changement de couleur sur toute la ligne sélectionnée*/
                    for(Labeled label: listAux){
                        label.setBackground(new Background(bf));
                    }
                    if(Controller.isAdmin()) { //ne fonctionne que dans le cas ou le logiciel est en mode admin
                        currentOutil = initDisplayedOutils().get(idOutil);
                        currentTitle = null;
                    }
                });
            }
        }
    }

    /**
     * Instancie un event activé lors d'un clic
     * Sélectionne une colonne et change sa couleur de fond
     * @param l le titre de colonne sélectionnable
     */
    public void setClicEvent(Label l){
        if(Controller.isAdmin()){
            l.setOnMouseClicked(event -> {
                resetLabels();
                resetTitles();
                l.setBackground(new Background(new BackgroundFill(Color.TURQUOISE, CornerRadii.EMPTY , Insets.EMPTY)));
                currentTitle = l.getText();
                currentOutil = null;
            });
        }
    }

    /**
     * Passe la couleur de background de tous les éléments de la grille (sauf titres) en blanc.
     */
    public void resetLabels(){
        BackgroundFill bf = new BackgroundFill(Color.WHITE, CornerRadii.EMPTY , Insets.EMPTY);
        for(ArrayList<Labeled> l: clicableItems){
            for(Labeled lab : l){
                lab.setBackground(new Background(bf));
            }
        }    
    }

    /**
     * Passe la couleur de background de tous les titres en une même couleur
     */
    public void resetTitles(){
        BackgroundFill bf = new BackgroundFill(Color.rgb(255, 248, 186), CornerRadii.EMPTY , Insets.EMPTY);
        for(Label l: clicableTitles){
            l.setBackground(new Background(bf));
        }
    }



    /*Fonctions "diverses"*/

    /**
     * Initialise un Stage dans l'optique d'une ouverture de nouvelle fenêtre
     * et met en place les paramètres de base de ce stage
     * @param fxmlLink le lien du fichier fxml parent du stage crée
     * @return le stage crée et préparé à l'utilisation
     */ 
    public Stage setNewStage(String fxmlLink){
        /*Déclaration de la nouvelle fenetre*/
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(fxmlLink));
            Scene scene  = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage = new Stage();
            stage.getIcons().add(new Image("media/logoActiaPetit.png"));
            stage.setScene(scene);
            stage.setResizable(false);
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initialise et ouvre un nouvel onglet
     * Incrémente le nombre de formulaires ouverts
     * @param xml le lien du fichier fxml correspondant à l'onglet
     * @param title le titre de l'onglet
     */
    public void setStage(String xml, String title){
        ControllerFormOutils.setOriginControl(this);
        /*Création du nouvel onglet*/
        Stage stage = setNewStage(xml);
        stage.setTitle(title);
        /*Ajout d'une action lors de la fermeture de l'onglet depuis la croix rouge*/
        addCloseEvent(stage);
        Controller.setCountOpenedForm(Controller.getCountOpenedForm()+1);
        stage.showAndWait();
    }

    /** 
     * Ajoute une action sur la fermeture d'une fenêtre formulaire qui décrémente 
     * le nb de formulaire ouvert et change en null le nom du form courant
     * @param stage la fenêtre à qui l'action est ajoutée
     */
    public void addCloseEvent(Stage stage){
        stage.setOnCloseRequest(event ->{
            Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1); //décrémentation du compteur de formulaires ouverts
            Controller.setForm("null");           
        });
    }

    /**
     * Rend invisible les boutons permettant des modifications sur les données ainsi
     * que le le bouton de choix de moyen générique
     * Modifie le titre de la page
     */
    public void shutDown(){
        if(listOutils.isEmpty()){
            adminMenu.setVisible(false);
        }
        else{
            Controller.setAdmin(false);
            setVisibility();
            Controller.setAdmin(true);
        }
        listMoyenGene.setVisible(false);
        title.setText("Ensemble supprimé");
        selectedElt=null;
    }

    /**
     * Instancie et ouvre l'onglet d'erreur lié au trop grand nombre de formulaires ouverts
     */
    public void openFormError(){
        Alert alert = new Alert(AlertType.WARNING);
        Controller.setAlert("Erreur formulaire", "Un autre formulaire est déjà ouvert, veuillez le fermer afin d'en ouvrir un nouveau", "Erreur", alert);
    }

    /**
     * Met en place les paramètres visuels d'un Labeled pour son insertion et so
     * affichage correct dans la grille
     * @param l le Labeled à paramétrer
     */
    public void setLabelParam(Labeled l){
        l.setWrapText(true);
        l.setAlignment(Pos.CENTER);
        l.setTextAlignment(TextAlignment.CENTER);
        l.maxHeight(115);
        l.setPrefHeight(115);
        l.maxWidth(200);
        l.setPrefWidth(200);
        l.setStyle("-fx-border-color: grey;");
    }
    

    
    /*Getter et setter*/

    public Outil getCurrentOutil() {
        return currentOutil;
    }

    public void setCurrentOutil(Outil currentOutil) {
        this.currentOutil = currentOutil;
    }

    public static ArrayList<String> getParamTitles() {
        return paramTitles;
    }

    public static ArrayList<MoyenGenerique> getMoyensGene() {
        return moyensGene;
    }

    public static void setMoyensGene(ArrayList<MoyenGenerique> moyensGene) {
        ControllerAffichageOutils.moyensGene = moyensGene;
    }

    public Element getSelectedElt() {
        return selectedElt;
    }

    public void setSelectedElt(Element selectedElt) {
        this.selectedElt = selectedElt;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }

    public ComboBox<String> getListMoyenGene() {
        return listMoyenGene;
    }

    public Pane getPane() {
        return pane;
    }
    
}
