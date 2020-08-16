package org.sirix.page;

import org.sirix.access.DatabaseType;
import org.sirix.access.ResourceConfiguration;
import org.sirix.api.PageReadOnlyTrx;
import org.sirix.cache.PageContainer;
import org.sirix.cache.TransactionIntentLog;
import org.sirix.node.SirixDeweyID;
import org.sirix.page.delegates.BitmapReferencesPage;
import org.sirix.page.delegates.ReferencesPage4;
import org.sirix.page.interfaces.Page;
import org.sirix.settings.Constants;
import org.sirix.settings.Fixed;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Page utilities.
 *
 * @author Johannes Lichtenberger
 */
public final class PageUtils {

  /**
   * Private constructor to prevent instantiation.
   */
  private PageUtils() {
    throw new AssertionError("May never be instantiated!");
  }

  public static Page setReference(Page pageDelegate, int offset, PageReference pageReference) {
    final var hasToGrow = pageDelegate.setOrCreateReference(offset, pageReference);

    if (hasToGrow) {
      if (pageDelegate instanceof ReferencesPage4) {
        pageDelegate = new BitmapReferencesPage(Constants.INP_REFERENCE_COUNT, (ReferencesPage4) pageDelegate);
        pageDelegate.setOrCreateReference(offset, pageReference);
      } else {
        throw new IllegalStateException();
      }
    }

    return pageDelegate;
  }

  public static Page createDelegate(DataInput in, SerializationType type) {
    try {
      final byte kind = in.readByte();
      return switch (kind) {
        case 0 -> new ReferencesPage4(in, type);
        case 1 -> new BitmapReferencesPage(Constants.INP_REFERENCE_COUNT, in, type);
        default -> throw new IllegalStateException();
      };
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Create the initial tree structure.
   *
   * @param reference reference from revision root
   * @param pageKind  the page kind
   */
  public static void createTree(@Nonnull PageReference reference, final PageKind pageKind,
      final PageReadOnlyTrx pageReadTrx, final TransactionIntentLog log) {
    final Page page = new IndirectPage();
    log.put(reference, PageContainer.getInstance(page, page));
    reference = page.getOrCreateReference(0);

    // Create new record page.
    final UnorderedKeyValuePage recordPage =
        new UnorderedKeyValuePage(Fixed.ROOT_PAGE_KEY.getStandardProperty(), pageKind, pageReadTrx);

    final ResourceConfiguration resourceConfiguration = pageReadTrx.getResourceManager().getResourceConfig();

    // Create a {@link DocumentRootNode}.
    final SirixDeweyID id = resourceConfiguration.areDeweyIDsStored ? SirixDeweyID.newRootID() : null;

    // TODO: Should be passed from the method... chaining up.
    final DatabaseType dbType = pageReadTrx.getResourceManager().getDatabase().getDatabaseConfig().getDatabaseType();

    recordPage.setRecord(0L, dbType.getDocumentNode(id));

    log.put(reference, PageContainer.getInstance(recordPage, recordPage));
  }
}
