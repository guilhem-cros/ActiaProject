
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Lanceur extends Application{

    public static void main(String[] args){
		
        launch(args);
    }
    
	/*Création et ouverture de la fenêtre d'accueil*/
    @Override
    public void start(Stage stage){
        try {
			//récupérer les sources ici  !!
			Parent root = FXMLLoader.load(getClass().getResource("accueil.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Menu Principal");
			stage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("media/logoActiaPetit.png")));
			stage.setResizable(false);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
}
