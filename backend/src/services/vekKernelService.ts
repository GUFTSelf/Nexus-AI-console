import * as crypto from 'crypto';

export interface MathOp {
  op: 'ADD' | 'SUBTRACT' | 'MULTIPLY' | 'DIVIDE';
  value: number;
}

export interface TransitionStep {
  stepIndex: number;
  op: string;
  operand: number;
  previousState: number;
  nextState: number;
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
  public static executeDeterministic(
    initialValue: number,
    operations: MathOp[],
    integerOnly: boolean = false
  ): DeterministicExecutionResult {
    if (integerOnly && !Number.isInteger(initialValue)) {
      throw new Error('Non-integral initial value rejected in integer-only mode');
    }

    let currentState = initialValue;
    const transitions: TransitionStep[] = [];

    operations.forEach((mathOp, index) => {
      if (integerOnly && !Number.isInteger(mathOp.value)) {
        throw new Error('Non-integral operand rejected in integer-only mode');
      }
      if (mathOp.op === 'DIVIDE' && mathOp.value === 0) {
        throw new Error('Division by zero is rejected');
      }

      const prev = currentState;
      switch (mathOp.op) {
        case 'ADD':
          currentState = prev + mathOp.value;
          break;
        case 'SUBTRACT':
          currentState = prev - mathOp.value;
          break;
        case 'MULTIPLY':
          currentState = prev * mathOp.value;
          break;
        case 'DIVIDE':
          currentState = prev / mathOp.value;
          break;
      }

      if (integerOnly && !Number.isInteger(currentState)) {
        throw new Error('Non-integral result rejected in integer-only mode');
      }

      transitions.push({
        stepIndex: index + 1,
        op: mathOp.op,
        operand: mathOp.value,
        previousState: prev,
        nextState: currentState
      });
    });

    const canonicalJson = this.buildCanonicalJson(initialValue, operations, currentState);
    const hash = this.computeSha256(canonicalJson);

    return {
      initialValue,
      operations,
      transitions,
      finalValue: currentState,
      canonicalJson,
      sha256Hash: hash
    };
  }

  public static buildCanonicalJson(initialValue: number, operations: MathOp[], finalValue: number): string {
    const opsFormatted = operations.map(op => {
      const valStr = Number.isInteger(op.value) ? op.value.toString() : op.value.toString();
      return `{"op":"${op.op}","value":${valStr}}`;
    }).join(',');

    const initStr = Number.isInteger(initialValue) ? initialValue.toString() : initialValue.toString();
    const finalStr = Number.isInteger(finalValue) ? finalValue.toString() : finalValue.toString();

    return `{"input":${initStr},"operations":[${opsFormatted}],"result":${finalStr}}`;
  }

  public static computeSha256(input: string): string {
    return crypto.createHash('sha256').update(input, 'utf8').digest('hex');
  }

  public static replay(
    initialValue: number,
    operations: MathOp[],
    replayCount: number = 2,
    integerOnly: boolean = false
  ): ReplayComparisonResult {
    const count = Math.min(Math.max(replayCount, 1), 10);
    const runs: DeterministicExecutionResult[] = [];

    for (let i = 0; i < count; i++) {
      runs.push(this.executeDeterministic(initialValue, operations, integerOnly));
    }

    const run1 = runs[0];
    let pass = true;
    let firstDivergenceStep: number | null = null;
    let divergenceReason: string | null = null;

    for (let i = 1; i < runs.length; i++) {
      const run = runs[i];
      if (run.canonicalJson !== run1.canonicalJson || run.sha256Hash !== run1.sha256Hash) {
        pass = false;
        divergenceReason = `Run ${i + 1} JSON or SHA-256 hash diverged from Run 1`;
        firstDivergenceStep = i + 1;
        break;
      }
    }

    return {
      pass,
      replayCount: count,
      runs,
      run1Final: run1.finalValue,
      run2Final: runs.length > 1 ? runs[1].finalValue : run1.finalValue,
      run1Hash: run1.sha256Hash,
      run2Hash: runs.length > 1 ? runs[1].sha256Hash : run1.sha256Hash,
      firstDivergenceStep,
      divergenceReason
    };
  }
}
