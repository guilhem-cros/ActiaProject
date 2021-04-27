
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller implements Initializable{

    /*Compteur du nombre de formulaires actuellement ouverts*/
    private static int countOpenedForm = 0;

    /*Nom représentatif du formulaire ouvert*/
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


    /*Initialisation des objets XML utilisés*/

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
    private ImageView actiaLogo;

    @FXML 
    private ComboBox<String> productList;

    @FXML
    private MenuButton elementButton;

    @FXML
    private TextField selectedElement;

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



    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productList.getItems().clear();
        productList.getItems().addAll(createComboBox().getItems());
        Config.unserializeConfig(); //initilisation de la config (mdp + log) actuelle
        form = "null";
        
    }


    /*Events FXML*/

    /**
     * Fonction appelée lors d'un appuie sur le bouton "Valider" du menu principal
     * Ouvre une nouvel onglet affichant les outils correspondants à l'élement sélectionné
     * ou un onglet d'erreur
     * @param action
    */
    @FXML
    public void validate(ActionEvent action){
        /*dans le cas ou aucun élément/produit n'a été sélectionné*/
        if(currentElement == null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun élément sélectionné", "Veuillez sélectionner un produit et un ensemble avant de valider la recherche.", "Erreur", alert);
        }
        /*dans le cas ou aucun mode d'utilisation n'a été sélectionné*/
        else if(auto==false && manuel==false){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun mode sélectionné", "Veuillez sélectionner au minimun un mode d'utilisation avant de valider la recherche.","Erreur", alert);
        }
        /*Si aucune erreur décectée, création et ouverture de la nouvelle fenetre*/
        else{
            /*Déclaration de la nouvelle fenetre*/
            Stage stage = setNewStage("outils.fxml");
            stage.setTitle("Constitution Bancs de Test : " + currentElement.getCodeElt() + " " + currentElement.getNom());
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
    }

    /**
     * Fonction appelée lors d'un changement de valeur de la checkbox "Auto"
     * Permettra la sélection des outils autos
     * @param action
    */
    @FXML
    public void setAuto(ActionEvent action){
        auto = !auto;
    }

    /**
     * Fonction appelée lors d'un changement de valeur (coché ou non) de la checkbox "Manuel"
     * Permettra la sélection des outils manuels 
     * @param action
    */
    @FXML
    public void setManuel(ActionEvent action){
        manuel = !manuel;
    }

    /**
     * Fonction appelé lors d'un changement de valeur dans la comboBox de produits
     * Récupère le produit sélectionné et et lance la création 
     * des menus d'éléments/sous-élèments correspondant à ce produit
     * @param action
     */
    @FXML
    public void selectProduct(ActionEvent action){
        if(productList.getValue()!=null){
            resetElt();
            selectedProd = getElementByCode(sliceCode(productList.getValue().toString()));
            selectedElement.setVisible(true);
            elementButton.setVisible(true);
            createElementMenu(); //création du menu de sélection d'élément
            initUsableEvent(); //instanciation des events sur les items du menu
        }
    }

    /**
     * Fonction appelée lors d'un appuie sur le bouton "Mots de passe" du menu principal
     * Ouvre une nouvel onglet affichant les mots de passes et logs correspondants à l'élement sélectionné
     * Ouvre un onglet d'erreur indicatif si aucun élement/produit n'a été sélectionné
     * @param action
    */
    @FXML
    public void openLogs(ActionEvent action){
        /*dans le cas ou aucun élément/produit n'a été sélectionné*/
        if(currentElement == null){
            Alert alert = new Alert(AlertType.WARNING);
            setAlert("Erreur : aucun élément sélectionné", "Veuillez sélectionner un produit et un ensemble avant de valider la recherche.", "Erreur",  alert);
        }
        /*Si aucune erreur décectée, création et ouverture de la nouvelle fenetre*/
        else{
            /*Déclaration de la nouvelle fenetre*/
            Stage stage = setNewStage("logs.fxml");
            stage.setTitle("Mots de passe : " + currentElement.getCodeElt() + " " + currentElement.getNom());
            stage.showAndWait();
        }
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
                    isAdmin = true;  //passage en mode administrateur
                    backToMenu(action); //retour à la page d'accueil
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
                /*Ongle d'erreur d'identifiant*/
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
        /*Affichage de la page d'accueil*/
        setMenuDisplay(true);
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

    @FXML
    public void displayHelp(ActionEvent action){

    }



    /*Mise en place et affichage du menu et sous-menus*/


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
     * Fonction appelée lors de la sélection d'un produit 
     * Met sous la forme de menuButton les éléments issus du produit sélectionné 
     * Ajoute les sous menus correspondant à ces éléments si nécessaire
     * Ajoute ces menus aux items de la liste déroulante d'ensembles
     */
    public void createElementMenu(){  
        usableMenuItems = new ArrayList<RadioMenuItem>(); //initilisation de la liste d'items sélectionnables
        elementButton.getItems().clear();
        RadioMenuItem baseItem = new RadioMenuItem(selectedProd.getCodeElt() + " " + selectedProd.getNom());
        elementButton.getItems().add(baseItem); //ajout de l'élément selectedProd à sa propre liste
        usableMenuItems.add(baseItem); //ajout de l'élément selectedProd à la liste d'items cliquables
        /*Si la liste de sous élément de slectedProd n'est pas vide*/
        if(!selectedProd.getListeSousElements().isEmpty()){
            ArrayList<Element> listElt = selectedProd.getListeSousElements();
            Element.sortElements(listElt);
            for(Element elt: listElt){
                String chaine = elt.getCodeElt() + " " + elt.getNom();
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
                    usableMenuItems.add(item); //Ajout de cet item à la liste des items sélectionnables
                }
            }  
        }
	}

    /**
     * Fonction appelé lors de la sélection d'un produit
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
            String chaine = e.getCodeElt() + " " + e.getNom();
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
     * Fonction appelé lors de l'ouverture de la fénêtre
     * Récupère les éléments enregistrés depuis la data et ajoute les éléments 
     * de type produit (code=95..)en tant à la comboBox "produits"
     * @return la ComboBox contenant l'ensemble des produits (95...)
     */
    public ComboBox<String> createComboBox(){
        allElements = Element.unserializeElement(); //récupération des données stockées dans le fichier .ser
        Element.sortElements(allElements); //tri des éléments dans l'ordre décroissant
        ComboBox<String> list = new ComboBox<String>();
        list.getItems().clear();
        list.setStyle("-fx-font: 16px;");//police d'écriture en taille 16px
        for(Element elt: allElements){
			if(elt.isProduit()){
				String chaine = elt.getCodeElt() + " " + elt.getNom();
                list.getItems().add(chaine);
			}
        }
        return list;

    }



    /*Utilisation et évènements relatifs au menu*/

    /**
     * Fonction appelé lors de la sélection d'un élément dans le menu
     * Met à jour l'élément selectionné courrant
     * Affiche dans le champs de texte le nom et code de l'élément sélectionné
     * Rend visible les checkBox auto et manuel
     * @param item l'item sélectionné par un clic
     */
    public void selectElement(RadioMenuItem item){
        currentElement = getElementByCode(getCodeByItem(item));
        selectedElement.setText(item.getText());
        autoBox.setVisible(true);
        manuelBox.setVisible(true);
    }

    /**
     * Fonction appelé lorsqu'un produit a été sélectionné
     * Parcours la liste des radiosItem du menu d'ensembles et 
     * crée les  évènements correspondant pour tous ces radioItems
     */
    public void initUsableEvent(){
        for(RadioMenuItem item:usableMenuItems){
            item.setOnAction((event) ->{
                RadioMenuItem source = (RadioMenuItem) event.getSource();
                /*si l'élement est déssélectionné*/
                if(!source.isSelected()){
                    currentElement = null; //l'élément courrant devient null
                    selectedElement.setText("");
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
     * Fonction appelé lors de la sélection d'un élément dans le menu
     * Parcours la liste des radiosItem du menu et les rend tous 
     * unchecké sauf l'élément sélectionné
     * @param i l'item sélectionné lors d'un clic
     */
    public void uncheckItemsButThis(RadioMenuItem i){
        for(RadioMenuItem item: usableMenuItems){
            if(!i.equals(item)){
                item.setSelected(false);
            }
        }
    }

    /**
     * Fonction appelé lorsque la valeur de l'élément courant doit être "supprimée"
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
        /*Affichage du menu */
        if(visible){
            helpButton.setVisible(true);
            configButton.setVisible(true);
            productList.setVisible(true);
            valideButton.setVisible(true);
            logsButton.setVisible(true);
        }
        /*Invisibilité du menu */
        else{
            helpButton.setVisible(false);
            configButton.setVisible(false);
            productList.setVisible(false);
            elementButton.setVisible(false);
            valideButton.setVisible(false);
            logsButton.setVisible(false);
            selectedElement.setVisible(false);
            productList.getSelectionModel().clearSelection(); //réinitialisation de la liste des produits
            resetElt(); //réinisialisation de l'ensemble des variables qui aurait pu être sélectionnées
        }
    }

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
            stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("media/logoActiaPetit.png")));;
            stage.setScene(scene);
            stage.setResizable(false);
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*Getter et setter des éléments static*/

    public static Element getCurrentElement() {
        return currentElement;
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
    
}