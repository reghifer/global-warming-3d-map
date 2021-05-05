package sample;

import java.util.function.UnaryOperator;



import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.data.CurrentState;
import sample.data.DataReader;
import sample.data.Model;

public class Controller {
	@FXML
	RadioButton radioPosition;
	@FXML
	RadioButton radioZone;
	@FXML
	Slider sliderSpeed;
	@FXML
	Label labelSpeed;
	@FXML
	TextField latitudeText;
	@FXML
	TextField longitudeText;
	@FXML
	Button showButton;
	@FXML
	Slider sliderYear;
	@FXML
	Label labelYear;
	@FXML
	Button backwardButton;
	@FXML
	Button playButton;
	@FXML 
	Button frontwardButton;
	@FXML
	Pane pane3d;
	@FXML
	VBox graphiqueVbox;
	@FXML
	Button stopButton;
	
	private Model model;
	private AnimationTimer timer;
	private double lastTimeCharged;
	@FXML
    public void initialize(){
		model = DataReader.getModel("src/sample/data/tempanomaly_4x4grid.csv");
		Earth.Initialise(this.getClass().getResource("EarthData/earth.obj"),latitudeText,longitudeText);
		timer = new AnimationTimer() {
			public void handle(long currentNanoTime) {
        		double t = (currentNanoTime - lastTimeCharged) /1000000000.0;
        		if(model.iterateEvery(t, sliderSpeed.getValue())) {
        			if(model.getCurrentYear() < 2020) changeSelectedYear(model.getCurrentYear() + 1);
        		}
        		lastTimeCharged = currentNanoTime;
        	}
		};
		Earth.generateEarth(pane3d);
		radioPosition.setSelected(true);
		radioPosition.setOnAction(e ->{
			changeSelectedRadioButton(radioPosition);
		});
		radioZone.setOnAction(e ->{
			changeSelectedRadioButton(radioZone);
		});
		labelSpeed.setText("1.0");
		sliderSpeed.setOnMouseDragged(e->{
			float value = (float)sliderSpeed.getValue();
			model.setAnimationSpeed(value);
			labelSpeed.setText("" + value);
		});
		labelYear.setText("1880");
		sliderYear.setOnMouseDragged(e -> {
			changeSelectedYear((int)sliderYear.getValue());
		});
		sliderYear.setOnMouseDragExited(e ->{
			Earth.ApplyEarthModification(model);
		});
		backwardButton.setOnAction(e ->{
			int value = Integer.parseInt(labelYear.getText()) - 5;
			if(value < 1880) {
				changeSelectedYear(1880);
			}else {
				changeSelectedYear(value);
			}
		});
		frontwardButton.setOnAction(e ->{
			int value = Integer.parseInt(labelYear.getText()) + 5;
			if(value > 2020) {
				changeSelectedYear(2020);
			}else {
				changeSelectedYear(value);
			}
		});
		playButton.setOnAction(e ->{
			if(!model.AnimationisOn) {
				lastTimeCharged = System.nanoTime();
				timer.start();
				playButton.setText("pause");
	    		stopButton.setDisable(false);
				model.AnimationisOn = true;
			}else {
				timer.stop();
				playButton.setText("start");
	    		stopButton.setDisable(true);
				model.AnimationisOn = false;
			}
		});
		stopButton.setDisable(true);
		stopButton.setOnAction(e ->{
			timer.stop();
    		stopButton.setDisable(true);
			playButton.setText("start");
			model.AnimationisOn = false;
			changeSelectedYear(1880);
		});
		if(model.getCurrentYear() < 2020) changeSelectedYear(model.getCurrentYear() + 1);
		Earth.ApplyEarthModification(model);
		UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {

            @Override
            public TextFormatter.Change apply(TextFormatter.Change t) {

                if (t.isReplaced()) 
                    if(t.getText().matches("[^0-9\\-]"))
                        t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));
                

                if (t.isAdded()) {
                    if (t.getControlText().contains(".")) {
                        if (t.getText().matches("[^0-9\\-]")) {
                            t.setText("");
                        }
                    } else if (t.getText().matches("[^0-9.\\-]")) {
                        t.setText("");
                    }
                }

                return t;
            }
        };
        latitudeText.setTextFormatter(new TextFormatter<>(filter));
        longitudeText.setTextFormatter(new TextFormatter<>(filter));
        
        Button reset = new Button("reset");
        reset.setPrefSize(150, 30);
        reset.setOnAction(e->{
        	if(!latitudeText.getText().equals("") && !longitudeText.getText().equals(""))
        		Earth.HandleCharts(pane3d,Float.parseFloat(latitudeText.getText()), Float.parseFloat(longitudeText.getText()), model, false);
        });
        showButton.setOnAction(e ->{
        	if(!model.ChartIsOpen){
            	if(!latitudeText.getText().equals("") && !longitudeText.getText().equals("")) {
            		Earth.HandleCharts(pane3d,Float.parseFloat(latitudeText.getText()), Float.parseFloat(longitudeText.getText()), model, false);
            		graphiqueVbox.getChildren().add(reset);
            		VBox.setMargin(reset, new Insets(5.0f,5.0f,0.0f,5.0f));
            		showButton.setText("close");
            		model.ChartIsOpen = true;
            	}
        	}else {
        		model.ChartIsOpen = false;
            	Earth.HandleCharts(pane3d,0.0f, 0.0f, model, true);
            	graphiqueVbox.getChildren().remove(reset);
            	showButton.setText("show");
        	}
        });
        
	}
	private void changeSelectedRadioButton(RadioButton radio){
		String idRadio = radio.getId();
		switch(idRadio){
		case "radioPosition" :
			radioZone.setSelected(false);
			radioPosition.setSelected(true);
			model.setCurrentState(CurrentState.position);
			break;
		case "radioZone" :
			radioPosition.setSelected(false);
			radioZone.setSelected(true);
			model.setCurrentState(CurrentState.zone);
			break;
			default :
				break;
		}	
		Earth.ApplyEarthModification(model);
	}
	
	private void changeSelectedYear(int year) {
		sliderYear.setValue(year);
		labelYear.setText("" + year);
		model.setCurrentYear(year);
		Earth.ApplyEarthModification(model);
	}
	
		
}
