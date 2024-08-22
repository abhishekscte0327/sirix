package io.sirix.node.immutable.xml;

import io.sirix.api.visitor.VisitResult;
import io.sirix.api.visitor.XmlNodeVisitor;
import io.sirix.node.NodeKind;
import io.sirix.node.SirixDeweyID;
import io.sirix.node.interfaces.Node;
import io.sirix.node.interfaces.immutable.ImmutableStructNode;
import io.sirix.node.interfaces.immutable.ImmutableValueNode;
import io.sirix.node.interfaces.immutable.ImmutableXmlNode;
import net.openhft.chronicle.bytes.Bytes;
import org.checkerframework.checker.nullness.qual.Nullable;
import io.sirix.node.xml.CommentNode;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNull;

/**
 * Immutable comment node wrapper.
 *
 * @author Johannes Lichtenberger
 *
 */
public final class ImmutableComment implements ImmutableValueNode, ImmutableStructNode, ImmutableXmlNode {

	/** Mutable {@link CommentNode}. */
	private final CommentNode node;

	/**
	 * Constructor.
	 *
	 * @param node
	 *            mutable {@link CommentNode}
	 */
	private ImmutableComment(final CommentNode node) {
		this.node = requireNonNull(node);
	}

	/**
	 * Get an immutable comment node instance.
	 *
	 * @param node
	 *            the mutable {@link CommentNode} to wrap
	 * @return immutable comment node instance
	 */
	public static ImmutableComment of(final CommentNode node) {
		return new ImmutableComment(node);
	}

	@Override
	public int getTypeKey() {
		return node.getTypeKey();
	}

	@Override
	public boolean isSameItem(final @Nullable Node other) {
		return node.isSameItem(other);
	}

	@Override
	public VisitResult acceptVisitor(final XmlNodeVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public long getHash() {
		return node.getHash();
	}

	@Override
	public long getParentKey() {
		return node.getParentKey();
	}

	@Override
	public boolean hasParent() {
		return node.hasParent();
	}

	@Override
	public long getNodeKey() {
		return node.getNodeKey();
	}

	@Override
	public NodeKind getKind() {
		return node.getKind();
	}

	@Override
	public int getPreviousRevisionNumber() {
		return node.getPreviousRevisionNumber();
	}

	@Override
	public int getLastModifiedRevisionNumber() {
		return node.getLastModifiedRevisionNumber();
	}

	@Override
	public byte[] getRawValue() {
		return node.getRawValue();
	}

	@Override
	public boolean hasFirstChild() {
		return node.hasFirstChild();
	}

	@Override
	public boolean hasLastChild() {
		return false;
	}

	@Override
	public boolean hasLeftSibling() {
		return node.hasLeftSibling();
	}

	@Override
	public boolean hasRightSibling() {
		return node.hasRightSibling();
	}

	@Override
	public long getChildCount() {
		return node.getChildCount();
	}

	@Override
	public long getDescendantCount() {
		return node.getDescendantCount();
	}

	@Override
	public long getFirstChildKey() {
		return node.getFirstChildKey();
	}

	@Override
	public long getLastChildKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLeftSiblingKey() {
		return node.getLeftSiblingKey();
	}

	@Override
	public long getRightSiblingKey() {
		return node.getRightSiblingKey();
	}

	@Override
	public SirixDeweyID getDeweyID() {
		return node.getDeweyID();
	}

	@Override
	public boolean equals(Object obj) {
		return node.equals(obj);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}

	@Override
	public String toString() {
		return node.toString();
	}

	@Override
	public String getValue() {
		return node.getValue();
	}

	@Override
	public long computeHash(Bytes<ByteBuffer> bytes) {
		return node.computeHash(bytes);
	}

	@Override
	public byte[] getDeweyIDAsBytes() {
		return node.getDeweyIDAsBytes();
	}
}
