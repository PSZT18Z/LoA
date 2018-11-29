package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

// Dialog wyboru opcji
// pierwszy element pary- gracz czerwony
// drugi element pary - gracz czarny
// jezeli element == 0 to gracz jest czlowiekiem
// jezeli element >=1 to gracz jest botem o glebokosci drzewa = elementowi
public class OpeningDialog
{
	ButtonType doneButton;
	Dialog<Pair<Integer, Integer>> dialog;
	GridPane grid;
	ChoiceBox<String> redPlayer, blackPlayer;
	
    public Optional<Pair<Integer, Integer>> showAndWait()
    {
    	createDialog();
    	setButtons();
    	createGrid();
        createChoiceBoxes();
        fillGridWithBoxesAndLabels();

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == doneButton) {
                return new Pair<>(redPlayer.getSelectionModel().getSelectedIndex(), blackPlayer.getSelectionModel().getSelectedIndex());
            }
            return null;
        });

        return dialog.showAndWait();
    }
    
    private void createDialog()
    {
    	 dialog = new Dialog<>();
         dialog.setTitle("Game Settings");
         dialog.setHeaderText("Choose game settings");
    }
    
    private void setButtons()
    {
    	doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);
    }
    
    private void createGrid()
    {
    	  grid = new GridPane();
          grid.setHgap(10);
          grid.setVgap(10);
          grid.setPadding(new Insets(20, 150, 10, 10));
    }
    
    private void createChoiceBoxes()
    {
    	redPlayer = new ChoiceBox<>();
        blackPlayer = new ChoiceBox<>();

        redPlayer.getItems().add("Human");
        blackPlayer.getItems().add("Human");
        for(int i = 1 ; i < 8 ; ++ i)
        {
            redPlayer.getItems().add("AI("+i+")");
            blackPlayer.getItems().add("AI("+i+")");
        }
        redPlayer.getSelectionModel().select(0);
        blackPlayer.getSelectionModel().select(0);
    }
    
    private void fillGridWithBoxesAndLabels()
    {
    	 grid.add(new Label("Red Player:"), 0, 0);
         grid.add(redPlayer, 1, 0);
         grid.add(new Label("Black Player:"), 0, 1);
         grid.add(blackPlayer, 1, 1);
    }
}
