package comq.Model;

public class GetCustomerOrder {
    private String frameName;
    private String framePrice;
    private String frameSize, status;
    private long orderDate;


    public GetCustomerOrder() {
    }

    public GetCustomerOrder(String frameName, String framePrice, String frameSize, String status, long orderDate) {
        this.frameName = frameName;
        this.framePrice = framePrice;
        this.frameSize = frameSize;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String getFrameName() {
        return frameName;
    }

    public void setFrameName(String frameName) {
        this.frameName = frameName;
    }

    public String getFramePrice() {
        return framePrice;
    }

    public void setFramePrice(String framePrice) {
        this.framePrice = framePrice;
    }

    public String getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(String frameSize) {
        this.frameSize = frameSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }
}
