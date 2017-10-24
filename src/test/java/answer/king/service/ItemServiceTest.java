package answer.king.service;

import answer.king.exception.InvalidNameException;
import answer.king.exception.InvalidPriceException;
import answer.king.model.Item;
import answer.king.repo.ItemRepository;
import answer.king.service.ItemService;
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
public class ItemServiceTest {

    
	    @InjectMocks
	    private ItemService itemService;

	    @Mock
	    private ItemRepository itemRepository;
	    
	    private Item first;
	    private Item second;
	    private List<Item> items = new ArrayList<>(); ;

	    @Before
	    public void init(){
	       
	         first = new Item();
	    	 first.setId(1L);
	    	 first.setName("Pepsi");
	    	 first.setPrice(new BigDecimal("1"));
	    	 
	          second = new Item();
	         second.setId(2L);
	         second.setName("Beer");
	         second.setPrice(new BigDecimal("2"));
	         items.add(first);
	         items.add(second);
	   }
	    
	    @Test
	    public void test_get_all_success() {
	        
	        when(itemRepository.findAll()).thenReturn(items);
	        List<Item> finalItemsList = itemService.getAll();

	        assertNotNull(finalItemsList);
	        assertEquals(items, finalItemsList);

	        verify(itemRepository, times(1)).findAll();
	        verifyNoMoreInteractions(itemRepository);
	    }
	    
	    @Test
	    public void test_create_item_success() throws Exception {
	        
	    when(itemRepository.save(first)).thenReturn(first);
	       Item item=  itemService.save(first);
	       
	        assertNotNull(item);
	        assertEquals(first, item);

	        verify(itemRepository, times(1)).save(first);
	        verifyNoMoreInteractions(itemRepository);
	    }

	    
	    @Test(expected = InvalidNameException.class)
	    public void test_create_item_unsuccess() throws Exception {
	    
	    	 Item item = new Item();
	    	 item.setPrice(new BigDecimal("3"));
	    	itemService.save(item); 
	        
	        verify(itemRepository, times(1)).save(second);
	        verifyNoMoreInteractions(itemRepository);
	        
	      
	    }
	    
	    @Test
	    public void updatePriceTest() throws Exception {
	        
	    	when(itemRepository.findOne(first.getId())).thenReturn(first);
	    	when(itemRepository.save(first)).thenReturn(first);
	        
	        Item result = itemService.updatePrice(first.getId(), new BigDecimal("5"));

	        assertNotNull(result);
	        assertEquals(first.getPrice(), result.getPrice());

	        verify(itemRepository, times(1)).findOne(first.getId());
	        verify(itemRepository, times(1)).save(first);
	    }
	    
	    
	    @Test(expected = InvalidPriceException.class)
	    public void updatePriceTest_fail() throws Exception {
	        
	        when(itemRepository.findOne(first.getId())).thenReturn(first);

	        itemService.updatePrice(first.getId(), new BigDecimal("6").negate());
             
	        verify(itemRepository, times(1)).findOne(first.getId());
	        verifyNoMoreInteractions(itemRepository);
	    }
}
