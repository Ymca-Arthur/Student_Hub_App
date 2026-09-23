# Mentor Notes — Student Hub

Running notes from mentoring sessions on this project. Newest at the bottom of each section as we go.
Class names below reflect the **current** code (`Project`, not the original `Task` — renamed partway
through, see "Renames" below).

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

- `37f15ff` — *"Rename Task to Project and add architecture docs"*. Renamed `Task`/`TaskAdapter`/
  `item_task.xml` to `Project`/`ProjectAdapter`/`item_project.xml` to match the finalized Home-screen
  naming. Added more dummy data + Logcat tracing to observe RecyclerView recycling. Added
  `docs/architecture.md` (Screen Flow + UML class diagram) and `docs/architecture-viewer.html`
  (rendered version). **Pushed to GitHub.**
- `0055a6b` — *"started learning about app structure and applied my app idea into the IDE"*. Removed
  the tutorial `SecondScreen` (placeholder greeting screen) and the default generated test files.
  Added the first real Student Hub feature: a task list using the RecyclerView "Big 4" pattern (see
  below) with hardcoded dummy data. (At this point the class was still called `Task`, later renamed —
  see `37f15ff` above.)
- `a5f63d6` — added the tutorial `SecondScreen` (greeting + back button) — since removed.
- Earlier commits (`68ce07f`, `ddf9d3a`, `966e57d`, ...) — early scaffolding/placeholder commits.

## Concept: RecyclerView and the "Big 4"

**The problem it solves:** if you tried to render a list of thousands of items by creating one `View`
object per item up front, you'd run out of memory and the UI would freeze. RecyclerView instead
creates just enough `View`s to fill the visible screen (plus a small buffer), and as you scroll, it
**reuses ("recycles") those same View objects** and just swaps the data/content inside them, rather
than destroying and recreating views.

Four pieces every RecyclerView list needs:

1. **Model** (`Project.java`) — plain Java class describing one item: `title`, `dueDate`, `done`.
   `title`/`dueDate` are `final` (facts that don't change); `done` is mutable because the user can
   toggle it via the checkbox.
2. **Row XML** (`item_project.xml`) — layout for a single row: checkbox + title + due-date text, each
   view given an `android:id` so Java code can find it.
3. **ViewHolder** (`ProjectAdapter.ProjectViewHolder`) — holds references to a row's views, found via
   `findViewById` **once**, in the constructor, and cached as `final` fields. This is the actual
   performance mechanism: without caching, `findViewById` (a tree search) would re-run every time a
   row scrolls into view.
4. **Adapter** (`ProjectAdapter`) — bridges the data list and the recycled `ViewHolder`s. Three
   required methods:
   - `onCreateViewHolder` — called rarely, only when a genuinely new row is needed (inflate XML, build
     a ViewHolder). Expensive, so RecyclerView minimizes calls to it.
   - `onBindViewHolder` — called constantly as you scroll: puts a (possibly different) `Project`'s data
     into an existing, possibly-reused `ViewHolder`. Cheap, called often.
   - `getItemCount` — total number of items, so RecyclerView knows scrollbar size and list bounds.

**MainActivity's job:** give the `RecyclerView` a `LayoutManager` (arrangement — vertical list here)
and an `Adapter` (data source). Currently uses a hardcoded `List<Project>` as stand-in "dummy data"
until a real data source (e.g. Firebase, per the sprint plan's December sprint) is wired in.

**Verified live:** ran the app, filtered Logcat by tag `RecyclerDemo` — over one scroll session,
`onCreateViewHolder` fired 17 times total while `onBindViewHolder` fired 43 times, and scrolling back
up through already-seen rows produced zero new `onCreateViewHolder` calls — confirms the same ~17
row objects were being reused throughout, regardless of how much scrolling happened.

## The stale-listener recycling bug (why `onBindViewHolder` clears the checkbox listener first)

```java
holder.doneCheckbox.setOnCheckedChangeListener(null);      // detach stale listener first
holder.doneCheckbox.setChecked(project.isDone());           // now safe to change visual state
holder.doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> project.setDone(isChecked));
```

Because ViewHolders are **reused** across different `Project`s as you scroll, a checkbox's listener
from a previous bind is a lambda that closed over the *previous* `Project` object. If you call
`setChecked(...)` to update the visual state while that stale listener is still attached, and the
checked state actually changes, it fires the *old* listener — silently writing `setDone(...)` onto the
wrong (now off-screen) `Project`. Detach → update visuals → reattach a fresh listener bound to the
*current* project, every bind, avoids this.

Concrete trace (3-row screen, scrolling item index 0 off-screen so its ViewHolder gets recycled to
show index 3): if the "index 0" listener isn't cleared before `setChecked` runs for "index 3", ticking
or unticking during that rebind would corrupt index 0's `done` value even though the user never touched
it — because index 0 is no longer even on screen.

## UML — what it actually is

A UML **class diagram** is just your Java class declarations drawn as boxes instead of typed as code:
class name on top, fields (`-` = private) in the middle, methods (`+` = public) at the bottom. An
arrow between two boxes (e.g. `ProjectAdapter --> Project`) means one class holds/uses the other —
same information as reading `private final List<Project> projects;` in the code, just visual. The
point of drawing it *before* writing code for the remaining 4 screens is to decide what classes/fields
are needed while it's still cheap to change your mind.

## Demo: watching recycling happen live

With only 4 dummy items, everything fit on one screen and recycling never actually triggered — nothing
to observe. Added more dummy data (20 items total, in `MainActivity.java`) so the list scrolls, plus
`Log.d(TAG, ...)` calls in `ProjectAdapter`'s `onCreateViewHolder`/`onBindViewHolder` (tag
`"RecyclerDemo"`) to watch the create-vs-bind frequency in Logcat while scrolling. Both are marked as
temporary/demo in comments — safe to trim once the concept is solid.

## Architecture folder (Submission 2) — decisions made

- **Framework tagging** (tracking whether an item belongs to School / Academic course / External
  class): **skipped for now** — can be added as a field later without restructuring anything.
- **Home screen squares:** Projects, Weekly Schedule, Upcoming Exams, Grades, 25-5 (Pomodoro
  Technique). Renamed from an initial draft (Tasks/Timetable/Exams/Grades/Timer).
- **Class naming matches the labels**, not just button text — already done in code for Projects
  (`Task`→`Project` etc., see git history above). The "25-5" button label maps to an internal class
  called `PomodoroActivity` (digits/hyphens aren't legal in a Java class name).
- Full Screen Flow diagram + UML class diagram: `docs/architecture.md` (renders natively on GitHub) and
  `docs/architecture-viewer.html` (a bigger, styled rendering of the same two diagrams, also published
  as a Claude artifact for quick viewing without opening the repo).

## Open threads / next steps

- **Still needs to happen:** get the finalized diagram into `Student Hub.docx` (the real project book)
  — redraw in draw.io (the sprint doc's suggested tool) or export/screenshot the Mermaid version. This
  is the one sprint-doc requirement not yet done — everything else in Submission 2's engineering task
  is complete and pushed.
- Once that's done: split `MainActivity` into the 5-square Home hub + a dedicated `ProjectsActivity`
  holding today's list (currently `MainActivity` shows the Projects list directly — it isn't the hub
  yet). Then build the other 4 screens (`WeeklyScheduleActivity`/`ExamsActivity`/`GradesActivity`/
  `PomodoroActivity`) using the same Big 4 pattern, one at a time — none of their Model/Adapter classes
  exist in code yet, only in the diagram.
- Eventually replace hardcoded dummy data with a real data source (sprint doc flags December as the
  "connect to Firebase" sprint).
