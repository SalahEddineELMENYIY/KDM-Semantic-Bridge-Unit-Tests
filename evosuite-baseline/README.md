# EvoSuite Baseline Comparison

This folder contains the EvoSuite-generated test baseline used in the empirical comparison reported in §4.4 of the manuscript *"An LLM-as-a-Judge Approach to Optimizing KDM-Based Test Units Generation"*. It is intended for independent reproduction and verification of the head-to-head numbers cited in the paper.

## Purpose

To provide a reproducible head-to-head comparison between (a) the KDM + LLM-as-a-Judge architecture proposed in this paper and (b) Search-Based Software Testing (SBST), exemplified here by the reference SBST tool EvoSuite. The comparison covers the same banking-system application across line, branch, and method coverage, along with readability indicators.

## Results summary

| Class | EvoSuite Line | EvoSuite Branch | EvoSuite Method | This Work (Table 2) | Goals covered |
|---|---|---|---|---|---|
| `SavingsAccount.java` | 100.0% | 100.0% | 100.0% | 100 / 100 / 100 | 151 / 166 |
| `InsolventException.java` | 100.0% | N/A* | 100.0% | 100 / N/A / 100 | 6 / 6 |
| `Main.java` | **96.36%** | **86.67%** | 100.0% | **100 / 100 / 100** | 151 / 171 |

*EvoSuite reports 100% because the exception class contains no branches; this matches the N/A reported in Table 2.

The `Main.java` gap (3.64% line, 13.33% branch) is the central empirical finding: EvoSuite cannot drive the interactive `Scanner`-based menu loop to its intended branches and instead achieves partial coverage by triggering `NoSuchElementException` from insufficient input streams. The judge-selected suite reaches 100% on the same class through the three-pattern harness described in §4.2 — `System.setIn` / `System.setOut` stream redirection, reflective invocation of private input-validation helpers (`readDouble`, `displayMenu`), and `@ParameterizedTest`-driven boundary inputs.

Raw per-class CSVs are in [`statistics/`](statistics/); a consolidated version is in [`statistics/summary.csv`](statistics/summary.csv).

## Readability indicators

| Metric | EvoSuite | This Work |
|---|---|---|
| Total `@Test` methods | 24 | 228 |
| Total lines of test code | 391 | 4,283 |
| Average test-method name length | 5.3 chars (`test0`–`test13`) | ~48 chars (semantic names) |
| `@DisplayName` annotations | 0 | 241 |
| `@Nested` test groupings | 0 | 8 |
| `@ParameterizedTest` declarations | 0 | 19 |
| `@BeforeEach` setup methods | 0 | 14 |

## Requirements

- **JDK 8.** EvoSuite 1.2.0 requires the Java 8 runtime, and the banking sources must be recompiled with `-target 1.8` so the resulting class files can be loaded by EvoSuite's instrumented JVM. The run reported here used Eclipse Temurin OpenJDK `1.8.0_492`.
- **EvoSuite 1.2.0.** Standalone JAR + runtime JAR.
- **JUnit 4.13.2 + Hamcrest Core 1.3.** Required only to verify that the EvoSuite-generated tests compile.

## Reproduction steps

1. **Install JDK 8** alongside any existing JDK (do not replace the default). Eclipse Temurin: <https://adoptium.net/temurin/releases/?version=8>

2. **Create the workspace and download the required JARs**:

   ```bash
   mkdir -p jars
   curl -L -o jars/evosuite-1.2.0.jar \
     https://github.com/EvoSuite/evosuite/releases/download/v1.2.0/evosuite-1.2.0.jar
   curl -L -o jars/evosuite-standalone-runtime-1.2.0.jar \
     https://github.com/EvoSuite/evosuite/releases/download/v1.2.0/evosuite-standalone-runtime-1.2.0.jar
   curl -L -o jars/junit-4.13.2.jar \
     https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
   curl -L -o jars/hamcrest-core-1.3.jar \
     https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
   ```

3. **Copy the banking sources** from the main repository into `src/com/bank/logic/`. The four files are `IBankService.java`, `InsolventException.java`, `Main.java`, and `SavingsAccount.java`.

4. **Backport the single Java 10+ call** in `SavingsAccount.java#getHistory()` to a Java 8 equivalent:

   ```java
   // Before (Java 10+):
   return List.copyOf(history);

   // After (Java 8 compatible):
   return Collections.unmodifiableList(new ArrayList<>(history));
   ```

   And add `import java.util.Collections;` to the imports. Functionally identical (returns an unmodifiable snapshot of the history list); only this single line requires modification.

5. **Compile the banking sources with Java 8 target**:

   ```bash
   export JAVA_HOME="/path/to/jdk-8"
   "$JAVA_HOME/bin/javac" -source 1.8 -target 1.8 -d classes \
     src/com/bank/logic/IBankService.java \
     src/com/bank/logic/InsolventException.java \
     src/com/bank/logic/SavingsAccount.java \
     src/com/bank/logic/Main.java
   ```

6. **Run the EvoSuite baseline**:

   ```bash
   ./run-evosuite.sh
   ```

   The script iterates over `SavingsAccount`, `InsolventException`, and `Main` with a 180-second search budget per class and writes the consolidated statistics into `statistics/`. Approximate wall-clock: 7 minutes.

7. **Verify that the generated tests compile** (optional but recommended):

   ```bash
   mkdir -p evosuite-tests-compiled
   "$JAVA_HOME/bin/javac" -source 1.8 -target 1.8 \
     -cp "classes:jars/evosuite-standalone-runtime-1.2.0.jar:jars/junit-4.13.2.jar:jars/hamcrest-core-1.3.jar" \
     -d evosuite-tests-compiled \
     evosuite-tests/com/bank/logic/*.java
   ```

   (On Windows replace the `:` separators with `;`.) Expected: exit code 0; six `.class` files produced under `evosuite-tests-compiled/com/bank/logic/`.

## EvoSuite configuration used

The exact configuration is implemented in `run-evosuite.sh`. Key flags:

| Flag | Value | Rationale |
|---|---|---|
| `-Dsearch_budget` | `180` (seconds) | The default budget used in the original EvoSuite paper. |
| `-Dassertion_strategy` | `ALL` | Maximally strong assertions for a fair coverage comparison. |
| `-Djunit` | `4` | EvoSuite's JUnit 5 output is experimental in v1.2.0; JUnit 4 is the stable path. |
| `-Djunit_check` | `FALSE` | Disables EvoSuite's post-hoc JUnit stability check, which fails on Temurin JDK 8 on Windows due to a missing `AttachProvider` service registration. Coverage figures in `statistics.csv` are produced by EvoSuite's primary bytecode instrumentation during the search phase and are unaffected by this flag. |
| `-Doutput_variables` | `TARGET_CLASS,Coverage,LineCoverage,BranchCoverage,MethodCoverage,Total_Goals,Covered_Goals` | Splits the per-criterion coverage so it can be aligned 1:1 with Table 2 of the paper. |

## File layout

```
evosuite-baseline/
├── README.md                                       (this file)
├── run-evosuite.sh                                 (reproduction script)
├── .gitignore
├── statistics/
│   ├── SavingsAccount.csv                          (per-class raw CSV)
│   ├── InsolventException.csv
│   ├── Main.csv
│   └── summary.csv                                 (consolidated)
└── evosuite-tests/
    └── com/bank/logic/
        ├── SavingsAccount_ESTest.java
        ├── SavingsAccount_ESTest_scaffolding.java
        ├── InsolventException_ESTest.java
        ├── InsolventException_ESTest_scaffolding.java
        ├── Main_ESTest.java
        └── Main_ESTest_scaffolding.java
```

Artefacts produced during reproduction but not committed (see `.gitignore`):
`jars/`, `classes/`, `evosuite-report-*/`, `evosuite-tests-compiled/`, `production-run.log`, `src/`.

## Related artefacts

- `../tests.json` — the 16 judge-selected test files produced by the KDM + Multi-LLM + Judge pipeline (this work).
- `../evosuite-tests.json` — the 6 EvoSuite-generated files (this baseline) in the same JSON format.
- `statistics/summary.csv` — consolidated coverage figures matching Table 3 of the manuscript.
