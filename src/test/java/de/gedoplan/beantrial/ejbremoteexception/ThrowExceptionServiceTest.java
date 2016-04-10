package de.gedoplan.beantrial.ejbremoteexception;

import de.gedoplan.baselibs.naming.JNDIContextFactory;
import de.gedoplan.baselibs.naming.LookupHelper;
import de.gedoplan.beantrial.ejbremoteexception.service.MyException;
import de.gedoplan.beantrial.ejbremoteexception.service.ThrowExceptionService;

import javax.ejb.EJBException;
import javax.naming.Context;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ThrowExceptionServiceTest {
	public static final String APPLICATION_NAME = null;
	public static final String MODULE_NAME = "ejb-remote-exception-demo";

	private static Context jndiCtx;
	private static ThrowExceptionService service;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void before() throws Exception {
		jndiCtx = JNDIContextFactory.getInitialContext();

		String lookupName = LookupHelper.getEjbLookupName(ThrowExceptionService.class, APPLICATION_NAME, MODULE_NAME,
				"ThrowExceptionServiceBean");
		System.out.println("Lookup-Name: " + lookupName);

		service = (ThrowExceptionService) jndiCtx.lookup(lookupName);
	}

	@AfterClass
	public static void after() throws Exception {
		jndiCtx.close();
	}

	@Test
	public void testThrowNoException() throws Exception {
		service.throwNoException();
	}

	@Test
	public void testThrowMyException() throws Exception {
		this.expectedException.expect(MyException.class);

		service.throwMyException();
	}

	@Test
	public void testNullPointerException() throws Exception {
		this.expectedException.expect(isEjbExceptionCausedBy(NullPointerException.class));

		service.throwNullPointerException();
	}

	@Test
	public void testThrowSqlException() throws Exception {
		this.expectedException.expect(isEjbExceptionCausedByStandardExceptionsExcudingClassNotFoundException());

		service.throwSqlException();
	}

	private Matcher<?> isEjbExceptionCausedByStandardExceptionsExcudingClassNotFoundException() {
		return new TypeSafeDiagnosingMatcher<Throwable>() {

			@Override
			public void describeTo(Description description) {
				description.appendText(
						"EJBException caused by standard exceptions only (excluding ClassNotFoundException)");

			}

			@Override
			protected boolean matchesSafely(Throwable item, Description mismatchDescription) {

				if (!item.getClass().equals(EJBException.class)) {
					appendExceptionChain(item, mismatchDescription);
					return false;
				}

				Throwable cause = item.getCause();
				while (cause != null) {
					String causeClassName = cause.getClass().getName();

					if (cause.getClass().equals(ClassNotFoundException.class)
							|| !causeClassName.startsWith("java.") && !causeClassName.startsWith("javax.")) {
						appendExceptionChain(item, mismatchDescription);
						return false;
					}

					cause = cause.getCause();
				}

				return true;
			}
		};
	}

	private static Matcher<?> isEjbExceptionCausedBy(final Class<? extends Throwable> causeClass) {
		return new TypeSafeDiagnosingMatcher<Throwable>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("EJBException caused by " + causeClass.getSimpleName());

			}

			@Override
			protected boolean matchesSafely(Throwable item, Description mismatchDescription) {

				if (!item.getClass().equals(EJBException.class)) {
					appendExceptionChain(item, mismatchDescription);
					return false;
				}

				Throwable cause = item.getCause();
				while (cause != null) {

					if (causeClass.isAssignableFrom(cause.getClass())) {
						return true;
					}

					cause = cause.getCause();
				}

				appendExceptionChain(item, mismatchDescription);
				return false;
			}
		};
	}

	private static void appendExceptionChain(Throwable item, Description mismatchDescription) {
		mismatchDescription.appendText(item.getClass().getName());
		while (true) {
			item = item.getCause();
			if (item == null) {
				return;
			}
			mismatchDescription.appendText("<-").appendText(item.getClass().getName());
		}
	}
}
