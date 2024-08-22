/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.sirix.service.xml.xpath.filter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.sirix.Holder;
import io.sirix.XmlTestHelper;
import io.sirix.axis.filter.FilterTest;
import io.sirix.axis.filter.xml.WildcardFilter;
import io.sirix.axis.filter.xml.WildcardFilter.EType;
import io.sirix.exception.SirixException;

public class WildcardFilterTest {

	private Holder holder;

	@Before
	public void setUp() throws SirixException {
		XmlTestHelper.deleteEverything();
		XmlTestHelper.createTestDocument();
		holder = Holder.generateRtx();
	}

	@After
	public void tearDown() throws SirixException {
		holder.close();
		XmlTestHelper.deleteEverything();
	}

	@Test
	public void testFilterConvetions() throws SirixException {
		holder.getXmlNodeReadTrx().moveTo(9L);
		FilterTest.testFilterConventions(new WildcardFilter(holder.getXmlNodeReadTrx(), "b", EType.LOCALNAME), true);
		holder.getXmlNodeReadTrx().moveToAttribute(0);
		FilterTest.testFilterConventions(new WildcardFilter(holder.getXmlNodeReadTrx(), "p", EType.PREFIX), true);
		holder.getXmlNodeReadTrx().moveTo(1L);
		FilterTest.testFilterConventions(new WildcardFilter(holder.getXmlNodeReadTrx(), "p", EType.PREFIX), true);
		FilterTest.testFilterConventions(new WildcardFilter(holder.getXmlNodeReadTrx(), "a", EType.LOCALNAME), true);
		FilterTest.testFilterConventions(new WildcardFilter(holder.getXmlNodeReadTrx(), "c", EType.LOCALNAME), false);
		FilterTest.testFilterConventions(new WildcardFilter(holder.getXmlNodeReadTrx(), "b", EType.PREFIX), false);
	}
}
