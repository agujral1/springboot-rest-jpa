package answer.king.service;


import answer.king.exception.InsufficientPaymentException;
import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.repo.ReceiptRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.mockito.runners.MockitoJUnitRunner;



@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
	
	@Mock
    private ItemRepository itemRepository;
	
	@Mock
    private OrderRepository orderRepository;
	

    @Mock
    private ReceiptRepository receiptRepository;
	
	@InjectMocks
    private OrderService orderService;
	
	private Order firstOrder;
    private Order secondOrder;
    private  List<Order> orders = new ArrayList<>();
    private Item item;
	
	@Before
    public void init() {
		 firstOrder = new Order();
     	   secondOrder=new Order();
     	  firstOrder.setId(1l);
     	  firstOrder.setItem(new ArrayList<LineItem>());
     	  
     	  secondOrder.setId(1l);
     	  secondOrder.setItem(new ArrayList<LineItem>());
     	
     	     item = new Item(); 
           item.setId(1L);
           item.setName("Pepsi");
           item.setPrice(new BigDecimal("1"));
     	  
           LineItem lineItem = new LineItem();

           lineItem.setId(item.getId());
           lineItem.setQuantity(1);
           lineItem.setPrice(item.getPrice());
           lineItem.setItem(item);
     	  firstOrder.getItem().add(lineItem);
     	  secondOrder.getItem().add(lineItem);
     	  orders.add(firstOrder);
     	  orders.add(secondOrder);
    }

    
	  @Test
	    public void test_get_all_success() {
	       
		  when(orderRepository.findAll()).thenReturn(orders);
	      List<Order> orderList = orderService.getAll();

	        assertNotNull(orderList);
	        assertEquals(orders, orderList);

	        verify(orderRepository, times(1)).findAll();
	        verifyNoMoreInteractions(orderRepository);
	    }
    

	  @Test
	    public void test_create_order_success() {
	        
	        when(orderRepository.save(firstOrder)).thenReturn(firstOrder);

	        Order order = orderService.save(firstOrder);

	        assertNotNull(order);
	        assertEquals(firstOrder, order);

	        verify(orderRepository, times(1)).save(firstOrder);
	        verifyNoMoreInteractions(orderRepository);
	    }
	  
	  
	  @Test
	    public void payTest() throws Exception {
		 
		  Receipt receipt = new Receipt();
		    receipt.setId(1L);
			receipt.setPayment(new BigDecimal("5"));
			receipt.setOrder(firstOrder);
			
		  when(orderRepository.findOne(firstOrder.getId())).thenReturn(firstOrder);
          //when(receiptRepository.save(receipt)).thenReturn(receipt);
          orderService.pay(firstOrder.getId(), new BigDecimal("5"));
          Receipt finalReceipt = firstOrder.getReceipt();
          assertEquals(new BigDecimal("5"), finalReceipt.getPayment());
          
	  }
	  
	 
	  @Test(expected = InsufficientPaymentException.class)
	    public void pay_fail_test() throws Exception {
	        
	      when(orderRepository.findOne(firstOrder.getId())).thenReturn(firstOrder);

	        orderService.pay(firstOrder.getId(), new BigDecimal("2").negate());

	}
	  
	  
	  @Test
	    public void addItemTest_already_existing() throws Exception {
	       

	        when(orderRepository.findOne(firstOrder.getId())).thenReturn(firstOrder);
	        when(itemRepository.findOne(item.getId())).thenReturn(item);

	        orderService.addItem(firstOrder.getId(),item.getId(), 2);

	        LineItem lineItem = firstOrder.getItem().get(0);

	        
	        assertEquals(Integer.valueOf(3), lineItem.getQuantity());

	       
	    }
	  
	  @Test
	    public void addItemTest_non_existing() throws Exception {
	       

		  Item itemSecond = new Item(); 
		  itemSecond.setId(2L);
		  itemSecond.setName("Beer");
		  itemSecond.setPrice(new BigDecimal("2"));
          
	        when(orderRepository.findOne(firstOrder.getId())).thenReturn(firstOrder);
	        when(itemRepository.findOne(itemSecond.getId())).thenReturn(itemSecond);

	        orderService.addItem(firstOrder.getId(),itemSecond.getId(), 1);

	        LineItem lineItem = firstOrder.getItem().get(1);

	        
	        assertEquals(Integer.valueOf(1), lineItem.getQuantity());
	        assertEquals(itemSecond.getPrice(), lineItem.getPrice());
	       
	    }

}
