
package eventticketbocingsystemproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class EventTicketBocingsystemproject extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent root = loader.load();

           
            Scene scene = new Scene(root, 500, 400);

            
            primaryStage.setTitle("Event Ticket Booking System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(500);
            primaryStage.setMinHeight(400);

            
            primaryStage.show();

            System.out.println("Application started successfully!");

        } catch (Exception e) {
            System.err.println("Error loading application:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       
        launch(args);
    }

}
