package io.sirix.axis.temporal;

import java.util.Iterator;

import io.sirix.api.xml.XmlNodeReadOnlyTrx;
import io.sirix.api.xml.XmlNodeTrx;
import io.sirix.Holder;
import io.sirix.XmlTestHelper;
import io.sirix.utils.XmlDocumentCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.testing.IteratorFeature;
import com.google.common.collect.testing.IteratorTester;

/**
 * Test {@link LastAxis}.
 *
 * @author Johannes Lichtenberger
 *
 */
public final class LastAxisTest {

	/** Number of iterations. */
	private static final int ITERATIONS = 5;

	/** The {@link Holder} instance. */
	private Holder holder;

	@Before
	public void setUp() {
		XmlTestHelper.deleteEverything();
		try (final XmlNodeTrx wtx = Holder.generateWtx().getXdmNodeWriteTrx()) {
			XmlDocumentCreator.createVersioned(wtx);
		}
		holder = Holder.generateRtx();
	}

	@After
	public void tearDown() {
		holder.close();
		XmlTestHelper.closeEverything();
	}

	@Test
	public void testAxis() {
		final XmlNodeReadOnlyTrx firstRtx = holder.getResourceManager().beginNodeReadOnlyTrx(1);
		final XmlNodeReadOnlyTrx thirdRtx = holder.getXmlNodeReadTrx();

		new IteratorTester<>(ITERATIONS, IteratorFeature.UNMODIFIABLE, ImmutableList.of(thirdRtx), null) {
			@Override
			protected Iterator<XmlNodeReadOnlyTrx> newTargetIterator() {
				return new LastAxis<>(firstRtx.getResourceSession(), firstRtx);
			}
		}.test();
	}

}
