package bankaccountsfx;

import bankaccountsfx.model.*;
import bankaccountsfx.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bankaccountsfx.utils.MessageUtils.showError;
import static bankaccountsfx.utils.MessageUtils.showMessage;

public class FXMLDocumentController implements Initializable {

    @FXML private TextField accountNumber, owner, transactionDescription, transactionAmount;
    @FXML private Label totalSalary;
    @FXML private Button btnAccountAdd, btnTransactionAdd, btnTransactionChart;
    @FXML private ComboBox<Account> cmbAccount;
    @FXML private DatePicker transactionDate;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private TableView<Transaction> tableTransactions;
    @FXML private AnchorPane panePie, paneData;
    @FXML private VBox vBox;
    @FXML private PieChart blanceChartPositive, blanceChartNegative;
    @FXML private TableColumn<Transaction, String> dateCol;
    @FXML private TableColumn<Transaction, String> descriptionCol;
    @FXML private TableColumn<Transaction, Number> amountCol;
    private ObservableList<Account> listAccounts;
    private ObservableList<Transaction> listTransactions;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            DatabaseConnector.connect();
            initializeUIComponents();
            loadAccountsIntoComboBox();
            setupActionListeners();
        } catch (Exception e) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, e);
            showError(FXMLDocumentController.class.getName(), e.getMessage());
        }
    }


    private void setupTableColumns() {
        // Assuming the Transaction model class has properties named as "date", "description", and "amount"
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void initializeUIComponents() {
        btnAccountAdd.setStyle("-fx-text-fill: white; -fx-background-color: #000000");
        btnTransactionAdd.setStyle("-fx-text-fill: white; -fx-background-color: #000000");
        btnTransactionChart.setStyle("-fx-text-fill: white; -fx-background-color: #000000");

        disableTransactionFields(true);
        setupComboBoxFilters();
        setupTableColumns();
    }

    private void loadAccountsIntoComboBox() throws SQLException {
        listAccounts = FXCollections.observableArrayList(FileUtils.loadAccounts());
        cmbAccount.setItems(listAccounts);
    }

    private void setupActionListeners() {
        cmbAccount.setOnAction(this::accountSelectionChanged);
        btnAccountAdd.setOnAction(this::saveNewAccount);
        btnTransactionAdd.setOnAction(this::saveNewTransactionAction);
        // Other action listeners as previously defined...
    }

    private void accountSelectionChanged(ActionEvent event) {
        try {
            if (cmbAccount.getValue() != null) {
                listTransactions = FXCollections.observableArrayList(FileUtils.loadTransactionsForAccount(cmbAccount.getValue()));
                tableTransactions.setItems(listTransactions);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            showError(FXMLDocumentController.class.getName(), ex.getMessage());
        }
    }

    private void saveNewAccount(ActionEvent event) {
        // Implementation remains the same...
    }

    private void saveNewTransactionAction(ActionEvent event) {
        if (validateTransactionForm()) {
            try {
                saveNewTransaction(cmbAccount.getValue(), transactionDate.getValue(),
                        transactionDescription.getText(), Float.parseFloat(transactionAmount.getText()));
            } catch (SQLException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                showError(FXMLDocumentController.class.getName(), ex.getMessage());
            }
        }
    }

    private void saveNewTransaction(Account account, LocalDate date, String description, float amount) throws SQLException {
        // Convert LocalDate to java.sql.Date
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        // Create a new Transaction object
        Transaction newTransaction = new Transaction(sqlDate, description, amount);

        // Save the new transaction using your FileUtils class
        FileUtils.saveTransaction(newTransaction);

        // Reload the transactions list for the current account
        List<Transaction> transactions = FileUtils.loadTransactionsForAccount(account);

        // Convert the List<Transaction> to ObservableList<Transaction> for compatibility with TableView
        ObservableList<Transaction> observableTransactions = FXCollections.observableArrayList(transactions);

        // Update the TableView with the new list of transactions
        tableTransactions.setItems(observableTransactions);

        // Optional: Refresh the TableView to show the latest data
        tableTransactions.refresh();

        // Show success message
        showMessage("Success!", "New transaction was registered successfully.");
    }
    private void createTable() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        ObservableList<Transaction> transactionObservableList = FXCollections.observableArrayList(listTransactions);
        tableTransactions.setItems(transactionObservableList);
    }

    private void disableTransactionFields(boolean disable) {
        transactionDate.setDisable(disable);
        transactionDescription.setDisable(disable);
        transactionAmount.setDisable(disable);
        btnTransactionAdd.setDisable(disable);
        btnTransactionChart.setDisable(disable);
        cmbFilter.setDisable(disable);
    }

    private void setupComboBoxFilters() {
        cmbFilter.getItems().addAll("Filter", "Date", "Description", "Amount", "Income", "Expenses");
        cmbFilter.getSelectionModel().selectFirst();
    }

    private boolean validateTransactionForm() {
        // Validate the transaction form fields
        // Return true if valid, false otherwise
        // Use showError to display any validation errors
        return true;
    }

    private void filterTransactions() {
        // Method to filter transactions based on the selected criteria
    }

    // Additional private methods as needed for UI interaction and event handling...
}
