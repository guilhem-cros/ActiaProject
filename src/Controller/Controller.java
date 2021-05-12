package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Config;
import Model.Element;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Controller implements Initializable{

    /*Compteur du nombre de formulaires liés à la page d'affichage d'outils actuellement ouverts*/
    private static int countOpenedForm = 0;

    /*Type du de formulaire ouvert*/
    private static String form;

    /*Mode admin du logiciel*/
    private static boolean isAdmin = false;

    /*Liste de tous les objets Element stockés*/
    private static ArrayList<Element> allElements;

    /*L'element courant ayant été sélectionné*/
    private static Element currentElement;

    /*Le produit (95....) sélectionné dans la ComboBox*/
    private static Element selectedProd;

    private static boolean auto=false;

    private static boolean manuel=false;

    /*liste des items sélectionnables du menu*/
    private ArrayList<RadioMenuItem> usableMenuItems;

    /*Liste des ControllerAffichageOutils (=pages d'affichages) ouvertes*/
    private static  ArrayList<ControllerAffichageOutils> openedController;
    
    /*Liste des ControllerAffichageOutils (=pages d'affichages) ouvertes*/
    private static  ArrayList<ControllerAffichageLogs> openedControllerLogs;


    /*Initialisation des objets XML utilisés*/

    @FXML
    private ImageView actiaLogo;
   
    @FXML
    private Pane paneId;

    @FXML
    private Button valideButton;

    @FXML
    private Button configButton;

    @FXML
    private CheckBox autoBox;

    @FXML
    private CheckBox manuelBox;

    @FXML 
    private ComboBox<String> productList;

    @FXML
    private MenuButton elementButton;

    @FXML
    private TextField selectedElement;

    @FXML 
    private TextField searchField;

    @FXML
    private VBox autoCompleteBox;

    @FXML 
    private ScrollPane searchResultsPane;

    @FXML
    private Button helpButton;

    @FXML
    private Button logsButton;

    @FXML
    private Label infoConnect;

    @FXML
    private Label id;

    @FXML
    private Label mdp;

    @FXML
    private Label confirmMdp;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField password2;

    @FXML
    private Button confirmButton;

    @FXML
    private Button backButton;

    @FXML
    private Button adminQuitButton;

    @FXML 
    private Button reInitButton;

    @FXML
    private Button addEltButton;

    @FXML
    private Button deleteEltButton;

    @FXML
    private Button updateEltButton;

    @FXML
    private Button addSubButton;

    @FXML
    private Button removeSubButton;

    @FXML
    private Label lab1;

    @FXML
    private Label lab2;

    @FXML
    private Label lab3;



    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BackgroundFill bf = new BackgroundFill(Color.rgb(47, 48, 54), CornerRadii.EMPTY , Insets.EMPTY);
        paneId.setBackground(new Background(bf));
        setSearch();
        productList.getItems().clear();
        productList.getItems().addAll(createComboBox().getItems());
        Config.unserializeConfig(); //initilisation de la config (mdp + log) actuelle
        form = "null";
        
    }



    /*Events FXML*/

    /**
     * Fonction appelé lors d'un changement de valeur dans la comboBox de produits
     * Récupère le produit sélectionné et et lance la création 
     * des menus d'éléments/sous-élèments correspondant à ce produit
     * @param action
     */
    @FXML
    public void selectProduct(ActionEvent action){
        setDisplayModifButtons(isAdmin);
        resetVisibility();
        searchField.setText("");
        if(productList.getValue()!=null){
            selectedProd = getElementByCode(sliceCode(productList.getValue().toString()));
            elementButton.setVisible(true);
            createElementMenu();
            initUsableEvent();
        }
    }

    /**
     * Fonction appelée lors d'un appuie sur le bouton "Valider" du menu principal
     * Ouvre une nouvel onglet affichant les outils correspondants à l'élement sélectionné
     * ou un onglet d'erreur
     * @param action
    */
    @FXML
    public void validate(ActionEvent action){
        resetVisibility();
        /*dans le cas ou aucun élément/produit n'a été sélectionné*/
        if(currentElement == null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun élément sélectionné", "Veuillez sélectionner un produit et un ensemble avant de valider la recherche.", "Erreur", alert);
        }
        /*Si l'élèment sélectionné ne possède aucun objet Outil enregistré*/
        else if(currentElement.hasNoOutil() && !isAdmin){
            Alert alert = new Alert(AlertType.INFORMATION);
            setAlert("Aucun résultat disponible", "Aucun moyen de test n'a été enregistré pour cet ensemble.", "Information", alert);
        }
        /*dans le cas ou aucun mode d'utilisation n'a été sélectionné*/
        else if((auto==false && manuel==false  && currentElement.hasAutoOutils() && currentElement.hasManuelOutils())||(auto==false && manuel==false  && isAdmin)){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun mode sélectionné", "Veuillez sélectionner au minimun un mode d'utilisation avant de valider la recherche.","Erreur", alert);
        }
        /*Si aucune erreur détectée, création et ouverture de la nouvelle fenetre*/
        else{
            /*Si l'ensemble n'a que des outils dit "auto"*/
            if(currentElement.hasAutoOutils() && !currentElement.hasManuelOutils() && !isAdmin){
                auto = true;
                manuel = false;
            }
            /*Si l'ensemble n'a que des outils dit "manuel"*/
            else if(!currentElement.hasAutoOutils() && currentElement.hasManuelOutils() && !isAdmin){
                auto = false;
                manuel = true;
            }
            /*Déclaration de la nouvelle fenetre*/
            Stage stage = setNewStage("../View/outils.fxml");
            /*Evènement de fermeture de fenêtre : retire le controllerAffichage correspondant de la liste des affichages ouverts*/
            stage.setOnCloseRequest(event ->{
                for(int i=0; i<openedController.size(); i++){
                    if(openedController.get(i).getPane().getScene().getWindow().equals(stage)){
                        openedController.remove(i);
                    }
                }
            });
            stage.setTitle("Constitution Bancs de Test : " + currentElement.toString());
            /*Reset de la page d'accueil*/
            currentElement = null;
            selectedElement.setText("");
            searchField.setText("");
            resetElt();
            if(usableMenuItems!=null){
                for(RadioMenuItem item: usableMenuItems){ 
                    item.setSelected(false);
                }
            }
            stage.showAndWait();
        }
    }

    /**
     * Fonction appelée lors d'un changement de valeur de la checkbox "Auto"
     * Permettra la sélection des outils autos
     * @param action
    */
    @FXML
    public void setAuto(ActionEvent action){
        resetVisibility();
        auto = !auto;
    }

    /**
     * Fonction appelée lors d'un changement de valeur (coché ou non) de la checkbox "Manuel"
     * Permettra la sélection des outils manuels 
     * @param action
    */
    @FXML
    public void setManuel(ActionEvent action){
        resetVisibility();
        manuel = !manuel;
    }

    /**
     * Fonction appelée lors d'un appuie sur le bouton "Mots de passe" du menu principal
     * Ouvre une nouvel onglet affichant les mots de passes et logs correspondants à l'élement sélectionné
     * Ouvre un onglet d'erreur indicatif si aucun élement/produit n'a été sélectionné
     * @param action
    */
    @FXML
    public void openLogs(ActionEvent action){
        resetVisibility();
        /*dans le cas ou aucun élément/produit n'a été sélectionné*/
        if(currentElement == null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun élément sélectionné", "Veuillez sélectionner un produit et un ensemble avant de valider la recherche.", "Erreur",  alert);
        }
        /*Si aucune erreur décectée, création et ouverture de la nouvelle fenetre*/
        else{
            /*Déclaration de la nouvelle fenetre*/
            Stage stage = setNewStage("../View/logs.fxml");
            /*Ajout d'un évènement de fermeture de fenêtre*/
            stage.setOnCloseRequest(event ->{
                for(int i=0; i<openedControllerLogs.size(); i++){
                    if(openedControllerLogs.get(i).getPane().getScene().getWindow().equals(stage)){
                        openedControllerLogs.remove(i);
                    }
                }
            });
            stage.setTitle("Mots de passe : " + currentElement.toString());
            stage.showAndWait();
        }
    }

    /**
     * Fonction appelée lors d'un appuie sur le bouton "Configurer" du menu principal
     * Affiche les items permettant la connexion de l'administrateur et le passage en 
     * mode admin s'il n'est pas déjà activé.
     * Affiche les items permettant la modification de l'identifiant et mot de passe
     * si le mode admin est actif.
     * @param action
    */
    @FXML
    public void openConfig(ActionEvent action){
        /*Gestion de l'affichage en fonction du monde admin*/
        if(isAdmin){
            infoConnect.setText("Entrez le nouvel identifiant et le nouveau mot de passe que vous souhaitez utiliser : ");
            password2.setVisible(true);
            confirmMdp.setVisible(true);
            adminQuitButton.setVisible(true);
            reInitButton.setVisible(false);
        }
        else{
            infoConnect.setText("Entrez les identifiants de connexion afin de pouvoir configurer le logiciel");
            password2.setVisible(false);
            confirmMdp.setVisible(false);
            adminQuitButton.setVisible(false);
            reInitButton.setVisible(true);
        }
        /*Affichage de la page d'identification*/
        login.setText("");
        password.setText("");
        password2.setText("");
        infoConnect.setVisible(true);
        id.setVisible(true);
        mdp.setVisible(true);
        login.setVisible(true);
        password.setVisible(true);
        confirmButton.setVisible(true);
        backButton.setVisible(true);
        /*Invisibilité de la page d'accueil*/
        setMenuDisplay(false);
        setDisplayModifButtons(false);
    }
     
    /**
     * Fonction appelé lors du l'appuie sur le bouton "confirmer" du menu Configurer.
     * Si le mode admin n'est pas actif, vérifie la validité du login et mot de passe saisi 
     * et passe le logiciel en mode administrateur
     * Si le mode admin est actif, modifie le login et le mdp par ceux saisis
     * en vérifiant si les deux mot de passes saisis sont les mêmes
     * @param action
     */
    @FXML
    public void confirmToConfig(ActionEvent action){
        /*Si le logiciel n'est pas en mode admin*/
        if(!isAdmin){
            /*Si l'un des deux champs n'a pas été rempli*/
            if(login.getText().equals("") || password.getText().equals("")){
                Alert alert = new Alert(AlertType.WARNING);
                setAlert("Erreur : certains champs sont vides", "Veuillez saisir un identifiant et un mot de passe afin de passer le logiciel en mode administrateur.", "Erreur", alert);
            }
            /*Si l'identifiant est valide*/
            else if(login.getText().equals(Config.getLogin())){
                /*Si le mot de passe est valide */
                if(password.getText().equals(Config.getPassword())){
                    isAdmin = true;
                    backToMenu(action);
                    /*Ouverture d'une alerte informative quant au succès de l'action*/
                    Alert alert = new Alert(AlertType.INFORMATION);
                    setAlert("Logiciel en mode administrateur", "Vous avez désormais accès à l'ajout, la modification et la suppression d'éléments.", "Confirmation", alert);
                    alert.setTitle("Confirmation");
                }
                /*Si le mot de passe est incorrect */
                else{
                    /*Onglet d'erreur de mot de passe*/
                    Alert alert = new Alert(AlertType.WARNING);
                    setAlert("Erreur : le mot de passe ne correspond pas", "Veuillez vérifier que le mot de passe saisi correspond bien au mot de passe enregistré.", "Erreur", alert);
                }
            }
            /*Si l'identifiant est invalide*/
            else{
                /*Onglet d'erreur d'identifiants*/
                Alert alert = new Alert(AlertType.WARNING);
                setAlert("Erreur : identifiant inconnu", "Veuillez vérifier que le l'identifiant saisi correspond bien à l'identifiant enregistré.", "Erreur",  alert);
            }
        }
        /*Si le logiciel est en mode admin*/
        else{
            if(login.getText().length()<4 || password.getText().length()<4){
                Alert alert = new Alert(AlertType.WARNING);
                setAlert("Erreur : un des champs saisi est trop court", "Veuillez entrer un login et un mot de passe d'au minimum 4 caractères", "Erreur", alert);
            }
            else if(password.getText().equals(password2.getText())){
                Config.serializeConfig(password.getText(), login.getText());
                backToMenu(action);
                /*Ouverture d'une alerte informative quant au succès de l'action*/
                Alert alert = new Alert(AlertType.INFORMATION);
                setAlert("Modifications enregistréees", "Les modifications apportées ont bien été enregistrées", "Confirmation", alert);
                alert.setTitle("Confirmation");
            }
            else{
                Alert alert = new Alert(AlertType.WARNING);
                setAlert("Erreur : les mots de passe ne correspondent pas", "Les deux mots de passes saisis sont différents.", "Erreur",alert);
            }
        }
    }

    /**
     * Fonction appelée lors de l'appuie sur le bouton "Réinitialiser identifiants"
     * Supprime les identifiants de connexion admin actuels et les remplace
     * par les identifiants de base
     * @param action
     */
    @FXML
    public void reInitConfig(ActionEvent action){
        Config.serializeConfig("actia", "admin");
        Alert alert = new Alert(AlertType.INFORMATION);
        setAlert("Login réinitisalisé", "L'identifiant et le mot de passe de connexion ont bien été réinitialisés.", "Confirmation", alert);
    }

    /**
     * Fonction appelée lors de l'appuie sur le bouton "Quitter le mode Admin".
     * Désacive le mode administrateur du logiciel et retourne sur le menu d'accueil
     * Ouvre un onglet d'alerte confirmant la réalisation de l'action
     * @param action
     */
    @FXML
    public void exitAdminMode(ActionEvent action){
        isAdmin = false;
        backToMenu(action);
        Alert alert = new Alert(AlertType.INFORMATION);
        setAlert("Fin du mode Administrateur", "Le logiciel n'est plus en mode administrateur, seuls les affichages de données sont disponibles", "Confirmation", alert);
    }

    /**
     * Fonction appelé lors d'un appuie sur le bouton "Retour"
     * Masque la sous-page actuellement affichée et affiche l'accueil
     * @param action
     */
    @FXML
    public void backToMenu(ActionEvent action){
        /*Invisibilité de la page d'identification*/
        infoConnect.setVisible(false);
        id.setVisible(false);
        mdp.setVisible(false);
        login.setVisible(false);
        password.setVisible(false);
        confirmButton.setVisible(false);
        backButton.setVisible(false);
        password2.setVisible(false);
        confirmMdp.setVisible(false);
        adminQuitButton.setVisible(false);
        reInitButton.setVisible(false);
        setDisplayModifButtons(isAdmin);
        /*Affichage de la page d'accueil*/
        setMenuDisplay(true);
    }

    @FXML
    public void displayHelp(ActionEvent action){
        resetVisibility();
    }

    /**
     * Appelée lors d'un clic sur le champs de recherche
     * Rend visible le résultat de la recherche courante si la valeur
     * du champs n'est pas vide
     * @param clic
     */
    @FXML
    public void clicOnSearch(MouseEvent clic){
        if(searchField.getText().length()>0){
            searchResultsPane.setVisible(true);
            autoCompleteBox.setVisible(true);
        }
    }

     /**
     * Appelée lors d'un clic sur un autre élèment que la barre de recherche sur le
     * menu d'accueil.
     * Masque les résultats de la recherche courante.
     * @param clic
     */
    @FXML
    public void clickOut(MouseEvent clic){
        resetVisibility();
    }

    /**
     * Appelée lors d'un clic sur la bouton "Ajouter un ensemble"
     * Ouvre le formulaire de création d'ensemble si aucun autre formulaire
     * n'est déjà ouvert sur l'application, ouvre un onglet d'erreur sinon
     * @param action
     */
    @FXML 
    public void addEnsemble(ActionEvent action){
        if(countOpenedForm==0){
            form="addElementForm";
            setFormStage("../View/formModifEnsemble.fxml", "Nouvel ensemble");
        }
        else{
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : formulaire déjà ouvert", "Un autre formulaire de modification est déjà ouvert, veuillez le fermer avant de continuer.", "Erreur", alert);
        }  
    }

    /**
     * Appelée lors d'un clic sur le bouton "Modifier l'ensemble"
     * Ouvre le formulaire de modification de l'ensemble si aucun autre form n'est
     * déjà ouvert et si un ensemble a été sélectionné, ouvre un onglet d'erreur sinon
     * @param action
     */
    @FXML
    private void updateEnsemble(ActionEvent action){
        /*Si aucun element n'est sélectionné : erreur*/
        if(currentElement==null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun ensemble sélectionné", "Veuillez sélectionner un ensemble à modifier.", "Erreur", alert);
        }
        /*Si un autre form est ouvert : erreur préventive*/
        else if(countOpenedForm>0){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : formulaire déjà ouvert", "Un autre formulaire de modification est déjà ouvert, veuillez le fermer avant de continuer.", "Erreur", alert);
        }
        else{
            form = "updateElementForm";
            setFormStage("../View/formModifEnsemble.fxml", "Modification d'ensemble");
        }
    }

    /**
     * Appelée lors d'un clic sur le bouton "Supprimer l'ensemble"
     * Ouvre un onglet de confirmation de suppression, si l'utilisateur confirme,
     * l'élément courant est supprimé
     * Ouvre un onglet d'erreur si aucun element n'est sélectionné ou si un formulaire
     * de modification est ouvert
     * @param action
     */
    @FXML
    private void deleteEnsemble(ActionEvent action){
        /*Si aucun element n'est sélectionné*/
        if(currentElement==null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun ensemble sélectionné", "Veuillez sélectionner un ensemble à supprimer.", "Erreur", alert);
        }
        else{
            /*Si aucun formulaire de modification n'est ouvert*/
            if(countOpenedForm==0){
                Alert alert = new Alert(AlertType.CONFIRMATION, "Supprimer l'ensemble'  " + currentElement.toString() +
                     " ? Toutes les données associées disparaitront avec lui (mots de passes, outils, etc).", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait(); 
                if(alert.getResult()==ButtonType.YES){
                    allElements.remove(currentElement);
                    /*Suppression de l'element dans toutes les listes de sous-element qui le contiennent*/
                    for(Element e: allElements){
                        e.removeElement(currentElement);
                    }
                    Element.serializeAllElements(allElements);
                    Alert alert2 = new Alert(AlertType.INFORMATION);
                    setAlert("Modifications enregistrées", "L'element a été supprimé des données avec succès.", "Confirmation", alert2);
                    shutDownConcernedWindows(currentElement);
                    initialize(null, null);
                    isAdmin = true;
                    resetAccueil();
                } 
            }
            /*Si un formulaire est ouvert*/
            else{
                Alert alert3 = new Alert(AlertType.WARNING);
                setAlert("Conflits potentiels", "Veuillez fermez les formulaires ouverts avant d'eefectuer de nouvelles modifications.", "Erreur", alert3);
            }
        }
    }

    /**
     * Appelée lors d'un clic sur le bouton "Ajouter un sous-ensemble".
     * Ouvre un onglet d'erreur si aucun ensemble n'a été selectionné ou 
     * si un formulaire est ouvert.
     * Ouvre l'onglet d'ajout de sous-ensemble sinon.
     */
    @FXML
    private void addSubElt(ActionEvent action){
        if(currentElement==null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun ensemble sélectionné", "Veuillez sélectionner un ensemble pour lequel un sous-ensemble doit être attribué", "Erreur", alert);
        }
        else if(countOpenedForm!=0){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : formulaire déjà ouvert", "Un autre formulaire de modification est déjà ouvert, veuillez le fermer avant de continuer.", "Erreur", alert);
        }
        else{
            form="addSubForm";
            setFormStage("../View/formAddSub.fxml", "Ajout d'un sous-ensemble");
            createElementMenu();
        } 
    }    

    /**
     * Appelée lors d'un clic sur le bouton "Supprimer un sous-ensemble".
     * Ouvre un onglet d'erreur si aucun ensemble n'a été sélectionné, si un autre formulaire
     * est ouvert, ou si l'ensemble sélectionné ne possède pas de sous-ensembles.
     * Ouvre l'onglet de suppression d'ensemble sinon.
     * @param action
     */
    @FXML
    private void removeSubElt(ActionEvent action){
        if(currentElement==null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun ensemble sélectionné", "Veuillez sélectionner un ensemble pour lequel un sous-ensemble doit être attribué", "Erreur", alert);
        }
        else if(currentElement.getListeSousElements().size()==0){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun ensemble supprimable", "L'ensemble sélectionné ne possède aucun sous ensemble à supprimer.", "Erreur", alert);
        }
        else if(countOpenedForm!=0){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : formulaire déjà ouvert", "Un autre formulaire de modification est déjà ouvert, veuillez le fermer avant de continuer.", "Erreur", alert);
        }
        else{
            form="removeSubForm";
            setFormStage("../View/formRemoveSub.fxml", "Suppression d'un sous-ensemble");
            createElementMenu();
        }
    }
    

    /*Récupération d'élément par chaines de caractères*/

    /**
     * Récupère un objet Element dans la liste à partir de son code
     * @param code de l'élément voulu
     * @return l'élement pour lequel le code est égal au paramètre code
    */
    public static Element getElementByCode(String code){
        for(Element elt: allElements){
            if (elt.getCodeElt().equals(code)){
                return elt;
            }
        }
        return null;
    }

    /**
     * Récupère sous forme de chaine de caractères le code de l'item sélectionné
     * @param item l'item sélectionné
     * @return uniquement le code depuis le texte de item
     */
    public static String getCodeByItem(MenuItem item){
        return sliceCode(item.getText());
    }

    /**
     * Récupère uniquement le code d'un élèment depuis une chaine de caractère
     * @param chain une chaine de caractère de type : "codeElement Nom de l'élèment"
     * @return le code contenu dans la chaine de caractère
     */
    public static String sliceCode(String chain){
        String code = new String();
        int i =0;
        while(chain.charAt(i)!=' '){
            code+=chain.charAt(i);
            i++;
        }
        return code;
    }

    /**
     * Récupère uniquement le code d'un Element depuis une chaine de caractère
     * dans laquelle il se trouve à la fin
     * @param chain la chaine de cractère de type : "nomElement codeElement"
     * @return le code ontenu dans la chaine de caractère
     */
    public static String sliceInvertCode(String chain){
        String code = new String();
        int i = chain.length()-1;
        while(chain.charAt(i)!=' '){
            code = chain.charAt(i) + code;
            i--;
        }
        return code;
    }



    /*Mise en place et affichage du menu déroulant et sous-menus*/
 
    /**
     * Met sous la forme de menuButton les éléments issus du produit sélectionné 
     * Ajoute les sous menus correspondant à ces éléments si nécessaire
     * Ajoute ces menus aux items de la liste déroulante d'ensembles
     */
    public void createElementMenu(){  
        usableMenuItems = new ArrayList<RadioMenuItem>(); //initilisation de la liste d'items sélectionnables
        elementButton.getItems().clear();
        RadioMenuItem baseItem = new RadioMenuItem(selectedProd.toString());
        elementButton.getItems().add(baseItem); //ajout de l'élément selectedProd à sa propre liste
        usableMenuItems.add(baseItem); //ajout de l'élément selectedProd à la liste d'items cliquables
        /*Si la liste de sous élément de slectedProd n'est pas vide*/
        if(!selectedProd.getListeSousElements().isEmpty()){
            ArrayList<Element> listElt = selectedProd.getListeSousElements();
            Element.sortElements(listElt);
            for(Element elt: listElt){
                String chaine = elt.toString();
                /*Si l'élément courant possède des sous éléments, on l'ajoute au menu en que sous menu*/
                if(!elt.hasNoSubElmt()){
                    Menu menu = new Menu(chaine);
                    setSubMenu(menu);
                    elementButton.getItems().add(menu);
                }
                /*Si l'élément courant ne possède pas de sous élèment on l'ajoute en tant que "bouton radio"*/
                else{
                    RadioMenuItem item = new RadioMenuItem(chaine);
                    elementButton.getItems().add(item);
                    usableMenuItems.add(item);
                }
            }  
        }
	}

    /**
     * Crée récursivement un sous menu d'éléments pour l'élément sélectionné
     * @param item l'item auquel on crée un sous menu
     */
    public void setSubMenu(Menu item){    
        Element courant = getElementByCode(getCodeByItem(item));
        RadioMenuItem baseItem = new RadioMenuItem(item.getText());
        item.getItems().clear();
        item.getItems().add(baseItem); //Ajout de l'item de base au menu, sous forme d'ItemMenu (sans sous menu)
        usableMenuItems.add(baseItem); //Ajout de cet item à la liste des items sélectionnables
        ArrayList<Element> sousListe = courant.getListeSousElements();
        Element.sortElements(sousListe);
        for(Element e: sousListe){
            String chaine = e.toString();
            /*Si l'élément courant ne possède pas de sous élèment on l'ajoute en tant que "bouton radio"*/
            if(e.getListeSousElements().isEmpty()){
                RadioMenuItem sousItem= new RadioMenuItem(chaine);
                item.getItems().add(sousItem);               
                usableMenuItems.add(sousItem); //Ajout de cet item à la liste des items sélectionnables
            }
            /*Si l'élément courant possède des sous éléments, on l'ajoute au menu en que sous menu*/
            else{
                Menu sousItemMenu = new Menu(chaine);
                item.getItems().add(sousItemMenu);
                setSubMenu(sousItemMenu); //appel récursif : tant que les éléments parcourus possèdent des sous éléments->création de sous menu
            }
        }
    } 

    /**
     * Récupère les éléments enregistrés depuis la data et ajoute les éléments 
     * de type produit (code=95..)en tant à la comboBox "produits"
     * @return la ComboBox contenant l'ensemble des produits (95...)
     */
    public ComboBox<String> createComboBox(){
        allElements = Element.unserializeElement();
        Element.sortElements(allElements);
        ComboBox<String> list = new ComboBox<String>();
        list.getItems().clear();
        list.setStyle("-fx-font: 16px;");//police d'écriture en taille 16px
        for(Element elt: allElements){
			if(elt.isProduit()){
				String chaine = elt.toString();
                list.getItems().add(chaine);
			}
        }
        return list;

    }



    /*Utilisation et évènements relatifs au menu*/

    /**
     * Met à jour l'élément selectionné courrant
     * Affiche dans le champs de texte le nom et code de l'élément sélectionné
     * @param item l'item sélectionné par un clic
     */
    public void selectElement(RadioMenuItem item){
        currentElement = getElementByCode(getCodeByItem(item));
        selectedElement.setText(item.getText());
        /*On affiche les checkboxs uniquement si un choix est possible*/
        setCheckBoxes(currentElement);
    }

    /**
     * Met en place l'affichage des checkboxes en fonction de la possibilité
     * d'un choix pour le produit sélectionné (cad que si l'ensemble possède 
     * outil auto et manuel, on affiche les checkbox, sinon, l'affichage des
     * outils est direct)
     * @param e l'element pour lequel on met en place l'affichage des checkboxes ou non
     */
    public void setCheckBoxes(Element e){
        if(e.hasAutoOutils() && e.hasManuelOutils()|| isAdmin){
            autoBox.setVisible(true);
            manuelBox.setVisible(true);
        }
        else {
            autoBox.setVisible(false);
            manuelBox.setVisible(false);
        }
    }

    /**
     * Parcours la liste des radiosItem du menu d'ensembles et 
     * crée les  évènements correspondant pour tous ces radioItems
     */
    public void initUsableEvent(){
        for(RadioMenuItem item:usableMenuItems){
            item.setOnAction((event) ->{
                resetVisibility();
                RadioMenuItem source = (RadioMenuItem) event.getSource();
                /*si l'élement est déssélectionné*/
                if(!source.isSelected()){
                    currentElement = null;
                    selectedElement.setText("");
                    searchField.setText("");
                    resetElt();
                }
                /*Si l'élément est sélectionné*/
                else{
                    uncheckItemsButThis(item); 
                    selectElement(item);
                }
            });
        }
    }

    /**
     * Parcours la liste des radiosItem du menu et les rend tous 
     * unchecké sauf l'élément sélectionné
     * @param i l'item sélectionné lors d'un clic
     */
    public void uncheckItemsButThis(RadioMenuItem i){
        for(RadioMenuItem item: usableMenuItems){
            if(i==null || !i.equals(item)){
                item.setSelected(false);
            }
        }
    }

    /**
     * Repasse la valeur de l'élément courant à null et réinitialise les objets 
     * liés (checkbox, textField)
     */
    public void resetElt(){
        currentElement = null;
        autoBox.setSelected(false); //les checkbox sont décochées et leur valeur repasse à false
        manuelBox.setSelected(false);
        manuel = false;
        auto = false;
        autoBox.setVisible(false); //les checkbox redeviennent invisibles
        manuelBox.setVisible(false);
        selectedElement.setText("");
    }


    /*Fonctions relatives à la recherche et sélection d'elt via barre de recherche*/

    /**
     * Instancie l'évènement provoquant la mise à jour de la liste d'élèments
     * correspondant à la recherche
     * L'événement est appelé pour chaque modification du texte dans le champs de recherche 
     */
    public void setSearch(){
        searchField.textProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> observable,
                String oldValue, String newValue){
                    fillVBox(searchField.getText());
                }
        });
    }

    /**
     * Instancie une liste de chaines de caractère correspondant aux nom et code 
     * de chaque élèment sélectionné selon une chaine de caractère spécifique
     * @param value la chaine de caractère similaire au code / nom des élèments sélectionnées
     * @return la liste des noms et codes de chaque élément correspondant au paramètre
     */
    public static ArrayList<String> setSearchedElements(String value){
        ArrayList<String> elementsList = new ArrayList<>();
        for(Element e : Element.selectByCode(value)){
            elementsList.add(e.toString());
        }
        for(Element e : Element.selectByName(value)){
            elementsList.add(e.getNom() + " " + e.getCodeElt());
        }
        return elementsList;
    }

    /**
     * Complète la VBox des résultats de recherche par des Labels contenant le nom et le
     * code de chaque ensemble correspondant à la recherche
     * Affiche le résultat de recherche s'il n'est pas vide
     * @param value la valeur sur laquelle s'effectue la recherche
     */
    public void fillVBox(String value){
        autoCompleteBox.getChildren().clear();
        /*Si la valeur du champs de recherche n'est pas vide*/
        if(value.length()!=0){
            searchResultsPane.setPrefHeight(0);
            for(String s : setSearchedElements(value)){
                Label l = new Label(s);
                l.setPrefHeight(26);
                l.setPrefWidth(280);
                setMouseOverEvent(l);
                setMouseOutEvent(l);
                setClickedEvent(l);
                autoCompleteBox.getChildren().add(l);
                /*Adaptation de la hauteur du scrollpane en fct du nombre de résultats de la recherche*/
                searchResultsPane.setPrefHeight(searchResultsPane.getPrefHeight() + 26);
            }
            /*Si le résultat de la recherche n'est pas vide : affichage*/
            if(autoCompleteBox.getChildren().size()!=0){
                searchResultsPane.setVisible(true);
                autoCompleteBox.setVisible(true);
            }
            /*Si le résulat de la recherche est vide*/
            else{
                searchResultsPane.setVisible(false);
                autoCompleteBox.setVisible(false);
            }   
        }
        /*Invisibilité des zones de résultats si aucun résultat n'est dispo*/
        else{
            searchResultsPane.setVisible(false);
            autoCompleteBox.setVisible(false);
        }
    }

    /**
     * Masque les résultats et la zone de résultat de la recherche
     */
    public void resetVisibility(){
        searchResultsPane.setVisible(false);
        autoCompleteBox.setVisible(false);
    }

    /**
     * Instancie l'événement appelée lors d'un passage du cuseur sur un label de résultat
     * Le label pointé est alors écrit en gras et sa couleur de fond est modifiée
     * @param l le label visable par le curseur
     */
    public static void setMouseOverEvent(Label l){
        BackgroundFill bf = new BackgroundFill(Color.rgb(77, 106, 255), CornerRadii.EMPTY , Insets.EMPTY);
        l.setOnMouseEntered((event) -> {
            l.setBackground(new Background(bf));
            l.setStyle("-fx-font-weight: bold");
        });

    }

    /**
     * Instancie l'évènement appelée lorsque le curseurs sort de la zone d'un label
     * La couleur de fond du label rdevient celle de base et la police n'est plus en gras
     * @param l le label précédemment visé par le curseur
     */
    public static void setMouseOutEvent(Label l){
        BackgroundFill bf = new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY , Insets.EMPTY);
        l.setOnMouseExited((event) -> {
            l.setBackground(new Background(bf));
            l.setStyle("-fx-font-weight: regular");
        });
    }

    /**
     * Instancie l'évènement appelée lorsqu'un label de la liste des résultats de recherche
     * est cliqué
     * Recupère l'Element lié au label et passe celui-ci en element courant
     * Masque le menu et le champs de texte lié à la sélection d'élèment par 
     * liste déroulante et réinitialise la valeur de la de liste produits à null
     * @param l un label résultat de recherche
     */
    public void setClickedEvent(Label l){
        l.setOnMouseClicked((event) -> {
            resetElt();
            searchField.setText(l.getText());
            selectedElement.setText(l.getText());
            if(Character.isDigit(l.getText().charAt(0))){
                currentElement = getElementByCode(sliceCode(l.getText()));
            }
            else{
                currentElement = getElementByCode(sliceInvertCode(l.getText()));
            }
            resetVisibility();
            elementButton.setVisible(false);
            productList.setValue(null);
            setCheckBoxes(currentElement);
        });
    }



    /*Affichage */

    /**
     * Fonction instanciant et ouvrant un onglet de type alerte
     * @param header le tetxe du header de l'onglet retourné
     * @param content le texte explicatif dans l'onglet retourné
     * @param title le titre de l'onglet
     * @param alert l'alerte instanciée
     */ 
    public static void setAlert(String header, String content, String title, Alert alert){
        alert.setTitle(title);
        TilePane alertpane = new TilePane();
        Scene scene = new Scene(alertpane, 320, 240);
        Stage stage = new Stage();
        stage.setScene(scene);
        try {
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait(); //ouverture de l'onglet d'erreur
            /*L'onglet d'accueil reste ouvert mais innaccessible tant que l'onglet erreur n'a pas été fermé*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche ou rend invisible le menu d'accueil de l'application
     * @param visible true pour afficher, false pour masquer
     */
    public void setMenuDisplay(boolean visible){
        helpButton.setVisible(visible);
        configButton.setVisible(visible);
        productList.setVisible(visible);
        valideButton.setVisible(visible);
        logsButton.setVisible(visible);
        searchField.setVisible(visible);
        lab1.setVisible(visible);
        lab2.setVisible(visible);
        lab3.setVisible(visible);
        elementButton.setVisible(false);
        selectedElement.setVisible(visible);
        if(visible==false){
            productList.getSelectionModel().clearSelection(); //réinitialisation de la liste des produits
            resetElt();
            resetVisibility();
        }
    }

    /**
     * Modifie la visibilité des boutons disponibles uniquement en mode admin
     * @param displayed le boolean de l'affichage des boutons : affichés pour true,
     * invisibles pour false
     */
    public void setDisplayModifButtons(Boolean displayed){
        addEltButton.setVisible(displayed);
        updateEltButton.setVisible(displayed);
        deleteEltButton.setVisible(displayed);
        addSubButton.setVisible(displayed);
        removeSubButton.setVisible(displayed);
    }



    /*Fonctions diverses*/

    /**
     * Initialise un Stage dans l'optique d'une ouverture de nouvelle fenêtre
     * et met en place les paramètres de base de ce stage
     * @param fxmlLink le lien du fichier fxml parent du stage crée
     * @return le stage crée et préparé à l'utilisation
     */ 
    public Stage setNewStage(String fxmlLink){
        /*Déclaration de la nouvelle fenetre*/
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlLink));
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
     * Initialise un stage dans l'optique d'une ouverture de fenêtre de type formulaire
     * Met en place les paramètres du stage et instancie un évènement de fermeture de
     * fenêtre qui décrémente le nb de formulaires ouverts
     * Met à jour l'affichage nécessaire une fois la fenêtre fermée
     * @param fxml le lien vers le code fxml du stage ouvert
     * @param title le titre de la fenêtre ouverte
     */
    public void setFormStage(String fxml, String title){
        Stage stage = setNewStage(fxml);
        stage.setOnCloseRequest(event ->{
            countOpenedForm --;
        });
        stage.setTitle(title);
        countOpenedForm ++;
        ControllerFormEnsembles.setOriginControll(this);
        stage.showAndWait();
        /*Une fois la fenêtre fermée*/
        if(currentElement==null){
            resetAccueil();
        } 
    }

    /**
     * Remet la page d'accueil à zéro : l'affichage redevient l'affichage présent à
     * l'ouverture de la fenêtre
     */
    public void resetAccueil(){
        searchField.setText("");
        resetElt();
        if(usableMenuItems!=null){
            uncheckItemsButThis(null);
        }
        elementButton.setVisible(false);
        productList.setValue(null);
    }

    /**
     * Met à jour l'affichage et les données pour toutes les fenêtres ouvertes.
     * Met à jour la valeur de l'element sélectionné pour les fenêtres ouvertes
     * lié à un Element en particulier
     * @param updatedElt l'Element pour lequel on met à jour les fenêtres d'affichage
     * @param newElt la nouvelle valeur prise par l'element sélectionné des fenêtres concernés
     */
    public static void updateConcernedWindows(Element updatedElt, Element newElt){
        /*Si au moins une page d'affichage de Logs est ouverte*/
        if(openedControllerLogs!=null){
            for(ControllerAffichageLogs c : openedControllerLogs){
                if(c.getSelectedElt()!=null && c.getSelectedElt().getCodeElt().equals(updatedElt.getCodeElt())){
                    c.setSelectedElt(newElt);
                }
                c.initialize(null, null); //mise à jour du controller courant
            }
        }
        /*Si au moins une page d'affiche d' Outils est ouverte*/
        if(openedController!=null){
            for(ControllerAffichageOutils c : openedController){
                if(c.getSelectedElt()!=null && c.getSelectedElt().getCodeElt().equals(updatedElt.getCodeElt())){
                    c.setSelectedElt(newElt);

                }
                c.initialize(null, null); //mise à jour du controller courant
            }
        } 
    }

    /**
     * "Désactive" toutes les pages d'affichages ouvertes qui sont liées à un élèment afin
     * de ne plus pouvoir y apporter de modifications
     * @param deletedElt un Element pour lequel on désactive les modifs sur ses pages d'affichages ouvertes
     */
    public static void shutDownConcernedWindows(Element deletedElt){
        /*Si au moins une page d'affichage de Logs est ouverte*/
        if(openedControllerLogs!=null){
            for(ControllerAffichageLogs c : openedControllerLogs){
                if(c.getSelectedElt()!=null && c.getSelectedElt().getCodeElt().equals(deletedElt.getCodeElt())){
                    c.shutDown();
                }
            }
        }
        /*Si au moins une page d'affiche d' Outils est ouverte*/
        if(openedController!=null){
            for(ControllerAffichageOutils c : openedController){
                if(c.getSelectedElt()!=null && c.getSelectedElt().getCodeElt().equals(deletedElt.getCodeElt())){
                    c.shutDown();
                }
            }
        } 
    }



    /*Getter et setter des éléments static*/

    public static Element getCurrentElement() {
        return currentElement;
    }

    public static void setCurrentElement(Element currentElement) {
        Controller.currentElement = currentElement;
    }

    public static boolean isAuto() {
        return auto;
    }

    public static boolean isManuel() {
        return manuel;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static int getCountOpenedForm() {
        return countOpenedForm;
    }

    public static void setCountOpenedForm(int countOpenedForm) {
        Controller.countOpenedForm = countOpenedForm;
    }

    public static String getForm() {
        return form;
    }

    public static void setForm(String form) {
        Controller.form = form;
    }

    public static ArrayList<Element> getAllElements() {
        return allElements;
    }

    public static void setAllElements(ArrayList<Element> allElements) {
        Controller.allElements = allElements;
    }


    public static ArrayList<ControllerAffichageOutils> getOpenedController() {
        return openedController;
    }


    public static void setOpenedController(ArrayList<ControllerAffichageOutils> openedController) {
        Controller.openedController = openedController;
    }

    public static ArrayList<ControllerAffichageLogs> getOpenedControllerLogs() {
        return openedControllerLogs;
    }

    public static void setOpenedControllerLogs(ArrayList<ControllerAffichageLogs> openedControllerLogs) {
        Controller.openedControllerLogs = openedControllerLogs;
    }

    public static void setAdmin(boolean isAdmin) {
        Controller.isAdmin = isAdmin;
    }
}