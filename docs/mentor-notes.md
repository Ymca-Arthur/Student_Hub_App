# Mentor Notes — Student Hub

Running notes from mentoring sessions on this project. Newest at the bottom of each section as we go.

## Project context

**App:** Student Hub (`app/src/main/java/com/example/studenthubapp`) — one Android app centralizing
everything a high-schooler needs to manage school: timetable, tasks/homework, exam countdown,
weighted grade average, and a Pomodoro study timer. Target audience: grades 10-12, especially students
juggling bagrut prep with extra academic frameworks. Source spec: `Student Hub.docx`
(`8200 Speedrun/Work that isnt code/`), Part A of the bagrut project book.

Competitive analysis (from the same doc) compared against:
- **My Study Life** — good personal planner, but no grade tracking and not in Hebrew.
- **Mashov** — the official Israeli school app; shows grades/attendance, but only teacher-entered data,
  no personal task/study tools.

Student Hub's pitch: combine both, add a weighted bagrut-average calculator and Pomodoro timer, all
in Hebrew.

**Curriculum context:** `3 ספרינט נובמבר.pdf` (same folder) is the mentor's November sprint plan:
4 hours teaching RecyclerView architecture, 4 lab hours to (1) integrate it into your own project with
dummy data and (2) use AI to draft a UML diagram. Page 2: **Submission 2 — "Architecture folder,"
due before Hanukkah break** — a Screen Flow diagram (all screens + navigation arrows) and a UML class
diagram, both meant to be AI-assisted rather than invented from scratch.

## Git history so far

- `0055a6b` — *"started learning about app structure and applied my app idea into the IDE"*
  Removed the tutorial `SecondScreen` (placeholder greeting screen) and the default generated test
  files. Added the first real Student Hub feature: a task list using the RecyclerView "Big 4" pattern
  (see below) with hardcoded dummy data.
- `a5f63d6` — added the tutorial `SecondScreen` (greeting + back button) — since removed.
- Earlier commits (`68ce07f`, `ddf9d3a`, `966e57d`, ...) — early scaffolding/placeholder commits.

## Concept: RecyclerView and the "Big 4"

**The problem it solves:** if you tried to render a list of thousands of items by creating one `View`
object per item up front, you'd run out of memory and the UI would freeze. RecyclerView instead
creates just enough `View`s to fill the visible screen (plus a small buffer), and as you scroll, it
**reuses ("recycles") those same View objects** and just swaps the data/content inside them, rather
than destroying and recreating views.

Four pieces every RecyclerView list needs:

1. **Model** (`Task.java`) — plain Java class describing one item: `title`, `dueDate`, `done`.
   `title`/`dueDate` are `final` (facts that don't change); `done` is mutable because the user can
   toggle it via the checkbox.
2. **Row XML** (`item_task.xml`) — layout for a single row: checkbox + title + due-date text, each
   view given an `android:id` so Java code can find it.
3. **ViewHolder** (`TaskAdapter.TaskViewHolder`) — holds references to a row's views, found via
   `findViewById` **once**, in the constructor, and cached as `final` fields. This is the actual
   performance mechanism: without caching, `findViewById` (a tree search) would re-run every time a
   row scrolls into view.
4. **Adapter** (`TaskAdapter`) — bridges the data list and the recycled `ViewHolder`s. Three required
   methods:
   - `onCreateViewHolder` — called rarely, only when a genuinely new row is needed (inflate XML, build
     a ViewHolder). Expensive, so RecyclerView minimizes calls to it.
   - `onBindViewHolder` — called constantly as you scroll: puts a (possibly different) `Task`'s data
     into an existing, possibly-reused `ViewHolder`. Cheap, called often.
   - `getItemCount` — total number of items, so RecyclerView knows scrollbar size and list bounds.

**MainActivity's job:** give the `RecyclerView` a `LayoutManager` (arrangement — vertical list here)
and an `Adapter` (data source). Currently uses a hardcoded `List<Task>` as stand-in "dummy data" until
a real data source (e.g. Firebase, per the sprint plan's December sprint) is wired in.

## The stale-listener recycling bug (why `onBindViewHolder` clears the checkbox listener first)

```java
holder.doneCheckbox.setOnCheckedChangeListener(null);   // detach stale listener first
holder.doneCheckbox.setChecked(task.isDone());            // now safe to change visual state
holder.doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));
```

Because ViewHolders are **reused** across different `Task`s as you scroll, a checkbox's listener from
a previous bind is a lambda that closed over the *previous* `Task` object. If you call `setChecked(...)`
to update the visual state while that stale listener is still attached, and the checked state actually
changes, it fires the *old* listener — silently writing `setDone(...)` onto the wrong (now off-screen)
`Task`. Detach → update visuals → reattach a fresh listener bound to the *current* task, every bind,
avoids this.

Concrete trace (3-row screen, scrolling task index 0 off-screen so its ViewHolder gets recycled to
show index 3): if the "index 0" listener isn't cleared before `setChecked` runs for "index 3", ticking
or unticking during that rebind would corrupt index 0's `done` value even though the user never touched
it — because index 0 is no longer even on screen.

## Demo added this session: watching recycling happen live

With only 4 tasks, everything fits on one screen and recycling never actually triggers — nothing to
observe. To make it visible:

- **`MainActivity.java`** — added more dummy `Task`s (20 total) so the list is longer than one screen
  and actually scrolls. Marked as temporary/demo data in a comment.
- **`TaskAdapter.java`** — added `Log.d(TAG, ...)` calls in `onCreateViewHolder` and `onBindViewHolder`
  (tag `"RecyclerDemo"`) so you can watch, in Logcat, how few times `onCreateViewHolder` fires versus
  how many times `onBindViewHolder` fires while scrolling.

**How to observe it:** run the app on a device/emulator, open Logcat, filter by tag `RecyclerDemo`,
then scroll the task list up and down. You should see `onCreateViewHolder` fire only a handful of
times near the start (enough rows to fill the screen + a small buffer), while `onBindViewHolder` keeps
firing on every scroll as rows get reused for new positions.

These are marked as temporary/demo — safe to trim the extra dummy tasks and remove the logging once
the concept is solid and you're building the real data-driven version.

## Open threads / next steps

- **Architecture folder (Submission 2)** — finalized in `docs/architecture.md`. Home screen with 5
  tappable squares: Projects, Weekly Schedule, Upcoming Exams, Grades, 25-5 (Pomodoro). Framework
  tagging (School/Academic/External) explicitly deferred to a later sprint.
- **`Task` renamed to `Project` throughout the codebase** (this session): `Task.java` → `Project.java`,
  `TaskAdapter.java` → `ProjectAdapter.java`, `item_task.xml` → `item_project.xml`,
  `R.id.taskRecyclerView` → `R.id.projectRecyclerView`, all `MainActivity` references updated. Done via
  `git mv` + content edits, verified with `./gradlew compileDebugJavaWithJavac`.
- Still outstanding: get the finalized diagram into `Student Hub.docx` (the real project book) —
  redraw in draw.io or export the Mermaid version; commit + push current work to GitHub.
- Once pushed, the real refactor: split `MainActivity` into the 5-square Home hub + a dedicated
  `ProjectsActivity` holding today's list. Then build the other 4 screens
  (`WeeklyScheduleActivity`/`ExamsActivity`/`GradesActivity`/`PomodoroActivity`) using the same Big 4
  pattern, one at a time.
- Eventually replace hardcoded dummy data with a real data source (sprint doc flags December as the
  "connect to Firebase" sprint).
