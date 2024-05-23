package view;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import util.ConnectDB;

public class Register{

	private Scene scene;
	private Stage stage;
	private GridPane gp;
	
	private Label register;
	private Label lbUsername;
	private Label lbEmail;
	private Label lbPassword;
	private Label gender;
	
	private TextField tfUsername;
	private TextField tfEmail;
	private PasswordField pfPassword;
	
	private RadioButton rbMale;
	private RadioButton rbFemale;
	private ToggleGroup tgGender;
	
	private Button btRegis;
	
	private Hyperlink hlLogin;
	
	private HBox hbGender;
	
	private void createRegister() {
		gp = new GridPane();
		
		register = new Label("Register");
		lbUsername = new Label("Username");
		lbEmail = new Label("Email");
		lbPassword = new Label("Password");
		gender = new Label("Gender");
		 
		hlLogin = new Hyperlink("Already have an account? Click here to login!");
		
		tfUsername = new TextField();
		tfEmail = new TextField();
		pfPassword = new PasswordField();
		
		rbMale = new RadioButton("Male");
		rbFemale = new RadioButton("Female");
		tgGender = new ToggleGroup();
		
		btRegis = new Button("Register");
		
		hbGender = new HBox(20);
		
		scene = new Scene(gp, 800, 700);

		gp.add(register, 0, 0);
		gp.add(lbUsername, 0, 1);
		gp.add(tfUsername, 0, 2);
		gp.add(lbEmail, 0, 3);
		gp.add(tfEmail, 0, 4);
		gp.add(lbPassword, 0, 5);
		gp.add(pfPassword, 0, 6);
		gp.add(gender, 0, 7);
		gp.add(hbGender, 0, 8);
		gp.add(btRegis, 0, 9);
		gp.add(hlLogin, 0, 10);
		hbGender.getChildren().addAll(rbMale, rbFemale);
		rbMale.setToggleGroup(tgGender);
		rbFemale.setToggleGroup(tgGender);
		gp.setAlignment(Pos.CENTER);
		GridPane.setHalignment(register, HPos.CENTER);
		GridPane.setHalignment(btRegis, HPos.CENTER);
		gp.setVgap(10);
		register.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
		gender.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
		tfUsername.setMaxWidth(350);
		pfPassword.setMaxWidth(350);
		btRegis.setMinWidth(100);
		tfUsername.setPromptText("Input your username here");
		tfEmail.setPromptText("Input your email here");
		pfPassword.setPromptText("Input your password here");
	}
	
	private void eventHandling() {
		hlLogin.setOnMouseClicked(e -> {
			Login login = new Login(stage);
		});
		btRegis.setOnMouseClicked(e -> {
		    String username = tfUsername.getText();
		    String email = tfEmail.getText();
		    String password = pfPassword.getText();
		    String selectedGender = rbMale.isSelected() ? "Male" : "Female";
		    String role = username.toLowerCase().contains("admin") ? "Admin" : "User";
		    
		   if (username.isEmpty() || email.isEmpty() || password.isEmpty() || selectedGender.isEmpty()) {
               showAlert("All fields are required.");
               return;
           }
		   else if (!email.endsWith("@gmail.com")) {
               showAlert("Make sure your email ends with @gmail.com");
               return;
           }
		   else if (password.length() < 8 || password.length() > 15) {
               showAlert("Make sure your password has a length of 8 - 15 characters");
               return;
		   }
		   else if (!password.matches("^[a-zA-Z0-9]+$")) {
               showAlert("Password must be alphanumeric.");
               return;
           }else if (!ConnectDB.getDB().isUsernameUnique(username)) {
               showAlert("Please choose a different username");
               return;
           }else if (!ConnectDB.getDB().isEmailUnique(email)) {
               showAlert("Email already exists.");
               return;
           }

		   else {
			   ConnectDB.getDB().addUser(username, email, password, selectedGender, role, ConnectDB.getDB().generateUserId());
			   Login login = new Login(stage);
		   }
       });
	}
	
	private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Register Error");
        alert.setContentText(message);
        alert.showAndWait();
	}
	 
	public Register(Stage stage){
		createRegister();
		eventHandling();
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
	}
	
	
}
