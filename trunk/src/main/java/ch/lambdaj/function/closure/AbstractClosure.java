package ch.lambdaj.function.closure;

import java.lang.reflect.*;
import java.util.*;

abstract class AbstractClosure {

	private Object closed;
	
	private List<Method> methodList = new ArrayList<Method>();
	private List<Object[]> argsList = new ArrayList<Object[]>();
	
	private int unboundParamsCount = 0;
	
	AbstractClosure() { }

	void setClosed(Object closed) {
		this.closed = closed;
	}
	
	void registerInvocation(Method method, Object[] args) {
		methodList.add(method);
		if (args == null || args.length == 0) argsList.add(null);
		else {
			for (int i = 0; i < args.length; i++) {
				if (!isBoundParam(args[i])) {
					args[i] = null;
					unboundParamsCount++;
				}
			}
			argsList.add(args);
		}
	}
	
	protected Object closeOne(Object... params) {
		List<Object[]> boundParams = bindParams(params);
		Object result = closed;
		
		Iterator<Object[]> argsIterator = boundParams != null ? boundParams.iterator() : null;
		for (Method method : methodList) {
			if (result == null) return null;
			try {
				result = method.invoke(result, argsIterator != null ? argsIterator.next() : null);
			} catch (Exception e) {
				throw new WrongClosureInvocationException("Error invoking " + method + " on " + result, e);
			}
		}
		
		return result;
	}

	protected List<Object> closeAll(Object... params) {
		List<Object> results = new ArrayList<Object>();
		for (Object param : params) results.add(closeOne(param));
		return results;
	}
	
	protected List<Object> closeAll(Iterable<?>... params) {
		List<Object> results = new ArrayList<Object>();
		
		int length = params.length;
		Iterator<?>[] iterators = new Iterator<?>[length];
		for (int i = 0; i < length; i++) iterators[i] = params[i].iterator();

		boolean finished = false;
		while (true) {
			Object[] paramSet = new Object[length];
			for (int i = 0; i < length; i++) {
				if (!iterators[i].hasNext()) {
					finished = true;
					break;
				}
				paramSet[i] = iterators[i].next();
			}
			if (finished) break;
			results.add(closeOne(paramSet));
		}
		
		return results;
	}
	
	private List<Object[]> bindParams(Object... params) {
		if (params == null || params.length == 0) {
			if (unboundParamsCount == 0) return null;
			throw new WrongClosureInvocationException("Closure invoked without params instead of the expected " + unboundParamsCount);
		}
		if (unboundParamsCount != params.length)
			throw new WrongClosureInvocationException("Closure invoked with " + params.length + " params instead of the expected " + unboundParamsCount);
		
		int paramCounter = 0;
		List<Object[]> boundParams = new ArrayList<Object[]>();
		for (Object[] args : argsList) {
			if (args == null) boundParams.add(null);
			else {
				Object[] objs = new Object[args.length];
				for (int i = 0; i < args.length; i++) objs[i] = args[i] != null ? args[i] : params[paramCounter++]; 
				boundParams.add(objs);
			}
		}
		return boundParams;
	}
	
	private boolean isBoundParam(Object param) {
		if (param == null) return false;
		if (param instanceof Number && ((Number)param).intValue() == 0) return false;
		return param != Boolean.FALSE;	
	}
}