package AgentsTests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class BasicTest
{
	@Rule public TestName testName = new TestName();
	
	@After
	public void printCompletion()
	{
		System.out.println(testName.getMethodName() + " completed");
	}
}
