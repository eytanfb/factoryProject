package helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum TestHelper
{
	INSTANCE;
	
	public void callPrivateMethodWithArguments(String className, String methodName, Object callingObject, Object... args)
	{
		Class targetClass = null;
		try
		{
			targetClass = Class.forName(className);
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		Method method = null;
		try
		{
			Class[] classes = new Class[args.length];
			
			for(int i = 0; i < args.length; i++)
			{
				classes[i] = args[i].getClass();
			}
			method = targetClass.getDeclaredMethod(methodName, classes);
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		method.setAccessible(true);
		
		try
		{
			method.invoke(callingObject, args);
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
	
	public void callPrivateMethod(String className, String methodName, Object callingObject)
	{
		Class targetClass = null;
		try
		{
			targetClass = Class.forName(className);
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		Method method = null;
		try
		{
			method = targetClass.getDeclaredMethod(methodName);
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		method.setAccessible(true);
		
		try
		{
			method.invoke(callingObject);
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}


}
