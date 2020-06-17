/**
 * The controller of the application
 * @author Nicolas Sylvestre
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import converter.Converter;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import manager.CameraManager;
import model.ClickableQuad;
import model.DrawType;
import model.ExplicitShape;
import model.Model;

public class AppController implements Initializable {
	

	@FXML
	private Button buttonPlay;
	
	@FXML
	private Button buttonPause;
	
	@FXML
	private Button buttonStop;
	
	@FXML
	private RadioButton radioQuad;
	
	@FXML
	private RadioButton radioHist;
	
	@FXML
	private MenuItem animationMenuPlay;
	
	@FXML
	private MenuItem animationMenuPause;
	
	@FXML
	private MenuItem animationMenuStop;

	@FXML
	private RadioMenuItem displayMenuQuad;
	
	@FXML
	private RadioMenuItem displayMenuHist;
	
	@FXML
	private MenuItem fileMenuClose;
	
	@FXML
	private TextField textSpeed;
	
	@FXML
	private ComboBox<Integer> comboYear;
	
	@FXML
	private Slider sliderYear;
	
	@FXML
    private Pane pane3D;
	
	@FXML
	private CheckBox checkShow;
	
	@FXML
	private BorderPane globalPane;
	
	@FXML
	private AnchorPane animationPane;
	
	@FXML
	private BarChart<String,Double> chart;
	
	//We give the default values for the years and the speed
	private int defaultSpeed = 1;
	private int defaultYear = 1880;
	private int currentSpeed;
	private int currentYear;
	private Model model;
	private DrawType drawType;
	//The group containing the earth object, and attached to it the dataDrawing group and the sphere group, to follow the transformations
	private Group earth;
	private Group dataDrawing;
	private Group sphere;
	//This group is filled with quadrilateral with listeners on them to get the value of the latitude and longitude of where we click
	private Group clickableQuad;
	
	private int startYear;
	//The aniation timer for the animation
	private AnimationTimer timer;
	private boolean isPlay =false;
	private boolean isPaused = false;
	//A class that will hold both quadrilateral list and histogram list and generate them
	private ExplicitShape shapes;
	private boolean isAreaSelected = false;
	private double maxData;
	private double minData;
	private boolean isDraw = false;
	
	public AppController() {
		this.currentSpeed = defaultSpeed;
		this.currentYear = defaultYear;
		this.startYear = currentYear;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//We set the default draw type to quadrilateral
		this.drawType = DrawType.Quad;
		//We call the method setup UI to mainly add listeners and binds to every FXML objects
		this.setupUI();
		//We call the method setup Earth GUI to import the earth obj and put it in the earth group
		this.setupEarthGUI();
		//We call the method setup Legend to setup the legend on the right side, inside the pane so it wont move with the planet
		this.setupLegend();
		//We call the constructor for the model so it loads the data from the CSV
		this.model = new Model();
		this.dataDrawing = new Group();
		this.earth.getChildren().addAll(this.dataDrawing);
		//We pre-initialize the array of quadrilateral and the array of histograms
		this.shapes = new ExplicitShape();
		this.maxData = this.model.getMax();
		this.minData = this.model.getMin();
		this.chart.setLegendVisible(false);
		//We setup the transparent quadrilateral to be clicked
		this.setupSelection();
		this.earth.getChildren().addAll(clickableQuad);
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
	textSpeed.setText(Float.toString(currentSpeed));
	
	//We add an event handler to the combo box to set the controller's selected year and the slider's position
	comboYear.setOnAction(event->{
		this.changeYear(this.comboYear.getValue());
	});


	//We setup these two listener for the slider event to allow the years to be modified and shown inside the combo box
	sliderYear.setOnMousePressed(event ->{
		this.currentYear = (int)this.sliderYear.getValue();
		this.comboYear.setValue((int)sliderYear.getValue());
		//We wont draw in the sliderTear listeners as the combo box has a listener on a setonaction, so is detecting change in its value
	});
	
	sliderYear.setOnMouseDragged(event ->{
		this.currentYear = (int)this.sliderYear.getValue();
		this.comboYear.setValue((int)sliderYear.getValue());
	});
	
	//We setup the radio button for the draw types
	radioQuad.setOnMouseClicked(event->{
		this.drawType = DrawType.Quad;
		if(checkShow.isSelected()) {
			this.draw();
		}
	});
	
	radioHist.setOnMouseClicked(event ->{
		this.drawType = DrawType.Hist;
		if(checkShow.isSelected()) {
			this.draw();
		}
	});
	
	//We setup the check box to allow it to fire the draw event
	checkShow.setOnAction(event ->{
		if(!isDraw) {
			isDraw = true;
			this.changeYear(currentYear);
		}else {
			this.dataDrawing.getChildren().clear();
			isDraw = false;
		}
		
	});
	
	//We setup the Quad menu so it fire the change of display
	displayMenuQuad.setOnAction(event ->{
		this.drawType = DrawType.Quad;
		if(checkShow.isSelected()) {
			this.draw();
		}
	});
	
	//We setup the Hist menu so it fire the change of display
	displayMenuHist.setOnAction(event ->{
		this.drawType = DrawType.Hist;
		if(checkShow.isSelected()) {
			this.draw();
		}
	});
	
	//We setup the speed text 
	textSpeed.setOnKeyReleased(event ->{
		try {
			this.currentSpeed = Integer.parseInt(this.textSpeed.getText()) ;
		}catch(NumberFormatException e) {
			//We check if the entered number is in the right format. If it isn't then nothing is done
		}
		
	});
	//This event handler is related to the textSpeed : if we exit the pane (the textField was too small and so too sensitive), the last correctly formatted animation speed is displayed in the textField
	animationPane.setOnMouseExited(event ->{
		this.textSpeed.setText(Float.toString(this.currentSpeed));
	});
	
	//We setup the buttons for the animation
	buttonPlay.setOnAction(event ->{
		//if the animation is set not set yet to be played and the draw checkbox is selected, we can start the animation
		if(!isPlay && this.checkShow.isSelected()) {
			isPlay = true;
			this.startYear = currentYear;
			this.playAnimation();
			this.timer.start();
		}
	});
	
	buttonPause.setOnAction(event ->{
		isPlay = false;
		isPaused = true;
		this.timer.stop();
	});
	
	buttonStop.setOnAction(event ->{
		//if we put an end to the animation, we reset our year to the one we started the animation at
		if(isPlay ||isPaused) {
			isPlay = false;
			isPaused = false;
			this.timer.stop();
			this.currentYear = this.startYear;
			this.sliderYear.setValue(this.currentYear);
			this.comboYear.setValue(this.currentYear);
			this.startYear = this.defaultYear;
			draw();
		}
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
		System.out.println("Exception in setupEarth");
		System.out.println(f);
	}
	MeshView[] meshView = objImporter.getImport();
	Group output = new Group(meshView);
	return(output);
}





/**
 * A method to setup the gui part containing the earth
 */
public void setupEarthGUI() {
	//Group setup
	this.earth = this.setupEarth();
	//Subscene setup
	SubScene subscene = new SubScene(this.earth,700,685,true,SceneAntialiasing.BALANCED);
	subscene.setFill(Color.GRAY);
	//Camera setup
	PerspectiveCamera camera = new PerspectiveCamera(true);
	new CameraManager(camera, pane3D, this.earth);
	subscene.setCamera(camera);
	//Light setup
	PointLight light = new PointLight(Color.WHITE);
	light.setTranslateX(-180);
	light.setTranslateY(-90);
	light.setTranslateZ(-120);
	light.getScope().addAll(this.earth);
	earth.getChildren().add(light);
	//Ambient Light setup
	AmbientLight ambientLight = new AmbientLight(Color.WHITE);
	ambientLight.getScope().addAll(this.earth);
	earth.getChildren().add(ambientLight);
	
	
	//We add the subscene to the pane containing the earth
	pane3D.getChildren().addAll(subscene);
}










/**
 * A method to draw the data on the planet
 */
public void draw() {
	//We start by clearing the dataDrawing group
	this.dataDrawing.getChildren().clear();
	switch(this.drawType) {
	//Then draw the correct type of data
	case Quad:
		this.dataDrawing.getChildren().addAll(this.shapes.getQuad());
		break;
	case Hist:
		this.dataDrawing.getChildren().addAll(this.shapes.getHist());
		break;
	}
	
}



/**
 * A method to setup the color legend, in the right corner
 */
public void setupLegend() {
	Group rectGroup = new Group();
	for(float i = 0; i<6 ; i++) {
		Rectangle rect = new Rectangle(30,30);
		//We setup the color of the rectangle
		Color color = null;
		if(i<3) {
			color = new Color(1,i/3,0,1);
		}else {
			color = new Color(0,2-i/3,1,1);
		}
		rect.setFill(color);
		//Then we translate the rectangles to the place of the legend
		rect.setTranslateX(670);
		rect.setTranslateY(260+i*30);
		//And we add it to the pane, before the earth so the rotations and translations doesn't applies to it 
		rectGroup.getChildren().add(rect);
	}
	this.pane3D.getChildren().addAll(rectGroup);
}


/**
 * A method to play an animation
 */
public void playAnimation() {
	final long startTime = System.nanoTime();
	this.timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			double t = (now-startTime)/Math.pow(10, 9);
			//to change the year, we check if our modulo is higher than 0.98, which roughly corresponds to the time we want
			if(isPlay && currentYear<2020 && t%currentSpeed > 0.98) {
				currentYear++;
				comboYear.setValue(currentYear);
				sliderYear.setValue(currentYear);
				draw();
			}//if the year to display is, or is more than 2020, we stop the animation
			if(currentYear >= 2020) {
				this.stop();
			}
		}
	};
}
	
/**
 * A method to update the years in the shapes model
 * @param year the year you want to set the model in
 */
public void changeYear(int year) {
	this.currentYear = year;
	this.comboYear.setValue(this.currentYear);
	this.sliderYear.setValue(this.currentYear);
	this.shapes.setColorAndData(this.model.getYearData(this.currentYear), this.maxData, this.minData);
	if(checkShow.isSelected()) {
		this.draw();
	}
}


/**
 * A method to put a sphere where you selected
 * @param me the mouse event associated
 * @param selectedLat the latitude selected with the mouse
 * @param selectedLon the longitude selected with the mouse
 */
public void changeSelected(MouseEvent me,int selectedLat, int selectedLon) {
	if(!isAreaSelected && me.isShiftDown()) {//We check if we already selected an area
		//If the area is selected, we put a follow attribute
		isAreaSelected = true;
		//We put the sphere inside its own group, that we then will put in the earth
		this.sphere = new Group();
		Sphere point = new Sphere(0.1);
		//We get where we want to put the sphere and then we translate it
		Point3D translateVector = Converter.geoCoordTo3dCoord(selectedLat, selectedLon, 1);
		point.setTranslateX(translateVector.getX());
		point.setTranslateY(translateVector.getY());
		point.setTranslateZ(translateVector.getZ());
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.PURPLE);
		material.setSpecularColor(Color.PURPLE);
		this.sphere.getChildren().add(point);
		earth.getChildren().addAll(this.sphere);
		//We update the chart with the right selection
		this.updateChart(selectedLat,selectedLon);
	}else if(this.isAreaSelected && me.isShiftDown()){//if we already have a selection, we deselect what we already have
		isAreaSelected = false;
		this.sphere.getChildren().clear();
		//Same as before, we update the chart, setting it to visible(false)
		this.updateChart(selectedLat,selectedLon);
	}
}

/**
 * We setup the quadrilateral array which will be clickable
 */
public void setupSelection() {
	this.clickableQuad = new Group();
	for(int lat = -88; lat <89; lat+=4) {
		for(int lon = -178; lon<179 ; lon+=4) {
			PhongMaterial material = new PhongMaterial();
    		Point3D topRight = Converter.geoCoordTo3dCoord(lat+2, lon+2,1.005f);
    		Point3D bottomRight = Converter.geoCoordTo3dCoord(lat-2, lon+2,1.005f);
    		Point3D bottomLeft = Converter.geoCoordTo3dCoord(lat-2, lon-2,1.005f);
    		Point3D topLeft = Converter.geoCoordTo3dCoord(lat+2, lon-2,1.005f);
    		//The clickableQuad object contains a meshView and the latitude and longitude attribute
    		ClickableQuad Quad = new ClickableQuad(topRight, bottomRight, bottomLeft, topLeft, material,lat,lon);
    		this.clickableQuad.getChildren().add(Quad.getQuadMesh());
    		//We request the focus to be in front of everything in terms of click
    		Quad.getQuadMesh().requestFocus();
    		//We set the the diffuse color to null to make the color transparent
    		material.setDiffuseColor(null);
    		//We add an event handler to detect which quadrilateral is selected
    		Quad.getQuadMesh().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
    			@Override
    			public void handle(MouseEvent event) {
    				changeSelected(event, Quad.getLat(), Quad.getLon());
    			} 	
    		});
		}
	}
}


/**
 * A method to update the chart depending on what you selected
 * @param lat the latitude of what you selected
 * @param lon the longitude of what you selected
 */
public void updateChart(int lat, int lon) {
	//We draw the chart only if an area is selected
	if(this.isAreaSelected) {
		//We setup the serie for the chart to be drawn of
		XYChart.Series<String, Double> dataSeries = new Series<>();
		//For each year, we add the anomaly to the series thanks to the XYChart
		for(Integer year = 1880; year <= 2020 ; year++) {
			dataSeries.getData().add(new XYChart.Data<String,Double>(year.toString(),this.model.getData(year.intValue(), lat, lon)));
		}
		chart.getData().add(dataSeries);
		chart.setVisible(true);
	}else {
		//if nothing is selected, we set the chart to invisible
		chart.getData().clear();
		chart.setVisible(false);
	}
}

}