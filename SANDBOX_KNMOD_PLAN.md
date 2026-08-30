# Sandbox Ren'Py KNMod — Universal Handler Plan

## Problem Statement

Current KNMod works well for **linear visual novels** — it flattens a single-file script by wrapping control-flow lines as `KN_MOD "..."` and passing dialogue through. But **sandbox Ren'Py games** are fundamentally different:

- **Non-linear**: The player navigates between rooms/locations via map screens
- **Multi-file**: Game logic is spread across dozens of `.rpy` files (one per room, per day, per character)
- **State-driven**: Progress is gated by variables (`day`, `time_of_day`, relationship scores, `seen_sb` sets, flags)
- **Event-based**: Events are registered in Python data structures (`Event(location=..., condition=..., label=...)`) and dispatched dynamically
- **Cyclic**: The game loop is `room → event → room → advance_time → room → ...` not `label_start → label_end`
- **Dynamic dispatch**: `jump expression current_room`, `go_to(location)`, `call screen map` — labels are computed at runtime

Current KNMod just produces garbage for these games: it flattens all branches in file order without resolving which events are reachable when, producing a disjointed mess that references unset variables and skips unreachable content.

**Goal**: A new feature `SANDBOX_KNMOD` that can take *any* sandbox Ren'Py game's `game/` folder and produce a single linear script that plays through **all storylines** in logical order — all Day 1 scenarios first, then Day 2, etc.

---

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────┐
│                    SANDBOX_KNMOD Pipeline                     │
│                                                              │
│  Phase 1: Discovery    → Scan all .rpy files, build index    │
│  Phase 2: Analysis     → Build control-flow graph (CFG)      │
│  Phase 3: Scheduling   → Topological sort by (day, time, loc)│
│  Phase 4: Linearization→ Walk CFG, emit linear KN script     │
│  Phase 5: Emit         → Write output .rpy file              │
└──────────────────────────────────────────────────────────────┘
```

---

## Phase 1: Multi-File Discovery & Parsing

### 1.1 Recursive File Scanner
**What**: Scan an entire `game/` directory tree for all `.rpy` files.
**Why**: Sandbox games spread logic across files like `day1.rpy`, `bedroom.rpy`, `kitchen.rpy`, `events.rpy`, `navigation.rpy`, etc.

```
Input:  --file=/path/to/game/
Output: List<RpyFile> with parsed ASTs
```

**Implementation**:
- New class `org.bullithulli.feature.SandboxKNMod`
- Reuse `fileUtils` pattern from `labelLookup.java` which already does recursive `.rpy` scanning
- Parse each file with the existing `parser.java` into its own AST
- Build a **global symbol table**: `Map<String, renpyLabel>` mapping every label name to its parsed symbol across all files
- Build a **global pathMatrix**: merge all per-file `parser.pathMatrix` maps

### 1.2 File Classification Heuristic
**What**: Automatically classify files by their role in the sandbox architecture.
**Why**: Different files need different treatment.

Detection rules (applied in order):
| Pattern | Classification |
|---------|---------------|
| Contains `screen` with `imagemap`/`imagebutton` + `Jump`/`action Jump` | **Navigation/Map file** |
| Contains `label` with `day` or `night` or `morning`/`evening`/`afternoon` in name | **Day/Time file** |
| File named `events.rpy` or contains `Event(` class definitions | **Event registry** |
| Contains `label` with room/location names (detected from nav files) | **Room file** |
| Contains `init python:` with class definitions | **Engine/Framework file** |
| Contains `define` / `default` predominantly | **Variables/Config file** |
| Everything else | **Content file** |

### 1.3 Global Label Index
```java
class GlobalLabelIndex {
    Map<String, LabelEntry> labels;           // labelName → entry
    Map<String, List<String>> fileToLabels;   // fileName → labels defined in it
    Map<String, List<String>> labelCallers;   // labelName → list of labels that call/jump to it
    Map<String, List<String>> labelCallees;   // labelName → list of labels it calls/jumps to
}

class LabelEntry {
    String name;
    String sourceFile;
    renpyLabel symbol;
    List<String> conditions;     // conditions guarding this label's execution
    Set<String> variablesRead;   // variables referenced in conditions
    Set<String> variablesSet;    // variables assigned within label body
    LabelCategory category;     // EVENT, ROOM, DAY_CONTROLLER, NAVIGATION, UTILITY, STORYLINE
}
```

---

## Phase 2: Control-Flow Graph (CFG) Construction

### 2.1 Intra-Label CFG
**What**: For each label, build an internal control-flow graph of its `if/elif/else` branches, `menu` choices, `jump`s, `call`s, and `return`s.
**Why**: Need to know all possible exits from a label and what conditions lead to each.

```java
class LabelCFG {
    String labelName;
    List<CFGNode> nodes;
    List<CFGEdge> edges;
}

class CFGNode {
    enum Type { ENTRY, EXIT, BRANCH, MENU_CHOICE, ASSIGNMENT, DIALOGUE, CALL, JUMP, RETURN }
    Type type;
    String content;           // the rpy line
    String condition;         // for BRANCH nodes: the if/elif condition
    List<String> varsRead;
    List<String> varsWritten;
}

class CFGEdge {
    CFGNode from, to;
    String condition;         // null = unconditional, else the branch predicate
    String choiceText;        // for menu edges: the choice label text
}
```

### 2.2 Inter-Label CFG (Call Graph)
**What**: Build a graph where nodes are labels and edges are `jump`/`call` relationships.
**Why**: Need to follow execution flow across the entire game.

The existing `parser.pathMatrix` partially does this — it maps label names to the `renpyJump`/`renpyCall` symbols that reference them. Extend it to:
- Handle `jump expression <variable>` by recording the variable and later resolving possible values
- Handle `$ go_to(location)` by recognizing it as a dynamic jump pattern
- Handle `call screen <name>` by linking to the screen definition which may contain `action Jump("label")`
- Handle `renpy.jump(...)`, `renpy.call(...)` embedded in Python

### 2.3 Dynamic Dispatch Resolution
**What**: Resolve `jump expression current_room`, `$ go_to(location)`, and screen-based navigation to concrete labels.
**Why**: This is how sandbox games implement room navigation.

Strategy:
1. **Pattern recognition**: Detect common patterns:
   - `jump expression <var>` → find all assignments to `<var>` → possible targets
   - `$ go_to(<arg>)` → find the `go_to` function definition → extract the jump/call inside it
   - `action Jump("<label>")` in screens → direct resolution
   - `$ renpy.jump("<label>")` → direct resolution
2. **Conservative over-approximation**: If we can't resolve, include ALL labels that could plausibly be targets (based on naming conventions like `room_*`, `loc_*`)

---

## Phase 3: Execution Scheduling (The Core Algorithm)

### 4.1 Time-Slot Model
**What**: Organize all game content into a grid of `(day, timeOfDay, location)` slots.
**Why**: This is how sandbox games are structured — at any given (day, time, location), a specific set of events is available.

```java
class TimeSlot implements Comparable<TimeSlot> {
    int day;
    String timeOfDay;       // "morning", "afternoon", "evening", "night"
    String location;        // "kitchen", "bedroom", "school", etc.
    List<GameEvent> availableEvents;  // events whose conditions are met
}
```

### 4.2 Dependency Graph & Topological Sort
**What**: Build a DAG of events based on their prerequisites and sort them.
**Why**: Events must be played in an order where prerequisites are satisfied.

Algorithm:
1. For each event, extract dependencies from its condition:
   - `day >= N` → must be scheduled at or after day N
   - `"event_x" in seen_sb` → must come after event_x
   - `love >= 5` → must come after enough love-building events
   - `time_of_day == "morning"` → must be in a morning slot
2. Build a DAG: event A → event B means "A must be played before B"
2. Topological sort respecting the natural ordering: `(day, time_order, location_order)`
3. If cycles exist (shouldn't in well-formed games), break them arbitrarily and warn

### 4.3 State Simulation
**What**: Simulate game state as we schedule events to verify reachability.
**Why**: Need to ensure conditions are actually met when we play each event.

```java
class GameState {
    Map<String, Object> variables;    // current variable values
    Set<String> seenEvents;           // completed events
    int currentDay;
    String currentTimeOfDay;
    String currentLocation;
    
    boolean evaluateCondition(ParsedCondition condition);
    void applyAssignments(List<Assignment> assignments);
    void advanceTime();
    void advanceDay();
}
```

**Simulation loop**:
```
state = initial game state from defaults
schedule = []

for day = 1 to maxDay:
  for timeOfDay in [morning, afternoon, evening, night]:
    for location in allLocations:
      availableEvents = events where condition(state) == true
                        AND event.location == location
                        AND event not in state.seenEvents
      for event in availableEvents (sorted by priority):
        schedule.append((day, timeOfDay, location, event))
        state.seenEvents.add(event.id)
        state.applyAssignments(event.sideEffects)
    state.advanceTime()
  state.advanceDay()
```

### 4.4 Greedy Completeness Strategy
**What**: Ensure ALL events get scheduled, even if conditions are hard to satisfy.
**Why**: The user wants to see ALL content.

After the natural simulation:
1. Identify unscheduled events
2. For each, force-set the variables needed to satisfy its conditions
2. Schedule it in a "bonus round" after the main storyline
3. Emit `KN_MOD "FORCED: Setting day=X, love=Y to trigger event Z"`

---

## Phase 4: Linearization & Output Generation

### 5.1 Script Assembly
**What**: Walk the scheduled events in order and inline their label content.
**Why**: Produce a single playable script.

For each `(day, timeOfDay, location, event)` in the schedule:
1. Emit a day/time/location header:
   ```
   KN_MOD "═══ DAY [day] — [timeOfDay] — [location] ═══"
   ```
2. Resolve the event's label from the global label index
2. Use `labelLookup`-style logic to extract the full label body (following inner calls)
3. Apply existing KNMod transformation to the extracted body:
   - Retain blocks (python, screen, image, etc.) verbatim
   - Wrap control flow as `KN_MOD "..."`
   - Pass through dialogue
4. For `menu:` choices: emit ALL branches sequentially with choice text as `KN_MOD`:
   ```
   KN_MOD "CHOICE: [choice text]"
   [branch content]
   KN_MOD "CHOICE: [other choice text]"  
   [other branch content]
   ```
5. For `if/elif/else` branches: emit ALL branches:
   ```
   KN_MOD "PATH: if [condition]"
   [if-branch content]
   KN_MOD "PATH: else [condition]"
   [else-branch content]
   ```
6. When encountering `jump` to another content label → inline that label's content (with recursion protection via a visited set)
7. When encountering `call` → inline similarly but mark entry/exit

### 5.2 Cross-File Label Inlining
**What**: When a label `jump`s or `call`s another label from a different file, resolve and inline it.
**Why**: The output must be self-contained.

Algorithm:
```
inlineLabel(labelName, visited, depth):
    if labelName in visited or depth > MAX_DEPTH:
        emit KN_MOD "RECURSION: [labelName] already visited"
        return
    visited.add(labelName)
    
    entry = globalLabelIndex.get(labelName)
    if entry is null:
        emit KN_MOD "UNRESOLVED: label [labelName] not found"
        return
    
    body = extractLabelBody(entry.symbol)
    for each line in body:
        if line is jump/call to contentLabel:
            inlineLabel(target, visited, depth+1)
        else:
            apply standard KNMod transformation
    
    visited.remove(labelName)  // allow revisit on different paths
```

### 5.3 Handling Dynamic/Computed Jumps
For `jump expression current_room` and similar:
- If resolved to concrete set of targets → emit all targets sequentially
- If unresolvable → emit `KN_MOD "DYNAMIC: jump expression [var] — could not resolve"` and list candidate labels

---

## Phase 5: Implementation Plan (Concrete Steps)

### Step 1: Multi-File Parser Infrastructure
**File**: `org.bullithulli.feature.SandboxScanner.java`
- Recursively scan directory for `.rpy` files
- Parse each with existing parser
- Build `GlobalLabelIndex`
- Build merged `pathMatrix`
- **Tests**: Unit tests with multi-file test fixtures in `src/test/resources/sandbox/`

### Step 2: Event System Detector
**File**: `org.bullithulli.feature.sandbox.EventDetector.java`  
- Pattern matching for Event class instantiation
- Pattern matching for conditional jump chains in room labels
- Build event registry with conditions
- **Tests**: Detect events from sample sandbox game fragments

### Step 3: Dependency Graph & Scheduler
**File**: `org.bullithulli.feature.sandbox.EventScheduler.java`
- Build dependency DAG from event conditions
- Topological sort with (day, time, location) ordering
- State simulation with condition evaluation
- Greedy completeness pass for unscheduled events
- **Tests**: Schedule ordering correctness

### Step 4: Linearizer & Output Writer
**File**: `org.bullithulli.feature.SandboxKNMod.java`
- Walk schedule, inline labels, apply KNMod transformation
- Handle all branch types (if/else, menu, jump, call)
- Cross-file label resolution
- Dynamic jump handling
- **Tests**: End-to-end tests with sample sandbox games

### Step 5: CLI Integration
**File**: Modify `Modder2.java`
- Add `SANDBOX_KNMOD` feature option
- Accept `--file=<directory>` (not single file)
- Optional: `--maxDay=N` to limit how many days to process
- Optional: `--locations=loc1,loc2` to limit locations
- Optional: `--startLabel=<label>` for custom entry point

---

## Common Sandbox Patterns to Handle

### Pattern 1: Day/Time Loop
```renpy
label day_loop:
    $ day += 1
    $ time_of_day = "morning"
    jump expression current_room

label advance_time:
    if time_of_day == "morning":
        $ time_of_day = "afternoon"
    elif time_of_day == "afternoon":
        $ time_of_day = "evening"
    elif time_of_day == "evening":
        $ time_of_day = "night"
    else:
        $ day += 1
        $ time_of_day = "morning"
    jump expression current_room
```
**Strategy**: Detect the time advancement pattern, model the time cycle, don't inline the loop controller.

### Pattern 2: Room/Map Navigation
```renpy
screen map_screen:
    imagemap:
        ground "images/map.png"
        hotspot (100, 200, 50, 50) action Jump("kitchen")
        hotspot (300, 400, 50, 50) action Jump("bedroom")
        hotspot (500, 100, 50, 50) action Jump("school")
```
**Strategy**: Parse screens for `action Jump("...")` to discover all navigable locations.

### Pattern 3: Conditional Events in Rooms
```renpy
label kitchen:
    if day >= 3 and not kitchen_event_1_seen:
        jump kitchen_event_1
    elif day >= 5 and kitchen_event_1_seen:
        jump kitchen_event_2
    else:
        "Nothing to do here."
        jump map
```
**Strategy**: Extract conditions, create events, schedule by day.

### Pattern 4: Event Class Registration
```renpy
init python:
    class Event:
        def __init__(self, label, location, condition, ...):
            ...
    
    events.append(Event(
        label="ev_kitchen_01",
        location="kitchen",
        condition="day >= 3 and love > 5"
    ))
```
**Strategy**: Parse Python `Event(...)` instantiations, extract parameters as strings, parse conditions.

### Pattern 5: Repeatable/Non-Repeatable Events
```renpy
label kitchen:
    if "kitchen_chat" not in seen_events:
        jump kitchen_chat
    # repeatable
    jump kitchen_idle
```
**Strategy**: Track `seen_events` set in state simulation, schedule non-repeatable events once.

### Pattern 6: go_to() Navigation Functions
```renpy
init python:
    def go_to(location, previous=True):
        global current_room
        if previous:
            previous_room = current_room
        current_room = location
        renpy.jump(current_room)
```
**Strategy**: Detect `go_to` as a jump-wrapper, trace the `renpy.jump(current_room)` pattern.

---

## Risks & Mitigations

| Risk | Mitigation |
|------|-----------|
| Event conditions too complex to parse | Fall back to string representation in KN_MOD output, schedule event anyway in forced mode |
| Dynamic label names unresolvable | Over-approximate: include all labels matching naming patterns |
| Infinite recursion in label inlining | Depth limit (default: 50) + visited set |
| Game uses obfuscated/compiled Python | Skip those files, warn user |
| Event system uses custom classes we don't recognize | Provide `--eventPattern` CLI option for regex |
| Some events are mutually exclusive | Show ALL paths with clear KN_MOD markers |
| Performance with very large games (1000+ .rpy files) | Use KNModFast streaming approach where possible |

---

## Testing Strategy

### Unit Test Fixtures
Create `src/test/resources/sandbox/` with:
1. `simple_sandbox/` — 3 rooms, 2 days, 5 events, direct conditional jumps
2. `event_class_sandbox/` — Event class registration pattern
2. `complex_sandbox/` — Real-world-like structure with 10+ rooms, relationship vars, seen sets

### Integration Tests
- End-to-end: input directory → output single .rpy → verify output contains all events in correct order
- Verify output is valid Ren'Py syntax (parseable by Ren'Py)
- Verify all events appear exactly once
- Verify day ordering is monotonically increasing

---

## CLI Usage (Final)

```bash
# Basic usage
java -jar modder-2.jar --feature=SANDBOX_KNMOD --file=/path/to/game/ --outfile=/tmp/sandbox_knmod.rpy

# With options
java -jar modder-2.jar --feature=SANDBOX_KNMOD \
    --file=/path/to/game/ \
    --outfile=/tmp/sandbox_knmod.rpy \
    --maxDay=30 \
    --startLabel="label start" \
    --forceDontKNModForStartsWith="call phone,call message"
```

---

## Implementation Priority Order

1. **Multi-file scanning + Global label index** (foundation for everything)
2. **Inter-label CFG + call graph** (needed to know what connects to what)
3. **Event detection** (the core sandbox-specific logic)
4. **Scheduler + state simulation** (orders events correctly)
5. **Linearizer with cross-file inlining** (produces output)
6. **CLI integration** (makes it usable)
7. **Edge cases & polish** (dynamic jumps, forced scheduling, error handling)

Each step is independently testable and builds on the previous one.
