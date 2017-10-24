package answer.king.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@Entity
@Table(name = "T_RECEIPT")
public class Receipt {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal payment;

    @JsonIgnore
    @OneToOne(mappedBy = "receipt")
	private Order order;
    
    public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public BigDecimal getPayment() {
		return payment;
	}

	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	public BigDecimal getChange() {
	
		BigDecimal orderPrice = BigDecimal.ZERO;
		 List<LineItem> items=order.getItem();
		 for(LineItem lineItem: items)
		 {
			int quantity = lineItem.getQuantity();
			BigDecimal totalItemPrice = lineItem.getPrice().multiply(new BigDecimal(quantity));
			 orderPrice=orderPrice.add(totalItemPrice);
		 }
		return payment.subtract(orderPrice);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
