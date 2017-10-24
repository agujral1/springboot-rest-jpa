package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.exception.AmanException;
import answer.king.exception.InsufficientPaymentException;
import answer.king.exception.ItemException;
import answer.king.exception.OrderException;
import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;

import answer.king.repo.*;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ItemRepository itemRepository;
	
	 @Autowired
	    private ReceiptRepository receiptRepository;

	public List<Order> getAll() {
		return orderRepository.findAll();
	}

	public Order save(Order order) {
		return orderRepository.save(order);
	}

	public void addItem(Long id, Long itemId, Integer quantity)throws AmanException {
		Order order = orderRepository.findOne(id);
		Item item = itemRepository.findOne(itemId);
		
		if (item == null)
	    throw new ItemException("Item does not exist."); 
		if (order == null)
	    throw new OrderException("Order does not exist.");
	    LineItem lineItem = new LineItem();
	  
         for(LineItem orderLineItem : order.getItem()){
        	  boolean itemFound = false; 
    	   if(orderLineItem.getItem().equals(item)){
    		   Integer newQty = orderLineItem.getQuantity() + quantity;
    		   orderLineItem.setQuantity(newQty);  
    		   itemFound = true;
    	   }
    	   if(itemFound)
    		   return;
         }
    	   
    		   lineItem.setQuantity(quantity);
               lineItem.setPrice(item.getPrice());
               lineItem.setItem(item);
               order.getItem().add(lineItem); 
    	  
       orderRepository.save(order);
	}
	public Receipt pay(Long id, BigDecimal payment) throws AmanException {
		Order order = orderRepository.findOne(id);
		orderValidation(order);

		Receipt receipt = new Receipt();
		receipt.setPayment(payment);
		receipt.setOrder(order);
	 boolean insufficientFunds = receipt.getChange().compareTo(BigDecimal.ZERO) < 0;

       if (insufficientFunds)
        throw new InsufficientPaymentException("InsufficienPayment:Payment can not be lesser than order value.");
       
       order.setPaid(true);
        order.setReceipt(receipt);
        

    return receiptRepository.save(receipt);
}
	 private void orderValidation(Order order) throws AmanException {
	        if (order == null)
	            throw new OrderException("Order does not exist.");

	        if (order.getReceipt() != null)
	            throw new OrderException("Order Already Paid");
	    }

}
