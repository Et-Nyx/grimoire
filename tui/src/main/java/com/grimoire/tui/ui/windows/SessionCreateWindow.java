package com.grimoire.tui.ui.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.grimoire.client.exception.GrimoireApiException;
import com.grimoire.client.service.SessionService;
import com.grimoire.common.model.Session;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class SessionCreateWindow extends StandardWindow {

    private final String campaignId;
    private final SessionService sessionService;
    private final Runnable onSuccess;

    private TextBox titleTextBox;
    private TextBox dateTextBox;
    private TextBox summaryTextBox;

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public SessionCreateWindow(
        String campaignId,
        SessionService sessionService,
        Runnable onSuccess
    ) {
        super("Agendar Nova Sessão");
        this.campaignId = campaignId;
        this.sessionService = sessionService;
        this.onSuccess = onSuccess;

        setHints(java.util.Arrays.asList(Hint.CENTERED, Hint.MODAL));

        Panel mainPanel = new Panel(new GridLayout(2));

        mainPanel.addComponent(new Label("Título:"));
        titleTextBox = new TextBox(new TerminalSize(40, 1));
        mainPanel.addComponent(titleTextBox);

        mainPanel.addComponent(new Label("Data (dd/MM/yyyy HH:mm):"));
        dateTextBox = new TextBox(new TerminalSize(40, 1));
        dateTextBox.setText(LocalDateTime.now().format(DATE_FORMATTER));
        mainPanel.addComponent(dateTextBox);

        mainPanel.addComponent(new Label("Sinopse:"));
        summaryTextBox = new TextBox(new TerminalSize(50, 5), TextBox.Style.MULTI_LINE);
        mainPanel.addComponent(summaryTextBox);

        mainPanel.addComponent(new EmptySpace());

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        Button createButton = new Button("Criar", this::onCreateClick);
        Button cancelButton = new Button("Cancelar", this::close);

        buttonPanel.addComponent(createButton);
        buttonPanel.addComponent(cancelButton);

        mainPanel.addComponent(buttonPanel);

        setComponent(mainPanel);
    }

    private void onCreateClick() {
        String title = titleTextBox.getText().trim();
        String dateStr = dateTextBox.getText().trim();
        String summary = summaryTextBox.getText().trim();

        if (title.isEmpty()) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Título da sessão é obrigatório.",
                MessageDialogButton.OK
            );
            return;
        }

        LocalDateTime date;
        try {
            date = LocalDateTime.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Data inválida. Use o formato: dd/MM/yyyy HH:mm\nExemplo: 25/12/2023 19:30",
                MessageDialogButton.OK
            );
            return;
        }

        try {
            Session session = Session.builder()
                .campaignId(UUID.fromString(campaignId))
                .title(title)
                .date(date)
                .summary(summary.isEmpty() ? null : summary)
                .build();

            Session created = sessionService.createSession(session);

            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Sucesso",
                "Sessão \"" + created.getTitle() + "\" criada com sucesso!",
                MessageDialogButton.OK
            );

            close();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (GrimoireApiException e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Erro ao criar sessão: " + e.getErrorMessage(),
                MessageDialogButton.OK
            );
        } catch (Exception e) {
            MessageDialog.showMessageDialog(
                getTextGUI(),
                "Erro",
                "Erro inesperado: " + e.getMessage(),
                MessageDialogButton.OK
            );
        }
    }
}
