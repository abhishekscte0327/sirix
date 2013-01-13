package org.sirix.index.bplustree;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sirix.api.PageReadTrx;
import org.sirix.api.PageWriteTrx;
import org.sirix.exception.SirixException;
import org.sirix.node.interfaces.Record;
import org.sirix.node.interfaces.RecordPersistenter;
import org.sirix.page.PageKind;
import org.sirix.page.PageReference;
import org.sirix.page.delegates.PageDelegate;
import org.sirix.page.interfaces.KeyValuePage;
import org.sirix.page.interfaces.Page;

import com.google.common.base.Optional;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

/**
 * Leaf node key/value page.
 * 
 * @author Johannes Lichtenberger
 * 
 * @param <K>
 *          the key
 * @param <V>
 *          the value
 */
public class BPlusLeafNodePage<K extends Comparable<? super K> & Record, V extends Record> 
		implements KeyValuePage<K, V> {

	/** Key of record page. This is the base key of all contained nodes. */
	private final long mRecordPageKey;

	/** Key/Value records. */
	private final Map<K, V> mRecords;

	/** {@link PageDelegate} reference. */
	private int mRevision;

	/** Determine if node page has been modified. */
	private boolean mIsDirty;

	/** Sirix {@link PageReadTrx}. */
	private final PageReadTrx mPageReadTrx;

	/** Optional left page reference (leaf page). */
	private Optional<PageReference> mLeftPage;

	/** Optional right page reference (inner node page). */
	private Optional<PageReference> mRightPage;

	private PageKind mPageKind;

	/** Determines the node kind. */
	public enum Kind {
		/** Leaf node. */
		LEAF,

		/** Inner node. */
		INNERNODE
	}

	/**
	 * Create record page.
	 * 
	 * @param recordPageKey
	 *          base key assigned to this node page
	 * @param pageKind
	 * 					the kind of page (in which subtree it is)
	 * @param revision
	 *          revision the page belongs to
	 * @param pageReadTrx
	 *          Sirix page reading transaction
	 * @param kind
	 *          determines if it's a leaf or inner node page
	 */
	public BPlusLeafNodePage(final @Nonnegative long recordPageKey, final @Nonnull PageKind pageKind,
			final @Nonnegative int revision, final @Nonnull PageReadTrx pageReadTrx) {
		// Assertions instead of checkNotNull(...) checks as it's part of the
		// internal flow.
		assert recordPageKey >= 0 : "recordPageKey must not be negative!";
		assert pageKind != null;
		assert revision >= 0 : "revision must not be negative!";
		assert pageReadTrx != null : "pageReadTrx must not be null!";
		mRevision = revision;
		mRecordPageKey = recordPageKey;
		mRecords = new TreeMap<>();
		mIsDirty = true;
		mPageReadTrx = pageReadTrx;
		mLeftPage = Optional.absent();
		mRightPage = Optional.absent();
		mPageKind = pageKind;
	}

	/**
	 * Read node page.
	 * 
	 * @param in
	 *          input bytes to read page from
	 * @param pageReadTrx
	 *          {@link 
	 */
	protected BPlusLeafNodePage(final @Nonnull ByteArrayDataInput in,
			final @Nonnull PageReadTrx pageReadTrx) {
		mRevision = in.readInt();
		mRecordPageKey = in.readLong();
		final int size = in.readInt();
		mRecords = new TreeMap<>();
		final RecordPersistenter persistenter = pageReadTrx.getSession()
				.getResourceConfig().mPersistenter;
		for (int offset = 0; offset < size; offset++) {
			// Must be the key which has been serialized.
			@SuppressWarnings("unchecked")
			final K key = (K) persistenter.deserialize(in, pageReadTrx);
			// Must be the value which has been serialized.
			@SuppressWarnings("unchecked")
			final V value = (V) persistenter.deserialize(in, pageReadTrx);
			mRecords.put(key, value);
		}
		assert pageReadTrx != null : "pageReadTrx must not be null!";
		mPageReadTrx = pageReadTrx;
		mPageKind = PageKind.getKind(in.readByte());
	}

	public void setLeftPage(final @Nonnull Optional<PageReference> leftPage) {
		mLeftPage = leftPage;
	}

	public void setRightPage(final @Nonnull Optional<PageReference> rightPage) {
		mLeftPage = rightPage;
	}

	@Override
	public void serialize(final @Nonnull ByteArrayDataOutput out) {
		out.writeInt(mRevision);
		out.writeLong(mRecordPageKey);
		serializePointer(mLeftPage, out);
		serializePointer(mRightPage, out);
		final RecordPersistenter persistenter = mPageReadTrx.getSession()
				.getResourceConfig().mPersistenter;
		for (final Map.Entry<K, V> node : mRecords.entrySet()) {
			persistenter.serialize(out, node.getKey(), mPageReadTrx);
			persistenter.serialize(out, node.getValue(), mPageReadTrx);
		}
	}

	private void serializePointer(final @Nonnull Optional<PageReference> page,
			final @Nonnull ByteArrayDataOutput out) {
		if (page.isPresent()) {
			out.writeBoolean(page.get().getKey() == org.sirix.settings.Constants.NULL_ID ? false
					: true);
		} else {
			out.writeBoolean(false);
		}
	}

	@Override
	public int getRevision() {
		return mRevision;
	}

	@Override
	public PageReference[] getReferences() {
		return null;
	}

	@Override
	public <KE extends Comparable<? super KE>, VA extends Record, S extends KeyValuePage<KE, VA>> void commit(
			PageWriteTrx<KE, VA, S> pageWriteTrx) throws SirixException {
	}

	@Override
	public PageReference getReference(@Nonnegative int offset) {
		return null;
	}

	@Override
	public boolean isDirty() {
		return mIsDirty;
	}

	@Override
	public Page setDirty(final boolean dirty) {
		mIsDirty = dirty;
		return this;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return mRecords.entrySet();
	}

	@Override
	public Collection<V> values() {
		return mRecords.values();
	}

	@Override
	public long getPageKey() {
		return mRecordPageKey;
	}

	@Override
	public V getValue(final @Nonnull K key) {
		return mRecords.get(key);
	}

	@Override
	public void setEntry(final @Nonnull K key, final @Nullable V value) {
		mRecords.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends KeyValuePage<K, V>> C newInstance(
			final @Nonnegative long recordPageKey, final @Nonnull PageKind pageKind, final @Nonnegative int revision,
			final @Nonnull PageReadTrx pageReadTrx) {
		return (C) new BPlusLeafNodePage<K, V>(recordPageKey, pageKind, revision, pageReadTrx);
	}

	@Override
	public PageReadTrx getPageReadTrx() {
		return mPageReadTrx;
	}

	@Override
	public PageKind getPageKind() {
		return mPageKind;
	}
	
	@Override
	public KeyValuePage<K, V> setRevision(final @Nonnegative int revision) {
		// Only used internally, thus assertions are preferred!
		assert revision >= 0 : "revision parameter must be >= 0!";
		mRevision  = revision;
		return this;
	}
}
