package nonAgentTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import AgentsTests.BasicTest;
import NonAgent.Kit;
import NonAgent.Kit.KitState;

public class KitTest extends BasicTest
{

	Kit kit;
	
	@Before
	public void setUp()
	{
		kit = new Kit();
	}
	
	@Test
	public void testContructor()
	{
		assertTrue(kit.getState() == KitState.EMPTY);
	}
	
	@Test
	public void testSetState()
	{
		kit.setState(KitState.DONE);
		
		assertTrue(kit.getState() == KitState.DONE);
	}
	
	

}
