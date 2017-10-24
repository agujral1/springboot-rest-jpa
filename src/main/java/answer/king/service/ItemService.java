package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.exception.*;
import answer.king.model.Item;
import answer.king.repo.ItemRepository;



@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public List<Item> getAll() {
		return itemRepository.findAll();
	}

	public Item save(Item item)throws AmanException {
		itemValidation(item);
		return itemRepository.save(item);
	}
	
	private void itemValidation(Item item) throws AmanException {
		BigDecimal price = item.getPrice();
		String name = item.getName();
        
		boolean invalidPrice = ( price == null || price.compareTo(BigDecimal.ZERO) < 0 );
		boolean invalidName = (name == null || name.isEmpty()) ;
		 if (invalidPrice)
	            throw new InvalidPriceException("Invalid Price:Price can not be negative.");

        if (invalidName)
            throw new InvalidNameException("Invalid Name:Name can not be empty.");

       
    }
	
	public Item updatePrice(Long id, BigDecimal price) throws AmanException {
        Item item = itemRepository.findOne(id);
        
        boolean invalidPrice = ( price == null || price.compareTo(BigDecimal.ZERO) < 0 );
		if (invalidPrice)
	            throw new InvalidPriceException("Invalid Price:Price can not be negative.");

        item.setPrice(price);
        return save(item);
    }
}
