package application;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Field {

	private int row, column;
	private Button button;
	private Status status;
	private Type type;
	private LoAController loAController;
	
	public Field(int row, int column, GridPane grid, LoAController loAController)
	{
		this.row = row;
		this.column = column;
		this.loAController = loAController;
		button = new Button();
		setStatus(Status.EMPTY);
		setType(Type.CLEAR);
		
		button.setOnAction(e -> buttonClicked());
		button.setPrefSize(80, 80);
		grid.add(button, column, row);
	}
	
	private void buttonClicked() 
	{
		loAController.buttonClicked(row, column);
	}
	
	public void setStatus(Status status)
	{
		this.status = status;
		drawPawn();
	}
	
	public void setType(Type type) 
	{
		this.type = type;
		drawType();
	}
	
	private void drawPawn()
	{
		Image pawn = null; 
		
		if(status == Status.BLACK) pawn = new Image(getClass().getResourceAsStream("/black.png"));
		else if(status == Status.RED) pawn = new Image(getClass().getResourceAsStream("/red.png"));
			
		button.setGraphic(new ImageView(pawn)); // JESLI BYLO STATUS EMPTY TO TU WEJDZIE NULL I OBRAZEK ZNIKNIEI ALBO NIC SIE NIE STANIE
	}
	
	private void drawType() 
	{
		String color = "#ffffff";
		
		if(type == Type.MOVE) color = "#00ff00";
		else if(type == Type.SELECTED) color = "#0000ff";
		else if((row + column)%2 == 0) color = "D3D3D3";
		
		button.setStyle("-fx-background-color: " + color + "; ");
	}
	
	public Status getStatus() 
	{
		return status;
	}
	
	public Type getType() 
	{
		return type;
	}
}
