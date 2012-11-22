package nonAgentTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import NonAgent.EnteringConveyor;
import NonAgent.Kit;

public class EnteringConveyorTest
{
	EnteringConveyor enteringConveyor;

	@Before
	public void setUp()
	{
		enteringConveyor = new EnteringConveyor();
	}
	
	@Test
	public void testSetAndGetCurrentKit()
	{
		Kit kit = new Kit();
		
		enteringConveyor.setCurrentKit(kit);
		
		assertEquals(kit, enteringConveyor.getCurrentKit());
	}
	
	@Test
	public void testAddKit()
	{
		Kit kit = new Kit();
		
		enteringConveyor.addKit(kit);
		
		assertEquals(kit, enteringConveyor.getCurrentKit());
		assertTrue(enteringConveyor.getKits().size() == 1);
	}
	
	@Test
	public void testRemoveKit()
	{
		Kit kit = new Kit();
		
		enteringConveyor.addKit(kit);
		
		assertEquals(kit, enteringConveyor.getCurrentKit());
		assertTrue(enteringConveyor.getKits().size() == 1);
		
		enteringConveyor.removeKit(kit);
		
		assertNull(enteringConveyor.getCurrentKit());
		assertTrue(enteringConveyor.getKits().size() == 0);
	}

}
