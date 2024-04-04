/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankaccountsfx.utils;

import java.util.List;
import bankaccountsfx.model.*;
import static bankaccountsfx.utils.MessageUtils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    
    /**
     * Get all accounts from DB (database)
     * @return List<Account>
     * @throws FileNotFoundException, IOException 
     */

    public static List<Account> loadAccounts() {
        List<Account> listAccounts = new ArrayList<>();
        String sql = "SELECT account_number, owner_name FROM accounts";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                String ownerName = rs.getString("owner_name");
                listAccounts.add(new Account(accountNumber, ownerName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MessageUtils.showError(FileUtils.class.getName(), e.getMessage());
        }
        return listAccounts;
    }


    /**
     * Save a list of new accounts to DB (database)
     * @param listAccounts
     * @throws FileNotFoundException, IOException 
     */

    public static void saveAccounts(List<Account> listAccounts) {
        String sql = "INSERT INTO accounts (account_number, owner_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE owner_name = VALUES(owner_name)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Account account : listAccounts) {
                pstmt.setString(1, account.getAccountNumber());
                pstmt.setString(2, account.getOwner());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MessageUtils.showError(FileUtils.class.getName(), e.getMessage());
        }
    }
    
    /**
     * Get all of transactions from DB (transactions.txt)
     * @return List<Transaction>
     * @throws FileNotFoundException, IOException 
     */

    public static List<Transaction> loadTransactions() throws SQLException {
        List<Transaction> listTransactions = new ArrayList<>();
        String sql = "SELECT transaction_id, date, description, amount FROM transactions";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                Date date = rs.getDate("date");
                String description = rs.getString("description");
                float amount = rs.getFloat("amount");
                listTransactions.add(new Transaction(transactionId, date, description, amount));
            }
        }
        return listTransactions;
    }
    
    /**
     * Get all of transactions for an account from DB (transactions.txt)
     * @param account
     * @param typeParams
     * @param params
     * @return List<Transaction>
     * @throws FileNotFoundException, IOException 
     */

    public static List<Transaction> loadTransactionsForAccount(Account account) throws SQLException {
        List<Transaction> listTransactions = new ArrayList<>();
        String sql = "SELECT transaction_id, date, description, amount FROM transactions WHERE account_number = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int transactionId = rs.getInt("transaction_id");
                    Date date = rs.getDate("date");
                    String description = rs.getString("description");
                    float amount = rs.getFloat("amount");
                    listTransactions.add(new Transaction(transactionId, date, description, amount));
                }
            }
        }
        return listTransactions;
    }

    /**
     * Save a list of new transactions to DB (transactions.txt)
     * @param listTransactions
     * @throws FileNotFoundException, IOException
     */

    public static void saveTransactions(List<Transaction> listTransactions) throws SQLException {
        String sql = "INSERT INTO transactions (date, description, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Transaction transaction : listTransactions) {
                pstmt.setDate(1, new java.sql.Date(transaction.getDate().getTime()));
                pstmt.setString(2, transaction.getDescription());
                pstmt.setFloat(3, transaction.getAmount());
                pstmt.executeUpdate();
            }
        }
    }
    public static void saveTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (date, description, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, new java.sql.Date(transaction.getDate().getTime()));
            pstmt.setString(2, transaction.getDescription());
            pstmt.setFloat(3, transaction.getAmount());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Get the generated transaction ID if needed
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }
    
    /**
     * Get total amount from transactions list
     * @param listTransactions
     * @return 
     */
    public static Float getTotalAmount( List<Transaction> listTransactions ){
        float result = 0;
        
        for( Transaction dataTransaction: listTransactions ){

            result += dataTransaction.getAmount();

        }
        
        return result;
    }
}
