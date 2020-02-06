package comq.Model;

public class FeedbackModel {
    private String customerName, feedback, uId;
    private long dateSent;

    public FeedbackModel() {
    }

    public FeedbackModel(String customerName, String feedback, String uId, long dateSent) {
        this.customerName = customerName;
        this.feedback = feedback;
        this.uId = uId;
        this.dateSent = dateSent;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }
}
