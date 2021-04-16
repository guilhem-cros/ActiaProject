
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller implements Initializable{

    private static ArrayList<Element> allElements;

    /*liste des items sélectionnables du menu*/
    private ArrayList<RadioMenuItem> usableMenuItems;

    /*L'element courant ayant été sélectionné*/
    private static Element currentElement;

    /*Le produit (95....) sélectionné dans la ComboBox*/
    private static Element selectedProd;

    private static boolean auto=false;

    private static boolean manuel=false;


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
    private Button logsButton;


    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productList.getItems().clear();
        productList.getItems().addAll(createComboBox().getItems());
        
    }


    /*Events FXML*/

    /**
     * Fonction appelée lors d'un appuie sur le bouton "Valider" du menu principal
     * Ouvre une nouvel onglet affichant les outils de tests correspondants à l'élement sélectionné
     * Ouvre un onglet d'erreur indicatif si aucun élement/produit n'a été sélectionné
     * Ouvre un onglet d'erreur indicatif si aucun mode d'utilisation n'a été sélectionné
     * @param action
    */
    @FXML
    public void validate(ActionEvent action){
        /*dans le cas ou aucun élément/produit n'a été sélectionné*/
        if(currentElement == null){
            setWarningAlert("Erreur : aucun élément sélectionné", "Veuillez sélectionner un produit et un ensemble avant de valider la recherche.");
        }
        /*dans le cas ou aucun mode d'utilisation n'a été sélectionné*/
        else if(auto==false && manuel==false){
            setWarningAlert("Erreur : aucun mode sélectionné", "Veuillez sélectionner au minimun un mode d'utilisation avant de valider la recherche.");
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
     * Redirige vers un onglet de connexion pour l'administrateur
     * @param action
    */
    @FXML
    public void openConfig(ActionEvent action){
        //
    }

    /**
     * Fonction appelée lors d'un changement de valeur (coché ou non) de la checkbox "Auto"
     * Permettra la sélectionn des tests autos uniquement
     * Peut être combiné avec la checkbox Manuel
     * @param action
    */
    @FXML
    public void setAuto(ActionEvent action){
        auto = !auto;
    }

    /**
     * Fonction appelée lors d'un changement de valeur (coché ou non) de la checkbox "Manuel"
     * Permettra la sélectionn des tests manuels uniquement
     * Peut être combiné avec la checkbox Auto
     * @param action
    */
    @FXML
    public void setManuel(ActionEvent action){
        manuel = !manuel;
    }

    /**
     * Fonction appelé lors d'un changement de valeur saisie dans la comboBox de produits
     * Récupère le produit sélectionné et appelle la fonction permettant la création 
     * des menus d'éléments correspondant à ce produit
     * @param action
     */
    @FXML
    public void selectProduct(ActionEvent action){
        resetElt();
        selectedProd = getElementByCode(sliceCode(productList.getValue().toString()));
        selectedElement.setVisible(true);
        elementButton.setVisible(true);
        createElementMenu(); //création du menu de sélection d'élément
        initUsableEvent(); //instanciation des events sur les items du menu
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
            setWarningAlert("Erreur : aucun élément sélectionné", "Veuillez sélectionner un produit et un ensemble avant de valider la recherche.");
        }
        /*Si aucune erreur décectée, création et ouverture de la nouvelle fenetre*/
        else{
            /*Déclaration de la nouvelle fenetre*/
            Stage stage = setNewStage("logs.fxml");
            System.out.println(stage);
            stage.setTitle("Mots de passe : " + currentElement.getCodeElt() + " " + currentElement.getNom());
            stage.showAndWait();
        }
    }

    /**
     * Initialise un Stage dans l'optique d'un ouverture de nouvelle fenêtre
     * et met en place les paramètres de base de ce stage
     * @param fxmlLink le lien du fichier fxml parent du stage crée
     * @return le stage crée et préparé à l'utilisation
     */ 
    public Stage setNewStage(String fxmlLink){
        System.out.println("set");
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


    /*Getter des éléments static*/

    public static Element getCurrentElement() {
        return currentElement;
    }

    public static boolean isAuto() {
        return auto;
    }

    public static boolean isManuel() {
        return manuel;
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
     * Récupère sous forme de chaine de caractères 
     * le code contenu dans l'item sélectionné
     * @param item l'item sélectionné
     * @return uniquement le code depuis le texte de item
     */
    public static String getCodeByItem(MenuItem item){
        return sliceCode(item.getText());
    }

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
     * Parcours la liste d'éléments 
     * Met sous la forme de menuButton les éléments issus du produit sélectionné 
     * Ajoute les sous menus correspondant à ces éléments si nécessaire
     * Ajoute ces menus aux items de la liste déroulante d'ensembles
     */
    public void createElementMenu(){  
        usableMenuItems = new ArrayList<RadioMenuItem>(); //initilisation de la liste d'items sélectionnables
        elementButton.getItems().clear();
        RadioMenuItem baseItem = new RadioMenuItem(selectedProd.getCodeElt() + " " + selectedProd.getNom());
        elementButton.getItems().add(baseItem); //ajout de l'élément selectedProd à sa propre liste
        usableMenuItems.add(baseItem); //ajot de l'élément selectedProd à la liste d'items cliquables
        /*Si la liste de sous élément de slectedProd n'est pas vide*/
        if(!selectedProd.getListeSousElements().isEmpty()){
            ArrayList<Element> listElt = selectedProd.getListeSousElements();
            Element.sortElements(listElt); //tri de la liste
            for(Element elt: listElt){
                String chaine = elt.getCodeElt() + " " + elt.getNom();
                /*Si l'élément courant possède des sous éléments, on l'ajoute au menu en que sous menu*/
                if(!elt.hasNoSubElmt()){
                    Menu menu = new Menu(chaine);
                    setSubMenu(menu); //mise en place du sous menu de l'item 
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
     * Crée un sous menu d'éléments pour l'élément sélectionné
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
     * Récupère les éléments enregistrés depuis la data
     * et ajoute les éléments de type produit (code=95..)en 
     * tant que chaine de caractère à une comboBox "produits"
     * @return la ComboBox contenant l'ensemble des produits 
     * 95... contenus dans la data
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
     * Affiche dans le chams de texte le nom et code de l'élément sélectionné
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
     * créee les  évènements correspondant pour tous les radioItems
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
     * Fonction appelé lorsque la valeur de l'élément courant 
     * doit être "supprimée"
     * Repasse la valeur de l'élément courant à null et 
     * réinitialise les objets liés (checkbox, textField)
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

    /**
     * Fonction instanciant et ouvrant un onglet de type alerte
     * @param header le texe du header de l'onglet retourné
     * @param content le texte explicatif dans l'onglet retourné
     * @return l'alerte crée à partir des 2 paramètres
     */ 
    public Alert setWarningAlert(String header, String content){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Erreur");
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
        return alert;
    }
    
}