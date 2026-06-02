#!/usr/bin/env bash
#
# Reproduces the EvoSuite baseline reported in §4.4 of the manuscript.
#
# Prerequisites:
#   - JDK 8 installed (EvoSuite 1.2.0 requires Java 8 runtime)
#   - Banking sources compiled with Java 8 target into ./classes
#   - Required JAR files present in ./jars (see README.md, section "Reproduction steps")
#
# Usage:
#   JAVA_HOME=/path/to/jdk-8 ./run-evosuite.sh
#
# Output:
#   - Generated tests:     ./evosuite-tests/com/bank/logic/<Class>_ESTest{,_scaffolding}.java
#   - Per-class report:    ./evosuite-report-<Class>/statistics.csv
#   - Consolidated log:    ./production-run.log

set -e

if [ -z "$JAVA_HOME" ]; then
    echo "ERROR: JAVA_HOME must be set to a JDK 8 installation." >&2
    echo "Example: export JAVA_HOME=/c/Program\\ Files/Eclipse\\ Adoptium/jdk-8.0.432.6-hotspot" >&2
    exit 1
fi

JAVA="$JAVA_HOME/bin/java"
EVOSUITE_JAR="jars/evosuite-1.2.0.jar"
BUDGET=180

if [ ! -f "$EVOSUITE_JAR" ]; then
    echo "ERROR: $EVOSUITE_JAR not found. See README.md for download instructions." >&2
    exit 1
fi

if [ ! -d "classes/com/bank/logic" ]; then
    echo "ERROR: classes/com/bank/logic not found. Compile sources first:" >&2
    echo "  \"\$JAVA_HOME/bin/javac\" -source 1.8 -target 1.8 -d classes src/com/bank/logic/*.java" >&2
    exit 1
fi

# Clean previous output
rm -rf evosuite-tests evosuite-report-SavingsAccount evosuite-report-InsolventException evosuite-report-Main production-run.log
mkdir -p evosuite-tests

echo "===== START $(date) =====" > production-run.log

for CLS in SavingsAccount InsolventException Main; do
    echo "" >> production-run.log
    echo "===== RUNNING $CLS at $(date) =====" >> production-run.log
    echo ">>> Running EvoSuite on com.bank.logic.$CLS (budget=${BUDGET}s)"

    "$JAVA" -jar "$EVOSUITE_JAR" \
        -class "com.bank.logic.$CLS" \
        -projectCP classes \
        -Dsearch_budget=$BUDGET \
        -Dassertion_strategy=ALL \
        -Dtest_dir=evosuite-tests \
        -Dreport_dir=evosuite-report-$CLS \
        -Doutput_variables=TARGET_CLASS,Coverage,LineCoverage,BranchCoverage,MethodCoverage,Total_Goals,Covered_Goals \
        -Djunit_check=FALSE \
        -Djunit=4 >> production-run.log 2>&1

    echo "===== DONE $CLS at $(date) =====" >> production-run.log
done

echo "" >> production-run.log
echo "===== ALL DONE $(date) =====" >> production-run.log

# Consolidate statistics
mkdir -p statistics
for CLS in SavingsAccount InsolventException Main; do
    cp "evosuite-report-$CLS/statistics.csv" "statistics/$CLS.csv"
done

echo ""
echo ">>> Run complete. Per-class CSVs in ./statistics/"
echo ">>> Generated tests in ./evosuite-tests/com/bank/logic/"
