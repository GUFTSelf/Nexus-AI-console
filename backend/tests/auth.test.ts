import { Request, Response, NextFunction } from 'express';
import { verifyFirebaseTokens, AuthenticatedRequest } from '../src/middleware/auth';
import * as admin from 'firebase-admin';

jest.mock('firebase-admin', () => {
  const mockVerifyIdToken = jest.fn();
  const mockVerifyAppCheckToken = jest.fn();

  return {
    apps: [{}],
    initializeApp: jest.fn(),
    auth: jest.fn(() => ({
      verifyIdToken: mockVerifyIdToken
    })),
    appCheck: jest.fn(() => ({
      verifyToken: mockVerifyAppCheckToken
    })),
    __mockVerifyIdToken: mockVerifyIdToken,
    __mockVerifyAppCheckToken: mockVerifyAppCheckToken
  };
});

describe('Authentication Middleware - verifyFirebaseTokens', () => {
  let req: Partial<AuthenticatedRequest>;
  let res: Partial<Response>;
  let next: jest.Mock<NextFunction>;
  const originalNodeEnv = process.env.NODE_ENV;

  beforeEach(() => {
    req = {
      headers: {}
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis()
    };
    next = jest.fn();
    jest.clearAllMocks();
    process.env.NODE_ENV = 'test';
  });

  afterAll(() => {
    process.env.NODE_ENV = originalNodeEnv;
  });

  it('should call next() without error when no authorization headers are present', async () => {
    await verifyFirebaseTokens(req as AuthenticatedRequest, res as Response, next);

    expect(next).toHaveBeenCalledTimes(1);
    expect(res.status).not.toHaveBeenCalled();
    expect(req.user).toBeUndefined();
  });

  it('should verify valid Bearer token and attach user to req', async () => {
    const mockUser = { uid: 'user_nexus_123', email: 'verified@nexus.ai' };
    const mockAuth = admin.auth();
    (mockAuth.verifyIdToken as jest.Mock).mockResolvedValueOnce(mockUser);

    req.headers = { authorization: 'Bearer valid_jwt_token_123' };

    await verifyFirebaseTokens(req as AuthenticatedRequest, res as Response, next);

    expect(mockAuth.verifyIdToken).toHaveBeenCalledWith('valid_jwt_token_123');
    expect(req.user).toEqual(mockUser);
    expect(next).toHaveBeenCalledTimes(1);
    expect(res.status).not.toHaveBeenCalled();
  });

  it('should return 401 when Bearer token verification fails', async () => {
    const mockAuth = admin.auth();
    (mockAuth.verifyIdToken as jest.Mock).mockRejectedValueOnce(new Error('Token expired'));

    req.headers = { authorization: 'Bearer expired_or_invalid_token' };

    await verifyFirebaseTokens(req as AuthenticatedRequest, res as Response, next);

    expect(mockAuth.verifyIdToken).toHaveBeenCalledWith('expired_or_invalid_token');
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({
      error: 'UNAUTHORIZED_INVALID_USER_TOKEN',
      message: 'Invalid or expired Firebase Auth token'
    });
    expect(next).not.toHaveBeenCalled();
  });

  it('should verify App Check token in production environment and call next()', async () => {
    process.env.NODE_ENV = 'production';
    const mockAppCheckToken = { appId: 'app_nexus_999' };
    const mockAppCheck = admin.appCheck();
    (mockAppCheck.verifyToken as jest.Mock).mockResolvedValueOnce(mockAppCheckToken);

    req.headers = { 'x-firebase-appcheck': 'valid_app_check_header' };

    await verifyFirebaseTokens(req as AuthenticatedRequest, res as Response, next);

    expect(mockAppCheck.verifyToken).toHaveBeenCalledWith('valid_app_check_header');
    expect(req.appCheckToken).toEqual(mockAppCheckToken);
    expect(next).toHaveBeenCalledTimes(1);
    expect(res.status).not.toHaveBeenCalled();
  });

  it('should return 401 when App Check token verification fails in production', async () => {
    process.env.NODE_ENV = 'production';
    const mockAppCheck = admin.appCheck();
    (mockAppCheck.verifyToken as jest.Mock).mockRejectedValueOnce(new Error('Invalid app check token'));

    req.headers = { 'x-firebase-appcheck': 'invalid_app_check_header' };

    await verifyFirebaseTokens(req as AuthenticatedRequest, res as Response, next);

    expect(mockAppCheck.verifyToken).toHaveBeenCalledWith('invalid_app_check_header');
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({
      error: 'UNAUTHORIZED_APP_CHECK_FAILED',
      message: 'Invalid or expired Firebase App Check token'
    });
    expect(next).not.toHaveBeenCalled();
  });
});
