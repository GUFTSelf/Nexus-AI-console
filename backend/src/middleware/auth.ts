import { Request, Response, NextFunction } from 'express';
import * as admin from 'firebase-admin';

// Initialize Firebase Admin SDK if service account environment variables are present
if (!admin.apps.length) {
  try {
    admin.initializeApp();
  } catch (err) {
    console.warn('Firebase Admin initialization notice: Running without explicit service account config.');
  }
}

export interface AuthenticatedRequest extends Request {
  user?: admin.auth.DecodedIdToken;
  appCheckToken?: admin.appCheck.DecodedAppCheckToken;
}

export async function verifyFirebaseTokens(req: AuthenticatedRequest, res: Response, next: NextFunction) {
  const authHeader = req.headers.authorization;
  const appCheckHeader = req.headers['x-firebase-appcheck'] as string | undefined;

  // Verify App Check Header when available in production
  if (appCheckHeader && process.env.NODE_ENV === 'production') {
    try {
      const appCheckToken = await admin.appCheck().verifyToken(appCheckHeader);
      req.appCheckToken = appCheckToken;
    } catch (error) {
      return res.status(401).json({
        error: 'UNAUTHORIZED_APP_CHECK_FAILED',
        message: 'Invalid or expired Firebase App Check token'
      });
    }
  }

  // Verify Bearer Auth Token if header is provided
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.split('Bearer ')[1];
    try {
      const decodedUser = await admin.auth().verifyIdToken(token);
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
