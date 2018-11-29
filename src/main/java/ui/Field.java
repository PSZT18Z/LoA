package ui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ai.Status;

// reprezentacja pola na planszy okienka gry NIE BOTA!!
public class Field 
{
	private int row, column;
	private Button button;
	private Status status; // na polu znajduje sie: RED- czerwony pionek, BLACK- czarnypionek, empty- pole puste
	private Type type;	   // CLEAR- pole czyste, MOVE- pole na ktore mozna sie ruszyc, SELECTED- pole akutalnie wybrane
	private LoAController loAController;
	
	Field(int row, int column, GridPane grid, LoAController loAController)
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
	
	// metoda wywolywana gdy klikniete zostanie pole
	private void buttonClicked() 
	{
		// jezeli jest tura bota, to nie pozwalamy graczowi klikać pól
		if(loAController.isHumanTurn())
			loAController.buttonClicked(row, column);
	}
	
	void setStatus(Status status)
	{
		this.status = status;
		drawPawn(); // przy zmiane statusu rysujemy na polu pionka lub usuwamy z niego pionka
	}
	
	void setType(Type type)
	{
		this.type = type;
		drawType(); // przu zmianie typu zmieniamy kolor pola
	}
	
	private void drawPawn()
	{
		Image pawn = null; 
		
		if(status == Status.BLACK) pawn = new Image(getClass().getResourceAsStream("/black.png"));
		else if(status == Status.RED) pawn = new Image(getClass().getResourceAsStream("/red.png"));
			
		button.setGraphic(new ImageView(pawn)); // jezeli ominie ify, i poda jako arg null to metoda wyczysci nam pole
												// (jezeli byly pionki to je usunie)
	}
	
	private void drawType() 
	{
		String color = "#ffffff"; // kolor bialy
		
		if(type == Type.MOVE) color = "#cfff00"; // cieply zielony
		else if(type == Type.SELECTED) color = "#87ceeb"; // bblekitny
		else if((row + column)%2 == 0) color = "#D3D3D3"; // co drugie pole na szaro aby cala plansza nie byla tylko biala
		
		button.setStyle("-fx-background-color: " + color + ";");
	}
	
	public Status getStatus() 
	{
		return status;
	}
	
	Type getType()
	{
		return type;
	}
	
	int getRow()
	{
		return row;
	}
	
	int getColumn()
	{
		return column;
	}
}
