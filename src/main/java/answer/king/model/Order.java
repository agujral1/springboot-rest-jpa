package answer.king.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "T_ORDER")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Boolean paid = false;

	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL, CascadeType.PERSIST })
    @JoinColumn(name = "ORDER_ID")
    private List<LineItem> lineItem;
	
    @OneToOne(cascade = { CascadeType.ALL, CascadeType.PERSIST })
	    @JoinColumn(name = "RECEIPT_ID")
	    private Receipt receipt;
    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	 public Receipt getReceipt() {
	        return receipt;
	    }

	    public void setReceipt(Receipt receipt) {
	        this.receipt = receipt;
	    }
	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public List<LineItem> getItem() {
		return lineItem;
	}

	public void setItem(List<LineItem> items) {
		this.lineItem = items;
	}
}
