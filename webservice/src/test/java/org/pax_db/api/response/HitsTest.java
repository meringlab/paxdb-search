package org.pax_db.api.response;

import org.junit.Test;
import org.pax_db.api.search.Hits;

import static org.junit.Assert.assertEquals;

public class HitsTest {
	final int pageSize = 10;
	@Test
	public void testPaging() {
		assertTotalPages(0, pageSize, 0);
		assertTotalPages(7, pageSize, 1);
		assertTotalPages(10, pageSize, 1);
		assertTotalPages(11, pageSize, 2);
		assertTotalPages(17, pageSize, 2);
		assertTotalPages(20, pageSize, 2);
	}

	private void assertTotalPages(final int totalResults, final int pageSize, final Integer expectedPages) {
		Hits cut = new Hits(null, null, null, totalResults, 1, pageSize);
		assertEquals("wrong total pages", expectedPages, cut.getTotalPages());
	}
}
