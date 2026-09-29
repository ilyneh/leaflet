# core:phloem

Phloem is named after the plant tissue that moves nutrients around a plant. It moves data from
remote APIs into local storage, and the UI only ever reads from local storage.

This is a plain Kotlin/JVM library with no Android or Room dependency.

## Two fetchers

- **`PhloemPageFetcher`** is a resumable pull of a paged collection, such as a catalog. It is
  pulled on demand, for example by a Paging 3 `RemoteMediator` as the user scrolls, rather than
  crawling everything up front. Progress is keyed by `PhloemModel`, so each collection keeps its
  own cursor.
- **`PhloemItemFetcher`** is a cache-through refresh of one item, such as a detail view.

See the KDoc on each type for its guarantees.

## Design choices

- **Local storage is the source of truth.** Fetchers write into storage and return only a
  status. The UI observes storage, so a fetch never needs to hand data back to the screen.
- **Coverage over freshness.** An interrupted page pull always resumes from its cursor, however
  old it is. The TTL clock starts only after a complete pass. That suits slow-changing catalog
  data, where finishing the pass matters more than restarting it with fresher data.
- **Staleness belongs to the data.** Freshness is judged from timestamps stored with the data,
  not from the result of the last fetch.
