import request from 'supertest';
import app from '../src/index';

describe('Nexus Verification Gateway API Endpoints', () => {
  describe('POST /v1/verify-claim', () => {
    it('should return 400 when rawInput is missing', async () => {
      const response = await request(app)
        .post('/v1/verify-claim')
        .send({});

      expect(response.status).toBe(400);
      expect(response.body.error).toBe('INVALID_INPUT');
    });

    it('should return 200 with assessment summary and canonical SHA-256 hash when rawInput is valid', async () => {
      const response = await request(app)
        .post('/v1/verify-claim')
        .send({
          rawInput: 'Statistically zero variance in canonical math engine execution',
          domain: 'Computer Science',
          contentType: 'System Specification'
        });

      expect(response.status).toBe(200);
      expect(response.body.status).toBe('CONDITIONALLY_VERIFIED');
      expect(response.body.canonicalHash).toHaveLength(64);
      expect(response.body.caseId).toMatch(/^NX-GW-/);
      expect(response.body.traceId).toMatch(/^VEK-GW-/);
    });
  });

  describe('POST /v1/evidence-record', () => {
    it('should return 400 when required evidence fields are missing', async () => {
      const response = await request(app)
        .post('/v1/evidence-record')
        .send({ caseId: 'NX-GW-123' });

      expect(response.status).toBe(400);
      expect(response.body.error).toBe('INVALID_EVIDENCE_PAYLOAD');
    });

    it('should record evidence and return 201 with SHA-256 commitment', async () => {
      const response = await request(app)
        .post('/v1/evidence-record')
        .send({
          caseId: 'NX-GW-999',
          evidenceTitle: 'Formal Verification Report',
          publisher: 'MIT CSIL',
          isPrimarySource: true
        });

      expect(response.status).toBe(201);
      expect(response.body.evidenceId).toMatch(/^EV-/);
      expect(response.body.sha256Commitment).toHaveLength(64);
      expect(response.body.isPrimarySource).toBe(true);
    });
  });

  describe('POST /v1/replay-verification', () => {
    it('should return 400 for invalid replay payload', async () => {
      const response = await request(app)
        .post('/v1/replay-verification')
        .send({ initialValue: 'invalid' });

      expect(response.status).toBe(400);
      expect(response.body.error).toBe('INVALID_REPLAY_PAYLOAD');
    });

    it('should perform deterministic multi-run replay verification', async () => {
      const response = await request(app)
        .post('/v1/replay-verification')
        .send({
          initialValue: 10,
          operations: [
            { op: 'ADD', value: 5 },
            { op: 'MULTIPLY', value: 2 }
          ],
          replayCount: 3
        });

      expect(response.status).toBe(200);
      expect(response.body.pass).toBe(true);
      expect(response.body.replayCount).toBe(3);
      expect(response.body.run1Final).toBe(30);
      expect(response.body.run2Final).toBe(30);
      expect(response.body.run1Hash).toEqual(response.body.run2Hash);
    });
  });
});
