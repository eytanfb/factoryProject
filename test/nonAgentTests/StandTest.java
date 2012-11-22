package nonAgentTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import NonAgent.Kit;
import NonAgent.Stand;

public class StandTest
{
	Stand stand;
	
	@Before
	public void setUp()
	{
		stand = new Stand();
	}

	@Test
	public void testSetAndGetCurrentKit()
	{
		Kit kit = new Kit();
		
		assertTrue(stand.getCurrentKit() == null);
		
		stand.setCurrentKit(kit);
		
		assertEquals(kit, stand.getCurrentKit());
	}
	
	@Test
	public void testIsEmpty()
	{
		assertTrue(stand.isEmpty());
	}
	
	@Test
	public void testAddAndRemoveKit()
	{
		Kit kit = new Kit();
		
		stand.addKit(kit);
		
		assertEquals(kit, stand.getCurrentKit());
		
		stand.removeKit();
		
		assertNull(stand.getCurrentKit());
	}

}
