package bankaccountsfx.model;

import java.text.*;
import java.util.Date;

public class Transaction {
    private Integer transactionId; // Use the wrapper class Integer to allow null values
    private Date date;
    private String description;
    private float amount;

    // Constructor for new transactions (without transactionId)
    public Transaction(Date date, String description, float amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // Constructor including transactionId, for existing transactions
    public Transaction(int transactionId, Date date, String description, float amount) {
        this.transactionId = transactionId;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // Getters and setters for all fields
    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(this.date);
    }
    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return "Transaction{" +
                "transactionId=" + (transactionId != null ? transactionId : "New") +
                ", date=" + dateFormat.format(date) +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }
}
