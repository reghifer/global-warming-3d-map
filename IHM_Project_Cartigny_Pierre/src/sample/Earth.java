package sample;

import java.net.URL;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import sample.data.Model;

public class Earth {
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
	private static URL earthData;
	private static Group Root3D;
	private static Group zoneState = new Group(),PositionState = new Group();
	private static SubScene subsceneEarth,subsceneGraph;
	private static LineChart<Number,Number> chart;
	private static VBox scale;
	private static TextField latitudeText, longitudeText;
	
	// ------- Earth view creation-----------
	public static void Initialise(URL earthData, TextField latText, TextField lonText) {
		Earth.earthData = earthData;
		final NumberAxis xAxis = new NumberAxis(1880, 2020, 1);
		xAxis.setLabel("année");
		xAxis.setAutoRanging(false);
		final NumberAxis yAxis = new NumberAxis(-7 , 7, 1);
		yAxis.setLabel("valeur d'anomalie");
        chart = new LineChart<Number,Number>(xAxis, yAxis);
        chart.setCreateSymbols(false);
        latitudeText = latText;
        longitudeText = lonText;
		subsceneGraph = new SubScene(chart, 600, 600);
	}
	public static void generateEarth(Pane pane3D) {
		Root3D = new Group();
		
        ObjModelImporter objImporter = new  ObjModelImporter();
        URL modelURL = earthData;
        objImporter.read(modelURL);
        MeshView[] meshViews = objImporter.getImport();
        Group earth = new Group(meshViews);
        Root3D.getChildren().add(earth);
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane3D, Root3D);

        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(Root3D);
        Root3D.getChildren().add(ambientLight);
		Root3D.getChildren().add(PositionState);
		Root3D.getChildren().add(zoneState);
		Earth.hideZone();
		scale = new VBox();
		Label red = new Label("> 2,3°C       ");
		red.setStyle("-fx-background-color: RED");
		Label OrangeRed = new Label("1,3 - 2,3°C   ");
		OrangeRed.setStyle("-fx-background-color: ORANGERED");
		Label Orange = new Label("0,3 - 1,3°C   ");
		Orange.setStyle("-fx-background-color: ORANGE");
		Label white = new Label(" -0,3 - 0,3°C ");
		white.setStyle("-fx-background-color: BLANCHEDALMOND");
		Label lightBlue = new Label("-1,3 - -0,3°C");
		lightBlue.setStyle("-fx-background-color: LIGHTSKYBLUE");
		Label Blue = new Label("-2,3 - -1,3°C");
		Blue.setStyle("-fx-background-color: DODGERBLUE");
		Label DarkBlue = new Label("< -2,3°C      ");
		DarkBlue.setStyle("-fx-background-color: DARKBLUE");
		scale.getChildren().add(red);
		scale.getChildren().add(OrangeRed);
		scale.getChildren().add(Orange);
		scale.getChildren().add(white);
		scale.getChildren().add(lightBlue);
		scale.getChildren().add(Blue);
		scale.getChildren().add(DarkBlue);
		subsceneEarth = new SubScene(Root3D, 600, 600, true, SceneAntialiasing.BALANCED);
		subsceneEarth.setCamera(camera);
		subsceneEarth.setFill(Color.GREY);
        pane3D.getChildren().addAll(subsceneEarth);
        pane3D.getChildren().add(scale);
        
	}
	
	//---------application of the modifications -----------
	
	public static void ApplyEarthModification(Model model) {
		switch(model.getCurrentState()) {
		case zone :
			earthToZone(model);
			Earth.hidePosition();
			break;
		case position :
			earthToPosition(model);
			Earth.hideZone();
			break;
		default:
			break;
		}
	}
    public static void HandleCharts(Pane pane, float latitude, float longitude, Model model, boolean close) {
    	if(close) {
    		pane.getChildren().remove(subsceneGraph);
    		pane.getChildren().add(subsceneEarth);
    		pane.getChildren().add(scale);
    	}else {
    		pane.getChildren().clear();
    		generateChart(latitude,longitude,model);
        	pane.getChildren().add(subsceneGraph);
    	}
    }
    
    private static void earthToPosition(Model model) {
		float lat = -88.0f, lon = -178.0f;
    	if(PositionState.getChildren().isEmpty()) {
			do {
				do {
					float value = model.getYearData(model.getCurrentYear()).getData(lat, lon);
					PositionState.getChildren().add(createLine(lat, lon, value));
					lon += 4.0f;
				}while(lon <= 178.0f);
				lon = -178.0f;
				lat += 4.0f;
			}while(lat <= 88.0f);
    	}else {
    		int i = 0;
			do {
				do {
					float value = model.getYearData(model.getCurrentYear()).getData(lat, lon);
					Color c = Earth.chooseColor(value);
					if(Float.isNaN(value)) {
						changeValueLine((Cylinder)PositionState.getChildren().get(i),geoCoordTo3dCoord(lat, lon, 1.1f),c,true);
					}else {
						changeValueLine((Cylinder)PositionState.getChildren().get(i),geoCoordTo3dCoord(lat, lon, 1.05f + value * 0.07f),c,false);
					}
					lon += 4.0f;
					i++;
				}while(lon <= 178.0f);
				lon = -178.0f;
				lat += 4.0f;
			}while(lat <= 88.0f);
    	}
    }
    
    private static void earthToZone(Model model) {
    	float lat = -88.0f, lon = -178.0f;
    	if(zoneState.getChildren().isEmpty()) {
			do {
				do {
					float value = model.getYearData(model.getCurrentYear()).getData(lat, lon);
					Color c = Earth.chooseColor(value);
						zoneState.getChildren().add(AddQuadrilateral(lat,lon, c));
					lon += 4.0f;
				}while(lon <= 178.0f);
				lon = -178.0f;
				lat += 4.0f;
			}while(lat <= 88.0f);
    	}else {
    		int i = 0;
			do {
				do {
					float value = model.getYearData(model.getCurrentYear()).getData(lat, lon);
					Color c = Earth.chooseColor(value);
					ChangeQuadrilateral((MeshView)zoneState.getChildren().get(i),c);
					lon += 4.0f;
					i++;
				}while(lon <= 178.0f);
				lon = -178.0f;
				lat += 4.0f;
			}while(lat <= 88.0f);
    	}
    }
    
    private static void hideZone() {
    	zoneState.getChildren().forEach(e ->{
            final PhongMaterial Material = new PhongMaterial();
            Material.setDiffuseColor(Color.TRANSPARENT);
    		((MeshView) e).setMaterial(Material);
    	});
    }
    
    private static void hidePosition() {
    	PositionState.getChildren().forEach(e ->{
    		e.setVisible(false);
    	});
    }
    
    private static void generateChart(float latitude, float longitude, Model model) {
    	if(!model.getYearData(1880).contain(latitude, longitude)){
    		chart.setTitle("coordonnees inconnues");
    		chart.getData().clear();
    	}else {
    		chart.setTitle("coordonnee :" + latitude + " ; " + longitude);
    		XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
    		series.setName("anomalie");
    		for(int i = 1880 ; i <= 2020; i++) {
    			float value = model.getYearData(i).getData(latitude, longitude);
    			if(Float.isNaN(value)) {
    				value = 0.0f;
    			}
    			series.getData().add(new XYChart.Data<Number, Number>(i, value));
    		}
    		chart.getData().clear();
    		chart.getData().add(series);
    	}
    }

    // -----------tools for creating 3D elements-------------
    
    private static Point3D geoCoordTo3dCoord(float lat, float lon, float radius) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)) * radius,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius);
    }
    
    
    private static Cylinder createLine(float latitude, float longitude, float value) {
        final PhongMaterial Material = new PhongMaterial();
        Material.setDiffuseColor(Earth.chooseColor(value));
        Point3D origin = new Point3D(0,0,0);
        Point3D target;
        if(!Float.isNaN(value)) {
        	target = geoCoordTo3dCoord(latitude, longitude, 1.2f + value * 0.07f);
        }else {
        	target = geoCoordTo3dCoord(latitude, longitude, 1.2f);
        }
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
        
        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        
        Cylinder line = new Cylinder(0.01f, 1.2f);
        line.setMaterial(Material);
        Scale lenght;
        if(Float.isNaN(value)) {
        	lenght = new Scale(0.5,0.5,0.5);
        }else {
        	lenght = new Scale(height,height,height);
        }
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter,lenght);
        line.setOnMouseClicked(e ->{
        	latitudeText.setText(latitude + "");
        	longitudeText.setText(longitude + "");
        });
        return line;
    }
    
    private static void changeValueLine(Cylinder line, Point3D target, Color c, boolean isNaN) {
		line.setVisible(true);
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.6f);
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(c);
        material.setSpecularColor(c);
        line.getTransforms().remove(2);
        Point3D origin = new Point3D(0,0,0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();
        Scale lenght = new Scale(height, height,height);
        if(isNaN) {
        	lenght = new Scale(0.5,0.5,0.5);
        }else {
        	lenght = new Scale(height,height,height);
        }
        line.getTransforms().add(lenght);
        line.setMaterial(material);
    }
    
    private static MeshView AddQuadrilateral(float latitude,float longitude, Color c) {
        final PhongMaterial material = new PhongMaterial();
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.00001f);
        material.setDiffuseColor(c);
        material.setSpecularColor(c);
        
		Point3D topRight, bottomRight, bottomLeft, topLeft;
		topRight = geoCoordTo3dCoord(latitude -2f, longitude + 2f, 1.01f);
		bottomRight = geoCoordTo3dCoord(latitude + 2f, longitude + 2f, 1.01f);
		bottomLeft = geoCoordTo3dCoord(latitude + 2f, longitude - 2f, 1.01f);
		topLeft = geoCoordTo3dCoord(latitude - 2f, longitude - 2f, 1.01f);
		
    	final TriangleMesh triangleMesh = new TriangleMesh();
    	
    	final float[] points = {
    			(float)topRight.getX(),(float)topRight.getY(),(float)topRight.getZ(),
    			(float)bottomRight.getX(),(float)bottomRight.getY(),(float)bottomRight.getZ(),
    			(float)bottomLeft.getX(),(float)bottomLeft.getY(),(float)bottomLeft.getZ(),
    			(float)topLeft.getX(),(float)topLeft.getY(),(float)topLeft.getZ()
    			};
    	final float[] texCoords = {
    			1, 1,
    			1, 0,
    			0, 1,
    			0, 0
    	};
    	final int[] faces = {
    			0, 1, 1, 0, 2, 2,
    			0, 1, 2, 2, 3, 3
    	};
    	triangleMesh.getPoints().setAll(points);
    	triangleMesh.getTexCoords().setAll(texCoords);
    	triangleMesh.getFaces().setAll(faces);
    	final MeshView meshView = new MeshView(triangleMesh);
    	meshView.setMaterial(material);
    	meshView.setOnMouseClicked(e ->{
        	latitudeText.setText(latitude + "");
        	longitudeText.setText(longitude + "");
        });
    	return meshView;
    }
    private static void ChangeQuadrilateral(MeshView meshView, Color c) {
        final PhongMaterial material = new PhongMaterial();
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.00001f);
        material.setDiffuseColor(c);
        material.setSpecularColor(c);
        meshView.setMaterial(material);
    }
    
    private static Color chooseColor(float value) {
    	if(Float.isNaN(value)) {
    		return Color.TRANSPARENT;
    	}else if (value < -2.2f) {
    		return Color.DARKBLUE;
    	}else if(value >= -2.3f && value < -1.3f) {
    		return Color.DODGERBLUE;
    	}else if (value >= -1.3f && value < -0.3f) {
    		return Color.LIGHTSKYBLUE;
    	}else if (value >= -0.3f &&  value < 0.3f) {
    		return Color.BLANCHEDALMOND;
    	}else if (value >= 0.3f &&  value < 1.3f) {
    		return Color.ORANGE;
    	}else if (value >= 1.3f &&  value < 2.3f) {
    		return Color.ORANGERED;
    	}else {
    		return Color.RED;
    	}
    }
}
