import express, { Request, Response } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import dotenv from 'dotenv';
import { verifyFirebaseTokens, AuthenticatedRequest } from './middleware/auth';
import { apiRateLimiter } from './middleware/rateLimiter';
import { VekKernelService, MathOp } from './services/vekKernelService';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 8080;
const configuredOrigins = (process.env.ALLOWED_ORIGINS || '')
  .split(',')
  .map(origin => origin.trim())
  .filter(Boolean);

app.use(helmet());
app.use(cors({
  origin(origin, callback) {
    if (!origin || configuredOrigins.includes(origin) || process.env.NODE_ENV !== 'production') {
      callback(null, true);
      return;
    }
    callback(new Error('Origin is not permitted'));
  }
}));
app.use(express.json());
app.use(apiRateLimiter);

// Structured JSON Logger Middleware
app.use((req: Request, res: Response, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    console.log(JSON.stringify({
      timestamp: new Date().toISOString(),
      method: req.method,
      path: req.path,
      statusCode: res.statusCode,
      durationMs: duration,
      userAgent: req.headers['user-agent'] || 'unknown'
    }));
  });
  next();
});

// 1. Health & VEK Kernel Readiness Check
app.get('/health', (req: Request, res: Response) => {
  res.status(200).json({
    status: 'HEALTHY',
    service: 'Nexus Verification Gateway',
    version: '1.0.0',
    vekKernelReady: true,
    timestamp: new Date().toISOString()
  });
});

// 2. Claim Verification Endpoint
app.post('/v1/verify-claim', verifyFirebaseTokens, (req: AuthenticatedRequest, res: Response) => {
  try {
    const { rawInput, domain, contentType } = req.body;

    if (!rawInput || typeof rawInput !== 'string') {
      return res.status(400).json({
        error: 'INVALID_INPUT',
        message: 'rawInput string parameter is required'
      });
    }

    // Server-side VEK evaluation
    const traceId = 'VEK-GW-' + Math.random().toString(36).substring(2, 8).toUpperCase();
    const canonicalHash = VekKernelService.computeSha256(rawInput);

    return res.status(200).json({
      status: 'INCONCLUSIVE',
      caseId: 'NX-GW-' + Math.random().toString(36).substring(2, 8).toUpperCase(),
      domain: domain || 'General Consumer',
      contentType: contentType || 'Text Claim',
      rawInput,
      traceId,
      canonicalHash,
      requiresAuthenticatedEvidence: true,
      verifiedStatus: false, // Unauthenticated evidence cannot be represented as verified
      assessmentSummary: 'No authenticated evidence was supplied; factual verification is inconclusive.',
      timestamp: new Date().toISOString()
    });
  } catch (err: any) {
    return res.status(500).json({
      error: 'VERIFICATION_FAILED',
      message: err.message || 'Error executing claim verification'
    });
  }
});

// 3. Evidence Logging Endpoint
app.post('/v1/evidence-record', verifyFirebaseTokens, (req: AuthenticatedRequest, res: Response) => {
  try {
    const { caseId, evidenceTitle, publisher, sourceUrl, isPrimarySource } = req.body;

    if (!caseId || !evidenceTitle) {
      return res.status(400).json({
        error: 'INVALID_EVIDENCE_PAYLOAD',
        message: 'caseId and evidenceTitle are required'
      });
    }

    const canonicalContent = JSON.stringify({
      caseId,
      evidenceTitle,
      publisher: publisher || 'Unknown Publisher',
      isPrimarySource: Boolean(isPrimarySource),
      sourceUrl: sourceUrl || ''
    });

    const sha256Commitment = VekKernelService.computeSha256(canonicalContent);

    return res.status(201).json({
      evidenceId: 'EV-' + Math.random().toString(36).substring(2, 8).toUpperCase(),
      caseId,
      evidenceTitle,
      publisher,
      isPrimarySource: Boolean(isPrimarySource),
      canonicalJson: canonicalContent,
      sha256Commitment,
      authenticatedUser: req.user ? req.user.uid : 'ANONYMOUS_UNAUTHENTICATED',
      timestamp: new Date().toISOString()
    });
  } catch (err: any) {
    return res.status(500).json({
      error: 'EVIDENCE_RECORD_FAILED',
      message: err.message || 'Failed to record evidence'
    });
  }
});

// 4. Replay Verification Endpoint
app.post('/v1/replay-verification', verifyFirebaseTokens, (req: AuthenticatedRequest, res: Response) => {
  try {
    const { initialValue, operations, replayCount, integerOnly } = req.body;

    if ((typeof initialValue !== 'number' && typeof initialValue !== 'string') || !Array.isArray(operations)) {
      return res.status(400).json({
        error: 'INVALID_REPLAY_PAYLOAD',
        message: 'initialValue (number or decimal string) and operations (array) are required'
      });
    }

    const mathOps: MathOp[] = operations.map((opItem: any) => ({
      op: opItem.op,
      value: opItem.value
    }));

    const result = VekKernelService.replay(
      initialValue,
      mathOps,
      Number(replayCount || 2),
      Boolean(integerOnly)
    );

    return res.status(200).json(result);
  } catch (err: any) {
    return res.status(400).json({
      error: 'REPLAY_VERIFICATION_REJECTED',
      message: err.message || 'Failed to complete replay verification'
    });
  }
});

app.listen(PORT, () => {
  console.log(JSON.stringify({
    event: 'SERVER_STARTED',
    message: `Nexus Verification Gateway running on port ${PORT}`,
    environment: process.env.NODE_ENV || 'development'
  }));
});
