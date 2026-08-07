import assert from 'node:assert/strict';
import test from 'node:test';
import { VekKernelService } from './vekKernelService';

const fixedOperations = [
  { op: 'MULTIPLY' as const, value: 8 },
  { op: 'SUBTRACT' as const, value: 11 },
  { op: 'DIVIDE' as const, value: 5 },
  { op: 'ADD' as const, value: 9 }
];

test('fixed vector derives the cross-platform SHA-256 from canonical JSON', () => {
  const result = VekKernelService.executeDeterministic(7, fixedOperations, true);

  assert.equal(result.finalValue, 18);
  assert.equal(
    result.sha256Hash,
    '3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996'
  );
  assert.equal(result.sha256Hash, VekKernelService.computeSha256(result.canonicalJson));
});

test('replay records are byte-identical', () => {
  const result = VekKernelService.replay(7, fixedOperations, 2, true);

  assert.equal(result.pass, true);
  assert.equal(result.replayCount, 2);
  assert.equal(result.runs[0].canonicalJson, result.runs[1].canonicalJson);
  assert.equal(result.run1Hash, result.run2Hash);
});

test('integer-only mode rejects non-integral results', () => {
  assert.throws(
    () => VekKernelService.executeDeterministic(10, [{ op: 'DIVIDE', value: 4 }], true),
    /non-integer result/
  );
});

test('division by zero is rejected', () => {
  assert.throws(
    () => VekKernelService.executeDeterministic(10, [{ op: 'DIVIDE', value: 0 }]),
    /Division by zero/
  );
});
