package lk.sadeep.itt.retail.communication.dto;

public class UpdateStockCheckoutRequestDTO {

    private Long itemId;
    private int requestedQty;

    public UpdateStockCheckoutRequestDTO(Long itemId, int requestedQty) {
        this.itemId = itemId;
        this.requestedQty = requestedQty;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(int requestedQty) {
        this.requestedQty = requestedQty;
    }
}
