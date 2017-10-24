package answer.king.model;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_ITEM")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private BigDecimal price;

	/*@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID")
	private Order order;*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/*public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}*/
	
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (!Item.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final Item other = (Item) obj;
	    if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) {
	        return false;
	    }
	    if ((this.getPrice() == null) ? (other.getPrice() != null) : !this.getPrice().equals(other.getPrice())) {
	        return false;
	    }
	    return true;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
	    hash = 53 * hash + (this.getPrice() != null ? this.getPrice().hashCode() : 0);
	    return hash;
	}
}
