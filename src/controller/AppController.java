package controller;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MeshView;

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
	private ComboBox<Integer> comboYear;
	
	@FXML
	Slider sliderYear;
	
	@FXML
    Pane drawPane;
	
	private int defaultSpeed = 1;
	private int defaultYear = 1880;
	private int currentSpeed;
	private int currentYear;
	
	public AppController() {
		this.currentSpeed = defaultSpeed;
		this.currentYear = defaultYear;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//We call the method to setup the UI
		this.setupUI();
		Group earth = this.setupEarth();
		//drawPane.getChildren().addAll(earth);
}


/**
 * A method to configure our UI : the combo box, the binding of the menus and buttons, the slider and the actions
 */
public void setupUI() {
	//We initialize our combo box for the years
	ObservableList<Integer> comboChoice = FXCollections.observableArrayList();
	for(int year = 1880; year <2021; year++) {
		comboChoice.add(year);
	}
	comboYear.setItems(FXCollections.observableArrayList(comboChoice));
	comboYear.getSelectionModel().selectFirst();
	
	//We setup the slider
	sliderYear.setMin(1880);
	sliderYear.setMax(2020);
	sliderYear.setShowTickLabels(true);
	sliderYear.setShowTickMarks(true);
	sliderYear.setValue(currentYear);
	sliderYear.valueProperty().addListener((obs , oldval, newval) ->{
		sliderYear.setValue(Math.round(sliderYear.getValue()));
	});
	
	//We bind our menu list to our corresponding buttons
	buttonPlay.onActionProperty().bindBidirectional(animationMenuPlay.onActionProperty());
	
	buttonPause.onActionProperty().bindBidirectional(animationMenuPause.onActionProperty());
	
	buttonStop.onActionProperty().bindBidirectional(animationMenuStop.onActionProperty());
	
	radioQuad.selectedProperty().bindBidirectional(displayMenuQuad.selectedProperty());
	
	radioHist.selectedProperty().bindBidirectional(displayMenuHist.selectedProperty());
	
	//We bind the menu item close to the event of closing a window
	fileMenuClose.setOnAction(event ->{
		Platform.exit();
	});
	
	//We setup the default text for the speed of the animation
	textSpeed.setText(Integer.toString(currentSpeed));
	
	//We add an event handler to the combo box to set the controller's selected year and the slider's position
	comboYear.setOnAction(event->{
		this.currentYear = comboYear.getValue();
		this.sliderYear.setValue(comboYear.getValue());
	});

	//We setup these two listener for the slider event to allow the years to be modified and shown inside the combo box
	sliderYear.setOnMouseClicked(event ->{
		this.currentYear = (int)this.sliderYear.getValue();
		this.comboYear.setValue((int)sliderYear.getValue());
	});
	sliderYear.setOnMouseDragged(event ->{
		this.currentYear = (int)this.sliderYear.getValue();
		this.comboYear.setValue((int)sliderYear.getValue());
	});
	}

/**
 * A method to initialize the earth group
 * @return a javafx group containing the earth obj loaded
 */
public Group setupEarth() {
	ObjModelImporter objImporter = new ObjModelImporter();
	try {
		URL earthURL = this.getClass().getResource("earth/earth.obj");
		objImporter.read(earthURL);
	}catch(ImportException e) {
		System.out.println(e);
	}catch(Exception f) {
		System.out.println(f);
	}
	MeshView[] meshView = objImporter.getImport();
	Group output = new Group(meshView);
	return(output);
}
}