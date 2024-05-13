package Util;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class EmoticonPicker extends Dialog<String> {
    private String[] emoticons = {
            "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04",
            "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE07", "\uD83D\uDE08", "\uD83D\uDE09"
    };

    private String chosenEmoticon;

    public EmoticonPicker() {
        setTitle("Choose an emoticon");
        setHeaderText("Click on an emoticon to select it.");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        for (int i = 0; i < emoticons.length; i++) {
            Button emoticonButton = new Button(emoticons[i]);
            emoticonButton.setOnAction(event -> chosenEmoticon = emoticonButton.getText());
            grid.add(emoticonButton, i % 5, i / 5);
        }

        getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Ok");
        getDialogPane().getButtonTypes().add(buttonTypeOk);

        setResultConverter(b -> b == buttonTypeOk ? chosenEmoticon : null);
    }
}
