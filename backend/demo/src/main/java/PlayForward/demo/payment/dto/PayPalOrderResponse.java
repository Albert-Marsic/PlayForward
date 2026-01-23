package PlayForward.demo.payment.dto;

import java.math.BigDecimal;

public class PayPalOrderResponse {
    public String orderId;
    public String status;
    public BigDecimal amount;

    public PayPalOrderResponse(String orderId, String status, BigDecimal amount) {
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
    }
}
