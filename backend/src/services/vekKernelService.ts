import * as crypto from 'crypto';
import Decimal from 'decimal.js';

Decimal.set({
  precision: 34,
  rounding: Decimal.ROUND_HALF_EVEN,
  toExpNeg: -1000000,
  toExpPos: 1000000
});

export type MathOpName = 'ADD' | 'SUBTRACT' | 'MULTIPLY' | 'DIVIDE';

export interface MathOp {
  op: MathOpName;
  value: number | string;
}

export interface TransitionStep {
  stepIndex: number;
  op: MathOpName;
  operand: number;
  previousState: number;
  nextState: number;
}

interface CanonicalTransitionStep {
  index: number;
  operation: MathOpName;
  operand: string;
  before: string;
  after: string;
}

export interface DeterministicExecutionResult {
  initialValue: number;
  operations: MathOp[];
  transitions: TransitionStep[];
  finalValue: number;
  canonicalJson: string;
  sha256Hash: string;
}

export interface ReplayComparisonResult {
  pass: boolean;
  replayCount: number;
  runs: DeterministicExecutionResult[];
  run1Final: number;
  run2Final: number;
  run1Hash: string;
  run2Hash: string;
  firstDivergenceStep: number | null;
  divergenceReason: string | null;
}

export class VekKernelService {
  private static readonly schemaVersion = 'nexus.execution.v1';

  public static executeDeterministic(
    initialValue: number | string,
    operations: MathOp[],
    integerOnly: boolean = false
  ): DeterministicExecutionResult {
    if (operations.length === 0) {
      throw new Error('At least one operation is required');
    }
    if (operations.length > 100) {
      throw new Error('Execution is limited to 100 operations per request');
    }

    const initial = this.decimal(initialValue, 'initial value');
    if (integerOnly && !initial.isInteger()) {
      throw new Error('Non-integral initial value rejected in integer-only mode');
    }

    let current = initial;
    const transitions: TransitionStep[] = [];
    const canonicalSteps: CanonicalTransitionStep[] = [];

    operations.forEach((mathOp, index) => {
      if (!['ADD', 'SUBTRACT', 'MULTIPLY', 'DIVIDE'].includes(mathOp.op)) {
        throw new Error(`Unsupported operation: ${String(mathOp.op)}`);
      }

      const operand = this.decimal(mathOp.value, `operand for operation ${index + 1}`);
      if (integerOnly && !operand.isInteger()) {
        throw new Error('Non-integral operand rejected in integer-only mode');
      }
      if (mathOp.op === 'DIVIDE' && operand.isZero()) {
        throw new Error('Division by zero is not permitted.');
      }

      const before = current;
      switch (mathOp.op) {
        case 'ADD':
          current = before.plus(operand);
          break;
        case 'SUBTRACT':
          current = before.minus(operand);
          break;
        case 'MULTIPLY':
          current = before.times(operand);
          break;
        case 'DIVIDE':
          current = before.dividedBy(operand);
          break;
      }

      if (integerOnly && !current.isInteger()) {
        throw new Error(
          `Operation ${mathOp.op} ${this.canonicalNumber(operand)} produced a non-integer result.`
        );
      }

      const canonicalStep: CanonicalTransitionStep = {
        index: index + 1,
        operation: mathOp.op,
        operand: this.canonicalNumber(operand),
        before: this.canonicalNumber(before),
        after: this.canonicalNumber(current)
      };
      canonicalSteps.push(canonicalStep);
      transitions.push({
        stepIndex: canonicalStep.index,
        op: canonicalStep.operation,
        operand: Number(canonicalStep.operand),
        previousState: Number(canonicalStep.before),
        nextState: Number(canonicalStep.after)
      });
    });

    const canonicalJson = this.buildCanonicalJson(initial, canonicalSteps, current);
    return {
      initialValue: Number(this.canonicalNumber(initial)),
      operations,
      transitions,
      finalValue: Number(this.canonicalNumber(current)),
      canonicalJson,
      sha256Hash: this.computeSha256(canonicalJson)
    };
  }

  public static buildCanonicalJson(
    initialValue: Decimal,
    steps: CanonicalTransitionStep[],
    finalValue: Decimal
  ): string {
    const stepsJson = steps.map(step =>
      `{"index":${step.index},` +
      `"operation":"${step.operation}",` +
      `"operand":"${this.escapeJson(step.operand)}",` +
      `"before":"${this.escapeJson(step.before)}",` +
      `"after":"${this.escapeJson(step.after)}"}`
    ).join(',');

    return `{"schemaVersion":"${this.schemaVersion}",` +
      `"initialValue":"${this.escapeJson(this.canonicalNumber(initialValue))}",` +
      `"steps":[${stepsJson}],` +
      `"finalValue":"${this.escapeJson(this.canonicalNumber(finalValue))}",` +
      `"disposition":"ACCEPT"}`;
  }

  public static computeSha256(input: string): string {
    return crypto.createHash('sha256').update(input, 'utf8').digest('hex');
  }

  public static replay(
    initialValue: number | string,
    operations: MathOp[],
    replayCount: number = 2,
    integerOnly: boolean = false
  ): ReplayComparisonResult {
    const count = Math.min(Math.max(Math.trunc(replayCount), 1), 10);
    const runs = Array.from({ length: count }, () =>
      this.executeDeterministic(initialValue, operations, integerOnly)
    );

    const run1 = runs[0];
    const firstDifferenceIndex = runs.findIndex(
      run => run.canonicalJson !== run1.canonicalJson || run.sha256Hash !== run1.sha256Hash
    );
    const pass = firstDifferenceIndex === -1;

    return {
      pass,
      replayCount: count,
      runs,
      run1Final: run1.finalValue,
      run2Final: (runs[1] ?? run1).finalValue,
      run1Hash: run1.sha256Hash,
      run2Hash: (runs[1] ?? run1).sha256Hash,
      firstDivergenceStep: pass ? null : firstDifferenceIndex + 1,
      divergenceReason: pass
        ? null
        : `Run ${firstDifferenceIndex + 1} canonical JSON or SHA-256 diverged from Run 1`
    };
  }

  private static decimal(value: number | string, label: string): Decimal {
    let parsed: Decimal;
    try {
      parsed = new Decimal(value);
    } catch {
      throw new Error(`Invalid ${label}`);
    }
    if (!parsed.isFinite()) {
      throw new Error(`${label} must be finite`);
    }
    return parsed;
  }

  private static canonicalNumber(value: Decimal): string {
    return value.isZero() ? '0' : value.toFixed();
  }

  private static escapeJson(value: string): string {
    return value
      .replace(/\\/g, '\\\\')
      .replace(/"/g, '\\"')
      .replace(/\n/g, '\\n')
      .replace(/\r/g, '\\r')
      .replace(/\t/g, '\\t');
  }
}
