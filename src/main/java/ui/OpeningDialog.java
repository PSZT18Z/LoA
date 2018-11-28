package main.java.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class OpeningDialog
{
    public Optional<Pair<Integer, Integer>> showAndWait()
    {
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Game Settings");
        dialog.setHeaderText("Choose game settings");

        ButtonType doneButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ChoiceBox<String> redPlayer = new ChoiceBox<>();
        ChoiceBox<String> blackPlayer = new ChoiceBox<>();

        redPlayer.getItems().add("Human");
        blackPlayer.getItems().add("Human");
        for(int i = 1 ; i < 8 ; ++ i)
        {
            redPlayer.getItems().add("AI("+i+")");
            blackPlayer.getItems().add("AI("+i+")");
        }

        grid.add(new Label("Red Player:"), 0, 0);
        grid.add(redPlayer, 1, 0);
        grid.add(new Label("Black Player:"), 0, 1);
        grid.add(blackPlayer, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == doneButton) {
                return new Pair<>(redPlayer.getSelectionModel().getSelectedIndex(), blackPlayer.getSelectionModel().getSelectedIndex());
            }
            return null;
        });

        return dialog.showAndWait();
    }
}