package answer.king.controller;


import com.fasterxml.jackson.databind.ObjectMapper;

import answer.king.Application;
import answer.king.exception.AmanException;
import answer.king.exception.InvalidNameException;
import answer.king.exception.InvalidPriceException;
import answer.king.model.Item;
import answer.king.service.ItemService;
import answer.king.controller.ItemController;
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
import java.util.Arrays;
import java.util.Properties;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration

public class ItemControllerTest {

	 private MockMvc mockMvc;

	    @Mock
	    private ItemService itemService;
	    @Autowired
	    @InjectMocks
	    private ItemController itemController;
	    
	   private Item first;
	    private Item second;

	    @Before
	    public void init(){
	        MockitoAnnotations.initMocks(this);
	        mockMvc = MockMvcBuilders
	                .standaloneSetup(itemController)
	                .setHandlerExceptionResolvers(exceptionResolver())
	                .build();
	        
	         first = new Item();
	    	 first.setId(1L);
	    	 first.setName("Pepsi");
	    	 first.setPrice(new BigDecimal("1"));
	    	 
	          second = new Item();
	         second.setId(2L);
	         second.setName("Beer");
	         second.setPrice(new BigDecimal("2"));
	   }
    // =========================================== Get All Users ==========================================

    @Test
    public void test_get_all_success() throws Exception {
    	 
         
      when(itemService.getAll()).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/item"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Pepsi")))
                .andExpect(jsonPath("$[0].price", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Beer")))
                .andExpect(jsonPath("$[1].price", is(2)));
                
        verify(itemService, times(1)).getAll();
        verifyNoMoreInteractions(itemService);
    }

    

    // =========================================== Create New Item ========================================

    @Test
    public void test_create_item_success() throws Exception {
        
        when(itemService.save(first)).thenReturn(first);

        mockMvc.perform(
                post("/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(first)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Pepsi"))
            .andExpect(jsonPath("$.price").value(1));

        verify(itemService, times(1)).save(first);
        verifyNoMoreInteractions(itemService);
    }

    // =========================================== Create New Item  Unsuccess ========================================
    @Test
    public void test_create_item_unsuccess() throws Exception {
      
    	Item second = new Item();
         
        when(itemService.save(second))
            .thenThrow(new InvalidNameException("Invalid Name:Name can not be empty."));

        
        mockMvc.perform(
                post("/item")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(second)))
            .andExpect(status().isInternalServerError());

        verify(itemService, times(1)).save(second);
        verifyNoMoreInteractions(itemService);
        
      
    }
    // =========================================== Update Item ========================================
    @Test
    public void updatePriceTest() throws Exception {
       
        when(itemService.updatePrice(first.getId(), new BigDecimal("4")))
            .thenReturn(first);
         mockMvc.perform(
                put("/item/" + first.getId() + "/updatePrice")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(new BigDecimal("4"))))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.price").value(first.getPrice().doubleValue()));
          }

    @Test
    public void updatePriceTest_fail() throws Exception {
    	
   when(itemService.updatePrice(first.getId(), new BigDecimal("4").negate()))
        .thenThrow(new InvalidPriceException("Invalid Price:Price can not be negative."));
       
        
        mockMvc.perform(
                put("/item/" + first.getId() + "/updatePrice")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(asJsonString(new BigDecimal("4").negate())))
           .andExpect(status().isInternalServerError());
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
    
    private HandlerExceptionResolver exceptionResolver() {
    	SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
   	 
        Properties exceptionMappings = new Properties();
 
        exceptionMappings.put(AmanException.class.getName(), "/exception");
        exceptionResolver.setExceptionMappings(exceptionMappings);
        exceptionResolver.setDefaultStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
       
 
        return exceptionResolver;
    }
    
   

}