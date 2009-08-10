// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco, Luca Marrocco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.closure;

import static ch.lambdaj.function.argument.ArgumentsFactory.*;
import static ch.lambdaj.proxy.ProxyUtil.*;

/**
 * An utility class of static factory methods that create closures
 * @author Mario Fusco
 */
public final class ClosuresFactory {
	
	private ClosuresFactory() { }

	private static final ThreadLocal<AbstractClosure> closures = new ThreadLocal<AbstractClosure>();

    /**
     * Binds an object to the active closure that is the last one created in the current thread.
     * @param closed The object that has to be bind to the active closure
     * @param closedClass The actual class of the proxied object
     * @return An instance of the closedClass that is actually a proxy used to register all the invocation on the closed object
     */
	public static <T> T bindClosure(T closed, Class<T> closedClass) {
		AbstractClosure closure = closures.get();
		closure.setClosed(closed);
		return createProxyClosure(closure, closedClass);
	}

    /**
     * Creates a proxy used to register all the invocation on the closed object
     * @param closure The closure to which the invocations on the returned proxy are related
     * @param closedClass The actual class of the proxied object
     * @return An instance of the closedClass that is actually a proxy used to register all the invocation on the closed object
     */
	static <T> T createProxyClosure(AbstractClosure closure, Class<T> closedClass) {
		return createProxy(new ProxyClosure(closure), closedClass, true);
	}

    /**
     * Creates a generic (not typed) closure and binds it to the current thread
     * @return The newly created closure
     */
	public static Closure createClosure() {
		Closure closure = new Closure();
		closures.set(closure);
		return closure;
	}

    /**
     * Creates a closure with a single free variable and binds it to the current thread
     * @param type1 The type of the free variable of the newly created closure
     * @return The newly created closure
     */
	public static <A> Closure1<A> createClosure(Class<A> type1) {
		Closure1<A> closure = new Closure1<A>();
		closures.set(closure);
		return closure;
	}

    /**
     * Creates a closure with two free variables and binds it to the current thread
     * @param type1 The type of the first free variable of the newly created closure
     * @param type2 The type of the second free variable of the newly created closure
     * @return The newly created closure
     */
	public static <A, B> Closure2<A, B> createClosure(Class<A> type1, Class<B> type2) {
		Closure2<A, B> closure = new Closure2<A, B>();
		closures.set(closure);
		return closure;
	}

    /**
     * Creates a closure with three free variables and binds it to the current thread
     * @param type1 The type of the first free variable of the newly created closure
     * @param type2 The type of the second free variable of the newly created closure
     * @param type3 The type of the third free variable of the newly created closure
     * @return The newly created closure
     */
	public static <A, B, C> Closure3<A, B, C> createClosure(Class<A> type1, Class<B> type2, Class<C> type3) {
		Closure3<A, B, C> closure = new Closure3<A, B, C>();
		closures.set(closure);
		return closure;
	}

    /**
     * Defines a free variable of the given Class for the currently active closure
     * @param clazz The Class of the new variable
     * @return A placeholder that represent a free closure variable of the given Class
     */
    public static <T> T createClosureVarPlaceholder(Class<T> clazz) {
		return isProxable(clazz) ? createVoidProxy(clazz) : createFinalArgumentPlaceholder(clazz);
	}

    /**
     * Tests if the given object is actually a placeholder for a free variable of a closure
     * @param object The object to be tested
     * @return true if the given object is actually a placeholder for a free variable of a closure
     */
	public static boolean isClosureVarPlaceholder(Object object) {
		return object != null && (isVoidProxy(object) || object.equals(createFinalArgumentPlaceholder(object.getClass())));
	}
}
