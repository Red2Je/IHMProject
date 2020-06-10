package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AppController implements Initializable {
	
	@FXML
	Button buttonPlay;
	
	@FXML
	Button buttonPause;
	
	@FXML
	Button buttonStop;
	
	@FXML
	RadioButton radioQuad;
	
	@FXML
	RadioButton radioHist;
	
	@FXML
	MenuItem animationMenuPlay;
	
	@FXML
	MenuItem animationMenuPause;
	
	@FXML
	MenuItem animationMenuStop;

	@FXML
	RadioMenuItem displayMenuQuad;
	
	@FXML
	RadioMenuItem displayMenuHist;
	
	@FXML
	MenuItem fileMenuClose;
	
	@FXML
	TextField textSpeed;
	
	@FXML
	ComboBox<Integer> comboYear;
	
	@FXML
	Slider sliderYear;
	
	@FXML
	AnchorPane drawPane;
	
	public AppController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//from here we bind our menu list to our corresponding buttons
		buttonPlay.onActionProperty().bindBidirectional(animationMenuPlay.onActionProperty());
		
		buttonPause.onActionProperty().bindBidirectional(animationMenuPause.onActionProperty());
		
		buttonStop.onActionProperty().bindBidirectional(animationMenuStop.onActionProperty());
		
		radioQuad.selectedProperty().bindBidirectional(displayMenuQuad.selectedProperty());
		
		radioHist.selectedProperty().bindBidirectional(displayMenuHist.selectedProperty());
		
		//We bind the menu item close to the event of closing a window
		fileMenuClose.setOnAction(event ->{
			Platform.exit();
		});
		
	}

}
