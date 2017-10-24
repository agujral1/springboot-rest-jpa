package answer.king.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import answer.king.Application;
import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.OrderService;
import answer.king.controller.OrderController;
import answer.king.exception.AmanException;
import answer.king.exception.InsufficientPaymentException;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class OrderControllerTest {
	
	 private MockMvc mockMvc;

	    @Mock
	    private OrderService orderService;
	    
	    @Autowired
	    @InjectMocks
	    private OrderController orderController;
	    
	    private Order firstOrder;
	    private Order secondOrder;
	   private  List<Order> orders = new ArrayList<>();
	   private Item item;
	   
	   private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
	            MediaType.APPLICATION_JSON.getSubtype(),
	            Charset.forName("utf8"));
	    
	    @Before
	    public void init() {
        	MockitoAnnotations.initMocks(this);
        	
        	mockMvc = MockMvcBuilders
	                .standaloneSetup(orderController)
	                .setHandlerExceptionResolvers(exceptionResolver())
	                .build();
        	
        	 
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
        public void test_get_all_success() throws Exception {
        	
        	      when(orderService.getAll()).thenReturn(orders);

                    mockMvc.perform(get("/order")
                   .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                   .andExpect(jsonPath("$", hasSize(2)))
                   .andExpect(jsonPath("$[0].id", is(firstOrder.getId().intValue())))
                   .andExpect(jsonPath("$[1].id", is(secondOrder.getId().intValue())));
                   
                   verify(orderService, times(1)).getAll();
                   verifyNoMoreInteractions(orderService);
            
 
        }
   
        @Test
        public void test_create_order_success() throws Exception {
           
         
        	when(orderService.save(any(Order.class))).thenReturn(new Order());

            
           mockMvc.perform(
                    post("/order")
                    .contentType(contentType))
                    
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(contentType))
                    .andExpect(jsonPath("$").exists())
                    .andExpect(jsonPath("$.paid").value(false));
                 
                    

        }
        
        
        @Test
        public void payTest() throws Exception {
           
        	Receipt receipt = new Receipt();
        	receipt.setId(1L);
        	receipt.setPayment(new BigDecimal("5"));
        	receipt.setOrder(firstOrder);
            

            when(orderService.pay(firstOrder.getId(),new BigDecimal("5"))).thenReturn(receipt);

              mockMvc.perform(put("/order/" + firstOrder.getId() + "/pay/")
            	.contentType(contentType)
                .content(asJsonString(new BigDecimal("5"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.payment", is(new BigDecimal("5").intValue())));
                
        }

        @Test
        public void pay_fail_test() throws Exception {
           
            when(orderService.pay(firstOrder.getId(),new BigDecimal("0.5") ))
                .thenThrow(new InsufficientPaymentException("InsufficienPayment:Payment can not be lesser than order value"));

            // execution and verification
             mockMvc.perform(put("/order/" + firstOrder.getId() + "/pay/")
                	.contentType(contentType)
                	.content(asJsonString(new BigDecimal("0.5"))))
                .andExpect(status().isInternalServerError());
        }

        @Test
        public void addItemTest() throws Exception {
        	
            mockMvc.perform(put("/order/" + firstOrder.getId() + "/addItem/" + item.getId())
            	.contentType(contentType)
                .content(asJsonString(2)))
                .andExpect(status().isOk());
        }
        
        private HandlerExceptionResolver exceptionResolver() {
        	SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
       	 
            Properties exceptionMappings = new Properties();
     
            exceptionMappings.put(AmanException.class.getName(), "/exception");
            exceptionResolver.setExceptionMappings(exceptionMappings);
            exceptionResolver.setDefaultStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
           
     
            return exceptionResolver;
        }
        
        
        /*
         * converts a Java object into JSON representation
         */
        public static String asJsonString(final Object obj) {
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

}
