import { Request, Response, NextFunction } from 'express';
import { getApps, initializeApp } from 'firebase-admin/app';
import { DecodedIdToken, getAuth } from 'firebase-admin/auth';
import { getAppCheck, VerifyAppCheckTokenResponse } from 'firebase-admin/app-check';

// Initialize Firebase Admin SDK if service account environment variables are present
if (!getApps().length) {
  try {
    initializeApp();
  } catch (err) {
    console.warn('Firebase Admin initialization notice: Running without explicit service account config.');
  }
}

export interface AuthenticatedRequest extends Request {
  user?: DecodedIdToken;
  appCheckToken?: VerifyAppCheckTokenResponse;
}

export async function verifyFirebaseTokens(req: AuthenticatedRequest, res: Response, next: NextFunction) {
  const authHeader = req.headers.authorization;
  const appCheckHeader = req.headers['x-firebase-appcheck'] as string | undefined;
  const production = process.env.NODE_ENV === 'production';

  if (production && !appCheckHeader) {
    return res.status(401).json({
      error: 'UNAUTHORIZED_APP_CHECK_REQUIRED',
      message: 'Firebase App Check token is required'
    });
  }

  if (production && (!authHeader || !authHeader.startsWith('Bearer '))) {
    return res.status(401).json({
      error: 'UNAUTHORIZED_USER_TOKEN_REQUIRED',
      message: 'Firebase Auth bearer token is required'
    });
  }

  if (appCheckHeader) {
    try {
      const appCheckToken = await getAppCheck().verifyToken(appCheckHeader);
      req.appCheckToken = appCheckToken;
    } catch (error) {
      return res.status(401).json({
        error: 'UNAUTHORIZED_APP_CHECK_FAILED',
        message: 'Invalid or expired Firebase App Check token'
      });
    }
  }

  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.split('Bearer ')[1];
    try {
      const decodedUser = await getAuth().verifyIdToken(token);
      req.user = decodedUser;
    } catch (error) {
      return res.status(401).json({
        error: 'UNAUTHORIZED_INVALID_USER_TOKEN',
        message: 'Invalid or expired Firebase Auth token'
      });
    }
  }

  next();
}
