package nonAgentTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import AgentsTests.BasicTest;
import NonAgent.ExitingConveyor;
import NonAgent.Kit;

public class ExitingConveyorTest extends BasicTest
{
	ExitingConveyor exitingConveyor;
	
	@Before
	public void setUp()
	{
		exitingConveyor = new ExitingConveyor();
	}

	@Test
	public void testAddKit()
	{
		Kit kit = new Kit();
		
		exitingConveyor.addKit(kit);
		
		assertEquals(1, exitingConveyor.getKits().size());
		assertEquals(kit, exitingConveyor.getKits().get(0));
	}
	
	@Test
	public void testRemoveKit()
	{
		Kit kit = new Kit();
		
		exitingConveyor.addKit(kit);
		
		assertEquals(1, exitingConveyor.getKits().size());
		assertEquals(kit, exitingConveyor.getKits().get(0));
		
		exitingConveyor.removeKit(kit);
		
		assertEquals(0, exitingConveyor.getKits().size());
		assertTrue(exitingConveyor.getKits().isEmpty());
	}

}
