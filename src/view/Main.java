package view;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception {
	primaryStage.setTitle("cangkIR");	
	Login login = new Login(primaryStage);	
	}

public static void main(String[] args) {
	launch(args);
	
}

}
