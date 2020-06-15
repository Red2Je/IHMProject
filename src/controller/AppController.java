package controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import converter.Converter;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import manager.CameraManager;
import model.DrawType;
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
	
	
	private int defaultSpeed = 1;
	private int defaultYear = 1880;
	private int currentSpeed;
	private int currentYear;
	private Model model;
	private DrawType drawType;
	private Group earth;
	private Group dataDrawing;
	private int startYear;
	private AnimationTimer timer;
	
	public AppController() {
		this.currentSpeed = defaultSpeed;
		this.currentYear = defaultYear;
		this.startYear = currentYear;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//We call the methods to setup the UI
		this.drawType = DrawType.Quad;
		this.setupUI();
		this.setupEarthGUI();
		this.setupLegend();
		
		globalPane.setCursor(Cursor.WAIT);
		this.model = new Model();
		globalPane.setCursor(Cursor.DEFAULT);
		this.dataDrawing = new Group();
		this.earth.getChildren().addAll(this.dataDrawing);
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
		this.currentYear = comboYear.getValue();
		this.sliderYear.setValue(comboYear.getValue());
		if(checkShow.isSelected()) {
			this.draw();
		}
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
		this.draw();
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
			this.startYear = currentYear;
			this.playAnimation();
			this.timer.start();
	});
	
	buttonPause.setOnAction(event ->{
		this.timer.stop();
	});
	
	buttonStop.setOnAction(event ->{
		this.timer.stop();
		this.currentYear = this.startYear;
		this.sliderYear.setValue(this.currentYear);
		this.comboYear.setValue(this.currentYear);
		this.startYear = this.defaultYear;
		draw();
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
 * A method to create a line
 * @param parent the group you want to add the line to
 * @param origin the origin point of the line
 * @param target the end point of the line
 * @param material the phong material of the line
 */
public void createLine(Group parent, Point3D origin, Point3D target, PhongMaterial material) {
    Point3D yAxis = new Point3D(0, 1, 0);
    Point3D diff = target.subtract(origin);
    double height = diff.magnitude();

    Point3D mid = target.midpoint(origin);
    Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

    Point3D axisOfRotation = diff.crossProduct(yAxis);
    double angle = Math.acos(diff.normalize().dotProduct(yAxis));
    Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

    Cylinder line = new Cylinder(0.01f, height);

    line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
    line.setMaterial(material);

    parent.getChildren().add(line);
}


/**
 * A method to add a quadrilateral thanks to its 4 points
 * @param parent the parent root to add the quadrilateral to
 * @param topRight the top right point of the quadrilateral
 * @param bottomRight the bottom right point of the quadrilateral
 * @param bottomLeft the bottom left point of the quadrilateral
 * @param topLeft the top left point of the quadrilateral
 * @param material the material of the quadrilateral
 */
public void addQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material) {
	final TriangleMesh triangleMesh = new TriangleMesh();
	
	final float[] points = {
			(float)topRight.getX(),(float)topRight.getY(),(float)topRight.getZ(),
			(float)topLeft.getX(),(float)topLeft.getY(),(float)topLeft.getZ(),
			(float)bottomLeft.getX(),(float)bottomLeft.getY(),(float)bottomLeft.getZ(),
			(float)bottomRight.getX(),(float)bottomRight.getY(),(float)bottomRight.getZ()
	};
	
	final float[] texCoords = {
		1,1,
		1,0,
		0,1,
		0,0
	};
	
	final int[] faces = {
		0,1,1,0,2,2,
		0,1,2,2,3,3
	};
	
	triangleMesh.getPoints().setAll(points);
	triangleMesh.getTexCoords().setAll(texCoords);
	triangleMesh.getFaces().setAll(faces);
	
	final MeshView meshView = new MeshView(triangleMesh);
	meshView.setMaterial(material);
	parent.getChildren().addAll(meshView);
}




/**
 * A method to draw the data on the planet
 */
public void draw() {
	double max = this.model.getMax();
	try {
		this.dataDrawing.getChildren().clear();
	}catch(Exception e) {
		e.printStackTrace();
	}	
	for(int lat = -88; lat <89; lat+=4) {
		for(int lon = -178; lon<179 ; lon+=4) {
			PhongMaterial material = new PhongMaterial();
			double data = this.model.getData(this.currentYear, lat, lon);
			Color dataColor = Converter.dataToColor(data, max, 0.7);//we give the next shape a color corresponding to the data
			material.setDiffuseColor(dataColor);
			material.setSpecularColor(dataColor);
			switch(drawType) {
				case Quad : {
	        		Point3D topRight = Converter.geoCoordTo3dCoord(lat+2, lon+2,1.05f);
	        		Point3D bottomRight = Converter.geoCoordTo3dCoord(lat-2, lon+2,1.05f);
	        		Point3D bottomLeft = Converter.geoCoordTo3dCoord(lat-2, lon-2,1.05f);
	        		Point3D topLeft = Converter.geoCoordTo3dCoord(lat+2, lon-2,1.05f);
	        		this.addQuadrilateral(this.dataDrawing, topRight, bottomRight, bottomLeft, topLeft, material);
					break;
				}
				case Hist : {
					Point3D origin = Converter.geoCoordTo3dCoord(lat, lon, 1.05f);
					Point3D target = Converter.geoCoordTo3dCoord(lat, lon, Math.abs((float)data));
					this.createLine(this.dataDrawing, origin, target,material);
					break;
				}
			}
		}
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



public void playAnimation() {
	this.timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			if(currentYear + currentSpeed < 2020) {
				final Service<Void> animationService = new Service<Void>() {
					@Override
					protected Task<Void> createTask() {
						return new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								try {
									//System.out.println("i sleep");
									Thread.sleep(5000);
								}catch(InterruptedException e) {
								}
								return null;
							}
						};
					}
				};
				
				animationService.stateProperty().addListener((ObservableValue<? extends Worker.State> ov,Worker.State olv,Worker.State nv) ->{
					switch(nv) {
						case FAILED : 
						case CANCELLED :
						case SUCCEEDED :
							currentYear += currentSpeed;
							sliderYear.setValue(currentYear);
							comboYear.setValue(currentYear);
							draw();
							break;
					}
						
				});
				animationService.start();
			}else {
				currentYear = 2020;
				sliderYear.setValue(currentYear);
				comboYear.setValue(currentYear);
				draw();
				this.stop();
			}
		}
	};
}
/*Premier essai de sleeper, échec à cause de l'extinction du mauvais thread
 * Task<Void> sleeper = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try {
							//System.out.println("i sleep");
							Thread.sleep(5000);
						}catch(InterruptedException e) {
						}
						return null;
					}
				};
				sleeper.setOnSucceeded(event ->{
					currentYear += currentSpeed;
					sliderYear.setValue(currentYear);
					comboYear.setValue(currentYear);
					draw();
				});
				new Thread(sleeper).start();
 */


}