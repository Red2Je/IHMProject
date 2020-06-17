/**
 * The main method of the app anomaly visualiser
 * @author Nicolas Sylvestre
 */
package app;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	

	public App() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent appContent = FXMLLoader.load(getClass().getResource("IHMProject.fxml"));
			Scene globalScene = new Scene(appContent);
			primaryStage.setTitle("Anomaly visualiser");
			primaryStage.setScene(globalScene);
			primaryStage.show();
			
		}catch(Exception e) {
			System.out.println("Exception in app");
			System.out.println(e);
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
