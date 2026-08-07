import request from 'supertest';
import app from '../src/index';

describe('GET /health - Nexus Verification Gateway Health Check', () => {
  it('should return 200 OK with HEALTHY status and VEK readiness indicator', async () => {
    const response = await request(app).get('/health');

    expect(response.status).toBe(200);
    expect(response.body).toBeDefined();
    expect(response.body.status).toBe('HEALTHY');
    expect(response.body.service).toBe('Nexus Verification Gateway');
    expect(response.body.version).toBe('1.0.0');
    expect(response.body.vekKernelReady).toBe(true);
    expect(response.body.timestamp).toBeDefined();
    expect(new Date(response.body.timestamp).getTime()).not.toBeNaN();
  });
});
